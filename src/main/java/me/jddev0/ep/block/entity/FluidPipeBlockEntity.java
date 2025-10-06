package me.jddev0.ep.block.entity;

import com.mojang.datafixers.util.Pair;
import me.jddev0.ep.block.FluidPipeBlock;
import me.jddev0.ep.block.EPBlockStateProperties;
import me.jddev0.ep.machine.tier.FluidPipeTier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class FluidPipeBlockEntity extends BlockEntity {
    private final FluidPipeTier tier;

    private final int maxTransfer;

    private final ResourceHandler<FluidResource> fluidStorage;

    private final Map<Pair<BlockPos, Direction>, ResourceHandler<FluidResource>> producers = new HashMap<>();
    private final Map<Pair<BlockPos, Direction>, ResourceHandler<FluidResource>> consumers = new HashMap<>();
    private final Deque<BlockPos> pipeBlocks = new ArrayDeque<>();

    public FluidPipeBlockEntity(BlockPos blockPos, BlockState blockState, FluidPipeTier tier) {
        super(tier.getEntityTypeFromTier(), blockPos, blockState);

        this.tier = tier;

        maxTransfer = tier.getTransferRate();

        fluidStorage = new ResourceHandler<>() {
            @Override
            public int size() {
                return 0;
            }

            @Override
            public FluidResource getResource(int index) {
                return FluidResource.EMPTY;
            }

            @Override
            public long getAmountAsLong(int index) {
                return 0;
            }

            @Override
            public long getCapacityAsLong(int index, FluidResource resource) {
                return 0;
            }

            @Override
            public boolean isValid(int index, FluidResource resource) {
                return false;
            }

            @Override
            public int insert(int index, FluidResource resource, int amount, TransactionContext transaction) {
                return 0;
            }

            @Override
            public int extract(int index, FluidResource resource, int amount, TransactionContext transaction) {
                return 0;
            }
        };
    }

    public FluidPipeTier getTier() {
        return tier;
    }

    public Map<Pair<BlockPos, Direction>, ResourceHandler<FluidResource>> getProducers() {
        return producers;
    }

    public Map<Pair<BlockPos, Direction>, ResourceHandler<FluidResource>> getConsumers() {
        return consumers;
    }

    public Deque<BlockPos> getPipeBlocks() {
        return pipeBlocks;
    }

    public static void updateConnections(Level level, BlockPos blockPos, BlockState state, FluidPipeBlockEntity blockEntity) {
        if(level.isClientSide())
            return;

        blockEntity.producers.clear();
        blockEntity.consumers.clear();
        blockEntity.pipeBlocks.clear();

        for(Direction direction:Direction.values()) {
            BlockPos testPos = blockPos.relative(direction);

            BlockEntity testBlockEntity = level.getBlockEntity(testPos);

            if(testBlockEntity instanceof FluidPipeBlockEntity fluidPipeBlockEntity) {
                if(fluidPipeBlockEntity.getTier() != blockEntity.getTier()) //Do not connect to different pipe tiers
                    continue;

                blockEntity.pipeBlocks.add(testPos);

                continue;
            }

            ResourceHandler<FluidResource> fluidStorage = level.getCapability(Capabilities.Fluid.BLOCK, testPos,
                    level.getBlockState(testPos), testBlockEntity, direction.getOpposite());
            if(fluidStorage == null || fluidStorage.size() == 0)
                continue;

            EPBlockStateProperties.PipeConnection pipeConnection = state.getValue(FluidPipeBlock.getPipeConnectionPropertyFromDirection(direction));
            if(pipeConnection.isExtract())
                blockEntity.producers.put(Pair.of(testPos, direction.getOpposite()), fluidStorage);
            else if(pipeConnection.isInsert())
                blockEntity.consumers.put(Pair.of(testPos, direction.getOpposite()), fluidStorage);
        }
    }

    public static Deque<ResourceHandler<FluidResource>> getConnectedConsumers(Level level, BlockPos blockPos, Set<BlockPos> checkedPipes) {
        Deque<ResourceHandler<FluidResource>> consumers = new ArrayDeque<>(1024);

        Deque<BlockPos> pipeBlocksLeft = new ArrayDeque<>(1024);
        pipeBlocksLeft.add(blockPos);

        checkedPipes.add(blockPos);

        while(pipeBlocksLeft.size() > 0) {
            BlockPos checkPos = pipeBlocksLeft.pop();

            BlockEntity blockEntity = level.getBlockEntity(checkPos);
            if(!(blockEntity instanceof FluidPipeBlockEntity fluidPipeBlockEntity))
                continue;

            fluidPipeBlockEntity.getPipeBlocks().forEach(pos -> {
                if(checkedPipes.add(pos)) {
                    pipeBlocksLeft.add(pos);
                }
            });

            consumers.addAll(fluidPipeBlockEntity.getConsumers().values());
        }

        return consumers;
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, FluidPipeBlockEntity blockEntity) {
        if(level.isClientSide())
            return;

        //TODO improve do not update all the time
        updateConnections(level, blockPos, state, blockEntity);

        Deque<ResourceHandler<FluidResource>> consumers = null;

        FluidResource extractedFluidType;

        List<ResourceHandler<FluidResource>> fluidProduction;
        List<Integer> fluidProductionValues;
        int productionSum;

        List<ResourceHandler<FluidResource>> fluidConsumption;
        List<Integer> fluidConsumptionValues;
        int consumptionSum;


        //List of all fluid types which where already checked
        List<FluidResource> alreadyCheckedFluidTypes = new ArrayList<>();

        //Try all fluid types but stop after the first fluid type which can be inserted somewhere
        while(true) {
            fluidProduction = new ArrayList<>();

            extractedFluidType = FluidResource.EMPTY;

            fluidProductionValues = new ArrayList<>();

            productionSum = 0;
            for(ResourceHandler<FluidResource> fluidStorage:blockEntity.producers.values()) {
                boolean extractedAnything = false;

                int fluidProductionValuesIndex = -1;

                tankLoop:
                for(int i = 0;i < fluidStorage.size();i++) {
                    FluidResource fluidResourceInTank = fluidStorage.getResource(i);
                    if(fluidResourceInTank.isEmpty())
                        continue;

                    boolean wasExtractedFluidTypeEmpty = extractedFluidType.isEmpty();
                    if(wasExtractedFluidTypeEmpty) {
                        for(FluidResource alreadyCheckedFluidType:alreadyCheckedFluidTypes)
                            if(alreadyCheckedFluidType.equals(fluidResourceInTank))
                                continue tankLoop;

                        extractedFluidType = fluidResourceInTank;
                    }

                    if(!fluidResourceInTank.equals(extractedFluidType))
                        continue;

                    int extracted;
                    try(Transaction transaction = Transaction.open(null)) {
                        extracted = fluidStorage.extract(extractedFluidType, blockEntity.maxTransfer, transaction);
                    }

                    if(extracted <= 0) {
                        if(wasExtractedFluidTypeEmpty)
                            extractedFluidType = FluidResource.EMPTY;

                        continue;
                    }

                    extractedAnything = true;

                    if(fluidProductionValuesIndex == -1) {
                        fluidProductionValuesIndex = fluidProductionValues.size();
                        fluidProductionValues.add(extracted);
                    }else {
                        fluidProductionValues.set(fluidProductionValuesIndex,
                                fluidProductionValues.get(fluidProductionValuesIndex) + extracted);
                    }

                    productionSum += extracted;
                }

                if(extractedAnything)
                    fluidProduction.add(fluidStorage);
            }

            if(productionSum <= 0 || extractedFluidType.isEmpty())
                return;

            fluidConsumption = new ArrayList<>();

            fluidConsumptionValues = new ArrayList<>();

            consumptionSum = 0;

            //Check consumers only if something was extracted
            if(consumers == null)
                consumers = getConnectedConsumers(level, blockPos, new HashSet<>());

            for(ResourceHandler<FluidResource> fluidStorage:consumers) {
                boolean receivedAnything = false;

                int fluidConsumptionValuesIndex = -1;

                for(int i = 0;i < fluidStorage.size();i++) {
                    int received;
                    try(Transaction transaction = Transaction.open(null)) {
                        received = fluidStorage.insert(extractedFluidType, Math.min(blockEntity.maxTransfer, productionSum), transaction);
                    }

                    receivedAnything = true;

                    if(fluidConsumptionValuesIndex == -1) {
                        fluidConsumptionValuesIndex = fluidConsumptionValues.size();
                        fluidConsumptionValues.add(received);
                    }else {
                        fluidConsumptionValues.set(fluidConsumptionValuesIndex,
                                fluidConsumptionValues.get(fluidConsumptionValuesIndex) + received);
                    }

                    consumptionSum += received;
                }

                if(receivedAnything)
                    fluidConsumption.add(fluidStorage);
            }

            //If everything was consumed -> continue after while(true)
            if(consumptionSum > 0)
                break;

            alreadyCheckedFluidTypes.add(extractedFluidType);
        }

        int transferLeft = Math.min(productionSum, consumptionSum);

        List<Integer> fluidProductionDistributed = new ArrayList<>();
        for(int i = 0;i < fluidProduction.size();i++)
            fluidProductionDistributed.add(0);

        int productionLeft = transferLeft;
        int divisor = fluidProduction.size();

        outer:
        while(productionLeft > 0) {
            int productionPerProducer = productionLeft / divisor;
            if(productionPerProducer == 0) {
                divisor = Math.max(1, divisor - 1);
                productionPerProducer = productionLeft / divisor;
            }

            for(int i = 0;i < fluidProductionValues.size();i++) {
                int productionDistributed = fluidProductionDistributed.get(i);
                int productionOfProducerLeft = fluidProductionValues.get(i) - productionDistributed;

                int productionDistributedNew = Math.min(productionPerProducer, Math.min(productionOfProducerLeft, productionLeft));
                fluidProductionDistributed.set(i, productionDistributed + productionDistributedNew);

                productionLeft -= productionDistributedNew;
                if(productionLeft == 0)
                    break outer;
            }
        }

        int realProduction = 0;
        int realProductionAmountMissing = 0;
        for(int i = 0;i < fluidProduction.size();i++) {
            int amount = fluidProductionDistributed.get(i) + realProductionAmountMissing;
            if(amount > 0) {
                FluidResource extract = extractedFluidType;
                int realExtract;
                try(Transaction transaction = Transaction.open(null)) {
                    realExtract = fluidProduction.get(i).extract(extract, amount, transaction);

                    transaction.commit();
                }
                realProduction += realExtract;
                realProductionAmountMissing = amount - realExtract;
            }
        }

        //Retry extraction of all producers if something is missing
        if(realProductionAmountMissing > 0) {
            for(ResourceHandler<FluidResource> producer:fluidProduction) {
                FluidResource extract = extractedFluidType;
                int realExtract;
                try(Transaction transaction = Transaction.open(null)) {
                    realExtract = producer.extract(extract, realProductionAmountMissing, transaction);

                    transaction.commit();
                }

                realProduction += realExtract;
                realProductionAmountMissing -= realExtract;

                if(realProductionAmountMissing == 0)
                    break;
            }
        }

        List<Integer> fluidConsumptionDistributed = new ArrayList<>();
        for(int i = 0;i < fluidConsumption.size();i++)
            fluidConsumptionDistributed.add(0);

        int consumptionLeft = realProduction;
        divisor = fluidConsumption.size();
        outer:
        while(consumptionLeft > 0) {
            int consumptionPerConsumer = consumptionLeft / divisor;
            if(consumptionPerConsumer == 0) {
                divisor = Math.max(1, divisor - 1);
                consumptionPerConsumer = consumptionLeft / divisor;
            }

            for(int i = 0;i < fluidConsumptionValues.size();i++) {
                int consumptionDistributed = fluidConsumptionDistributed.get(i);
                int consumptionOfConsumerLeft = fluidConsumptionValues.get(i) - consumptionDistributed;

                int consumptionDistributedNew = Math.min(consumptionOfConsumerLeft, Math.min(consumptionPerConsumer, consumptionLeft));
                fluidConsumptionDistributed.set(i, consumptionDistributed + consumptionDistributedNew);
                consumptionLeft -= consumptionDistributedNew;
                if(consumptionLeft == 0)
                    break outer;
            }
        }

        int realConsumptionAmountMissing = 0;
        for(int i = 0;i < fluidConsumption.size();i++) {
            int amount = fluidConsumptionDistributed.get(i) + realConsumptionAmountMissing;
            if(amount > 0) {
                FluidResource insert = extractedFluidType;
                int realInsert;
                try(Transaction transaction = Transaction.open(null)) {
                    realInsert = fluidConsumption.get(i).insert(insert, amount, transaction);

                    transaction.commit();
                }

                realConsumptionAmountMissing = amount - realInsert;
            }
        }

        //Retry insertion to all consumers if something is missing
        if(realConsumptionAmountMissing > 0) {
            for(ResourceHandler<FluidResource> consumer:fluidConsumption) {
                FluidResource insert = extractedFluidType;
                int realInsert;
                try(Transaction transaction = Transaction.open(null)) {
                    realInsert = consumer.insert(insert, realConsumptionAmountMissing, transaction);

                    transaction.commit();
                }

                realConsumptionAmountMissing -= realInsert;

                if(realConsumptionAmountMissing == 0)
                    break;
            }
        }
    }

    public @Nullable ResourceHandler<FluidResource> getFluidHandlerCapability(@Nullable Direction side) {
        return fluidStorage;
    }
}
