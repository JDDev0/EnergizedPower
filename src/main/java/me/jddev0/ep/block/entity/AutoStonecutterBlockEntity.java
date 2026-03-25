package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.SelectableRecipeMachineBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.screen.AutoStonecutterMenu;
import me.jddev0.ep.util.InventoryUtils;
import me.jddev0.ep.util.RecipeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraft.world.level.block.state.BlockState;

public class AutoStonecutterBlockEntity
        extends SelectableRecipeMachineBlockEntity<SingleRecipeInput, StonecutterRecipe> {
    final InputOutputItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0 || i == 1, i -> i == 2);

    public AutoStonecutterBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.AUTO_STONECUTTER_ENTITY, blockPos, blockState,

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
    protected SimpleContainer initInventoryStorage() {
        return new SimpleContainer(slotCount) {
            @Override
            public boolean canPlaceItem(int slot, ItemStack stack) {
                return switch(slot) {
                    case 0 -> ((level instanceof ServerLevel serverWorld)?
                            RecipeUtils.isIngredientOfAny(serverWorld, recipeType, stack):
                            RecipeUtils.isIngredientOfAny(ingredientsOfRecipes, stack));
                    case 1 -> stack.is(ItemTags.PICKAXES);
                    case 2 -> false;
                    default -> super.canPlaceItem(slot, stack);
                };
            }

            @Override
            public void setChanged() {
                super.setChanged();

                AutoStonecutterBlockEntity.this.setChanged();
            }
        };
    }

    @Override
    protected void craftItem(RecipeHolder<StonecutterRecipe> recipe) {
        if(level == null || !hasRecipe() || !(level instanceof ServerLevel serverWorld))
            return;

        ItemStack pickaxe = itemHandler.getItem(1).copy();
        if(pickaxe.isEmpty() && !pickaxe.is(ItemTags.PICKAXES))
            return;

        pickaxe.hurtAndBreak(1, serverWorld, null, item -> pickaxe.setCount(0));
        itemHandler.setItem(1, pickaxe);

        itemHandler.removeItem(0, 1);
        itemHandler.setItem(2, recipe.value().assemble(null, level.registryAccess()).
                copyWithCount(itemHandler.getItem(2).getCount() +
                        recipe.value().assemble(null, level.registryAccess()).getCount()));

        resetProgress();
    }

    @Override
    protected boolean canCraftRecipe(SimpleContainer inventory, RecipeHolder<StonecutterRecipe> recipe) {
        return level != null &&
                recipe.value().matches(new SingleRecipeInput(inventory.getItem(0)), level) &&
                itemHandler.getItem(1).is(ItemTags.PICKAXES) &&
                InventoryUtils.canInsertItemIntoSlot(inventory, 2, recipe.value().assemble(null, level.registryAccess()));
    }
}