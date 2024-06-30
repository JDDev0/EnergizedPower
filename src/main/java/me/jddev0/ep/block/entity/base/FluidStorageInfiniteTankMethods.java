package me.jddev0.ep.block.entity.base;

import me.jddev0.ep.fluid.InfinityFluidStorage;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.FluidSyncS2CPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public final class FluidStorageInfiniteTankMethods implements FluidStorageMethods<InfinityFluidStorage> {
    public static final FluidStorageInfiniteTankMethods INSTANCE = new FluidStorageInfiniteTankMethods();

    private FluidStorageInfiniteTankMethods() {}

    @Override
    public void saveFluidStorage(@NotNull InfinityFluidStorage fluidStorage, @NotNull CompoundTag nbt) {
        nbt.put("fluid", fluidStorage.writeToNBT(new CompoundTag()));
    }

    @Override
    public void loadFluidStorage(@NotNull InfinityFluidStorage fluidStorage, @NotNull CompoundTag nbt) {
        fluidStorage.readFromNBT(nbt.getCompound("fluid"));
    }

    @Override
    public void syncFluidToPlayer(InfinityFluidStorage fluidStorage, Player player, BlockPos pos) {
        ModMessages.sendToPlayer(new FluidSyncS2CPacket(0, fluidStorage.getFluid(), fluidStorage.getCapacity(),
                pos), (ServerPlayer)player);
    }

    @Override
    public void syncFluidToPlayers(InfinityFluidStorage fluidStorage, Level level, BlockPos pos, int distance) {
        ModMessages.sendToPlayersWithinXBlocks(
                new FluidSyncS2CPacket(0, fluidStorage.getFluid(), fluidStorage.getCapacity(), pos),
                pos, level.dimension(), distance
        );
    }

    @Override
    public FluidStack getFluid(InfinityFluidStorage fluidStorage, int tank) {
        return fluidStorage.getFluid();
    }

    @Override
    public int getTankCapacity(InfinityFluidStorage fluidStorage, int tank) {
        return fluidStorage.getCapacity();
    }

    @Override
    public void setFluid(InfinityFluidStorage fluidStorage, int tank, FluidStack fluidStack) {
        fluidStorage.setFluid(fluidStack);
    }

    @Override
    public void setTankCapacity(InfinityFluidStorage fluidStorage, int tank, int capacity) {}
}
