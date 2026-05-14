package me.jddev0.ep.block.entity.base;

import me.jddev0.ep.fluid.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public interface FluidStorageMethods<F extends Storage<FluidVariant>> {
    void saveFluidStorage(@NotNull F fluidStorage, @NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries);

    void loadFluidStorage(@NotNull F fluidStorage, @NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries);

    void syncFluidToPlayer(F fluidStorage, Player player, BlockPos pos);

    void syncFluidToPlayers(F fluidStorage, Level level, BlockPos pos, int distance);

    FluidStack getFluid(F fluidStorage, int tank);

    long getTankCapacity(F fluidStorage, int tank);

    void setFluid(F fluidStorage, int tank, FluidStack fluidStack);

    void setTankCapacity(F fluidStorage, int tank, long capacity);
}
