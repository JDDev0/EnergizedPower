package me.jddev0.ep.block.entity.base;

import me.jddev0.ep.energy.EnergizedPowerLimitingEnergyStorage;
import me.jddev0.ep.energy.EnergyStoragePacketUpdate;
import me.jddev0.ep.energy.IEnergizedPowerEnergyStorage;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.EnergySyncS2CPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

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
    protected void saveAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);

        nbt.putLong("energy", energyStorage.getAmount());
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);

        energyStorage.setAmountWithoutUpdate(nbt.getLong("energy"));
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
