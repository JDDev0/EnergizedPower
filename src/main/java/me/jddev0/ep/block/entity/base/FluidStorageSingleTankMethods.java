package me.jddev0.ep.block.entity.base;

import me.jddev0.ep.fluid.FluidStack;
import me.jddev0.ep.fluid.SimpleFluidStorage;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.FluidSyncS2CPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public final class FluidStorageSingleTankMethods implements FluidStorageMethods<SimpleFluidStorage> {
    public static final FluidStorageSingleTankMethods INSTANCE = new FluidStorageSingleTankMethods();

    private FluidStorageSingleTankMethods() {}

    @Override
    public void saveFluidStorage(@NotNull SimpleFluidStorage fluidStorage, @NotNull NbtCompound nbt,
                                  @NotNull RegistryWrapper.WrapperLookup registries) {
        nbt.put("fluid", fluidStorage.toNBT(new NbtCompound(), registries));
    }

    @Override
    public void loadFluidStorage(@NotNull SimpleFluidStorage fluidStorage, @NotNull NbtCompound nbt,
                                  @NotNull RegistryWrapper.WrapperLookup registries) {
        fluidStorage.fromNBT(nbt.getCompoundOrEmpty("fluid"), registries);
    }

    @Override
    public void syncFluidToPlayer(SimpleFluidStorage fluidStorage, PlayerEntity player, BlockPos pos) {
        ModMessages.sendServerPacketToPlayer((ServerPlayerEntity)player, new FluidSyncS2CPacket(0, fluidStorage.getFluid(),
                fluidStorage.getCapacity(), pos));
    }

    @Override
    public void syncFluidToPlayers(SimpleFluidStorage fluidStorage, World level, BlockPos pos, int distance) {
        ModMessages.sendServerPacketToPlayersWithinXBlocks(
                pos, (ServerWorld)level, distance,
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
