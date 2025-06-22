package me.jddev0.ep.block.entity.base;

import me.jddev0.ep.fluid.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public interface FluidStorageMethods<F extends Storage<FluidVariant>> {
    void saveFluidStorage(@NotNull F fluidStorage, WriteView view);

    void loadFluidStorage(@NotNull F fluidStorage, ReadView view);

    void syncFluidToPlayer(F fluidStorage, PlayerEntity player, BlockPos pos);

    void syncFluidToPlayers(F fluidStorage, World level, BlockPos pos, int distance);

    FluidStack getFluid(F fluidStorage, int tank);

    long getTankCapacity(F fluidStorage, int tank);

    void setFluid(F fluidStorage, int tank, FluidStack fluidStack);

    void setTankCapacity(F fluidStorage, int tank, long capacity);
}
