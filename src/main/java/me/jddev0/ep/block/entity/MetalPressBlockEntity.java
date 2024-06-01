package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.SimpleRecipeMachineBlockEntity;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.recipe.MetalPressRecipe;
import me.jddev0.ep.recipe.ModRecipes;
import me.jddev0.ep.registry.tags.EnergizedPowerItemTags;
import me.jddev0.ep.screen.MetalPressMenu;
import me.jddev0.ep.util.InventoryUtils;
import me.jddev0.ep.util.ItemStackUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MetalPressBlockEntity extends SimpleRecipeMachineBlockEntity<MetalPressRecipe> {
    private LazyOptional<IEnergyStorage> lazyEnergyStorage = LazyOptional.empty();

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private final LazyOptional<IItemHandler> lazyItemHandlerTopSided = LazyOptional.of(
            () -> new InputOutputItemHandler(itemHandler, (i, stack) -> i == 1, i -> i == 1));
    private final LazyOptional<IItemHandler> lazyItemHandlerOthersSided = LazyOptional.of(
            () -> new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0, i -> i == 2));

    public MetalPressBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                ModBlockEntities.METAL_PRESS_ENTITY.get(), blockPos, blockState,

                "metal_press", MetalPressMenu::new,

                3, ModRecipes.METAL_PRESS_TYPE.get(), ModConfigs.COMMON_METAL_PRESS_RECIPE_DURATION.getValue(),

                ModConfigs.COMMON_METAL_PRESS_CAPACITY.getValue(),
                ModConfigs.COMMON_METAL_PRESS_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_METAL_PRESS_ENERGY_CONSUMPTION_PER_TICK.getValue(),

                UpgradeModuleModifier.SPEED,
                UpgradeModuleModifier.ENERGY_CONSUMPTION,
                UpgradeModuleModifier.ENERGY_CAPACITY
        );
    }

    @Override
    protected ItemStackHandler initInventoryStorage() {
        return new ItemStackHandler(slotCount) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return switch(slot) {
                    case 0 -> level == null || level.getRecipeManager().getAllRecipesFor(MetalPressRecipe.Type.INSTANCE).stream().
                            map(MetalPressRecipe::getInput).anyMatch(ingredient -> ingredient.test(stack));
                    case 1 -> level == null || stack.is(EnergizedPowerItemTags.METAL_PRESS_MOLDS);
                    case 2 -> false;
                    default -> super.isItemValid(slot, stack);
                };
            }

            @Override
            public void setStackInSlot(int slot, @NotNull ItemStack stack) {
                if(slot == 0 || slot == 1) {
                    ItemStack itemStack = getStackInSlot(slot);
                    if(level != null && !stack.isEmpty() && !itemStack.isEmpty() && !ItemStack.isSameItemSameTags(stack, itemStack))
                        resetProgress();
                }

                super.setStackInSlot(slot, stack);
            }

            @Override
            public int getSlotLimit(int slot) {
                if(slot == 1)
                    return 1;

                return super.getSlotLimit(slot);
            }
        };
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.ITEM_HANDLER) {
            if(side == null)
                return lazyItemHandler.cast();

            if(side == Direction.UP)
                return lazyItemHandlerTopSided.cast();

            return lazyItemHandlerOthersSided.cast();
        }else if(cap == ForgeCapabilities.ENERGY) {
            return lazyEnergyStorage.cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();

        lazyItemHandler = LazyOptional.of(() -> itemHandler);
        lazyEnergyStorage = LazyOptional.of(() -> energyStorage);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();

        lazyItemHandler.invalidate();
        lazyEnergyStorage.invalidate();
    }

    @Override
    protected void craftItem(MetalPressRecipe recipe) {
        if(level == null || !hasRecipe())
            return;

         ItemStack pressMold = itemHandler.getStackInSlot(1).copy();
        if(pressMold.isEmpty() && !pressMold.is(EnergizedPowerItemTags.METAL_PRESS_MOLDS))
            return;

        if(pressMold.hurt(1, level.random, null))
            itemHandler.setStackInSlot(1, ItemStack.EMPTY);
        else
            itemHandler.setStackInSlot(1, pressMold);

        itemHandler.extractItem(0, recipe.getInputCount(), false);
        itemHandler.setStackInSlot(2, ItemStackUtils.copyWithCount(recipe.getResultItem(),
                itemHandler.getStackInSlot(2).getCount() +
                        recipe.getResultItem().getCount()));

        resetProgress();
    }

    @Override
    protected boolean canCraftRecipe(SimpleContainer inventory, MetalPressRecipe recipe) {
        return level != null &&
                InventoryUtils.canInsertItemIntoSlot(inventory, 2, recipe.getResultItem());
    }
}