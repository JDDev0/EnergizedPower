package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.SimpleRecipeMachineBlockEntity;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.recipe.CompressorRecipe;
import me.jddev0.ep.recipe.ContainerRecipeInputWrapper;
import me.jddev0.ep.recipe.ModRecipes;
import me.jddev0.ep.screen.CompressorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

public class CompressorBlockEntity extends SimpleRecipeMachineBlockEntity<RecipeInput, CompressorRecipe> {
    private final IItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0, i -> i == 1);

    public CompressorBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                ModBlockEntities.COMPRESSOR_ENTITY.get(), blockPos, blockState,

                "compressor", CompressorMenu::new,

                2, ModRecipes.COMPRESSOR_TYPE.get(), ModConfigs.COMMON_COMPRESSOR_RECIPE_DURATION.getValue(),

                ModConfigs.COMMON_COMPRESSOR_CAPACITY.getValue(),
                ModConfigs.COMMON_COMPRESSOR_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_COMPRESSOR_ENERGY_CONSUMPTION_PER_TICK.getValue(),

                UpgradeModuleModifier.SPEED,
                UpgradeModuleModifier.ENERGY_CONSUMPTION,
                UpgradeModuleModifier.ENERGY_CAPACITY
        );
    }

    public @Nullable IItemHandler getItemHandlerCapability(@Nullable Direction side) {
        if(side == null)
            return itemHandler;

        return itemHandlerSided;
    }

    public @Nullable IEnergyStorage getEnergyStorageCapability(@Nullable Direction side) {
        return energyStorage;
    }

    @Override
    protected RecipeInput getRecipeInput(Container inventory) {
        return new ContainerRecipeInputWrapper(inventory);
    }
}