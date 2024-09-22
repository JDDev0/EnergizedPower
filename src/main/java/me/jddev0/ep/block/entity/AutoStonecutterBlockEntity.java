package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.SelectableRecipeMachineBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.screen.AutoStonecutterMenu;
import me.jddev0.ep.util.InventoryUtils;
import me.jddev0.ep.util.ItemStackUtils;
import me.jddev0.ep.util.RecipeUtils;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.StonecuttingRecipe;
import net.minecraft.util.math.BlockPos;

public class AutoStonecutterBlockEntity
        extends SelectableRecipeMachineBlockEntity<StonecuttingRecipe> {
    final InputOutputItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0 || i == 1, i -> i == 2);

    public AutoStonecutterBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                ModBlockEntities.AUTO_STONECUTTER_ENTITY, blockPos, blockState,

                "auto_stonecutter", AutoStonecutterMenu::new,

                3,
                RecipeType.STONECUTTING,
                RecipeSerializer.STONECUTTING,
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
    protected SimpleInventory initInventoryStorage() {
        return new SimpleInventory(slotCount) {
            @Override
            public boolean isValid(int slot, ItemStack stack) {
                return switch(slot) {
                    case 0 -> world == null || RecipeUtils.isIngredientOfAny(world, recipeType, stack);
                    case 1 -> stack.isIn(ConventionalItemTags.PICKAXES);
                    case 2 -> false;
                    default -> super.isValid(slot, stack);
                };
            }

            @Override
            public void markDirty() {
                super.markDirty();
            }
        };
    }

    @Override
    protected void craftItem(StonecuttingRecipe recipe) {
        if(world == null || !hasRecipe())
            return;

        ItemStack pickaxe = itemHandler.getStack(1).copy();
        if(pickaxe.isEmpty() && !pickaxe.isIn(ConventionalItemTags.PICKAXES))
            return;

        if(pickaxe.damage(1, world.random, null))
            itemHandler.setStack(1, ItemStack.EMPTY);
        else
            itemHandler.setStack(1, pickaxe);

        itemHandler.removeStack(0, 1);
        itemHandler.setStack(2, ItemStackUtils.copyWithCount(recipe.getOutput(),
                itemHandler.getStack(2).getCount() +
                        recipe.getOutput().getCount()));

        resetProgress();
    }

    @Override
    protected boolean canCraftRecipe(SimpleInventory inventory, StonecuttingRecipe recipe) {
        return world != null &&
                recipe.matches(inventory, world) &&
                itemHandler.getStack(1).isIn(ConventionalItemTags.PICKAXES) &&
                InventoryUtils.canInsertItemIntoSlot(inventory, 2, recipe.getOutput());
    }
}