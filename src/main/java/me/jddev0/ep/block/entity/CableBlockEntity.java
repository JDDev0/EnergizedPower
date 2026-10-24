package me.jddev0.ep.block.entity;

import com.mojang.datafixers.util.Pair;
import me.jddev0.ep.block.CableBlock;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.energy.EnergizedPowerEnergyStorage;
import me.jddev0.ep.energy.EnergizedPowerLimitingEnergyStorage;
import me.jddev0.ep.energy.IEnergizedPowerEnergyStorage;
import me.jddev0.ep.machine.tier.CableTier;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.EnergySyncS2CPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CableBlockEntity extends BlockEntity {
    private static final CableBlock.EnergyExtractionMode ENERGY_EXTRACTION_MODE = ModConfigs.COMMON_CABLES_ENERGY_EXTRACTION_MODE.getValue();

    private final CableTier tier;

    final EnergizedPowerLimitingEnergyStorage limitingEnergyStorage;
    private final EnergizedPowerEnergyStorage energyStorage;

    private boolean loaded;
    private long lastTicked = -1;

    private final Map<Pair<BlockPos, Direction>, IEnergyStorage> producers = new HashMap<>();
    private final Map<Pair<BlockPos, Direction>, IEnergyStorage> consumers = new HashMap<>();
    private final Deque<BlockPos> cableBlocks = new ArrayDeque<>();

    public CableBlockEntity(BlockPos blockPos, BlockState blockState, CableTier tier) {
        super(tier.getEntityTypeFromTier(), blockPos, blockState);

        this.tier = tier;

        int capacity = ENERGY_EXTRACTION_MODE.isPush()?tier.getMaxTransfer():0;

        energyStorage = new EnergizedPowerEnergyStorage(capacity, capacity, capacity) {
            @Override
            protected void onFinalCommit() {
                setChanged();

                if(level != null && !level.isClientSide()) {
                    ModMessages.sendToPlayersWithinXBlocks(
                            new EnergySyncS2CPacket(getEnergy(), getCapacity(), getBlockPos()),
                            getBlockPos(), (ServerLevel)level, 32
                    );
                }
            }
        };
        limitingEnergyStorage = new EnergizedPowerLimitingEnergyStorage(energyStorage, capacity, 0);
    }

    public CableTier getTier() {
        return tier;
    }

    public Map<Pair<BlockPos, Direction>, IEnergyStorage> getProducers() {
        return producers;
    }

    public Map<Pair<BlockPos, Direction>, IEnergyStorage> getConsumers() {
        return consumers;
    }

    public Deque<BlockPos> getCableBlocks() {
        return cableBlocks;
    }

    public static void updateConnections(Level level, BlockPos blockPos, BlockState state, CableBlockEntity blockEntity) {
        if(level.isClientSide)
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

            IEnergyStorage energyStorage = level.getCapability(Capabilities.EnergyStorage.BLOCK, testPos,
                    level.getBlockState(testPos), testBlockEntity, direction.getOpposite());
            if(energyStorage == null)
                continue;

            if(ENERGY_EXTRACTION_MODE.isPull() && energyStorage.canExtract())
                blockEntity.producers.put(Pair.of(testPos, direction.getOpposite()), energyStorage);

            if(energyStorage.canReceive())
                blockEntity.consumers.put(Pair.of(testPos, direction.getOpposite()), energyStorage);
        }
    }

    private record CableNetwork(Deque<IEnergizedPowerEnergyStorage> cablesWithStoredEnergy, Deque<IEnergyStorage> consumers, Deque<IEnergyStorage> producers) {}
    private static CableNetwork gatherEnergyStoragesAndMarkTicked(
            Level level, BlockPos blockPos, Set<BlockPos> checkedCables
    ) {
        final long currentGameTick = level.getGameTime();

        Deque<IEnergizedPowerEnergyStorage> cablesWithStoredEnergy = new ArrayDeque<>(1024);
        Deque<IEnergyStorage> consumers = new ArrayDeque<>(1024);
        Deque<IEnergyStorage> producers = new ArrayDeque<>(1024);

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

            if(cableBlockEntity.energyStorage.getEnergyStored() > 0)
                cablesWithStoredEnergy.add(cableBlockEntity.energyStorage);

            consumers.addAll(cableBlockEntity.getConsumers().values());
            producers.addAll(cableBlockEntity.getProducers().values());
        }

        return new CableNetwork(cablesWithStoredEnergy, consumers, producers);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, CableBlockEntity blockEntity) {
        if(level.isClientSide)
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

        final int MAX_TRANSFER = blockEntity.tier.getMaxTransfer();
        List<IEnergyStorage> energyProduction = new ArrayList<>();
        List<Integer> energyProductionValues = new ArrayList<>();

        //Prioritize stored energy for PUSH mode
        int productionSum = cableNetwork.cablesWithStoredEnergy().stream().
                mapToInt(IEnergyStorage::getEnergyStored).
                sum(); //Will always be 0 if in PULL only mode
        for(IEnergyStorage energyStorage:cableNetwork.producers()) {
            int extracted = energyStorage.extractEnergy(MAX_TRANSFER, true);
            if(extracted <= 0)
                continue;

            energyProduction.add(energyStorage);
            energyProductionValues.add(extracted);
            productionSum += extracted;
        }

        if(productionSum <= 0)
            return;

        List<IEnergyStorage> energyConsumption = new ArrayList<>();
        List<Integer> energyConsumptionValues = new ArrayList<>();

        Deque<IEnergyStorage> consumers = cableNetwork.consumers();

        int consumptionSum = 0;
        for(IEnergyStorage energyStorage:consumers) {
            int received = energyStorage.receiveEnergy(MAX_TRANSFER, true);
            if(received <= 0)
                continue;

            energyConsumption.add(energyStorage);
            energyConsumptionValues.add(received);
            consumptionSum += received;
        }

        if(consumptionSum <= 0)
            return;

        int transferLeft = Math.min(Math.min(MAX_TRANSFER, productionSum), consumptionSum);

        int extractInternally = 0;
        if(ENERGY_EXTRACTION_MODE.isPush()) {
            //Prioritize stored energy for PUSH mode
            for(IEnergizedPowerEnergyStorage energyHandler:cableNetwork.cablesWithStoredEnergy()) {
                int extracted = Math.min(energyHandler.getEnergy(), transferLeft - extractInternally);
                energyHandler.setEnergy(energyHandler.getEnergy() - extracted);

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
                energyProduction.get(i).extractEnergy(energy, false);
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
                energyConsumption.get(i).receiveEnergy(energy, false);
        }
    }

    public @Nullable IEnergyStorage getEnergyStorageCapability(@Nullable Direction side) {
        return limitingEnergyStorage;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);

        if(ENERGY_EXTRACTION_MODE.isPush())
            nbt.putInt("energy", energyStorage.getEnergy());
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);

        if(ENERGY_EXTRACTION_MODE.isPush())
            energyStorage.setEnergyWithoutUpdate(nbt.getInt("energy"));
    }
}
