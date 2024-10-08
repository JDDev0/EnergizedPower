package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.SimpleRecipeMachineBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.recipe.ContainerRecipeInputWrapper;
import me.jddev0.ep.recipe.EPRecipes;
import me.jddev0.ep.recipe.PulverizerRecipe;
import me.jddev0.ep.screen.PulverizerMenu;
import me.jddev0.ep.util.InventoryUtils;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.util.math.BlockPos;

public class PulverizerBlockEntity extends SimpleRecipeMachineBlockEntity<RecipeInput, PulverizerRecipe> {
    final InputOutputItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0, i -> i == 1 || i == 2);

    public PulverizerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.PULVERIZER_ENTITY, blockPos, blockState,

                "pulverizer", PulverizerMenu::new,

                3, EPRecipes.PULVERIZER_TYPE, ModConfigs.COMMON_PULVERIZER_RECIPE_DURATION.getValue(),

                ModConfigs.COMMON_PULVERIZER_CAPACITY.getValue(),
                ModConfigs.COMMON_PULVERIZER_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_PULVERIZER_ENERGY_CONSUMPTION_PER_TICK.getValue(),

                UpgradeModuleModifier.SPEED,
                UpgradeModuleModifier.ENERGY_CONSUMPTION,
                UpgradeModuleModifier.ENERGY_CAPACITY
        );
    }

    @Override
    protected RecipeInput getRecipeInput(SimpleInventory inventory) {
        return new ContainerRecipeInputWrapper(inventory);
    }

    @Override
    protected void craftItem(RecipeEntry<PulverizerRecipe> recipe) {
        if(world == null || !hasRecipe())
            return;

        ItemStack[] outputs = recipe.value().generateOutputs(world.random, false);

        itemHandler.removeStack(0, 1);
        itemHandler.setStack(1, outputs[0].
                copyWithCount(itemHandler.getStack(1).getCount() + outputs[0].getCount()));
        if(!outputs[1].isEmpty())
            itemHandler.setStack(2, outputs[1].
                    copyWithCount(itemHandler.getStack(2).getCount() + outputs[1].getCount()));

        resetProgress();
    }

    @Override
    protected boolean canCraftRecipe(SimpleInventory inventory, RecipeEntry<PulverizerRecipe> recipe) {
        ItemStack[] maxOutputs = recipe.value().getMaxOutputCounts(false);

        return world != null &&
                InventoryUtils.canInsertItemIntoSlot(inventory, 1, maxOutputs[0]) &&
                (maxOutputs[1].isEmpty() ||
                        InventoryUtils.canInsertItemIntoSlot(inventory, 2, maxOutputs[1]));
    }
}