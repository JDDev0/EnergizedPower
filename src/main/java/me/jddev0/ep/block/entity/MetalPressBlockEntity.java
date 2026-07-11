package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.SimpleRecipeMachineBlockEntity;
import me.jddev0.ep.inventory.EnergizedPowerItemStackHandler;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.recipe.ContainerRecipeInputWrapper;
import me.jddev0.ep.recipe.MetalPressRecipe;
import me.jddev0.ep.recipe.EPRecipes;
import me.jddev0.ep.registry.tags.EnergizedPowerItemTags;
import me.jddev0.ep.screen.MetalPressMenu;
import me.jddev0.ep.util.InventoryUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MetalPressBlockEntity extends SimpleRecipeMachineBlockEntity<RecipeInput, MetalPressRecipe> {
    private final IItemHandler itemHandlerTopSided = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 1, i -> i == 1);
    private final IItemHandler itemHandlerOthersSided = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0, i -> i == 2);

    public MetalPressBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.METAL_PRESS_ENTITY.get(), blockPos, blockState,

                "metal_press", MetalPressMenu::new,

                3, EPRecipes.METAL_PRESS_TYPE.get(), ModConfigs.COMMON_METAL_PRESS_RECIPE_DURATION.getValue(),

                ModConfigs.COMMON_METAL_PRESS_CAPACITY.getValue(),
                ModConfigs.COMMON_METAL_PRESS_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_METAL_PRESS_ENERGY_CONSUMPTION_PER_TICK.getValue(),

                UpgradeModuleModifier.SPEED,
                UpgradeModuleModifier.ENERGY_CONSUMPTION,
                UpgradeModuleModifier.ENERGY_CAPACITY,
                UpgradeModuleModifier.ITEM_EJECTOR,
                UpgradeModuleModifier.ITEM_PULLING
        );

        slotCountPerRecipe = 3;
    }

    @Override
    protected EnergizedPowerItemStackHandler initInventoryStorage() {
        return new EnergizedPowerItemStackHandler(slotCount) {
            @Override
            public int getCapacity(int slot) {
                if(slot == 1)
                    return 1;

                return super.getCapacity(slot);
            }

            @Override
            public boolean isValid(int slot, @NotNull ItemStack stack) {
                return switch(slot) {
                    case 0 -> level == null || level.getRecipeManager().getAllRecipesFor(MetalPressRecipe.Type.INSTANCE).stream().
                            map(RecipeHolder::value).map(MetalPressRecipe::getInput).anyMatch(ingredient -> ingredient.test(stack));
                    case 1 -> level == null || stack.is(EnergizedPowerItemTags.METAL_PRESS_MOLDS);
                    case 2 -> false;
                    default -> super.isValid(slot, stack);
                };
            }

            @Override
            protected void onFinalCommit(int slot, @NotNull ItemStack previousItemStack) {
                if(slot == 0 || slot == 1) {
                    ItemStack stack = getStackInSlot(slot);
                    if(level != null && !stack.isEmpty() && !previousItemStack.isEmpty() && !ItemStack.isSameItemSameComponents(stack, previousItemStack))
                        resetProgress(0);
                }

                setChanged();
            }
        };
    }

    public @Nullable IItemHandler getItemHandlerCapability(@Nullable Direction side) {
        if(side == null)
            return itemHandler;

        if(side == Direction.UP)
            return itemHandlerTopSided;

        return itemHandlerOthersSided;
    }

    public @Nullable IEnergyStorage getEnergyStorageCapability(@Nullable Direction side) {
        return limitingEnergyStorage;
    }

    @Override
    protected RecipeInput getRecipeInput(Container inventory) {
        return new ContainerRecipeInputWrapper(inventory);
    }

    @Override
    protected void craftItem(int thread, RecipeHolder<MetalPressRecipe> recipe) {
        if(level == null || !hasRecipe(thread) || !(level instanceof ServerLevel serverLevel))
            return;

        int startOffset = getSlotStartOffsetFor(thread);
        ItemStack pressMold = itemHandler.getStackInSlot(startOffset + 1).copy();
        if(pressMold.isEmpty() && !pressMold.is(EnergizedPowerItemTags.METAL_PRESS_MOLDS))
            return;

        pressMold.hurtAndBreak(1, serverLevel, null, item -> pressMold.setCount(0));
        itemHandler.setStackInSlot(startOffset + 1, pressMold);

        itemHandler.extractItem(startOffset, recipe.value().getInputCount(), false);
        itemHandler.setStackInSlot(startOffset + 2, recipe.value().getResultItem(level.registryAccess()).
                copyWithCount(itemHandler.getStackInSlot(startOffset + 2).getCount() +
                        recipe.value().getResultItem(level.registryAccess()).getCount()));

        resetProgress(thread);
    }
}