package me.jddev0.ep.block.entity.base;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import org.jetbrains.annotations.NotNull;

public interface FluidStorageMethods<F extends ResourceHandler<FluidResource>> {
    void saveFluidStorage(@NotNull F fluidStorage, ValueOutput view);

    void loadFluidStorage(@NotNull F fluidStorage, ValueInput view);

    void syncFluidToPlayer(F fluidStorage, Player player, BlockPos pos);

    void syncFluidToPlayers(F fluidStorage, Level level, BlockPos pos, int distance);

    FluidStack getFluid(F fluidStorage, int tank);

    int getTankCapacity(F fluidStorage, int tank);

    void setFluid(F fluidStorage, int tank, FluidStack fluidStack);

    void setTankCapacity(F fluidStorage, int tank, int capacity);
}
