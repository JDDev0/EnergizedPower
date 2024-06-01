package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.SimpleRecipeMachineBlockEntity;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.recipe.CompressorRecipe;
import me.jddev0.ep.recipe.ModRecipes;
import me.jddev0.ep.screen.CompressorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CompressorBlockEntity extends SimpleRecipeMachineBlockEntity<CompressorRecipe> {
    private final IItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0, i -> i == 1);

    private final LazyOptional<IItemHandler> lazyItemHandlerSided = LazyOptional.of(
            () -> new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0, i -> i == 1));

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

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.ITEM_HANDLER) {
            if(side == null)
                return lazyItemHandler.cast();

            return lazyItemHandlerSided.cast();
        }else if(cap == ForgeCapabilities.ENERGY) {
            return lazyEnergyStorage.cast();
        }

        return super.getCapability(cap, side);
    }
}