package me.jddev0.ep.block.entity;

import com.mojang.datafixers.util.Pair;
import me.jddev0.ep.block.CableBlock;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.energy.EnergizedPowerEnergyStorage;
import me.jddev0.ep.energy.EnergizedPowerLimitingEnergyStorage;
import me.jddev0.ep.machine.tier.CableTier;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.EnergySyncS2CPacket;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.*;

public class CableBlockEntity extends BlockEntity {
    private static final CableBlock.EnergyExtractionMode ENERGY_EXTRACTION_MODE = ModConfigs.COMMON_CABLES_ENERGY_EXTRACTION_MODE.getValue();

    private final CableTier tier;

    final EnergizedPowerLimitingEnergyStorage limitingEnergyStorage;
    private final EnergizedPowerEnergyStorage energyStorage;

    private boolean loaded;
    private long lastTicked = -1;

    private final Map<Pair<BlockPos, Direction>, EnergyStorage> producers = new HashMap<>();
    private final Map<Pair<BlockPos, Direction>, EnergyStorage> consumers = new HashMap<>();
    private final Deque<BlockPos> cableBlocks = new ArrayDeque<>();

    public CableBlockEntity(BlockPos blockPos, BlockState blockState, CableTier tier) {
        super(tier.getEntityTypeFromTier(), blockPos, blockState);

        this.tier = tier;

        long capacity = ENERGY_EXTRACTION_MODE.isPush()?tier.getMaxTransfer():0;

        energyStorage = new EnergizedPowerEnergyStorage(capacity, capacity, capacity) {
            @Override
            protected void onFinalCommit() {
                setChanged();

                if(level != null && !level.isClientSide()) {
                    ModMessages.sendToPlayersWithinXBlocks(
                            new EnergySyncS2CPacket(getAmount(), getCapacity(), getBlockPos()), getBlockPos(), (ServerLevel)level, 32
                    );
                }
            }
        };
        limitingEnergyStorage = new EnergizedPowerLimitingEnergyStorage(energyStorage, capacity, 0);
    }

    public CableTier getTier() {
        return tier;
    }

    public Map<Pair<BlockPos, Direction>, EnergyStorage> getProducers() {
        return producers;
    }

    public Map<Pair<BlockPos, Direction>, EnergyStorage> getConsumers() {
        return consumers;
    }

    public Deque<BlockPos> getCableBlocks() {
        return cableBlocks;
    }

    public static void updateConnections(Level level, BlockPos blockPos, BlockState state, CableBlockEntity blockEntity) {
        if(level.isClientSide())
            return;

        blockEntity.producers.clear();
        blockEntity.consumers.clear();
        blockEntity.cableBlocks.clear();

        for(Direction direction:Direction.values()) {
            BlockPos testPos = blockPos.relative(direction);

            BlockEntity testBlockEntity = level.getBlockEntity(testPos);
            if(testBlockEntity == null)
                continue;

            if(testBlockEntity instanceof CableBlockEntity cableBlockEntity) {
                if(cableBlockEntity.getTier() != blockEntity.getTier()) //Do not connect to different cable tiers
                    continue;

                blockEntity.cableBlocks.add(testPos);

                continue;
            }

            EnergyStorage limitingEnergyStorage = EnergyStorage.SIDED.find(level, testPos, direction.getOpposite());
            if(limitingEnergyStorage == null)
                continue;

            if(ENERGY_EXTRACTION_MODE.isPull() && limitingEnergyStorage.supportsExtraction())
                blockEntity.producers.put(Pair.of(testPos, direction.getOpposite()), limitingEnergyStorage);

            if(limitingEnergyStorage.supportsInsertion())
                blockEntity.consumers.put(Pair.of(testPos, direction.getOpposite()), limitingEnergyStorage);
        }
    }

