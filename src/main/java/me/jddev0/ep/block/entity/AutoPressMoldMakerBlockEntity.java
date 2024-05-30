package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.SelectableRecipeMachineBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.recipe.PressMoldMakerRecipe;
import me.jddev0.ep.recipe.ModRecipes;
import me.jddev0.ep.screen.AutoPressMoldMakerMenu;
import me.jddev0.ep.util.InventoryUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.capabilities.Capabilities;
import net.neoforged.neoforge.common.capabilities.Capability;
import net.neoforged.neoforge.common.util.LazyOptional;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AutoPressMoldMakerBlockEntity
        extends SelectableRecipeMachineBlockEntity<PressMoldMakerRecipe> {
    private final LazyOptional<IEnergyStorage> lazyEnergyStorage;

    private final LazyOptional<IItemHandler> lazyItemHandler;
    private final LazyOptional<IItemHandler> lazyItemHandlerSided = LazyOptional.of(
            () -> new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0 || i == 1, i -> i == 2));

    public AutoPressMoldMakerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                ModBlockEntities.AUTO_PRESS_MOLD_MAKER_ENTITY.get(), blockPos, blockState,

                "auto_press_mold_maker", AutoPressMoldMakerMenu::new,

                3,
                ModRecipes.PRESS_MOLD_MAKER_TYPE.get(),
                ModRecipes.PRESS_MOLD_MAKER_SERIALIZER.get(),
                ModConfigs.COMMON_AUTO_PRESS_MOLD_MAKER_RECIPE_DURATION.getValue(),

                ModConfigs.COMMON_AUTO_PRESS_MOLD_MAKER_CAPACITY.getValue(),
                ModConfigs.COMMON_AUTO_PRESS_MOLD_MAKER_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_AUTO_PRESS_MOLD_MAKER_ENERGY_CONSUMPTION_PER_TICK.getValue(),

                UpgradeModuleModifier.SPEED,
                UpgradeModuleModifier.ENERGY_CONSUMPTION,
                UpgradeModuleModifier.ENERGY_CAPACITY
        );

        lazyItemHandler = LazyOptional.of(() -> itemHandler);
        lazyEnergyStorage = LazyOptional.of(() -> energyStorage);
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
                    case 0 -> stack.is(Items.CLAY_BALL);
                    case 1 -> stack.is(ItemTags.SHOVELS);
                    case 2 -> false;
                    default -> super.isItemValid(slot, stack);
                };
            }
        };
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == Capabilities.ITEM_HANDLER) {
            if(side == null)
                return lazyItemHandler.cast();

            return lazyItemHandlerSided.cast();
        }else if(cap == Capabilities.ENERGY) {
            return lazyEnergyStorage.cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    protected void craftItem(RecipeHolder<PressMoldMakerRecipe> recipe) {
        if(level == null || !hasRecipe())
            return;

        ItemStack shovel = itemHandler.getStackInSlot(1).copy();
        if(shovel.isEmpty() && !shovel.is(ItemTags.SHOVELS))
            return;

        if(shovel.hurt(1, level.random, null))
            itemHandler.setStackInSlot(1, ItemStack.EMPTY);
        else
            itemHandler.setStackInSlot(1, shovel);

        itemHandler.extractItem(0, recipe.value().getClayCount(), false);
        itemHandler.setStackInSlot(2, recipe.value().getResultItem(level.registryAccess()).
                copyWithCount(itemHandler.getStackInSlot(2).getCount() +
                        recipe.value().getResultItem(level.registryAccess()).getCount()));

        resetProgress();
    }

    @Override
    protected boolean canCraftRecipe(SimpleContainer inventory, RecipeHolder<PressMoldMakerRecipe> recipe) {
        return level != null &&
                itemHandler.getStackInSlot(0).is(Items.CLAY_BALL) &&
                itemHandler.getStackInSlot(0).getCount() >= recipe.value().getClayCount() &&
                itemHandler.getStackInSlot(1).is(ItemTags.SHOVELS) &&
                InventoryUtils.canInsertItemIntoSlot(inventory, 2, recipe.value().getResultItem(level.registryAccess()));
    }
}