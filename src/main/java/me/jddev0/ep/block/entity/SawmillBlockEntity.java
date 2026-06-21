package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.LegacySimpleRecipeMachineBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.inventory.LegacyInputOutputItemHandler;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.recipe.ContainerRecipeInputWrapper;
import me.jddev0.ep.recipe.EPRecipes;
import me.jddev0.ep.recipe.SawmillRecipe;
import me.jddev0.ep.screen.SawmillMenu;
import me.jddev0.ep.util.InventoryUtils;
import me.jddev0.ep.util.ItemStackUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.state.BlockState;

public class SawmillBlockEntity extends LegacySimpleRecipeMachineBlockEntity<RecipeInput, SawmillRecipe> {
    final LegacyInputOutputItemHandler itemHandlerSided = new LegacyInputOutputItemHandler(itemHandler, (i, stack) -> i == 0, i -> i == 1 || i == 2);

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
                UpgradeModuleModifier.ENERGY_CAPACITY,
                UpgradeModuleModifier.ITEM_EJECTOR,
                UpgradeModuleModifier.ITEM_PULLING
        );
    }

    @Override
    protected RecipeInput getRecipeInput(SimpleContainer inventory) {
        return new ContainerRecipeInputWrapper(inventory);
    }

    @Override
    protected void craftItem(RecipeHolder<SawmillRecipe> recipe) {
        if(level == null || !hasRecipe())
            return;

        itemHandler.removeItem(0, 1);
        itemHandler.setItem(1, recipe.value().assemble(null).
                copyWithCount(itemHandler.getItem(1).getCount() +
                        recipe.value().assemble(null).getCount()));

        if(recipe.value().getSecondaryOutput() != null)
            itemHandler.setItem(2, ItemStackUtils.fromNullableItemStackTemplate(recipe.value().getSecondaryOutput()).
                    copyWithCount(itemHandler.getItem(2).getCount() +
                            recipe.value().getSecondaryOutput().count()));

        resetProgress();
    }

    @Override
    protected boolean canCraftRecipe(SimpleContainer inventory, RecipeHolder<SawmillRecipe> recipe) {
        return level != null &&
                InventoryUtils.canInsertItemIntoSlot(inventory, 1, recipe.value().assemble(null)) &&
                (recipe.value().getSecondaryOutput() == null ||
                        InventoryUtils.canInsertItemIntoSlot(inventory, 2, ItemStackUtils.fromNullableItemStackTemplate(recipe.value().getSecondaryOutput())));
    }
}