    private record CableNetwork(Deque<EnergyStorage> cablesWithStoredEnergy, Deque<EnergyStorage> consumers, Deque<EnergyStorage> producers) {}
    private static CableNetwork gatherEnergyStoragesAndMarkTicked(
            Level level, BlockPos blockPos, Set<BlockPos> checkedCables
    ) {
        final long currentGameTick = level.getGameTime();

        Deque<EnergyStorage> cablesWithStoredEnergy = new ArrayDeque<>(1024);
        Deque<EnergyStorage> consumers = new ArrayDeque<>(1024);
        Deque<EnergyStorage> producers = new ArrayDeque<>(1024);

        Deque<BlockPos> cableBlocksLeft = new ArrayDeque<>(1024);
        cableBlocksLeft.add(blockPos);

        checkedCables.add(blockPos);

        while(cableBlocksLeft.size() > 0) {
            BlockPos checkPos = cableBlocksLeft.pop();

            BlockEntity blockEntity = level.getBlockEntity(checkPos);
            if(!(blockEntity instanceof CableBlockEntity cableBlockEntity))
                continue;

            cableBlockEntity.lastTicked = currentGameTick;

            cableBlockEntity.getCableBlocks().forEach(pos -> {
                if(checkedCables.add(pos)) {
                    cableBlocksLeft.add(pos);
                }
            });

            if(cableBlockEntity.energyStorage.getAmount() > 0)
                cablesWithStoredEnergy.add(cableBlockEntity.energyStorage);

            consumers.addAll(cableBlockEntity.getConsumers().values());
            producers.addAll(cableBlockEntity.getProducers().values());
        }

        return new CableNetwork(cablesWithStoredEnergy, consumers, producers);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, CableBlockEntity blockEntity) {
        if(level.isClientSide())
            return;

        if(!blockEntity.loaded) {
            updateConnections(level, blockPos, state, blockEntity);

            blockEntity.loaded = true;
        }

        if(blockEntity.lastTicked == level.getGameTime()) {
            //Cable algorithm was already run this tick for this network
            return;
        }

        CableNetwork cableNetwork = gatherEnergyStoragesAndMarkTicked(level, blockPos, new HashSet<>());

        final long MAX_TRANSFER = blockEntity.tier.getMaxTransfer();
        List<EnergyStorage> energyProduction = new ArrayList<>();
        List<Long> energyProductionValues = new ArrayList<>();

        //Prioritize stored energy for PUSH mode
        long productionSum = cableNetwork.cablesWithStoredEnergy().stream().
                mapToLong(EnergyStorage::getAmount).
                sum(); //Will always be 0 if in PULL only mode
        for(EnergyStorage limitingEnergyStorage:cableNetwork.producers()) {
            try(Transaction transaction = Transaction.openOuter()) {
                long extracted = limitingEnergyStorage.extract(MAX_TRANSFER, transaction);

                if(extracted <= 0)
                    continue;

                energyProduction.add(limitingEnergyStorage);
                energyProductionValues.add(extracted);
                productionSum += extracted;
            }
        }

        if(productionSum <= 0)
            return;

        List<EnergyStorage> energyConsumption = new ArrayList<>();
        List<Long> energyConsumptionValues = new ArrayList<>();

        Deque<EnergyStorage> consumers = cableNetwork.consumers();

        long consumptionSum = 0;
        for(EnergyStorage limitingEnergyStorage:consumers) {
            try(Transaction transaction = Transaction.openOuter()) {
                long received = limitingEnergyStorage.insert(MAX_TRANSFER, transaction);

                if(received <= 0)
                    continue;

                energyConsumption.add(limitingEnergyStorage);
                energyConsumptionValues.add(received);
                consumptionSum += received;
            }
        }

        if(consumptionSum <= 0)
            return;

        long transferLeft = Math.min(Math.min(MAX_TRANSFER, productionSum), consumptionSum);

        long extractInternally = 0;
        if(ENERGY_EXTRACTION_MODE.isPush()) {
            //Prioritize stored energy for PUSH mode
            for(EnergyStorage energyHandler:cableNetwork.cablesWithStoredEnergy()) {
                long extracted = Math.min(energyHandler.getAmount(), transferLeft - extractInternally);
                try(Transaction transaction = Transaction.openOuter()) {
                    energyHandler.extract(extracted, transaction);

                    transaction.commit();
                }

                extractInternally += extracted;
            }
        }

        List<Long> energyProductionDistributed = new ArrayList<>();
        for(int i = 0;i < energyProduction.size();i++)
            energyProductionDistributed.add(0L);

        //Set to 0 for PUSH only mode
        long productionLeft = ENERGY_EXTRACTION_MODE.isPull()?transferLeft - extractInternally:0;
        int divisor = energyProduction.size();
        outer:
        while(productionLeft > 0) {
            long productionPerProducer = productionLeft / divisor;
            if(productionPerProducer == 0) {
                divisor = Math.max(1, divisor - 1);
                productionPerProducer = productionLeft / divisor;
            }

            for(int i = 0;i < energyProductionValues.size();i++) {
                long productionDistributed = energyProductionDistributed.get(i);
                long productionOfProducerLeft = energyProductionValues.get(i) - productionDistributed;

                long productionDistributedNew = Math.min(productionPerProducer, Math.min(productionOfProducerLeft, productionLeft));
                energyProductionDistributed.set(i, productionDistributed + productionDistributedNew);
                productionLeft -= productionDistributedNew;
                if(productionLeft == 0)
                    break outer;
            }
        }

        for(int i = 0;i < energyProduction.size();i++) {
            long energy = energyProductionDistributed.get(i);
            if(energy > 0)
                try(Transaction transaction = Transaction.openOuter()) {
                    energyProduction.get(i).extract(energy, transaction);
                    transaction.commit();
                }
        }

        List<Long> energyConsumptionDistributed = new ArrayList<>();
        for(int i = 0;i < energyConsumption.size();i++)
            energyConsumptionDistributed.add(0L);

        long consumptionLeft = transferLeft;
        divisor = energyConsumption.size();
        outer:
        while(consumptionLeft > 0) {
            long consumptionPerConsumer = consumptionLeft / divisor;
            if(consumptionPerConsumer == 0) {
                divisor = Math.max(1, divisor - 1);
                consumptionPerConsumer = consumptionLeft / divisor;
            }

            for(int i = 0;i < energyConsumptionValues.size();i++) {
                long consumptionDistributed = energyConsumptionDistributed.get(i);
                long consumptionOfConsumerLeft = energyConsumptionValues.get(i) - consumptionDistributed;

                long consumptionDistributedNew = Math.min(consumptionOfConsumerLeft, Math.min(consumptionPerConsumer, consumptionLeft));
                energyConsumptionDistributed.set(i, consumptionDistributed + consumptionDistributedNew);
                consumptionLeft -= consumptionDistributedNew;
                if(consumptionLeft == 0)
                    break outer;
            }
        }

        for(int i = 0;i < energyConsumption.size();i++) {
            long energy = energyConsumptionDistributed.get(i);
            if(energy > 0) {
                try(Transaction transaction = Transaction.openOuter()) {
                    energyConsumption.get(i).insert(energy, transaction);
                    transaction.commit();
                }
            }
        }
    }

    public @Nullable EnergyStorage getEnergyStorageCapability(@Nullable Direction side) {
        return limitingEnergyStorage;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);

        if(ENERGY_EXTRACTION_MODE.isPush())
            nbt.putLong("energy", energyStorage.getAmount());
    }

    @Override
    public void loadAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);

        if(ENERGY_EXTRACTION_MODE.isPush())
            energyStorage.setAmountWithoutUpdate(nbt.getLong("energy"));
    }
}
