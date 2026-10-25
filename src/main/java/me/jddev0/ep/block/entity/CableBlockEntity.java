package me.jddev0.ep.block.entity;

import com.mojang.datafixers.util.Pair;
import me.jddev0.ep.block.CableBlock;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.energy.EnergizedPowerEnergyStorage;
import me.jddev0.ep.energy.EnergizedPowerLimitingEnergyStorage;
import me.jddev0.ep.machine.tier.CableTier;
import me.jddev0.ep.util.CapabilityUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import javax.annotation.Nullable;
import java.util.*;

public class CableBlockEntity extends BlockEntity {
    private static final CableBlock.EnergyExtractionMode ENERGY_EXTRACTION_MODE = ModConfigs.COMMON_CABLES_ENERGY_EXTRACTION_MODE.getValue();

    private final CableTier tier;

    final EnergizedPowerLimitingEnergyStorage limitingEnergyStorage;
    private final EnergizedPowerEnergyStorage energyStorage;

    private boolean loaded;
    private long lastTicked = -1;

    private final Map<Pair<BlockPos, Direction>, EnergyHandler> producers = new HashMap<>();
    private final Map<Pair<BlockPos, Direction>, EnergyHandler> consumers = new HashMap<>();
    private final Deque<BlockPos> cableBlocks = new ArrayDeque<>();

    public CableBlockEntity(BlockPos blockPos, BlockState blockState, CableTier tier) {
        super(tier.getEntityTypeFromTier(), blockPos, blockState);

        this.tier = tier;

        int capacity = ENERGY_EXTRACTION_MODE.isPush()?tier.getMaxTransfer():0;

        energyStorage = new EnergizedPowerEnergyStorage(capacity, capacity, capacity) {
            @Override
            protected void onFinalCommit() {
                setChanged();
            }
        };
        limitingEnergyStorage = new EnergizedPowerLimitingEnergyStorage(energyStorage, capacity, 0);
    }

    public CableTier getTier() {
        return tier;
    }

    public Map<Pair<BlockPos, Direction>, EnergyHandler> getProducers() {
        return producers;
    }

