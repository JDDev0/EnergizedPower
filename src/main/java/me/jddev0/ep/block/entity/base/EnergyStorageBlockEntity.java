package me.jddev0.ep.block.entity.base;

import me.jddev0.ep.energy.EnergizedPowerLimitingEnergyStorage;
import me.jddev0.ep.energy.EnergyStoragePacketUpdate;
import me.jddev0.ep.energy.IEnergizedPowerEnergyStorage;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.EnergySyncS2CPacket;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;

public abstract class EnergyStorageBlockEntity<E extends IEnergizedPowerEnergyStorage>
        extends BlockEntity
        implements EnergyStoragePacketUpdate {
    protected final E energyStorage;
    public final EnergizedPowerLimitingEnergyStorage limitingEnergyStorage;

    protected final long baseEnergyCapacity;
    protected final long baseEnergyTransferRate;

    public EnergyStorageBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState,
                                    long baseEnergyCapacity, long baseEnergyTransferRate) {
        super(type, blockPos, blockState);

        this.baseEnergyCapacity = baseEnergyCapacity;
        this.baseEnergyTransferRate = baseEnergyTransferRate;

        energyStorage = initEnergyStorage();
        limitingEnergyStorage = initLimitingEnergyStorage();
    }

    protected abstract E initEnergyStorage();

    protected abstract EnergizedPowerLimitingEnergyStorage initLimitingEnergyStorage();

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);

        view.putLong("energy", energyStorage.getAmount());
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);

        energyStorage.setAmountWithoutUpdate(view.getLong("energy", 0));
    }

    protected final void syncEnergyToPlayer(PlayerEntity player) {
        ModMessages.sendServerPacketToPlayer((ServerPlayerEntity)player,
                new EnergySyncS2CPacket(energyStorage.getAmount(), energyStorage.getCapacity(), getPos()));
    }

    protected final void syncEnergyToPlayers(int distance) {
        if(world != null && !world.isClient())
            ModMessages.sendServerPacketToPlayersWithinXBlocks(
                    getPos(), (ServerWorld)world, distance,
                    new EnergySyncS2CPacket(energyStorage.getAmount(), energyStorage.getCapacity(), getPos())
            );
    }

    public long getEnergy() {
        return energyStorage.getAmount();
    }

    public long getCapacity() {
        return energyStorage.getCapacity();
    }

    @Override
    public void setEnergy(long energy) {
        energyStorage.setAmountWithoutUpdate(energy);
    }

    @Override
    public void setCapacity(long capacity) {
        energyStorage.setCapacityWithoutUpdate(capacity);
    }
}
