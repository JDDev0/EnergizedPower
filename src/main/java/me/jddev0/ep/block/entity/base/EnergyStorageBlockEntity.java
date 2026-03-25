package me.jddev0.ep.block.entity.base;

import me.jddev0.ep.energy.EnergizedPowerLimitingEnergyStorage;
import me.jddev0.ep.energy.EnergyStoragePacketUpdate;
import me.jddev0.ep.energy.IEnergizedPowerEnergyStorage;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.EnergySyncS2CPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

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
    protected void saveAdditional(ValueOutput view) {
        super.saveAdditional(view);

        view.putLong("energy", energyStorage.getAmount());
    }

    @Override
    protected void loadAdditional(ValueInput view) {
        super.loadAdditional(view);

        energyStorage.setAmountWithoutUpdate(view.getLongOr("energy", 0));
    }

    protected final void syncEnergyToPlayer(Player player) {
        ModMessages.sendServerPacketToPlayer((ServerPlayer)player,
                new EnergySyncS2CPacket(energyStorage.getAmount(), energyStorage.getCapacity(), getBlockPos()));
    }

    protected final void syncEnergyToPlayers(int distance) {
        if(level != null && !level.isClientSide())
            ModMessages.sendServerPacketToPlayersWithinXBlocks(
                    getBlockPos(), (ServerLevel)level, distance,
                    new EnergySyncS2CPacket(energyStorage.getAmount(), energyStorage.getCapacity(), getBlockPos())
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
