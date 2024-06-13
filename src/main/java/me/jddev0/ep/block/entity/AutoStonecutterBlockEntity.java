package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.SelectableRecipeMachineBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.screen.AutoStonecutterMenu;
import me.jddev0.ep.util.InventoryUtils;
import me.jddev0.ep.util.RecipeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AutoStonecutterBlockEntity
        extends SelectableRecipeMachineBlockEntity<SingleRecipeInput, StonecutterRecipe> {
    private final IItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0 || i == 1, i -> i == 2);

    public AutoStonecutterBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                ModBlockEntities.AUTO_STONECUTTER_ENTITY.get(), blockPos, blockState,

                "auto_stonecutter", AutoStonecutterMenu::new,

                3,
                RecipeType.STONECUTTING,
                RecipeSerializer.STONECUTTER,
                ModConfigs.COMMON_AUTO_STONECUTTER_RECIPE_DURATION.getValue(),

                ModConfigs.COMMON_AUTO_STONECUTTER_CAPACITY.getValue(),
                ModConfigs.COMMON_AUTO_STONECUTTER_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_AUTO_STONECUTTER_ENERGY_CONSUMPTION_PER_TICK.getValue(),

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
                    case 0 -> level == null || RecipeUtils.isIngredientOfAny(level, recipeType, stack);
                    case 1 -> stack.is(ItemTags.PICKAXES);
                    case 2 -> false;
                    default -> super.isItemValid(slot, stack);
                };
            }
        };
    }

    public @Nullable IItemHandler getItemHandlerCapability(@Nullable Direction side) {
        if(side == null)
            return itemHandler;

        return itemHandlerSided;
    }

    public @Nullable IEnergyStorage getEnergyStorageCapability(@Nullable Direction side) {
        return energyStorage;
    }

    @Override
    protected void craftItem(RecipeHolder<StonecutterRecipe> recipe) {
        if(level == null || !hasRecipe() || !(level instanceof ServerLevel serverLevel))
            return;

        ItemStack pickaxe = itemHandler.getStackInSlot(1).copy();
        if(pickaxe.isEmpty() && !pickaxe.is(ItemTags.PICKAXES))
            return;

        pickaxe.hurtAndBreak(1, serverLevel, null, item -> pickaxe.setCount(0));
        itemHandler.setStackInSlot(1, pickaxe);

        itemHandler.extractItem(0, 1, false);
        itemHandler.setStackInSlot(2, recipe.value().getResultItem(level.registryAccess()).
                copyWithCount(itemHandler.getStackInSlot(2).getCount() +
                        recipe.value().getResultItem(level.registryAccess()).getCount()));

        resetProgress();
    }

    @Override
    protected boolean canCraftRecipe(SimpleContainer inventory, RecipeHolder<StonecutterRecipe> recipe) {
        return level != null &&
                recipe.value().matches(new SingleRecipeInput(inventory.getItem(0)), level) &&
                itemHandler.getStackInSlot(1).is(ItemTags.PICKAXES) &&
                InventoryUtils.canInsertItemIntoSlot(inventory, 2, recipe.value().getResultItem(level.registryAccess()));
    }
}