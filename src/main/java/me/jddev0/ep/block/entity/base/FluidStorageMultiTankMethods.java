package me.jddev0.ep.block.entity.base;

import me.jddev0.ep.fluid.EnergizedPowerFluidStorage;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.FluidSyncS2CPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public final class FluidStorageMultiTankMethods implements FluidStorageMethods<EnergizedPowerFluidStorage> {
    public static final FluidStorageMultiTankMethods INSTANCE = new FluidStorageMultiTankMethods();

    private FluidStorageMultiTankMethods() {}

    @Override
    public void saveFluidStorage(@NotNull EnergizedPowerFluidStorage fluidStorage, @NotNull CompoundTag nbt) {
        for(int i = 0;i < fluidStorage.getTanks();i++)
            fluidStorage.setFluid(i, FluidStack.loadFluidStackFromNBT(nbt.getCompound("fluid." + i)));
    }

    @Override
    public void loadFluidStorage(@NotNull EnergizedPowerFluidStorage fluidStorage, @NotNull CompoundTag nbt) {
        for(int i = 0;i < fluidStorage.getTanks();i++)
            nbt.put("fluid." + i, fluidStorage.getFluid(i).writeToNBT(new CompoundTag()));
    }

    @Override
    public void syncFluidToPlayer(EnergizedPowerFluidStorage fluidStorage, Player player, BlockPos pos) {
        for(int i = 0;i < fluidStorage.getTanks();i++)
            ModMessages.sendToPlayer(new FluidSyncS2CPacket(i, fluidStorage.getFluidInTank(i),
                    fluidStorage.getTankCapacity(i), pos), (ServerPlayer)player);
    }

    @Override
    public void syncFluidToPlayers(EnergizedPowerFluidStorage fluidStorage, Level level, BlockPos pos, int distance) {
        for(int i = 0;i < fluidStorage.getTanks();i++)
            ModMessages.sendToPlayersWithinXBlocks(
                    new FluidSyncS2CPacket(i, fluidStorage.getFluidInTank(i), fluidStorage.getTankCapacity(i), pos),
                    pos, level.dimension(), distance
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
        fluidStorage.setCapacity(tank, capacity);
    }
}
