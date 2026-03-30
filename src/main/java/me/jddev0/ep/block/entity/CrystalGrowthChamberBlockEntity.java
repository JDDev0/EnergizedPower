package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.SimpleRecipeMachineBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.recipe.ContainerRecipeInputWrapper;
import me.jddev0.ep.recipe.CrystalGrowthChamberRecipe;
import me.jddev0.ep.recipe.EPRecipes;
import me.jddev0.ep.screen.CrystalGrowthChamberMenu;
import me.jddev0.ep.util.InventoryUtils;
import me.jddev0.ep.util.RecipeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.state.BlockState;

public class CrystalGrowthChamberBlockEntity extends SimpleRecipeMachineBlockEntity<RecipeInput, CrystalGrowthChamberRecipe> {
    public static final float RECIPE_DURATION_MULTIPLIER = ModConfigs.COMMON_CRYSTAL_GROWTH_CHAMBER_RECIPE_DURATION_MULTIPLIER.getValue();

    final InputOutputItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0, i -> i == 1);

    public CrystalGrowthChamberBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.CRYSTAL_GROWTH_CHAMBER_ENTITY, blockPos, blockState,

                "crystal_growth_chamber", CrystalGrowthChamberMenu::new,

                2, EPRecipes.CRYSTAL_GROWTH_CHAMBER_TYPE, 1,

                ModConfigs.COMMON_CRYSTAL_GROWTH_CHAMBER_CAPACITY.getValue(),
                ModConfigs.COMMON_CRYSTAL_GROWTH_CHAMBER_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_CRYSTAL_GROWTH_CHAMBER_ENERGY_CONSUMPTION_PER_TICK.getValue(),

                UpgradeModuleModifier.SPEED,
                UpgradeModuleModifier.ENERGY_CONSUMPTION,
                UpgradeModuleModifier.ENERGY_CAPACITY,
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
                    case 1 -> false;
                    default -> super.canPlaceItem(slot, stack);
                };
            }

            @Override
            public void setItem(int slot, ItemStack stack) {
                if(slot == 0) {
                    ItemStack itemStack = getItem(slot);
                    if(level != null && !stack.isEmpty() && !itemStack.isEmpty() &&
                            !ItemStack.isSameItemSameComponents(stack, itemStack))
                        resetProgress();
                }

                super.setItem(slot, stack);
            }

            @Override
            public void setChanged() {
                super.setChanged();

                CrystalGrowthChamberBlockEntity.this.setChanged();
            }
        };
    }

    @Override
    protected double getRecipeDependentRecipeDuration(RecipeHolder<CrystalGrowthChamberRecipe> recipe) {
        return recipe.value().getTicks() * RECIPE_DURATION_MULTIPLIER;
    }

    @Override
    protected RecipeInput getRecipeInput(SimpleContainer inventory) {
        return new ContainerRecipeInputWrapper(inventory);
    }

    @Override
    protected void craftItem(RecipeHolder<CrystalGrowthChamberRecipe> recipe) {
        if(level == null || !hasRecipe())
            return;

        itemHandler.removeItem(0, recipe.value().getInput().count());

        ItemStack output = recipe.value().generateOutput(level.getRandom());

        if(!output.isEmpty())
            itemHandler.setItem(1, output.copyWithCount(
                    itemHandler.getItem(1).getCount() + output.getCount()));

        resetProgress();
    }

    @Override
    protected boolean canCraftRecipe(SimpleContainer inventory, RecipeHolder<CrystalGrowthChamberRecipe> recipe) {
        return level != null &&
                InventoryUtils.canInsertItemIntoSlot(inventory, 1, recipe.value().getMaxOutputCount());
    }
}