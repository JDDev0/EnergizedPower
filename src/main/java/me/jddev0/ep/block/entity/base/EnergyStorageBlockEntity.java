package me.jddev0.ep.block.entity.base;

import me.jddev0.ep.energy.EnergizedPowerLimitingEnergyStorage;
import me.jddev0.ep.energy.EnergyStoragePacketUpdate;
import me.jddev0.ep.energy.IEnergizedPowerEnergyStorage;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.EnergySyncS2CPacket;
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
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;

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
    protected void saveAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);

        nbt.putInt("energy", energyStorage.getEnergy());
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);

        energyStorage.setEnergyWithoutUpdate(nbt.getInt("energy"));
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

    protected void pushEnergyToOutputs(Direction... outputDirections) {
        if(level == null || outputDirections.length == 0)
            return;

        List<IEnergyStorage> consumers = new ArrayList<>();
        List<Integer> consumerEnergyValues = new ArrayList<>();
        int consumptionSum = 0;
        for(Direction direction:outputDirections) {
            BlockPos testPos = worldPosition.relative(direction);

            BlockEntity testBlockEntity = level.getBlockEntity(testPos);

            IEnergyStorage energyStorage = level.getCapability(Capabilities.EnergyStorage.BLOCK, testPos,
                    level.getBlockState(testPos), testBlockEntity, direction.getOpposite());
            if(energyStorage == null || !energyStorage.canReceive())
                continue;

            int received = energyStorage.receiveEnergy(Math.min(this.limitingEnergyStorage.getMaxExtract(),
                    this.energyStorage.getEnergy()), true);
            if(received <= 0)
                continue;

            consumptionSum += received;
            consumers.add(energyStorage);
            consumerEnergyValues.add(received);
        }

        List<Integer> consumerEnergyDistributed = new ArrayList<>();
        for(int i = 0;i < consumers.size();i++)
            consumerEnergyDistributed.add(0);

        int consumptionLeft = Math.min(this.limitingEnergyStorage.getMaxExtract(),
                Math.min(this.energyStorage.getEnergy(), consumptionSum));
        this.energyStorage.extractEnergy(consumptionLeft, false);

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
            if(energy > 0)
                consumers.get(i).receiveEnergy(energy, false);
        }
    }
}
