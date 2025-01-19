package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.SelectableRecipeMachineBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.screen.AutoStonecutterMenu;
import me.jddev0.ep.util.InventoryUtils;
import me.jddev0.ep.util.RecipeUtils;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.StonecuttingRecipe;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class AutoStonecutterBlockEntity
        extends SelectableRecipeMachineBlockEntity<SingleStackRecipeInput, StonecuttingRecipe> {
    final InputOutputItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0 || i == 1, i -> i == 2);

    public AutoStonecutterBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.AUTO_STONECUTTER_ENTITY, blockPos, blockState,

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
                    case 0 -> ((world instanceof ServerWorld serverWorld)?
                            RecipeUtils.isIngredientOfAny(serverWorld, recipeType, stack):
                            RecipeUtils.isIngredientOfAny(ingredientsOfRecipes, stack));
                    case 1 -> stack.isIn(ItemTags.PICKAXES);
                    case 2 -> false;
                    default -> super.isValid(slot, stack);
                };
            }

            @Override
            public void markDirty() {
                super.markDirty();

                AutoStonecutterBlockEntity.this.markDirty();
            }
        };
    }

    @Override
    protected void craftItem(RecipeEntry<StonecuttingRecipe> recipe) {
        if(world == null || !hasRecipe() || !(world instanceof ServerWorld serverWorld))
            return;

        ItemStack pickaxe = itemHandler.getStack(1).copy();
        if(pickaxe.isEmpty() && !pickaxe.isIn(ItemTags.PICKAXES))
            return;

        pickaxe.damage(1, serverWorld, null, item -> pickaxe.setCount(0));
        itemHandler.setStack(1, pickaxe);

        itemHandler.removeStack(0, 1);
        itemHandler.setStack(2, recipe.value().craft(null, world.getRegistryManager()).
                copyWithCount(itemHandler.getStack(2).getCount() +
                        recipe.value().craft(null, world.getRegistryManager()).getCount()));

        resetProgress();
    }

    @Override
    protected boolean canCraftRecipe(SimpleInventory inventory, RecipeEntry<StonecuttingRecipe> recipe) {
        return world != null &&
                recipe.value().matches(new SingleStackRecipeInput(inventory.getStack(0)), world) &&
                itemHandler.getStack(1).isIn(ItemTags.PICKAXES) &&
                InventoryUtils.canInsertItemIntoSlot(inventory, 2, recipe.value().craft(null, world.getRegistryManager()));
    }
}