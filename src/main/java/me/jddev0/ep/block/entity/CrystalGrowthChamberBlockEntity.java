package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.SimpleRecipeMachineBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.recipe.ContainerRecipeInputWrapper;
import me.jddev0.ep.recipe.CrystalGrowthChamberRecipe;
import me.jddev0.ep.recipe.ModRecipes;
import me.jddev0.ep.screen.CrystalGrowthChamberMenu;
import me.jddev0.ep.util.InventoryUtils;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.util.math.BlockPos;

public class CrystalGrowthChamberBlockEntity extends SimpleRecipeMachineBlockEntity<RecipeInput, CrystalGrowthChamberRecipe> {
    public static final float RECIPE_DURATION_MULTIPLIER = ModConfigs.COMMON_CRYSTAL_GROWTH_CHAMBER_RECIPE_DURATION_MULTIPLIER.getValue();

    final InputOutputItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0, i -> i == 1);

    public CrystalGrowthChamberBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                ModBlockEntities.CRYSTAL_GROWTH_CHAMBER_ENTITY, blockPos, blockState,

                "crystal_growth_chamber", CrystalGrowthChamberMenu::new,

                2, ModRecipes.CRYSTAL_GROWTH_CHAMBER_TYPE, 1,

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
    protected SimpleInventory initInventoryStorage() {
        return new SimpleInventory(slotCount) {
            @Override
            public boolean isValid(int slot, ItemStack stack) {
                return switch(slot) {
                    case 0 -> world == null || world.getRecipeManager().
                            listAllOfType(CrystalGrowthChamberRecipe.Type.INSTANCE).stream().
                            map(RecipeEntry::value).map(CrystalGrowthChamberRecipe::getInput).
                            anyMatch(ingredient -> ingredient.test(stack));
                    case 1 -> false;
                    default -> super.isValid(slot, stack);
                };
            }

            @Override
            public void setStack(int slot, ItemStack stack) {
                if(slot == 0) {
                    ItemStack itemStack = getStack(slot);
                    if(world != null && !stack.isEmpty() && !itemStack.isEmpty() &&
                            !ItemStack.areItemsAndComponentsEqual(stack, itemStack))
                        resetProgress();
                }

                super.setStack(slot, stack);
            }

            @Override
            public void markDirty() {
                super.markDirty();

                CrystalGrowthChamberBlockEntity.this.markDirty();
            }
        };
    }

    @Override
    protected double getRecipeDependentRecipeDuration(RecipeEntry<CrystalGrowthChamberRecipe> recipe) {
        return recipe.value().getTicks() * RECIPE_DURATION_MULTIPLIER;
    }

    @Override
    protected RecipeInput getRecipeInput(SimpleInventory inventory) {
        return new ContainerRecipeInputWrapper(inventory);
    }

    @Override
    protected void craftItem(RecipeEntry<CrystalGrowthChamberRecipe> recipe) {
        if(world == null || !hasRecipe())
            return;

        itemHandler.removeStack(0, recipe.value().getInputCount());

        ItemStack output = recipe.value().generateOutput(world.random);

        if(!output.isEmpty())
            itemHandler.setStack(1, output.copyWithCount(
                    itemHandler.getStack(1).getCount() + output.getCount()));

        resetProgress();
    }

    @Override
    protected boolean canCraftRecipe(SimpleInventory inventory, RecipeEntry<CrystalGrowthChamberRecipe> recipe) {
        return world != null &&
                InventoryUtils.canInsertItemIntoSlot(inventory, 1, recipe.value().getMaxOutputCount());
    }
}