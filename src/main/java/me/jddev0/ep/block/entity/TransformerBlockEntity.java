package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.TransformerBlock;
import me.jddev0.ep.block.entity.base.EnergyStorageBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.energy.ReceiveAndExtractEnergyStorage;
import me.jddev0.ep.energy.ReceiveExtractEnergyHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.capabilities.Capability;
import net.neoforged.neoforge.common.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.LazyOptional;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

public class TransformerBlockEntity extends EnergyStorageBlockEntity<ReceiveAndExtractEnergyStorage> {
    private final TransformerBlock.Tier tier;
    private final TransformerBlock.Type type;

    private final LazyOptional<IEnergyStorage> lazyEnergyStorage;
    private final LazyOptional<IEnergyStorage> lazyEnergyStorageSidedReceive;
    private final LazyOptional<IEnergyStorage> lazyEnergyStorageSidedExtract;

    public static BlockEntityType<TransformerBlockEntity> getEntityTypeFromTierAndType(TransformerBlock.Tier tier,
                                                                                       TransformerBlock.Type type) {
        return switch(tier) {
            case TIER_LV -> switch(type) {
                case TYPE_1_TO_N -> EPBlockEntities.LV_TRANSFORMER_1_TO_N_ENTITY.get();
                case TYPE_3_TO_3 -> EPBlockEntities.LV_TRANSFORMER_3_TO_3_ENTITY.get();
                case TYPE_N_TO_1 -> EPBlockEntities.LV_TRANSFORMER_N_TO_1_ENTITY.get();
            };
            case TIER_MV -> switch(type) {
                case TYPE_1_TO_N -> EPBlockEntities.MV_TRANSFORMER_1_TO_N_ENTITY.get();
                case TYPE_3_TO_3 -> EPBlockEntities.MV_TRANSFORMER_3_TO_3_ENTITY.get();
                case TYPE_N_TO_1 -> EPBlockEntities.MV_TRANSFORMER_N_TO_1_ENTITY.get();
            };
            case TIER_HV -> switch(type) {
                case TYPE_1_TO_N -> EPBlockEntities.HV_TRANSFORMER_1_TO_N_ENTITY.get();
                case TYPE_3_TO_3 -> EPBlockEntities.HV_TRANSFORMER_3_TO_3_ENTITY.get();
                case TYPE_N_TO_1 -> EPBlockEntities.HV_TRANSFORMER_N_TO_1_ENTITY.get();
            };
            case TIER_EHV -> switch(type) {
                case TYPE_1_TO_N -> EPBlockEntities.EHV_TRANSFORMER_1_TO_N_ENTITY.get();
                case TYPE_3_TO_3 -> EPBlockEntities.EHV_TRANSFORMER_3_TO_3_ENTITY.get();
                case TYPE_N_TO_1 -> EPBlockEntities.EHV_TRANSFORMER_N_TO_1_ENTITY.get();
            };
        };
    }

    public static int getMaxEnergyTransferFromTier(TransformerBlock.Tier tier) {
        return switch(tier) {
            case TIER_LV -> ModConfigs.COMMON_LV_TRANSFORMERS_TRANSFER_RATE.getValue();
            case TIER_MV -> ModConfigs.COMMON_MV_TRANSFORMERS_TRANSFER_RATE.getValue();
            case TIER_HV -> ModConfigs.COMMON_HV_TRANSFORMERS_TRANSFER_RATE.getValue();
            case TIER_EHV -> ModConfigs.COMMON_EHV_TRANSFORMERS_TRANSFER_RATE.getValue();
        };
    }

    public TransformerBlockEntity(BlockPos blockPos, BlockState blockState, TransformerBlock.Tier tier, TransformerBlock.Type type) {
        super(
                getEntityTypeFromTierAndType(tier, type), blockPos, blockState,

                getMaxEnergyTransferFromTier(tier),
                getMaxEnergyTransferFromTier(tier)
        );

        this.tier = tier;
        this.type = type;

        lazyEnergyStorageSidedReceive = LazyOptional.of(
                () -> new ReceiveExtractEnergyHandler(energyStorage, (maxReceive, simulate) -> true, (maxExtract, simulate) -> false)
        );

        lazyEnergyStorageSidedExtract = LazyOptional.of(
                () -> new ReceiveExtractEnergyHandler(energyStorage, (maxReceive, simulate) -> false, (maxExtract, simulate) -> true)
        );

        lazyEnergyStorage = LazyOptional.of(() -> energyStorage);
    }

    @Override
    protected ReceiveAndExtractEnergyStorage initEnergyStorage() {
        return new ReceiveAndExtractEnergyStorage(0, baseEnergyCapacity, baseEnergyTransferRate) {
            @Override
            protected void onChange() {
                setChanged();
                syncEnergyToPlayers(32);
            }
        };
    }

