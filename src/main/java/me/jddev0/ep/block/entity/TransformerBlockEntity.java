package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.ConfigurableTransformerBlock;
import me.jddev0.ep.block.EPBlockStateProperties;
import me.jddev0.ep.block.TransformerBlock;
import me.jddev0.ep.block.entity.base.ConfigurableEnergyStorageBlockEntity;
import me.jddev0.ep.inventory.CombinedContainerData;
import me.jddev0.ep.inventory.data.RedstoneModeValueContainerData;
import me.jddev0.ep.machine.tier.TransformerTier;
import me.jddev0.ep.machine.tier.TransformerType;
import me.jddev0.ep.screen.TransformerMenu;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;
import me.jddev0.ep.energy.EnergizedPowerEnergyStorage;
import me.jddev0.ep.energy.EnergizedPowerLimitingEnergyStorage;

import java.util.ArrayList;
import java.util.List;

public class TransformerBlockEntity extends ConfigurableEnergyStorageBlockEntity<EnergizedPowerEnergyStorage> {
    private final TransformerTier tier;
    private final TransformerType type;

    final EnergizedPowerLimitingEnergyStorage limitingEnergyStorageInsert;
    final EnergizedPowerLimitingEnergyStorage limitingEnergyStorageExtract;

    public TransformerBlockEntity(BlockPos blockPos, BlockState blockState, TransformerTier tier, TransformerType type) {
        super(
                tier.getEntityTypeFromTierAndType(type), blockPos, blockState,

                tier.getMachineNameFromTierAndType(type),

                tier.getMaxEnergyTransferFromTier(),
                tier.getMaxEnergyTransferFromTier()
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
                setChanged();
                syncEnergyToPlayers(32);
            }

            @Override
            public long extract(long maxAmount, TransactionContext transaction) {
                if(level != null && !redstoneMode.isActive(level.getBlockState(getBlockPos()).getValue(BlockStateProperties.POWERED))) {
                    //This will make the output "disconnected"
                    return 0;
                }

                return super.extract(maxAmount, transaction);
            }
        };
    }

    protected EnergizedPowerLimitingEnergyStorage initLimitingEnergyStorage() {
        //limitingEnergyStorage is unused
        return new EnergizedPowerLimitingEnergyStorage(energyStorage, baseEnergyTransferRate, baseEnergyTransferRate);
    }

    @Override
    protected ContainerData initContainerData() {
        return new CombinedContainerData(
                new RedstoneModeValueContainerData(() -> redstoneMode, value -> redstoneMode = value)
        );
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        syncEnergyToPlayer(player);

        return new TransformerMenu(id, this, inventory, this.data);
    }

    public TransformerType getTransformerType() {
        return type;
    }

    public TransformerTier getTier() {
        return tier;
    }

    EnergyStorage getEnergyStorageForDirection(Direction side) {
        if(side == null)
            return energyStorage;

        if(type == TransformerType.CONFIGURABLE) {
            BlockState state = level.getBlockState(worldPosition);

            EPBlockStateProperties.TransformerConnection transformerConnection = state.getValue(ConfigurableTransformerBlock.
                    getTransformerConnectionPropertyFromDirection(side));

            return switch(transformerConnection) {
                case NOT_CONNECTED -> null;
                case RECEIVE -> limitingEnergyStorageInsert;
                case EXTRACT -> limitingEnergyStorageExtract;
            };
        }

        Direction facing = getBlockState().getValue(TransformerBlock.FACING);

        return switch(type) {
            case TYPE_1_TO_N, TYPE_N_TO_1 -> {
                EnergyStorage singleSide = type == TransformerType.TYPE_1_TO_N?limitingEnergyStorageInsert:limitingEnergyStorageExtract;
                EnergyStorage multipleSide = type == TransformerType.TYPE_1_TO_N?limitingEnergyStorageExtract:limitingEnergyStorageInsert;

                if(facing == side)
                    yield singleSide;

                yield multipleSide;
            }
            case TYPE_3_TO_3 -> {
                if(facing.getCounterClockWise(Direction.Axis.X) == side || facing.getCounterClockWise(Direction.Axis.Y) == side
                        || facing.getCounterClockWise(Direction.Axis.Z) == side)
                    yield limitingEnergyStorageInsert;
                else
                    yield limitingEnergyStorageExtract;
            }
            default -> null;
        };
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, TransformerBlockEntity blockEntity) {
        if(level.isClientSide())
            return;

        transferEnergy(level, blockPos, state, blockEntity);
    }

    private static void transferEnergy(Level level, BlockPos blockPos, BlockState state, TransformerBlockEntity blockEntity) {
        if(level.isClientSide())
            return;

        if(!blockEntity.redstoneMode.isActive(state.getValue(BlockStateProperties.POWERED)))
            return; //This will make the output "disconnected"

        List<Direction> outputDirections = new ArrayList<>();
        if(blockEntity.type == TransformerType.CONFIGURABLE) {
            for(Direction side:Direction.values()) {
                EPBlockStateProperties.TransformerConnection transformerConnection = state.getValue(ConfigurableTransformerBlock.
                        getTransformerConnectionPropertyFromDirection(side));
                if(transformerConnection.isExtract()) {
                    outputDirections.add(side);
                }
            }
        }else {
            Direction facing = state.getValue(TransformerBlock.FACING);
            for(Direction side:Direction.values()) {
                switch(blockEntity.getTransformerType()) {
                    case TYPE_1_TO_N, TYPE_N_TO_1 -> {
                        boolean isOutputSingleSide = blockEntity.getTransformerType() != TransformerType.TYPE_1_TO_N;
                        boolean isOutputMultipleSide = blockEntity.getTransformerType() == TransformerType.TYPE_1_TO_N;

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
        }

        List<EnergyStorage> consumerItems = new ArrayList<>();
        List<Long> consumerEnergyValues = new ArrayList<>();
        int consumptionSum = 0;
        for(Direction direction:outputDirections) {
            BlockPos testPos = blockPos.relative(direction);

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

        List<Long> consumerEnergyDistributed = new ArrayList<>();
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