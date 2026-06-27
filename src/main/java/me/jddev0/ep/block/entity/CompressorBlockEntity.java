package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.LegacySimpleRecipeMachineBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.inventory.LegacyInputOutputItemHandler;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.recipe.CompressorRecipe;
import me.jddev0.ep.recipe.ContainerRecipeInputWrapper;
import me.jddev0.ep.recipe.EPRecipes;
import me.jddev0.ep.screen.CompressorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.state.BlockState;

public class CompressorBlockEntity extends LegacySimpleRecipeMachineBlockEntity<RecipeInput, CompressorRecipe> {
    final LegacyInputOutputItemHandler itemHandlerSided = new LegacyInputOutputItemHandler(itemHandler, (i, stack) -> i == 0, i -> i == 1);

    public CompressorBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.COMPRESSOR_ENTITY, blockPos, blockState,

                "compressor", CompressorMenu::new,

                2, EPRecipes.COMPRESSOR_TYPE, ModConfigs.COMMON_COMPRESSOR_RECIPE_DURATION.getValue(),

                ModConfigs.COMMON_COMPRESSOR_CAPACITY.getValue(),
                ModConfigs.COMMON_COMPRESSOR_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_COMPRESSOR_ENERGY_CONSUMPTION_PER_TICK.getValue(),

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
    protected void craftItem(RecipeHolder<CompressorRecipe> recipe) {
        if(level == null || !hasRecipe())
            return;

        itemHandler.removeItem(0, recipe.value().getInputCount());
        itemHandler.setItem(1, recipe.value().getResultItem(level.registryAccess()).
                copyWithCount(itemHandler.getItem(1).getCount() +
                        recipe.value().getResultItem(level.registryAccess()).getCount()));

        resetProgress();
    }
}