    public Map<Pair<BlockPos, Direction>, EnergyHandler> getConsumers() {
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

            if(testBlockEntity instanceof CableBlockEntity cableBlockEntity) {
                if(cableBlockEntity.getTier() != blockEntity.getTier()) //Do not connect to different cable tiers
                    continue;

                blockEntity.cableBlocks.add(testPos);

                continue;
            }

            EnergyHandler energyStorage = level.getCapability(Capabilities.Energy.BLOCK, testPos,
                    level.getBlockState(testPos), testBlockEntity, direction.getOpposite());
            if(energyStorage == null)
                continue;

            if(ENERGY_EXTRACTION_MODE.isPull() && CapabilityUtil.canExtract(energyStorage))
                blockEntity.producers.put(Pair.of(testPos, direction.getOpposite()), energyStorage);

            if(CapabilityUtil.canInsert(energyStorage))
                blockEntity.consumers.put(Pair.of(testPos, direction.getOpposite()), energyStorage);
        }
    }

    private record CableNetwork(Deque<EnergyHandler> cablesWithStoredEnergy, Deque<EnergyHandler> consumers, Deque<EnergyHandler> producers) {}
    private static CableNetwork gatherEnergyStoragesAndMarkTicked(
            Level level, BlockPos blockPos
    ) {
        final long currentGameTick = level.getGameTime();

        Deque<EnergyHandler> cablesWithStoredEnergy = new ArrayDeque<>(1024);
        Deque<EnergyHandler> consumers = new ArrayDeque<>(1024);
        Deque<EnergyHandler> producers = new ArrayDeque<>(1024);

        Deque<CableBlockEntity> cableBlocksLeft = new ArrayDeque<>(1024);
        if(level.getBlockEntity(blockPos) instanceof CableBlockEntity cableBlockEntity) {
            cableBlockEntity.lastTicked = currentGameTick;
            cableBlocksLeft.add(cableBlockEntity);
        }

        while(cableBlocksLeft.size() > 0) {
            CableBlockEntity cableBlockEntity = cableBlocksLeft.pop();

            cableBlockEntity.getCableBlocks().forEach(pos -> {
                BlockEntity blockEntity = level.getBlockEntity(pos);
                if(!(blockEntity instanceof CableBlockEntity cableBE))
                    return;

                if(cableBE.lastTicked != currentGameTick) {
                    cableBE.lastTicked = currentGameTick;
                    cableBlocksLeft.add(cableBE);
                }
            });

            if(cableBlockEntity.energyStorage.getAmountAsInt() > 0)
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

        CableNetwork cableNetwork = gatherEnergyStoragesAndMarkTicked(level, blockPos);

        final int MAX_TRANSFER = blockEntity.tier.getMaxTransfer();
        List<EnergyHandler> energyProduction = new ArrayList<>();
        List<Integer> energyProductionValues = new ArrayList<>();

        //Prioritize stored energy for PUSH mode
        int productionSum = cableNetwork.cablesWithStoredEnergy().stream().
                mapToInt(EnergyHandler::getAmountAsInt).
                sum(); //Will always be 0 if in PULL only mode
        for(EnergyHandler energyStorage:cableNetwork.producers()) {
            try(Transaction transaction = Transaction.open(null)) {
                int extracted = energyStorage.extract(MAX_TRANSFER, transaction);

                if(extracted <= 0)
                    continue;

                energyProduction.add(energyStorage);
                energyProductionValues.add(extracted);
                productionSum += extracted;
            }
        }

        if(productionSum <= 0)
            return;

        List<EnergyHandler> energyConsumption = new ArrayList<>();
        List<Integer> energyConsumptionValues = new ArrayList<>();

        Deque<EnergyHandler> consumers = cableNetwork.consumers();

        int consumptionSum = 0;
        for(EnergyHandler energyStorage:consumers) {
            try(Transaction transaction = Transaction.open(null)) {
                int received = energyStorage.insert(MAX_TRANSFER, transaction);

                if(received <= 0)
                    continue;

                energyConsumption.add(energyStorage);
                energyConsumptionValues.add(received);
                consumptionSum += received;
            }
        }

        if(consumptionSum <= 0)
            return;

        int transferLeft = Math.min(Math.min(MAX_TRANSFER, productionSum), consumptionSum);

        int extractInternally = 0;
        if(ENERGY_EXTRACTION_MODE.isPush()) {
            //Prioritize stored energy for PUSH mode
            for(EnergyHandler energyHandler:cableNetwork.cablesWithStoredEnergy()) {
                int extracted = Math.min(energyHandler.getAmountAsInt(), transferLeft - extractInternally);
                try(Transaction transaction = Transaction.open(null)) {
                    energyHandler.extract(extracted, transaction);

                    transaction.commit();
                }

                extractInternally += extracted;
            }
        }

        List<Integer> energyProductionDistributed = new ArrayList<>();
        for(int i = 0;i < energyProduction.size();i++)
            energyProductionDistributed.add(0);

        //Set to 0 for PUSH only mode
        int productionLeft = ENERGY_EXTRACTION_MODE.isPull()?transferLeft - extractInternally:0;
        int divisor = energyProduction.size();
        outer:
        while(productionLeft > 0) {
            int productionPerProducer = productionLeft / divisor;
            if(productionPerProducer == 0) {
                divisor = Math.max(1, divisor - 1);
                productionPerProducer = productionLeft / divisor;
            }

            for(int i = 0;i < energyProductionValues.size();i++) {
                int productionDistributed = energyProductionDistributed.get(i);
                int productionOfProducerLeft = energyProductionValues.get(i) - productionDistributed;

                int productionDistributedNew = Math.min(productionPerProducer, Math.min(productionOfProducerLeft, productionLeft));
                energyProductionDistributed.set(i, productionDistributed + productionDistributedNew);
                productionLeft -= productionDistributedNew;
                if(productionLeft == 0)
                    break outer;
            }
        }

        for(int i = 0;i < energyProduction.size();i++) {
            int energy = energyProductionDistributed.get(i);
            if(energy > 0)
                try(Transaction transaction = Transaction.open(null)) {
                    energyProduction.get(i).extract(energy, transaction);
                    transaction.commit();
                }
        }

        List<Integer> energyConsumptionDistributed = new ArrayList<>();
        for(int i = 0;i < energyConsumption.size();i++)
            energyConsumptionDistributed.add(0);

        int consumptionLeft = transferLeft;
        divisor = energyConsumption.size();
        outer:
        while(consumptionLeft > 0) {
            int consumptionPerConsumer = consumptionLeft / divisor;
            if(consumptionPerConsumer == 0) {
                divisor = Math.max(1, divisor - 1);
                consumptionPerConsumer = consumptionLeft / divisor;
            }

            for(int i = 0;i < energyConsumptionValues.size();i++) {
                int consumptionDistributed = energyConsumptionDistributed.get(i);
                int consumptionOfConsumerLeft = energyConsumptionValues.get(i) - consumptionDistributed;

                int consumptionDistributedNew = Math.min(consumptionOfConsumerLeft, Math.min(consumptionPerConsumer, consumptionLeft));
                energyConsumptionDistributed.set(i, consumptionDistributed + consumptionDistributedNew);
                consumptionLeft -= consumptionDistributedNew;
                if(consumptionLeft == 0)
                    break outer;
            }
        }

        for(int i = 0;i < energyConsumption.size();i++) {
            int energy = energyConsumptionDistributed.get(i);
            if(energy > 0)
                try(Transaction transaction = Transaction.open(null)) {
                    energyConsumption.get(i).insert(energy, transaction);
                    transaction.commit();
                }
        }
    }

    public @Nullable EnergyHandler getEnergyStorageCapability(@Nullable Direction side) {
        return limitingEnergyStorage;
    }

    @Override
    protected void saveAdditional(ValueOutput view) {
        super.saveAdditional(view);

        if(ENERGY_EXTRACTION_MODE.isPush())
            view.putInt("energy", energyStorage.getAmountAsInt());
    }

    @Override
    protected void loadAdditional(ValueInput view) {
        super.loadAdditional(view);

        if(ENERGY_EXTRACTION_MODE.isPush())
            energyStorage.setAmountWithoutUpdate(view.getIntOr("energy", 0));
    }
}
