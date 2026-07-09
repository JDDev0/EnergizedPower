package me.jddev0.ep.block.entity.base;

import me.jddev0.ep.energy.EnergizedPowerLimitingEnergyStorage;
import me.jddev0.ep.energy.EnergyStoragePacketUpdate;
import me.jddev0.ep.energy.IEnergizedPowerEnergyStorage;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.EnergySyncS2CPacket;
import me.jddev0.ep.util.CapabilityUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.ArrayList;
import java.util.List;

public abstract class EnergyStorageBlockEntity<E extends IEnergizedPowerEnergyStorage>
        extends BlockEntity
        implements EnergyStoragePacketUpdate {
    protected final E energyStorage;
    public final EnergizedPowerLimitingEnergyStorage limitingEnergyStorage;

    protected final int baseEnergyCapacity;
    protected final int baseEnergyTransferRate;

    public EnergyStorageBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState,
                                    int baseEnergyCapacity, int baseEnergyTransferRate) {
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

        view.putInt("energy", energyStorage.getAmountAsInt());
    }

    @Override
    protected void loadAdditional(ValueInput view) {
        super.loadAdditional(view);

        energyStorage.setAmountWithoutUpdate(view.getIntOr("energy", 0));
    }

    protected final void syncEnergyToPlayer(Player player) {
        ModMessages.sendToPlayer(new EnergySyncS2CPacket(energyStorage.getAmountAsInt(), energyStorage.getCapacityAsInt(),
                getBlockPos()), (ServerPlayer)player);
    }

    protected final void syncEnergyToPlayers(int distance) {
        if(level != null && !level.isClientSide())
            ModMessages.sendToPlayersWithinXBlocks(
                    new EnergySyncS2CPacket(energyStorage.getAmountAsInt(), energyStorage.getCapacityAsInt(), getBlockPos()),
                    getBlockPos(), (ServerLevel)level, distance
            );
    }

    public int getEnergy() {
        return energyStorage.getAmountAsInt();
    }

    public int getCapacity() {
        return energyStorage.getCapacityAsInt();
    }

    @Override
    public void setEnergy(int energy) {
        energyStorage.setAmountWithoutUpdate(energy);
    }

    @Override
    public void setCapacity(int capacity) {
        energyStorage.setCapacityWithoutUpdate(capacity);
    }

    protected void pushEnergyToOutputs(Direction... outputDirections) {
        if(level == null || outputDirections.length == 0)
            return;

        List<EnergyHandler> consumers = new ArrayList<>();
        List<Integer> consumerEnergyValues = new ArrayList<>();
        int consumptionSum = 0;
        for(Direction direction:outputDirections) {
            BlockPos testPos = worldPosition.relative(direction);

            BlockEntity testBlockEntity = level.getBlockEntity(testPos);

            EnergyHandler consumer = level.getCapability(Capabilities.Energy.BLOCK, testPos,
                    level.getBlockState(testPos), testBlockEntity, direction.getOpposite());
            if(consumer == null || !CapabilityUtil.canInsert(consumer))
                continue;

            try(Transaction transaction = Transaction.open(null)) {
                int received = consumer.insert(Math.min(this.limitingEnergyStorage.getMaxExtract(),
                        this.energyStorage.getCapacityAsInt()), transaction);

                if(received <= 0)
                    continue;

                consumptionSum += received;
                consumers.add(consumer);
                consumerEnergyValues.add(received);
            }
        }

        List<Integer> consumerEnergyDistributed = new ArrayList<>();
        for(int i = 0;i < consumers.size();i++)
            consumerEnergyDistributed.add(0);

        int consumptionLeft = Math.min(this.limitingEnergyStorage.getMaxExtract(),
                Math.min(this.energyStorage.getAmountAsInt(), consumptionSum));
        try(Transaction transaction = Transaction.open(null)) {
            this.energyStorage.extract(consumptionLeft, transaction);
            transaction.commit();
        }

        int divisor = consumers.size();
        outer:
        while(consumptionLeft > 0) {
            int consumptionPerConsumer = consumptionLeft / divisor;
            if(consumptionPerConsumer == 0) {
                divisor = Math.max(1, divisor - 1);
                consumptionPerConsumer = consumptionLeft / divisor;
            }

            for(int i = 0;i < consumerEnergyValues.size();i++) {
                int consumptionDistributed = consumerEnergyDistributed.get(i);
                int consumptionOfConsumerLeft = consumerEnergyValues.get(i) - consumptionDistributed;

                int consumptionDistributedNew = Math.min(consumptionOfConsumerLeft, Math.min(consumptionPerConsumer, consumptionLeft));
                consumerEnergyDistributed.set(i, consumptionDistributed + consumptionDistributedNew);
                consumptionLeft -= consumptionDistributedNew;
                if(consumptionLeft == 0)
                    break outer;
            }
        }

        for(int i = 0;i < consumers.size();i++) {
            int energy = consumerEnergyDistributed.get(i);
            if(energy > 0) {
                try(Transaction transaction = Transaction.open(null)) {
                    consumers.get(i).insert(energy, transaction);
                    transaction.commit();
                }
            }
        }
    }
}
