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
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class FluidPipeBlockEntity extends BlockEntity {
    private final FluidPipeTier tier;

    private final int maxTransfer;

    private final IFluidHandler fluidStorage;

    private final Map<Pair<BlockPos, Direction>, IFluidHandler> producers = new HashMap<>();
    private final Map<Pair<BlockPos, Direction>, IFluidHandler> consumers = new HashMap<>();
    private final List<BlockPos> pipeBlocks = new LinkedList<>();

    public FluidPipeBlockEntity(BlockPos blockPos, BlockState blockState, FluidPipeTier tier) {
        super(tier.getEntityTypeFromTier(), blockPos, blockState);

        this.tier = tier;

        maxTransfer = tier.getTransferRate();

        fluidStorage = new IFluidHandler() {
            @Override
            public int getTanks() {
                return 0;
            }

            @Override
            public @NotNull FluidStack getFluidInTank(int tank) {
                return FluidStack.EMPTY;
            }

            @Override
            public int getTankCapacity(int tank) {
                return 0;
            }

            @Override
            public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
                return false;
            }

            @Override
            public int fill(FluidStack resource, FluidAction action) {
                return 0;
            }

            @Override
            public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
                return FluidStack.EMPTY;
            }

            @Override
            public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
                return FluidStack.EMPTY;
            }
        };
    }

    public FluidPipeTier getTier() {
        return tier;
    }

    public Map<Pair<BlockPos, Direction>, IFluidHandler> getProducers() {
        return producers;
    }

    public Map<Pair<BlockPos, Direction>, IFluidHandler> getConsumers() {
        return consumers;
    }

    public List<BlockPos> getPipeBlocks() {
        return pipeBlocks;
    }

    public static void updateConnections(Level level, BlockPos blockPos, BlockState state, FluidPipeBlockEntity blockEntity) {
        if(level.isClientSide)
            return;

        blockEntity.producers.clear();
        blockEntity.consumers.clear();
        blockEntity.pipeBlocks.clear();

        for(Direction direction:Direction.values()) {
            BlockPos testPos = blockPos.relative(direction);

            BlockEntity testBlockEntity = level.getBlockEntity(testPos);

            if(testBlockEntity instanceof FluidPipeBlockEntity fluidPipeBlockEntity) {
                if(fluidPipeBlockEntity.getTier() != blockEntity.getTier()) //Do not connect to different cable tiers
                    continue;

                blockEntity.pipeBlocks.add(testPos);

                continue;
            }

            IFluidHandler fluidStorage = level.getCapability(Capabilities.FluidHandler.BLOCK, testPos,
                    level.getBlockState(testPos), testBlockEntity, direction.getOpposite());
            if(fluidStorage == null || fluidStorage.getTanks() == 0)
                continue;

            EPBlockStateProperties.PipeConnection pipeConnection = state.getValue(FluidPipeBlock.getPipeConnectionPropertyFromDirection(direction));
            if(pipeConnection.isExtract())
                blockEntity.producers.put(Pair.of(testPos, direction.getOpposite()), fluidStorage);
            else if(pipeConnection.isInsert())
                blockEntity.consumers.put(Pair.of(testPos, direction.getOpposite()), fluidStorage);
        }
    }

    public static List<IFluidHandler> getConnectedConsumers(Level level, BlockPos blockPos, List<BlockPos> checkedPipes) {
        List<IFluidHandler> consumers = new LinkedList<>();

        LinkedList<BlockPos> pipeBlocksLeft = new LinkedList<>();
        pipeBlocksLeft.add(blockPos);

        checkedPipes.add(blockPos);

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

    public static void tick(Level level, BlockPos blockPos, BlockState state, FluidPipeBlockEntity blockEntity) {
        if(level.isClientSide)
            return;

        //TODO improve do not update all the time
        updateConnections(level, blockPos, state, blockEntity);

        List<IFluidHandler> consumers = null;

        FluidStack extractedFluidType;

        List<IFluidHandler> fluidProduction;
        List<Integer> fluidProductionValues;
        int productionSum;

        List<IFluidHandler> fluidConsumption;
        List<Integer> fluidConsumptionValues;
        int consumptionSum;


        //List of all fluid types which where already checked
        List<FluidStack> alreadyCheckedFluidTypes = new LinkedList<>();

        //Try all fluid types but stop after the first fluid type which can be inserted somewhere
        while(true) {
            fluidProduction = new LinkedList<>();

            extractedFluidType = FluidStack.EMPTY;

            fluidProductionValues = new LinkedList<>();

            productionSum = 0;
            for(IFluidHandler fluidStorage:blockEntity.producers.values()) {
                boolean extractedAnything = false;

                int fluidProductionValuesIndex = -1;

                tankLoop:
                for(int i = 0;i < fluidStorage.getTanks();i++) {
                    FluidStack fluidStackInTank = fluidStorage.getFluidInTank(i);
                    if(fluidStackInTank.isEmpty())
                        continue;

                    boolean wasExtractedFluidTypeEmpty = extractedFluidType.isEmpty();
                    if(wasExtractedFluidTypeEmpty) {
                        for(FluidStack alreadyCheckedFluidType:alreadyCheckedFluidTypes)
                            if(FluidStack.isSameFluidSameComponents(alreadyCheckedFluidType, fluidStackInTank))
                                continue tankLoop;

                        extractedFluidType = fluidStackInTank.copy();
                        extractedFluidType.setAmount(blockEntity.maxTransfer);
                    }

                    if(!FluidStack.isSameFluidSameComponents(fluidStackInTank, extractedFluidType))
                        continue;

                    FluidStack extracted = fluidStorage.drain(extractedFluidType.copy(), IFluidHandler.FluidAction.SIMULATE);
                    if(extracted.getAmount() <= 0 || extracted.isEmpty() ||
                            !FluidStack.isSameFluidSameComponents(extracted, extractedFluidType)) {
                        if(wasExtractedFluidTypeEmpty)
                            extractedFluidType = FluidStack.EMPTY;

                        continue;
                    }

                    extractedAnything = true;

                    if(fluidProductionValuesIndex == -1) {
                        fluidProductionValuesIndex = fluidProductionValues.size();
                        fluidProductionValues.add(extracted.getAmount());
                    }else {
                        fluidProductionValues.set(fluidProductionValuesIndex,
                                fluidProductionValues.get(fluidProductionValuesIndex) + extracted.getAmount());
                    }

                    productionSum += extracted.getAmount();
                }

                if(extractedAnything)
                    fluidProduction.add(fluidStorage);
            }

            if(productionSum <= 0 || extractedFluidType.isEmpty())
                return;

            fluidConsumption = new LinkedList<>();

            fluidConsumptionValues = new LinkedList<>();

            consumptionSum = 0;

            //Check consumers only if something was extracted
            if(consumers == null)
                consumers = getConnectedConsumers(level, blockPos, new LinkedList<>());

            //Set fluid amount to at most production sum (Fixes bug where 900 mB Lava would be tried to be inserted to a cauldron and vanishes)
            extractedFluidType.setAmount(Math.min(blockEntity.maxTransfer, productionSum));

            for(IFluidHandler fluidStorage:consumers) {
                boolean receivedAnything = false;

                int fluidConsumptionValuesIndex = -1;

                for(int i = 0;i < fluidStorage.getTanks();i++) {
                    FluidStack extractedFluidTypeTmp = extractedFluidType.copy();
                    int received = fluidStorage.fill(extractedFluidTypeTmp, IFluidHandler.FluidAction.SIMULATE);
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

            //If everything was consumed -> continue after while(true)
            if(consumptionSum > 0)
                break;

            alreadyCheckedFluidTypes.add(extractedFluidType);
        }

        int transferLeft = Math.min(productionSum, consumptionSum);

        List<Integer> fluidProductionDistributed = new LinkedList<>();
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
                FluidStack extract = extractedFluidType.copy();
                extract.setAmount(amount);

                FluidStack realExtract = fluidProduction.get(i).drain(extract, IFluidHandler.FluidAction.EXECUTE);
                realProduction += realExtract.getAmount();
                realProductionAmountMissing = amount - realExtract.getAmount();
            }
        }

        //Retry extraction of all producers if something is missing
        if(realProductionAmountMissing > 0) {
            for(IFluidHandler producer:fluidProduction) {
                FluidStack extract = extractedFluidType.copy();
                extract.setAmount(realProductionAmountMissing);

                FluidStack realExtract = producer.drain(extract, IFluidHandler.FluidAction.EXECUTE);
                realProduction += realExtract.getAmount();
                realProductionAmountMissing -= realExtract.getAmount();

                if(realProductionAmountMissing == 0)
                    break;
            }
        }

        List<Integer> fluidConsumptionDistributed = new LinkedList<>();
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
                FluidStack insert = extractedFluidType.copy();
                insert.setAmount(amount);

                int realInsert = fluidConsumption.get(i).fill(insert, IFluidHandler.FluidAction.EXECUTE);
                realConsumptionAmountMissing = amount - realInsert;
            }
        }

        //Retry insertion to all consumers if something is missing
        if(realConsumptionAmountMissing > 0) {
            for(IFluidHandler consumer:fluidConsumption) {
                FluidStack insert = extractedFluidType.copy();
                insert.setAmount(realConsumptionAmountMissing);

                int realInsert = consumer.fill(insert, IFluidHandler.FluidAction.EXECUTE);
                realConsumptionAmountMissing -= realInsert;

                if(realConsumptionAmountMissing == 0)
                    break;
            }
        }
    }

    public @Nullable IFluidHandler getFluidHandlerCapability(@Nullable Direction side) {
        return fluidStorage;
    }
}
