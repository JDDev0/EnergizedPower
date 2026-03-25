package me.jddev0.ep.block.entity.base;

import me.jddev0.ep.fluid.FluidStack;
import me.jddev0.ep.fluid.InfinityFluidStorage;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.FluidSyncS2CPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;

public final class FluidStorageInfiniteTankMethods implements FluidStorageMethods<InfinityFluidStorage> {
    public static final FluidStorageInfiniteTankMethods INSTANCE = new FluidStorageInfiniteTankMethods();

    private FluidStorageInfiniteTankMethods() {}

    @Override
    public void saveFluidStorage(@NotNull InfinityFluidStorage fluidStorage, ValueOutput view) {
        FluidStack fluid = fluidStorage.getFluid();
        view.child("fluid").storeNullable("Fluid", InfinityFluidStorage.CODEC, fluid.isEmpty()?null:fluid);
    }

    @Override
    public void loadFluidStorage(@NotNull InfinityFluidStorage fluidStorage, ValueInput view) {
        FluidStack fluid = view.child("fluid").flatMap(subView -> subView.read("Fluid", InfinityFluidStorage.CODEC)).
                orElseGet(() -> new FluidStack(Fluids.EMPTY, 0));
        fluidStorage.setFluid(fluid);
    }

    @Override
    public void syncFluidToPlayer(InfinityFluidStorage fluidStorage, Player player, BlockPos pos) {
        ModMessages.sendServerPacketToPlayer((ServerPlayer)player, new FluidSyncS2CPacket(0, fluidStorage.getFluid(),
                fluidStorage.getCapacity(), pos));
    }

    @Override
    public void syncFluidToPlayers(InfinityFluidStorage fluidStorage, Level level, BlockPos pos, int distance) {
        ModMessages.sendServerPacketToPlayersWithinXBlocks(
                pos, (ServerLevel)level, distance,
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
