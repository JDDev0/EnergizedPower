package me.jddev0.ep.block.entity.base;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

public interface FluidStorageMethods<F extends IFluidHandler> {
    void saveFluidStorage(@NotNull F fluidStorage, @NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries);

    void loadFluidStorage(@NotNull F fluidStorage, @NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries);

    void syncFluidToPlayer(F fluidStorage, Player player, BlockPos pos);

    void syncFluidToPlayers(F fluidStorage, Level level, BlockPos pos, int distance);

    FluidStack getFluid(F fluidStorage, int tank);

    int getTankCapacity(F fluidStorage, int tank);

    void setFluid(F fluidStorage, int tank, FluidStack fluidStack);

    void setTankCapacity(F fluidStorage, int tank, int capacity);
}
