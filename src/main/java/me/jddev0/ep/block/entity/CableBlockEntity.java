package me.jddev0.ep.block.entity;

import com.mojang.datafixers.util.Pair;
import me.jddev0.ep.block.CableBlock;
import me.jddev0.ep.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CableBlockEntity extends BlockEntity {
    private final CableBlock.Tier tier;

    private final IEnergyStorage energyStorage;
    private LazyOptional<IEnergyStorage> lazyEnergyStorage = LazyOptional.empty();

    private boolean loaded;

    private final Map<Pair<BlockPos, Direction>, IEnergyStorage> producers = new HashMap<>();
    private final Map<Pair<BlockPos, Direction>, IEnergyStorage> consumers = new HashMap<>();
    private final List<BlockPos> cableBlocks = new LinkedList<>();

    public static BlockEntityType<CableBlockEntity> getEntityTypeFromTier(CableBlock.Tier tier) {
        return switch(tier) {
            case TIER_COPPER -> ModBlockEntities.COPPER_CABLE_ENTITY.get();
            case TIER_ENERGIZED_COPPER -> ModBlockEntities.ENERGIZED_COPPER_CABLE_ENTITY.get();
        };
    }

    public CableBlockEntity(BlockPos blockPos, BlockState blockState, CableBlock.Tier tier) {
        super(getEntityTypeFromTier(tier), blockPos, blockState);

        this.tier = tier;

        energyStorage = new IEnergyStorage() {
            @Override
            public int receiveEnergy(int maxReceive, boolean simulate) {
                return 0;
            }

            @Override
            public int extractEnergy(int maxExtract, boolean simulate) {
                return 0;
            }

            @Override
            public int getEnergyStored() {
                return 0;
            }

            @Override
            public int getMaxEnergyStored() {
                return 0;
            }

            @Override
            public boolean canExtract() {
                return false;
            }

            @Override
            public boolean canReceive() {
                return false;
            }
        };
    }

    public CableBlock.Tier getTier() {
        return tier;
    }

    public Map<Pair<BlockPos, Direction>, IEnergyStorage> getProducers() {
        return producers;
    }

    public Map<Pair<BlockPos, Direction>, IEnergyStorage> getConsumers() {
        return consumers;
    }

    public List<BlockPos> getCableBlocks() {
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
            BlockState testBlockState = level.getBlockState(testPos);
            if(testBlockState.is(ModBlocks.COPPER_CABLE.get()) || testBlockState.is(ModBlocks.ENERGIZED_COPPER_CABLE.get())) {
                blockEntity.cableBlocks.add(testPos);

                continue;
            }

            BlockEntity testBlockEntity = level.getBlockEntity(testPos);
            if(testBlockEntity == null)
                continue;

            LazyOptional<IEnergyStorage> energyStorageLazyOptional = testBlockEntity.getCapability(ForgeCapabilities.ENERGY, direction.getOpposite());
            if(!energyStorageLazyOptional.isPresent())
                continue;

            IEnergyStorage energyStorage = energyStorageLazyOptional.orElse(null);
            if(energyStorage.canExtract())
                blockEntity.producers.put(Pair.of(testPos, direction.getOpposite()), energyStorage);

            if(energyStorage.canReceive())
                blockEntity.consumers.put(Pair.of(testPos, direction.getOpposite()), energyStorage);
        }
    }

    public static List<IEnergyStorage> getConnectedConsumers(Level level, BlockPos blockPos, List<BlockPos> checkedCables) {
        List<IEnergyStorage> consumers = new LinkedList<>();

        if(checkedCables.contains(blockPos))
            return consumers;
        checkedCables.add(blockPos);

        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if(blockEntity == null || !(blockEntity instanceof CableBlockEntity))
            return consumers;

        CableBlockEntity cableBlockEntity = (CableBlockEntity)blockEntity;
        consumers.addAll(cableBlockEntity.consumers.values());

        cableBlockEntity.getCableBlocks().forEach(pos -> consumers.addAll(getConnectedConsumers(level, pos, checkedCables)));

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
        List<IEnergyStorage> energyProduction = new LinkedList<>();
        List<Integer> energyProductionValues = new LinkedList<>();

        int productionSum = 0;
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

        List<IEnergyStorage> energyConsumption = new LinkedList<>();
        List<Integer> energyConsumptionValues = new LinkedList<>();

        List<IEnergyStorage> consumers = getConnectedConsumers(level, blockPos, new LinkedList<>());

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

        List<Integer> energyProductionDistributed = new LinkedList<>();
        for(int i = 0;i < energyProduction.size();i++)
            energyProductionDistributed.add(0);

        int productionLeft = transferLeft;
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

        List<Integer> energyConsumptionDistributed = new LinkedList<>();
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
    protected void saveAdditional(CompoundTag nbt) {
        //TODO

        super.saveAdditional(nbt);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);

        //TODO
    }
}
