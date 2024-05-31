package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.TransformerBlock;
import me.jddev0.ep.block.entity.base.EnergyStorageBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import team.reborn.energy.api.EnergyStorage;
import me.jddev0.ep.energy.EnergizedPowerEnergyStorage;
import me.jddev0.ep.energy.EnergizedPowerLimitingEnergyStorage;

import java.util.LinkedList;
import java.util.List;

public class TransformerBlockEntity extends EnergyStorageBlockEntity<EnergizedPowerEnergyStorage> {
    private final TransformerBlock.Tier tier;
    private final TransformerBlock.Type type;

    final EnergizedPowerLimitingEnergyStorage limitingEnergyStorageInsert;
    final EnergizedPowerLimitingEnergyStorage limitingEnergyStorageExtract;

    public static BlockEntityType<TransformerBlockEntity> getEntityTypeFromTierAndType(TransformerBlock.Tier tier,
                                                                                       TransformerBlock.Type type) {
        return switch(tier) {
            case TIER_LV -> switch (type) {
                case TYPE_1_TO_N -> ModBlockEntities.LV_TRANSFORMER_1_TO_N_ENTITY;
                case TYPE_3_TO_3 -> ModBlockEntities.LV_TRANSFORMER_3_TO_3_ENTITY;
                case TYPE_N_TO_1 -> ModBlockEntities.LV_TRANSFORMER_N_TO_1_ENTITY;
            };
            case TIER_MV -> switch (type) {
                case TYPE_1_TO_N -> ModBlockEntities.MV_TRANSFORMER_1_TO_N_ENTITY;
                case TYPE_3_TO_3 -> ModBlockEntities.MV_TRANSFORMER_3_TO_3_ENTITY;
                case TYPE_N_TO_1 -> ModBlockEntities.MV_TRANSFORMER_N_TO_1_ENTITY;
            };
            case TIER_HV -> switch(type) {
                case TYPE_1_TO_N -> ModBlockEntities.HV_TRANSFORMER_1_TO_N_ENTITY;
                case TYPE_3_TO_3 -> ModBlockEntities.HV_TRANSFORMER_3_TO_3_ENTITY;
                case TYPE_N_TO_1 -> ModBlockEntities.HV_TRANSFORMER_N_TO_1_ENTITY;
            };
            case TIER_EHV -> switch(type) {
                case TYPE_1_TO_N -> ModBlockEntities.EHV_TRANSFORMER_1_TO_N_ENTITY;
                case TYPE_3_TO_3 -> ModBlockEntities.EHV_TRANSFORMER_3_TO_3_ENTITY;
                case TYPE_N_TO_1 -> ModBlockEntities.EHV_TRANSFORMER_N_TO_1_ENTITY;
            };
        };
    }

    public static long getMaxEnergyTransferFromTier(TransformerBlock.Tier tier) {
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

        limitingEnergyStorageInsert = new EnergizedPowerLimitingEnergyStorage(energyStorage, baseEnergyTransferRate, 0);

        limitingEnergyStorageExtract = new EnergizedPowerLimitingEnergyStorage(energyStorage, 0, baseEnergyTransferRate);
    }

    @Override
    protected EnergizedPowerEnergyStorage initEnergyStorage() {
        return new EnergizedPowerEnergyStorage(baseEnergyTransferRate, baseEnergyTransferRate, baseEnergyTransferRate) {
            @Override
            protected void onFinalCommit() {
                markDirty();
                syncEnergyToPlayers(32);
            }
        };
    }

    @Override
    protected EnergizedPowerLimitingEnergyStorage initLimitingEnergyStorage() {
        //limitingEnergyStorage is unused
        return new EnergizedPowerLimitingEnergyStorage(energyStorage, baseEnergyTransferRate, baseEnergyTransferRate);
    }

    public TransformerBlock.Type getTransformerType() {
        return type;
    }

    public TransformerBlock.Tier getTier() {
        return tier;
    }

