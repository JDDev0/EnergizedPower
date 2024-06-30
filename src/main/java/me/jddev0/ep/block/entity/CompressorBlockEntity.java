package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.SimpleRecipeMachineBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.recipe.CompressorRecipe;
import me.jddev0.ep.recipe.ModRecipes;
import me.jddev0.ep.screen.CompressorMenu;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class CompressorBlockEntity extends SimpleRecipeMachineBlockEntity<CompressorRecipe> {
    final InputOutputItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0, i -> i == 1);

    public CompressorBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                ModBlockEntities.COMPRESSOR_ENTITY, blockPos, blockState,

                "compressor", CompressorMenu::new,

                2, ModRecipes.COMPRESSOR_TYPE, ModConfigs.COMMON_COMPRESSOR_RECIPE_DURATION.getValue(),

                ModConfigs.COMMON_COMPRESSOR_CAPACITY.getValue(),
                ModConfigs.COMMON_COMPRESSOR_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_COMPRESSOR_ENERGY_CONSUMPTION_PER_TICK.getValue(),

                UpgradeModuleModifier.SPEED,
                UpgradeModuleModifier.ENERGY_CONSUMPTION,
                UpgradeModuleModifier.ENERGY_CAPACITY
        );
    }

    @Override
    protected void craftItem(CompressorRecipe recipe) {
        if(world == null || !hasRecipe())
            return;

        itemHandler.removeStack(0, recipe.getInputCount());
        itemHandler.setStack(1, recipe.getOutput(world.getRegistryManager()).
                copyWithCount(itemHandler.getStack(1).getCount() +
                        recipe.getOutput(world.getRegistryManager()).getCount()));

        resetProgress();
    }
}