package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.TransformerBlock;
import me.jddev0.ep.block.entity.base.ConfigurableEnergyStorageBlockEntity;
import me.jddev0.ep.energy.ReceiveAndExtractEnergyStorage;
import me.jddev0.ep.energy.ReceiveExtractEnergyHandler;
import me.jddev0.ep.inventory.CombinedContainerData;
import me.jddev0.ep.inventory.data.RedstoneModeValueContainerData;
import me.jddev0.ep.machine.tier.TransformerTier;
import me.jddev0.ep.machine.tier.TransformerType;
import me.jddev0.ep.screen.TransformerMenu;
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
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

public class TransformerBlockEntity extends ConfigurableEnergyStorageBlockEntity<ReceiveAndExtractEnergyStorage> {
    private final TransformerTier tier;
    private final TransformerType type;
    private final LazyOptional<IEnergyStorage> lazyEnergyStorageSidedReceive;
    private final LazyOptional<IEnergyStorage> lazyEnergyStorageSidedExtract;

    public TransformerBlockEntity(BlockPos blockPos, BlockState blockState, TransformerTier tier, TransformerType type) {
        super(
                tier.getEntityTypeFromTierAndType(type), blockPos, blockState,

                tier.getMachineNameFromTierAndType(type),

                tier.getMaxEnergyTransferFromTier(),
                tier.getMaxEnergyTransferFromTier()
        );

        this.tier = tier;
        this.type = type;

        lazyEnergyStorageSidedReceive = LazyOptional.of(
                () -> new ReceiveExtractEnergyHandler(energyStorage, (maxReceive, simulate) -> true, (maxExtract, simulate) -> false)
        );

        lazyEnergyStorageSidedExtract = LazyOptional.of(
                () -> new ReceiveExtractEnergyHandler(energyStorage, (maxReceive, simulate) -> false, (maxExtract, simulate) -> true)
        );
    }

    @Override
    protected ReceiveAndExtractEnergyStorage initEnergyStorage() {
        return new ReceiveAndExtractEnergyStorage(0, baseEnergyCapacity, baseEnergyTransferRate) {
            @Override
            protected void onChange() {
                setChanged();
                syncEnergyToPlayers(32);
            }

            @Override
            public int extractEnergy(int maxExtract, boolean simulate) {
                if(level != null && !redstoneMode.isActive(level.getBlockState(getBlockPos()).getValue(BlockStateProperties.POWERED))) {
                    //This will make the output "disconnected"
                    return 0;
                }

                return super.extractEnergy(maxExtract, simulate);
            }
        };
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

        return new TransformerMenu(id, inventory, this, this.data);
    }

    public TransformerType getTransformerType() {
        return type;
    }

    public TransformerTier getTier() {
        return tier;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.ENERGY) {
            if(side == null)
                return lazyEnergyStorage.cast();

            Direction facing = getBlockState().getValue(TransformerBlock.FACING);

            switch(type) {
                case TYPE_1_TO_N, TYPE_N_TO_1 -> {
                    LazyOptional<IEnergyStorage> singleSide = type == TransformerType.TYPE_1_TO_N?
                            lazyEnergyStorageSidedReceive:lazyEnergyStorageSidedExtract;

                    LazyOptional<IEnergyStorage> multipleSide = type == TransformerType.TYPE_1_TO_N?
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

        if(!blockEntity.redstoneMode.isActive(state.getValue(BlockStateProperties.POWERED)))
            return; //This will make the output "disconnected"

        List<Direction> outputDirections = new LinkedList<>();
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

        List<IEnergyStorage> consumerItems = new LinkedList<>();
        List<Integer> consumerEnergyValues = new LinkedList<>();
        int consumptionSum = 0;
        for(Direction direction:outputDirections) {
            BlockPos testPos = blockPos.relative(direction);

            BlockEntity testBlockEntity = level.getBlockEntity(testPos);
            if(testBlockEntity == null)
                continue;

            LazyOptional<IEnergyStorage> energyStorageLazyOptional = testBlockEntity.getCapability(ForgeCapabilities.ENERGY, direction.getOpposite());
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