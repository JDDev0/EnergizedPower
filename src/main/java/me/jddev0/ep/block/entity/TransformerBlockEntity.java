package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.TransformerBlock;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.energy.EnergyStoragePacketUpdate;
import me.jddev0.ep.networking.ModMessages;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.base.LimitingEnergyStorage;
import team.reborn.energy.api.base.SimpleEnergyStorage;

import java.util.LinkedList;
import java.util.List;

public class TransformerBlockEntity extends BlockEntity implements EnergyStoragePacketUpdate {
    private final long maxTransferRate;

    private final TransformerBlock.Tier tier;
    private final TransformerBlock.Type type;

    final LimitingEnergyStorage energyStorageInsert;
    final LimitingEnergyStorage energyStorageExtract;
    private final SimpleEnergyStorage internalEnergyStorage;

    public static BlockEntityType<TransformerBlockEntity> getEntityTypeFromTierAndType(TransformerBlock.Tier tier,
                                                                                       TransformerBlock.Type type) {
        return switch(tier) {
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
            case TIER_MV -> ModConfigs.COMMON_MV_TRANSFORMERS_TRANSFER_RATE.getValue();
            case TIER_HV -> ModConfigs.COMMON_HV_TRANSFORMERS_TRANSFER_RATE.getValue();
            case TIER_EHV -> ModConfigs.COMMON_EHV_TRANSFORMERS_TRANSFER_RATE.getValue();
        };
    }

    public TransformerBlockEntity(BlockPos blockPos, BlockState blockState, TransformerBlock.Tier tier, TransformerBlock.Type type) {
        super(getEntityTypeFromTierAndType(tier, type), blockPos, blockState);

        this.tier = tier;
        this.type = type;

        maxTransferRate = getMaxEnergyTransferFromTier(this.tier);

        internalEnergyStorage = new SimpleEnergyStorage(maxTransferRate, maxTransferRate, maxTransferRate) {
            @Override
            protected void onFinalCommit() {
                markDirty();

                if(world != null && !world.isClient()) {
                    PacketByteBuf buffer = PacketByteBufs.create();
                    buffer.writeLong(amount);
                    buffer.writeLong(capacity);
                    buffer.writeBlockPos(getPos());

                    ModMessages.sendServerPacketToPlayersWithinXBlocks(
                            getPos(), (ServerWorld)world, 32,
                            ModMessages.ENERGY_SYNC_ID, buffer
                    );
                }
            }
        };
        energyStorageInsert = new LimitingEnergyStorage(internalEnergyStorage, maxTransferRate, 0);
        energyStorageExtract = new LimitingEnergyStorage(internalEnergyStorage, 0, maxTransferRate);
    }

    public TransformerBlock.Type getTransformerType() {
        return type;
    }

    public TransformerBlock.Tier getTier() {
        return tier;
    }

    EnergyStorage getEnergyStorageForDirection(Direction side) {
        if(side == null)
            return internalEnergyStorage;

        Direction facing = getCachedState().get(TransformerBlock.FACING);

        return switch(type) {
            case TYPE_1_TO_N, TYPE_N_TO_1 -> {
                EnergyStorage singleSide = type == TransformerBlock.Type.TYPE_1_TO_N?energyStorageInsert:energyStorageExtract;
                EnergyStorage multipleSide = type == TransformerBlock.Type.TYPE_1_TO_N?energyStorageExtract:energyStorageInsert;

                if(facing == side)
                    yield singleSide;

                yield multipleSide;
            }
            case TYPE_3_TO_3 -> {
                if(facing.rotateCounterclockwise(Direction.Axis.X) == side || facing.rotateCounterclockwise(Direction.Axis.Y) == side
                        || facing.rotateCounterclockwise(Direction.Axis.Z) == side)
                    yield energyStorageInsert;
                else
                    yield energyStorageExtract;
            }
        };
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putLong("energy", internalEnergyStorage.amount);

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(@NotNull NbtCompound nbt) {
        super.readNbt(nbt);

        internalEnergyStorage.amount = nbt.getLong("energy");
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

            EnergyStorage energyStorage = EnergyStorage.SIDED.find(level, testPos, direction.getOpposite());
            if(energyStorage == null)
                continue;

            if(!energyStorage.supportsInsertion())
                continue;

            try(Transaction transaction = Transaction.openOuter()) {
                long received = energyStorage.insert(Math.min(blockEntity.maxTransferRate, blockEntity.internalEnergyStorage.amount), transaction);

                if(received <= 0)
                    continue;

                consumptionSum += received;
                consumerItems.add(energyStorage);
                consumerEnergyValues.add(received);
            }
        }

        List<Long> consumerEnergyDistributed = new LinkedList<>();
        for(int i = 0;i < consumerItems.size();i++)
            consumerEnergyDistributed.add(0L);

        long consumptionLeft = Math.min(blockEntity.maxTransferRate, Math.min(blockEntity.internalEnergyStorage.amount, consumptionSum));
        try(Transaction transaction = Transaction.openOuter()) {
            blockEntity.internalEnergyStorage.extract(consumptionLeft, transaction);
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

    @Override
    public void setEnergy(long energy) {
        internalEnergyStorage.amount = energy;
    }

    @Override
    public void setCapacity(long capacity) {
        //Does nothing (capacity is final)
    }
}