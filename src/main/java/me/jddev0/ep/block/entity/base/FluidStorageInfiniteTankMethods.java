package me.jddev0.ep.block.entity.base;

import me.jddev0.ep.fluid.FluidStack;
import me.jddev0.ep.fluid.InfinityFluidStorage;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.FluidSyncS2CPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public final class FluidStorageInfiniteTankMethods implements FluidStorageMethods<InfinityFluidStorage> {
    public static final FluidStorageInfiniteTankMethods INSTANCE = new FluidStorageInfiniteTankMethods();

    private FluidStorageInfiniteTankMethods() {}

    @Override
    public void saveFluidStorage(@NotNull InfinityFluidStorage fluidStorage, WriteView view) {
        FluidStack fluid = fluidStorage.getFluid();
        view.get("fluid").putNullable("Fluid", InfinityFluidStorage.CODEC, fluid.isEmpty()?null:fluid);
    }

    @Override
    public void loadFluidStorage(@NotNull InfinityFluidStorage fluidStorage, ReadView view) {
        FluidStack fluid = view.getOptionalReadView("fluid").flatMap(subView -> subView.read("Fluid", InfinityFluidStorage.CODEC)).
                orElseGet(() -> new FluidStack(Fluids.EMPTY, 0));
        fluidStorage.setFluid(fluid);
    }

    @Override
    public void syncFluidToPlayer(InfinityFluidStorage fluidStorage, PlayerEntity player, BlockPos pos) {
        ModMessages.sendServerPacketToPlayer((ServerPlayerEntity)player, new FluidSyncS2CPacket(0, fluidStorage.getFluid(),
                fluidStorage.getCapacity(), pos));
    }

    @Override
    public void syncFluidToPlayers(InfinityFluidStorage fluidStorage, World level, BlockPos pos, int distance) {
        ModMessages.sendServerPacketToPlayersWithinXBlocks(
                pos, (ServerWorld)level, distance,
                new FluidSyncS2CPacket(0, fluidStorage.getFluid(), fluidStorage.getCapacity(), pos)
        );
    }

    @Override
    public FluidStack getFluid(InfinityFluidStorage fluidStorage, int tank) {
        return fluidStorage.getFluid();
    }

    @Override
    public long getTankCapacity(InfinityFluidStorage fluidStorage, int tank) {
        return fluidStorage.getCapacity();
    }

    @Override
    public void setFluid(InfinityFluidStorage fluidStorage, int tank, FluidStack fluidStack) {
        fluidStorage.setFluid(fluidStack);
    }

    @Override
    public void setTankCapacity(InfinityFluidStorage fluidStorage, int tank, long capacity) {}
}