    EnergyStorage getEnergyStorageForDirection(Direction side) {
        if(side == null)
            return energyStorage;

        Direction facing = getCachedState().get(TransformerBlock.FACING);

        return switch(type) {
            case TYPE_1_TO_N, TYPE_N_TO_1 -> {
                EnergyStorage singleSide = type == TransformerBlock.Type.TYPE_1_TO_N?limitingEnergyStorageInsert:limitingEnergyStorageExtract;
                EnergyStorage multipleSide = type == TransformerBlock.Type.TYPE_1_TO_N?limitingEnergyStorageExtract:limitingEnergyStorageInsert;

                if(facing == side)
                    yield singleSide;

                yield multipleSide;
            }
            case TYPE_3_TO_3 -> {
                if(facing.rotateCounterclockwise(Direction.Axis.X) == side || facing.rotateCounterclockwise(Direction.Axis.Y) == side
                        || facing.rotateCounterclockwise(Direction.Axis.Z) == side)
                    yield limitingEnergyStorageInsert;
                else
                    yield limitingEnergyStorageExtract;
            }
        };
    }

    public static void tick(World level, BlockPos blockPos, BlockState state, TransformerBlockEntity blockEntity) {
        if(level.isClient())
            return;

        transferEnergy(level, blockPos, state, blockEntity);
    }

    private static void transferEnergy(World level, BlockPos blockPos, BlockState state, TransformerBlockEntity blockEntity) {
        if(level.isClient())
            return;

        List<Direction> outputDirections = new LinkedList<>();
        Direction facing = state.get(TransformerBlock.FACING);
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
                    if(!(facing.rotateCounterclockwise(Direction.Axis.X) == side || facing.rotateCounterclockwise(Direction.Axis.Y) == side
                            || facing.rotateCounterclockwise(Direction.Axis.Z) == side))
                        outputDirections.add(side);
                }
            }
        }

        List<EnergyStorage> consumerItems = new LinkedList<>();
        List<Long> consumerEnergyValues = new LinkedList<>();
        int consumptionSum = 0;
        for(Direction direction:outputDirections) {
            BlockPos testPos = blockPos.offset(direction);

            BlockEntity testBlockEntity = level.getBlockEntity(testPos);
            if(testBlockEntity == null)
                continue;

            EnergyStorage limitingEnergyStorage = EnergyStorage.SIDED.find(level, testPos, direction.getOpposite());
            if(limitingEnergyStorage == null)
                continue;

            if(!limitingEnergyStorage.supportsInsertion())
                continue;

            try(Transaction transaction = Transaction.openOuter()) {
                long received = limitingEnergyStorage.insert(Math.min(blockEntity.baseEnergyTransferRate, blockEntity.energyStorage.getAmount()), transaction);

                if(received <= 0)
                    continue;

                consumptionSum += received;
                consumerItems.add(limitingEnergyStorage);
                consumerEnergyValues.add(received);
            }
        }

        List<Long> consumerEnergyDistributed = new LinkedList<>();
        for(int i = 0;i < consumerItems.size();i++)
            consumerEnergyDistributed.add(0L);

        long consumptionLeft = Math.min(blockEntity.baseEnergyTransferRate, Math.min(blockEntity.energyStorage.getAmount(), consumptionSum));
        try(Transaction transaction = Transaction.openOuter()) {
            blockEntity.energyStorage.extract(consumptionLeft, transaction);
            transaction.commit();
        }

        int divisor = consumerItems.size();
        outer:
        while(consumptionLeft > 0) {
            long consumptionPerConsumer = consumptionLeft / divisor;
            if(consumptionPerConsumer == 0) {
                divisor = Math.max(1, divisor - 1);
                consumptionPerConsumer = consumptionLeft / divisor;
            }

            for(int i = 0;i < consumerEnergyValues.size();i++) {
                long consumptionDistributed = consumerEnergyDistributed.get(i);
                long consumptionOfConsumerLeft = consumerEnergyValues.get(i) - consumptionDistributed;

                long consumptionDistributedNew = Math.min(consumptionOfConsumerLeft, Math.min(consumptionPerConsumer, consumptionLeft));
                consumerEnergyDistributed.set(i, consumptionDistributed + consumptionDistributedNew);
                consumptionLeft -= consumptionDistributedNew;
                if(consumptionLeft == 0)
                    break outer;
            }
        }

        for(int i = 0;i < consumerItems.size();i++) {
            long energy = consumerEnergyDistributed.get(i);
            if(energy > 0) {
                try(Transaction transaction = Transaction.openOuter()) {
                    consumerItems.get(i).insert(energy, transaction);
                    transaction.commit();
                }
            }
        }
    }
}