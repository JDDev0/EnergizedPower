package me.jddev0.ep.block.entity.base;

import me.jddev0.ep.energy.EnergizedPowerLimitingEnergyStorage;
import me.jddev0.ep.energy.EnergyStoragePacketUpdate;
import me.jddev0.ep.energy.IEnergizedPowerEnergyStorage;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.EnergySyncS2CPacket;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import team.reborn.energy.api.EnergyStorage;

import java.util.ArrayList;
import java.util.List;

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
        ModMessages.sendToPlayer(new EnergySyncS2CPacket(energyStorage.getAmount(), energyStorage.getCapacity(), getBlockPos()), (ServerPlayer)player);
    }

    protected final void syncEnergyToPlayers(int distance) {
        if(level != null && !level.isClientSide())
            ModMessages.sendToPlayersWithinXBlocks(
                    new EnergySyncS2CPacket(energyStorage.getAmount(), energyStorage.getCapacity(), getBlockPos()), getBlockPos(), (ServerLevel)level, distance
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

    protected void pushEnergyToOutputs(Direction... outputDirections) {
        if(level == null || outputDirections.length == 0)
            return;

        List<EnergyStorage> consumers = new ArrayList<>();
        List<Long> consumerEnergyValues = new ArrayList<>();
        long consumptionSum = 0;
        for(Direction direction:outputDirections) {
            BlockPos testPos = worldPosition.relative(direction);

            BlockEntity testBlockEntity = level.getBlockEntity(testPos);
            if(testBlockEntity == null)
                continue;

            EnergyStorage consumer = EnergyStorage.SIDED.find(level, testPos, direction.getOpposite());
            if(consumer == null || !consumer.supportsInsertion())
                continue;

            try(Transaction transaction = Transaction.openOuter()) {
                long received = consumer.insert(Math.min(this.limitingEnergyStorage.getMaxExtract(),
                        this.energyStorage.getAmount()), transaction);

                if(received <= 0)
                    continue;

                consumptionSum += received;
                consumers.add(consumer);
                consumerEnergyValues.add(received);
            }
        }

        List<Long> consumerEnergyDistributed = new ArrayList<>();
        for(int i = 0;i < consumers.size();i++)
            consumerEnergyDistributed.add(0L);

        long consumptionLeft = Math.min(this.limitingEnergyStorage.getMaxExtract(),
                Math.min(this.energyStorage.getAmount(), consumptionSum));
        try(Transaction transaction = Transaction.openOuter()) {
            this.energyStorage.extract(consumptionLeft, transaction);
            transaction.commit();
        }

        int divisor = consumers.size();
        outer:
        while(consumptionLeft > 0) {
            long consumptionPerConsumer = consumptionLeft / divisor;
            if(consumptionPerConsumer == 0) {
                divisor = Math.max(1, divisor - 1);
                consumptionPerConsumer = consumptionLeft / divisor;
            }

            for(int i = 0;i < consumerEnergyValues.size();i++) {
                long consumptionDistributed = consumerEnergyDistributed.get(i);
                long consumptionOfConsumerLeft = consumerEnergyValues.get(i) - consumptionDistributed;

                long consumptionDistributedNew = Math.min(consumptionOfConsumerLeft, Math.min(consumptionPerConsumer, consumptionLeft));
                consumerEnergyDistributed.set(i, consumptionDistributed + consumptionDistributedNew);
                consumptionLeft -= consumptionDistributedNew;
                if(consumptionLeft == 0)
                    break outer;
            }
        }

        for(int i = 0;i < consumers.size();i++) {
            long energy = consumerEnergyDistributed.get(i);
            if(energy > 0) {
                try(Transaction transaction = Transaction.openOuter()) {
                    consumers.get(i).insert(energy, transaction);
                    transaction.commit();
                }
            }
        }
    }
}
