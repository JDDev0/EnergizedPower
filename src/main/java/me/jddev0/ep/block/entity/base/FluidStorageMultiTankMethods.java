package me.jddev0.ep.block.entity.base;

import me.jddev0.ep.fluid.EnergizedPowerFluidStorage;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.FluidSyncS2CPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public final class FluidStorageMultiTankMethods implements FluidStorageMethods<EnergizedPowerFluidStorage> {
    public static final FluidStorageMultiTankMethods INSTANCE = new FluidStorageMultiTankMethods();

    private FluidStorageMultiTankMethods() {}

    @Override
    public void saveFluidStorage(@NotNull EnergizedPowerFluidStorage fluidStorage, ValueOutput view) {
        for(int i = 0;i < fluidStorage.size();i++) {
            FluidStack fluid = fluidStorage.getFluid(i);
            view.storeNullable("fluid." + i, FluidStack.CODEC, fluid.isEmpty()?null:fluid);
        }
    }

    @Override
    public void loadFluidStorage(@NotNull EnergizedPowerFluidStorage fluidStorage, ValueInput view) {
        for(int i = 0;i < fluidStorage.size();i++) {
            FluidStack fluid = view.read("fluid." + i, FluidStack.CODEC).
                    orElse(FluidStack.EMPTY);
            fluidStorage.setFluid(i, fluid);
        }
    }

    @Override
    public void syncFluidToPlayer(EnergizedPowerFluidStorage fluidStorage, Player player, BlockPos pos) {
        for(int i = 0;i < fluidStorage.size();i++)
            ModMessages.sendToPlayer(new FluidSyncS2CPacket(i, fluidStorage.getFluid(i),
                    fluidStorage.getCapacity(i), pos), (ServerPlayer)player);
    }

    @Override
    public void syncFluidToPlayers(EnergizedPowerFluidStorage fluidStorage, Level level, BlockPos pos, int distance) {
        for(int i = 0;i < fluidStorage.size();i++)
            ModMessages.sendToPlayersWithinXBlocks(
                    new FluidSyncS2CPacket(i, fluidStorage.getFluid(i), fluidStorage.getCapacity(i), pos),
                    pos, (ServerLevel)level, distance
            );
    }

    @Override
    public FluidStack getFluid(EnergizedPowerFluidStorage fluidStorage, int tank) {
        return fluidStorage.getFluid(tank);
    }

    @Override
    public int getTankCapacity(EnergizedPowerFluidStorage fluidStorage, int tank) {
        return fluidStorage.getCapacity(tank);
    }

    @Override
    public void setFluid(EnergizedPowerFluidStorage fluidStorage, int tank, FluidStack fluidStack) {
        fluidStorage.setFluid(tank, fluidStack);
    }

    @Override
    public void setTankCapacity(EnergizedPowerFluidStorage fluidStorage, int tank, int capacity) {
        //Does nothing (capacity is final)
    }
}
