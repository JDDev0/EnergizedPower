package me.jddev0.ep.block.entity.base;

import me.jddev0.ep.fluid.FluidStack;
import me.jddev0.ep.fluid.SimpleFluidStorage;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.FluidSyncS2CPacket;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public final class FluidStorageMultiTankMethods
        implements FluidStorageMethods<CombinedStorage<FluidVariant, SimpleFluidStorage>> {
    public static final FluidStorageMultiTankMethods INSTANCE = new FluidStorageMultiTankMethods();

    private FluidStorageMultiTankMethods() {}

    @Override
    public void saveFluidStorage(@NotNull CombinedStorage<FluidVariant, SimpleFluidStorage> fluidStorage,
                                 @NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        for(int i = 0;i < fluidStorage.parts.size();i++)
            nbt.put("fluid." + i, fluidStorage.parts.get(i).getFluid().toNBT(new CompoundTag(), registries));
    }

    @Override
    public void loadFluidStorage(@NotNull CombinedStorage<FluidVariant, SimpleFluidStorage> fluidStorage,
                                 @NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        for(int i = 0;i < fluidStorage.parts.size();i++)
            fluidStorage.parts.get(i).setFluid(FluidStack.fromNbt(nbt.getCompound("fluid." + i), registries));
    }

    @Override
    public void syncFluidToPlayer(CombinedStorage<FluidVariant, SimpleFluidStorage> fluidStorage,
                                  Player player, BlockPos pos) {
        for(int i = 0;i < fluidStorage.parts.size();i++)
            ModMessages.sendServerPacketToPlayer((ServerPlayer)player, new FluidSyncS2CPacket(i,
                    fluidStorage.parts.get(i).getFluid(), fluidStorage.parts.get(i).getCapacity(), pos));
    }

    @Override
    public void syncFluidToPlayers(CombinedStorage<FluidVariant, SimpleFluidStorage> fluidStorage,
                                   Level level, BlockPos pos, int distance) {
        for(int i = 0;i < fluidStorage.parts.size();i++)
            ModMessages.sendServerPacketToPlayersWithinXBlocks(
                    pos, (ServerLevel)level, distance,
                    new FluidSyncS2CPacket(i, fluidStorage.parts.get(i).getFluid(),
                            fluidStorage.parts.get(i).getCapacity(), pos)
            );
    }

    @Override
    public FluidStack getFluid(CombinedStorage<FluidVariant, SimpleFluidStorage> fluidStorage, int tank) {
        return fluidStorage.parts.get(tank).getFluid();
    }

    @Override
    public long getTankCapacity(CombinedStorage<FluidVariant, SimpleFluidStorage> fluidStorage, int tank) {
        return fluidStorage.parts.get(tank).getCapacity();
    }

    @Override
    public void setFluid(CombinedStorage<FluidVariant, SimpleFluidStorage> fluidStorage, int tank, FluidStack fluidStack) {
        fluidStorage.parts.get(tank).setFluid(fluidStack);
    }

    @Override
    public void setTankCapacity(CombinedStorage<FluidVariant, SimpleFluidStorage> fluidStorage, int tank, long capacity) {
        //Does nothing (capacity is final)
    }
}
