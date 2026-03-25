package me.jddev0.ep.block.entity.base;

import me.jddev0.ep.fluid.FluidStack;
import me.jddev0.ep.fluid.SimpleFluidStorage;
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

public final class FluidStorageSingleTankMethods implements FluidStorageMethods<SimpleFluidStorage> {
    public static final FluidStorageSingleTankMethods INSTANCE = new FluidStorageSingleTankMethods();

    private FluidStorageSingleTankMethods() {}

    @Override
    public void saveFluidStorage(@NotNull SimpleFluidStorage fluidStorage, ValueOutput view) {
        FluidStack fluid = fluidStorage.getFluid();
        view.child("fluid").storeNullable("Fluid", SimpleFluidStorage.CODEC, fluid.isEmpty()?null:fluid);
    }

    @Override
    public void loadFluidStorage(@NotNull SimpleFluidStorage fluidStorage, ValueInput view) {
        FluidStack fluid = view.child("fluid").flatMap(subView -> subView.read("Fluid", SimpleFluidStorage.CODEC)).
                orElseGet(() -> new FluidStack(Fluids.EMPTY, 0));
        fluidStorage.setFluid(fluid);
    }

    @Override
    public void syncFluidToPlayer(SimpleFluidStorage fluidStorage, Player player, BlockPos pos) {
        ModMessages.sendServerPacketToPlayer((ServerPlayer)player, new FluidSyncS2CPacket(0, fluidStorage.getFluid(),
                fluidStorage.getCapacity(), pos));
    }

    @Override
    public void syncFluidToPlayers(SimpleFluidStorage fluidStorage, Level level, BlockPos pos, int distance) {
        ModMessages.sendServerPacketToPlayersWithinXBlocks(
                pos, (ServerLevel)level, distance,
                new FluidSyncS2CPacket(0, fluidStorage.getFluid(), fluidStorage.getCapacity(), pos)
        );
    }

    @Override
    public FluidStack getFluid(SimpleFluidStorage fluidStorage, int tank) {
        return fluidStorage.getFluid();
    }

    @Override
    public long getTankCapacity(SimpleFluidStorage fluidStorage, int tank) {
        return fluidStorage.getCapacity();
    }

    @Override
    public void setFluid(SimpleFluidStorage fluidStorage, int tank, FluidStack fluidStack) {
        fluidStorage.setFluid(fluidStack);
    }

    @Override
    public void setTankCapacity(SimpleFluidStorage fluidStorage, int tank, long capacity) {
        //Does nothing (capacity is final)
    }
}
