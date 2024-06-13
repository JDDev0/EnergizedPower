package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.SimpleRecipeMachineBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.recipe.ContainerRecipeInputWrapper;
import me.jddev0.ep.recipe.CrusherRecipe;
import me.jddev0.ep.recipe.ModRecipes;
import me.jddev0.ep.screen.CrusherMenu;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.util.math.BlockPos;

public class CrusherBlockEntity extends SimpleRecipeMachineBlockEntity<RecipeInput, CrusherRecipe> {
    final InputOutputItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0, i -> i == 1);

    public CrusherBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                ModBlockEntities.CRUSHER_ENTITY, blockPos, blockState,

                "crusher", CrusherMenu::new,

                2, ModRecipes.CRUSHER_TYPE, ModConfigs.COMMON_CRUSHER_RECIPE_DURATION.getValue(),

                ModConfigs.COMMON_CRUSHER_CAPACITY.getValue(),
                ModConfigs.COMMON_CRUSHER_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_CRUSHER_ENERGY_CONSUMPTION_PER_TICK.getValue(),

                UpgradeModuleModifier.SPEED,
                UpgradeModuleModifier.ENERGY_CONSUMPTION,
                UpgradeModuleModifier.ENERGY_CAPACITY
        );
    }

    @Override
    protected RecipeInput getRecipeInput(SimpleInventory inventory) {
        return new ContainerRecipeInputWrapper(inventory);
    }
}