    public TransformerBlock.Type getTransformerType() {
        return type;
    }

    public TransformerBlock.Tier getTier() {
        return tier;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == Capabilities.ENERGY) {
            if(side == null)
                return lazyEnergyStorage.cast();

            Direction facing = getBlockState().getValue(TransformerBlock.FACING);

            switch(type) {
                case TYPE_1_TO_N, TYPE_N_TO_1 -> {
                    LazyOptional<IEnergyStorage> singleSide = type == TransformerBlock.Type.TYPE_1_TO_N?
                            lazyEnergyStorageSidedReceive:lazyEnergyStorageSidedExtract;

                    LazyOptional<IEnergyStorage> multipleSide = type == TransformerBlock.Type.TYPE_1_TO_N?
                            lazyEnergyStorageSidedExtract:lazyEnergyStorageSidedReceive;

                    if(facing == side)
                        return singleSide.cast();

                    return multipleSide.cast();
                }
                case TYPE_3_TO_3 -> {
                   if(facing.getCounterClockWise(Direction.Axis.X) == side || facing.getCounterClockWise(Direction.Axis.Y) == side
                           || facing.getCounterClockWise(Direction.Axis.Z) == side)
                       return lazyEnergyStorageSidedReceive.cast();
                   else
                       return lazyEnergyStorageSidedExtract.cast();
                }
            }
        }

        return super.getCapability(cap, side);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, TransformerBlockEntity blockEntity) {
        if(level.isClientSide)
            return;

        transferEnergy(level, blockPos, state, blockEntity);
    }

    private static void transferEnergy(Level level, BlockPos blockPos, BlockState state, TransformerBlockEntity blockEntity) {
        if(level.isClientSide)
            return;

        List<Direction> outputDirections = new LinkedList<>();
        Direction facing = state.getValue(TransformerBlock.FACING);
        for(Direction side:Direction.values()) {
            switch(blockEntity.getTransformerType()) {
                case TYPE_1_TO_N, TYPE_N_TO_1 -> {
                    boolean isOutputSingleSide = blockEntity.getTransformerType() != TransformerBlock.Type.TYPE_1_TO_N;
                    boolean isOutputMultipleSide = blockEntity.getTransformerType() == TransformerBlock.Type.TYPE_1_TO_N;

                    if(facing == side) {
                        if(isOutputSingleSide)
                            outputDirections.add(side);
                    }else {
                        if(isOutputMultipleSide)
                            outputDirections.add(side);
                    }
                }
                case TYPE_3_TO_3 -> {
                    if(!(facing.getCounterClockWise(Direction.Axis.X) == side || facing.getCounterClockWise(Direction.Axis.Y) == side
                            || facing.getCounterClockWise(Direction.Axis.Z) == side))
                        outputDirections.add(side);
                }
            }
        }

        List<IEnergyStorage> consumerItems = new LinkedList<>();
        List<Integer> consumerEnergyValues = new LinkedList<>();
        int consumptionSum = 0;
        for(Direction direction:outputDirections) {
            BlockPos testPos = blockPos.relative(direction);

            BlockEntity testBlockEntity = level.getBlockEntity(testPos);
            if(testBlockEntity == null)
                continue;

            LazyOptional<IEnergyStorage> energyStorageLazyOptional = testBlockEntity.getCapability(Capabilities.ENERGY, direction.getOpposite());
            if(!energyStorageLazyOptional.isPresent())
                continue;

            IEnergyStorage energyStorage = energyStorageLazyOptional.orElse(null);
            if(!energyStorage.canReceive())
                continue;

            int received = energyStorage.receiveEnergy(Math.min(blockEntity.energyStorage.getMaxTransfer(),
                    blockEntity.energyStorage.getEnergy()), true);
            if(received <= 0)
                continue;

            consumptionSum += received;
            consumerItems.add(energyStorage);
            consumerEnergyValues.add(received);
        }

        List<Integer> consumerEnergyDistributed = new LinkedList<>();
        for(int i = 0;i < consumerItems.size();i++)
            consumerEnergyDistributed.add(0);

        int consumptionLeft = Math.min(blockEntity.energyStorage.getMaxTransfer(),
                Math.min(blockEntity.energyStorage.getEnergy(), consumptionSum));
        blockEntity.energyStorage.extractEnergy(consumptionLeft, false);

        int divisor = consumerItems.size();
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

        for(int i = 0;i < consumerItems.size();i++) {
            int energy = consumerEnergyDistributed.get(i);
            if(energy > 0)
                consumerItems.get(i).receiveEnergy(energy, false);
        }
    }
}