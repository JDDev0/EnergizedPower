package me.jddev0.ep.block.entity.base;

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

    protected final int baseEnergyCapacity;
    protected final int baseEnergyTransferRate;

    public EnergyStorageBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState,
                                    int baseEnergyCapacity, int baseEnergyTransferRate) {
        super(type, blockPos, blockState);

        this.baseEnergyCapacity = baseEnergyCapacity;
        this.baseEnergyTransferRate = baseEnergyTransferRate;

        energyStorage = initEnergyStorage();
    }

    protected abstract E initEnergyStorage();

    @Override
    protected void saveAdditional(ValueOutput view) {
        super.saveAdditional(view);

        view.putInt("energy", energyStorage.getEnergy());
    }

    @Override
    protected void loadAdditional(ValueInput view) {
        super.loadAdditional(view);

        energyStorage.setEnergyWithoutUpdate(view.getIntOr("energy", 0));
    }

    protected final void syncEnergyToPlayer(Player player) {
        ModMessages.sendToPlayer(new EnergySyncS2CPacket(energyStorage.getEnergy(), energyStorage.getCapacity(),
                getBlockPos()), (ServerPlayer)player);
    }

    protected final void syncEnergyToPlayers(int distance) {
        if(level != null && !level.isClientSide())
            ModMessages.sendToPlayersWithinXBlocks(
                    new EnergySyncS2CPacket(energyStorage.getEnergy(), energyStorage.getCapacity(), getBlockPos()),
                    getBlockPos(), (ServerLevel)level, distance
            );
    }

    public int getEnergy() {
        return energyStorage.getEnergy();
    }

    public int getCapacity() {
        return energyStorage.getCapacity();
    }

    @Override
    public void setEnergy(int energy) {
        energyStorage.setEnergyWithoutUpdate(energy);
    }

    @Override
    public void setCapacity(int capacity) {
        energyStorage.setCapacityWithoutUpdate(capacity);
    }
}
