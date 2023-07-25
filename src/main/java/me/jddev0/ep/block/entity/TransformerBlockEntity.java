package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.TransformerBlock;
import me.jddev0.ep.energy.EnergyStoragePacketUpdate;
import me.jddev0.ep.energy.ReceiveAndExtractEnergyStorage;
import me.jddev0.ep.energy.ReceiveExtractEnergyHandler;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.EnergySyncS2CPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

public class TransformerBlockEntity extends BlockEntity implements EnergyStoragePacketUpdate {
    public static final int MAX_ENERGY_TRANSFER = 65536;

    private final TransformerBlock.Type type;

    private final ReceiveAndExtractEnergyStorage energyStorage = new ReceiveAndExtractEnergyStorage(0, MAX_ENERGY_TRANSFER, MAX_ENERGY_TRANSFER) {
        @Override
        protected void onChange() {
            setChanged();

            if(level != null && !level.isClientSide())
                ModMessages.sendToAllPlayers(new EnergySyncS2CPacket(energy, capacity, getBlockPos()));
        }
    };
    private LazyOptional<IEnergyStorage> lazyEnergyStorage = LazyOptional.empty();
    private final LazyOptional<IEnergyStorage> lazyEnergyStorageSidedReceive = LazyOptional.of(
            () -> new ReceiveExtractEnergyHandler(energyStorage, (maxReceive, simulate) -> true, (maxExtract, simulate) -> false));
    private final LazyOptional<IEnergyStorage> lazyEnergyStorageSidedExtract = LazyOptional.of(
            () -> new ReceiveExtractEnergyHandler(energyStorage, (maxReceive, simulate) -> false, (maxExtract, simulate) -> true));

    public static BlockEntityType<TransformerBlockEntity> getEntityTypeFromType(TransformerBlock.Type type) {
        return switch(type) {
            case TYPE_1_TO_N -> ModBlockEntities.TRANSFORMER_1_TO_N_ENTITY.get();
            case TYPE_3_TO_3 -> ModBlockEntities.TRANSFORMER_3_TO_3_ENTITY.get();
            case TYPE_N_TO_1 -> ModBlockEntities.TRANSFORMER_N_TO_1_ENTITY.get();
        };
    }

    public TransformerBlockEntity(BlockPos blockPos, BlockState blockState, TransformerBlock.Type type) {
        super(getEntityTypeFromType(type), blockPos, blockState);

        this.type = type;
    }

    public TransformerBlock.Type getTransformerType() {
        return type;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.ENERGY) {
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
        nbt.put("energy", energyStorage.saveNBT());

        super.saveAdditional(nbt);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);

        energyStorage.loadNBT(nbt.get("energy"));
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

            LazyOptional<IEnergyStorage> energyStorageLazyOptional = testBlockEntity.getCapability(ForgeCapabilities.ENERGY, direction.getOpposite());
            if(!energyStorageLazyOptional.isPresent())
                continue;

            IEnergyStorage energyStorage = energyStorageLazyOptional.orElse(null);
            if(!energyStorage.canReceive())
                continue;

            int received = energyStorage.receiveEnergy(Math.min(MAX_ENERGY_TRANSFER, blockEntity.energyStorage.getEnergy()), true);
            if(received <= 0)
                continue;

            consumptionSum += received;
            consumerItems.add(energyStorage);
            consumerEnergyValues.add(received);
        }

        List<Integer> consumerEnergyDistributed = new LinkedList<>();
        for(int i = 0;i < consumerItems.size();i++)
            consumerEnergyDistributed.add(0);

        int consumptionLeft = Math.min(MAX_ENERGY_TRANSFER, Math.min(blockEntity.energyStorage.getEnergy(), consumptionSum));
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

    public int getEnergy() {
        return energyStorage.getEnergy();
    }

    public int getCapacity() {
        return energyStorage.getCapacity();
    }

    @Override
    public void setEnergy(int energy) {
        energyStorage.setEnergyWithoutUpdate(energy);
    }

    @Override
    public void setCapacity(int capacity) {
        energyStorage.setCapacityWithoutUpdate(capacity);
    }
}