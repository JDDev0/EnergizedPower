package me.jddev0.ep.block.entity;

import com.mojang.datafixers.util.Pair;
import me.jddev0.ep.block.CableBlock;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.energy.ReceiveOnlyEnergyStorage;
import me.jddev0.ep.machine.tier.CableTier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CableBlockEntity extends BlockEntity {
    private static final CableBlock.EnergyExtractionMode ENERGY_EXTRACTION_MODE = ModConfigs.COMMON_CABLES_ENERGY_EXTRACTION_MODE.getValue();

    private final CableTier tier;

    private final ReceiveOnlyEnergyStorage energyStorage;
    private LazyOptional<IEnergyStorage> lazyEnergyStorage = LazyOptional.empty();

    private boolean loaded;

    private final Map<Pair<BlockPos, Direction>, IEnergyStorage> producers = new HashMap<>();
    private final Map<Pair<BlockPos, Direction>, IEnergyStorage> consumers = new HashMap<>();
    private final Deque<BlockPos> cableBlocks = new ArrayDeque<>();

    public CableBlockEntity(BlockPos blockPos, BlockState blockState, CableTier tier) {
        super(tier.getEntityTypeFromTier(), blockPos, blockState);

        this.tier = tier;

        if(ENERGY_EXTRACTION_MODE.isPush()) {
            energyStorage = new ReceiveOnlyEnergyStorage(0, tier.getMaxTransfer(), tier.getMaxTransfer()) {
                @Override
                protected void onChange() {
                    setChanged();
                }
            };
        }else {
            //Do not allow energy insertion for PULL only mode

            energyStorage = new ReceiveOnlyEnergyStorage() {
                @Override
                public int receiveEnergy(int maxReceive, boolean simulate) {
                    return 0;
                }

                @Override
                public boolean canReceive() {
                    return false;
                }
            };
        }
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
            if(testBlockEntity == null)
                continue;

            if(testBlockEntity instanceof CableBlockEntity cableBlockEntity) {
                if(cableBlockEntity.getTier() != blockEntity.getTier()) //Do not connect to different cable tiers
                    continue;

                blockEntity.cableBlocks.add(testPos);

                continue;
            }

            LazyOptional<IEnergyStorage> energyStorageLazyOptional = testBlockEntity.getCapability(ForgeCapabilities.ENERGY, direction.getOpposite());
            if(!energyStorageLazyOptional.isPresent())
                continue;

            IEnergyStorage energyStorage = energyStorageLazyOptional.orElse(null);
            if(ENERGY_EXTRACTION_MODE.isPull() && energyStorage.canExtract())
                blockEntity.producers.put(Pair.of(testPos, direction.getOpposite()), energyStorage);

            if(energyStorage.canReceive())
                blockEntity.consumers.put(Pair.of(testPos, direction.getOpposite()), energyStorage);
        }
    }

    public static Deque<IEnergyStorage> getConnectedConsumers(Level level, BlockPos blockPos, Set<BlockPos> checkedCables) {
        Deque<IEnergyStorage> consumers = new ArrayDeque<>(1024);

        Deque<BlockPos> cableBlocksLeft = new ArrayDeque<>(1024);
        cableBlocksLeft.add(blockPos);

        checkedCables.add(blockPos);

        while(cableBlocksLeft.size() > 0) {
            BlockPos checkPos = cableBlocksLeft.pop();

            BlockEntity blockEntity = level.getBlockEntity(checkPos);
            if(!(blockEntity instanceof CableBlockEntity))
                continue;

            CableBlockEntity cableBlockEntity = (CableBlockEntity)blockEntity;
            cableBlockEntity.getCableBlocks().forEach(pos -> {
                if(checkedCables.add(pos)) {
                    cableBlocksLeft.add(pos);
                }
            });

            consumers.addAll(cableBlockEntity.getConsumers().values());
        }

        return consumers;
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, CableBlockEntity blockEntity) {
        if(level.isClientSide)
            return;

        if(!blockEntity.loaded) {
            updateConnections(level, blockPos, state, blockEntity);

            blockEntity.loaded = true;
        }

        final int MAX_TRANSFER = blockEntity.tier.getMaxTransfer();
        List<IEnergyStorage> energyProduction = new ArrayList<>();
        List<Integer> energyProductionValues = new ArrayList<>();

        //Prioritize stored energy for PUSH mode
        int productionSum = blockEntity.energyStorage.getEnergy(); //Will always be 0 if in PULL only mode
        for(IEnergyStorage energyStorage:blockEntity.producers.values()) {
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

        Deque<IEnergyStorage> consumers = getConnectedConsumers(level, blockPos, new HashSet<>());

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
            extractInternally = Math.min(blockEntity.energyStorage.getEnergy(), transferLeft);
            blockEntity.energyStorage.setEnergy(blockEntity.energyStorage.getEnergy() - extractInternally);
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

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.ENERGY) {
            return lazyEnergyStorage.cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();

        lazyEnergyStorage = LazyOptional.of(() -> energyStorage);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();

        lazyEnergyStorage.invalidate();
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);

        if(ENERGY_EXTRACTION_MODE.isPush())
            nbt.put("energy", energyStorage.saveNBT());
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);

        if(ENERGY_EXTRACTION_MODE.isPush())
            energyStorage.loadNBT(nbt.get("energy"));
    }
}
