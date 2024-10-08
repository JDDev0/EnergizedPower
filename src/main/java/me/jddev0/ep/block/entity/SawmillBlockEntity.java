package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.SimpleRecipeMachineBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.recipe.EPRecipes;
import me.jddev0.ep.recipe.SawmillRecipe;
import me.jddev0.ep.screen.SawmillMenu;
import me.jddev0.ep.util.InventoryUtils;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.util.math.BlockPos;

public class SawmillBlockEntity extends SimpleRecipeMachineBlockEntity<SawmillRecipe> {
    final InputOutputItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0, i -> i == 1 || i == 2);

    public SawmillBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.SAWMILL_ENTITY, blockPos, blockState,

                "sawmill", SawmillMenu::new,

                3, EPRecipes.SAWMILL_TYPE, ModConfigs.COMMON_SAWMILL_RECIPE_DURATION.getValue(),

                ModConfigs.COMMON_SAWMILL_CAPACITY.getValue(),
                ModConfigs.COMMON_SAWMILL_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_SAWMILL_ENERGY_CONSUMPTION_PER_TICK.getValue(),

                UpgradeModuleModifier.SPEED,
                UpgradeModuleModifier.ENERGY_CONSUMPTION,
                UpgradeModuleModifier.ENERGY_CAPACITY
        );
    }

    @Override
    protected void craftItem(RecipeEntry<SawmillRecipe> recipe) {
        if(world == null || !hasRecipe())
            return;

        itemHandler.removeStack(0, 1);
        itemHandler.setStack(1, recipe.value().getResult(world.getRegistryManager()).
                copyWithCount(itemHandler.getStack(1).getCount() +
                        recipe.value().getResult(world.getRegistryManager()).getCount()));

        if(!recipe.value().getSecondaryOutput().isEmpty())
            itemHandler.setStack(2, recipe.value().getSecondaryOutput().
                    copyWithCount(itemHandler.getStack(2).getCount() +
                            recipe.value().getSecondaryOutput().getCount()));

        resetProgress();
    }

    @Override
    protected boolean canCraftRecipe(SimpleInventory inventory, RecipeEntry<SawmillRecipe> recipe) {
        return world != null &&
                InventoryUtils.canInsertItemIntoSlot(inventory, 1, recipe.value().getResult(world.getRegistryManager())) &&
                (recipe.value().getSecondaryOutput().isEmpty() ||
                        InventoryUtils.canInsertItemIntoSlot(inventory, 2, recipe.value().getSecondaryOutput()));
    }
}