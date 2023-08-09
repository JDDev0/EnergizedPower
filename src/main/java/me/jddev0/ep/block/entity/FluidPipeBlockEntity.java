package me.jddev0.ep.block.entity;

import com.mojang.datafixers.util.Pair;
import me.jddev0.ep.block.FluidPipeBlock;
import me.jddev0.ep.block.ModBlockStateProperties;
import me.jddev0.ep.util.FluidUtils;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import me.jddev0.ep.config.ModConfigs;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class FluidPipeBlockEntity extends BlockEntity {
    public static long MAX_TRANSFER = FluidUtils.convertMilliBucketsToDroplets(
            ModConfigs.COMMON_FLUID_PIPE_FLUID_TRANSFER_RATE.getValue());

    final Storage<FluidVariant> fluidStorage;

    private final Map<Pair<BlockPos, Direction>, Storage<FluidVariant>> producers = new HashMap<>();
    private final Map<Pair<BlockPos, Direction>, Storage<FluidVariant>> consumers = new HashMap<>();
    private final List<BlockPos> pipeBlocks = new LinkedList<>();

    public FluidPipeBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.FLUID_PIPE_ENTITY, blockPos, blockState);

        fluidStorage = Storage.empty();
    }

    public Map<Pair<BlockPos, Direction>, Storage<FluidVariant>> getProducers() {
        return producers;
    }

    public Map<Pair<BlockPos, Direction>, Storage<FluidVariant>> getConsumers() {
        return consumers;
    }

    public List<BlockPos> getPipeBlocks() {
        return pipeBlocks;
    }

    public static void updateConnections(World level, BlockPos blockPos, BlockState state, FluidPipeBlockEntity blockEntity) {
        if(level.isClient())
            return;

        blockEntity.producers.clear();
        blockEntity.consumers.clear();
        blockEntity.pipeBlocks.clear();

        for(Direction direction:Direction.values()) {
            BlockPos testPos = blockPos.offset(direction);

            BlockEntity testBlockEntity = level.getBlockEntity(testPos);
            if(testBlockEntity == null)
                continue;

            if(testBlockEntity instanceof FluidPipeBlockEntity) {
                blockEntity.pipeBlocks.add(testPos);

                continue;
            }

            Storage<FluidVariant> fluidStorage = FluidStorage.SIDED.find(level, testPos, direction.getOpposite());
            if(fluidStorage == null)
                continue;

            //If first has no next, no tanks are present
            if(!fluidStorage.iterator().hasNext())
                continue;

            ModBlockStateProperties.PipeConnection pipeConnection = state.get(FluidPipeBlock.getPipeConnectionPropertyFromDirection(direction));
            if(pipeConnection.isExtract())
                blockEntity.producers.put(Pair.of(testPos, direction.getOpposite()), fluidStorage);
            else if(pipeConnection.isInsert())
                blockEntity.consumers.put(Pair.of(testPos, direction.getOpposite()), fluidStorage);
        }
    }

    public static List<Storage<FluidVariant>> getConnectedConsumers(World level, BlockPos blockPos, List<BlockPos> checkedPipes) {
        List<Storage<FluidVariant>> consumers = new LinkedList<>();

        LinkedList<BlockPos> pipeBlocksLeft = new LinkedList<>();
        pipeBlocksLeft.add(blockPos);

        while(pipeBlocksLeft.size() > 0) {
            BlockPos checkPos = pipeBlocksLeft.pop();

            BlockEntity blockEntity = level.getBlockEntity(checkPos);
            if(!(blockEntity instanceof FluidPipeBlockEntity fluidPipeBlockEntity))
                continue;

            fluidPipeBlockEntity.getPipeBlocks().forEach(pos -> {
                if(!checkedPipes.contains(pos)) {
                    checkedPipes.add(pos);

                    pipeBlocksLeft.add(pos);
                }
            });

            consumers.addAll(fluidPipeBlockEntity.getConsumers().values());
        }

        return consumers;
    }

    public static void tick(World level, BlockPos blockPos, BlockState state, FluidPipeBlockEntity blockEntity) {
        if(level.isClient())
            return;

        //TODO improve do not update all the time
        updateConnections(level, blockPos, state, blockEntity);

        List<Storage<FluidVariant>> consumers = null;

        FluidVariant extractedFluidType;

        List<Storage<FluidVariant>> fluidProduction;
        List<Long> fluidProductionValues;
        long productionSum;

        List<Storage<FluidVariant>> fluidConsumption;
        List<Long> fluidConsumptionValues;
        long consumptionSum;


        //List of all fluid types which where already checked
        List<FluidVariant> alreadyCheckedFluidTypes = new LinkedList<>();

        //Try all fluid types but stop after the first fluid type which can be inserted somewhere
        while(true) {
            fluidProduction = new LinkedList<>();

            extractedFluidType = FluidVariant.blank();

            fluidProductionValues = new LinkedList<>();

            productionSum = 0;
            for(Storage<FluidVariant> fluidStorage:blockEntity.producers.values()) {
                boolean extractedAnything = false;

                int fluidProductionValuesIndex = -1;

                tankLoop:
                for(StorageView<FluidVariant> fluidView:fluidStorage) {
                    FluidVariant fluidVariantInTank = fluidView.getResource();
                    if(fluidVariantInTank.isBlank())
                        continue;

                    boolean wasExtractedFluidTypeEmpty = extractedFluidType.isBlank();
                    if(wasExtractedFluidTypeEmpty) {
                        for(FluidVariant alreadyCheckedFluidType:alreadyCheckedFluidTypes)
                            if(alreadyCheckedFluidType.equals(fluidVariantInTank))
                                continue tankLoop;

                        extractedFluidType = FluidVariant.of(fluidVariantInTank.getFluid(), fluidVariantInTank.copyNbt());
                    }

                    if(!fluidVariantInTank.equals(extractedFluidType))
                        continue;

                    long extracted;
                    try(Transaction transaction = Transaction.openOuter()) {
                        extracted = fluidStorage.extract(extractedFluidType, MAX_TRANSFER, transaction);
                    }

                    if(extracted <= 0) {
                        if(wasExtractedFluidTypeEmpty)
                            extractedFluidType = FluidVariant.blank();

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

            if(productionSum <= 0 || extractedFluidType.isBlank())
                return;

            fluidConsumption = new LinkedList<>();

            fluidConsumptionValues = new LinkedList<>();

            consumptionSum = 0;

            //Check consumers only if something was extracted
            if(consumers == null)
                consumers = getConnectedConsumers(level, blockPos, new LinkedList<>());

            for(Storage<FluidVariant> fluidStorage:consumers) {
                boolean receivedAnything = false;

                int fluidConsumptionValuesIndex = -1;

                for(StorageView<FluidVariant> fluidView:fluidStorage) {
                    long received;
                    try(Transaction transaction = Transaction.openOuter()) {
                        received = fluidStorage.insert(extractedFluidType, MAX_TRANSFER, transaction);
                    }
                    if(received <= 0)
                        continue;

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

            //If something was consumed -> continue after while(true)
            if(consumptionSum > 0)
                break;

            alreadyCheckedFluidTypes.add(extractedFluidType);
        }

        long transferLeft = Math.min(productionSum, consumptionSum);

        List<Long> fluidProductionDistributed = new LinkedList<>();
        for(int i = 0;i < fluidProduction.size();i++)
            fluidProductionDistributed.add(0L);

        long productionLeft = transferLeft;
        int divisor = fluidProduction.size();

        outer:
        while(productionLeft > 0) {
            long productionPerProducer = productionLeft / divisor;
            if(productionPerProducer == 0) {
                divisor = Math.max(1, divisor - 1);
                productionPerProducer = productionLeft / divisor;
            }

            for(int i = 0;i < fluidProductionValues.size();i++) {
                long productionDistributed = fluidProductionDistributed.get(i);
                long productionOfProducerLeft = fluidProductionValues.get(i) - productionDistributed;

                long productionDistributedNew = Math.min(productionPerProducer, Math.min(productionOfProducerLeft, productionLeft));
                fluidProductionDistributed.set(i, productionDistributed + productionDistributedNew);

                productionLeft -= productionDistributedNew;
                if(productionLeft == 0)
                    break outer;
            }
        }

        for(int i = 0;i < fluidProduction.size();i++) {
            long amount = fluidProductionDistributed.get(i);
            if(amount > 0) {
                FluidVariant extract = FluidVariant.of(extractedFluidType.getFluid(), extractedFluidType.copyNbt());
                try(Transaction transaction = Transaction.openOuter()) {
                    fluidProduction.get(i).extract(extract, amount, transaction);

                    transaction.commit();
                }
            }
        }

        List<Long> fluidConsumptionDistributed = new LinkedList<>();
        for(int i = 0;i < fluidConsumption.size();i++)
            fluidConsumptionDistributed.add(0L);

        long consumptionLeft = transferLeft;
        divisor = fluidConsumption.size();
        outer:
        while(consumptionLeft > 0) {
            long consumptionPerConsumer = consumptionLeft / divisor;
            if(consumptionPerConsumer == 0) {
                divisor = Math.max(1, divisor - 1);
                consumptionPerConsumer = consumptionLeft / divisor;
            }

            for(int i = 0;i < fluidConsumptionValues.size();i++) {
                long consumptionDistributed = fluidConsumptionDistributed.get(i);
                long consumptionOfConsumerLeft = fluidConsumptionValues.get(i) - consumptionDistributed;

                long consumptionDistributedNew = Math.min(consumptionOfConsumerLeft, Math.min(consumptionPerConsumer, consumptionLeft));
                fluidConsumptionDistributed.set(i, consumptionDistributed + consumptionDistributedNew);
                consumptionLeft -= consumptionDistributedNew;
                if(consumptionLeft == 0)
                    break outer;
            }
        }

        for(int i = 0;i < fluidConsumption.size();i++) {
            long amount = fluidConsumptionDistributed.get(i);
            if(amount > 0) {
                FluidVariant insert = FluidVariant.of(extractedFluidType.getFluid(), extractedFluidType.copyNbt());
                try(Transaction transaction = Transaction.openOuter()) {
                    fluidConsumption.get(i).insert(insert, amount, transaction);

                    transaction.commit();
                }
            }
        }
    }
}
