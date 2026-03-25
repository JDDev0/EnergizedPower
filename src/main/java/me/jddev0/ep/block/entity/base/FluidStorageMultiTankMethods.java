package me.jddev0.ep.block.entity.base;

import me.jddev0.ep.fluid.FluidStack;
import me.jddev0.ep.fluid.SimpleFluidStorage;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.FluidSyncS2CPacket;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;

public final class FluidStorageMultiTankMethods
        implements FluidStorageMethods<CombinedStorage<FluidVariant, SimpleFluidStorage>> {
    public static final FluidStorageMultiTankMethods INSTANCE = new FluidStorageMultiTankMethods();

    private FluidStorageMultiTankMethods() {}

    @Override
    public void saveFluidStorage(@NotNull CombinedStorage<FluidVariant, SimpleFluidStorage> fluidStorage, ValueOutput view) {
        for(int i = 0;i < fluidStorage.parts.size();i++) {
            FluidStack fluid = fluidStorage.parts.get(i).getFluid();
            view.storeNullable("fluid." + i, SimpleFluidStorage.CODEC, fluid.isEmpty()?null:fluid);
        }
    }

    @Override
    public void loadFluidStorage(@NotNull CombinedStorage<FluidVariant, SimpleFluidStorage> fluidStorage, ValueInput view) {
        for(int i = 0;i < fluidStorage.parts.size();i++) {
            FluidStack fluid = view.read("fluid." + i, SimpleFluidStorage.CODEC).
                    orElseGet(() -> new FluidStack(Fluids.EMPTY, 0));
            fluidStorage.parts.get(i).setFluid(fluid);
        }
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
