package me.jddev0.ep.block.entity;

import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.energy.EnergyStoragePacketUpdate;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.EnergySyncS2CPacket;
import me.jddev0.ep.screen.AdvancedBatteryBoxMenu;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import me.jddev0.ep.util.EnergyUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.base.LimitingEnergyStorage;
import me.jddev0.ep.energy.EnergizedPowerEnergyStorage;

import java.util.LinkedList;
import java.util.List;

public class AdvancedBatteryBoxBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory<BlockPos>, EnergyStoragePacketUpdate {
    public static final long CAPACITY = ModConfigs.COMMON_ADVANCED_BATTERY_BOX_CAPACITY.getValue();
    public static final long MAX_TRANSFER = ModConfigs.COMMON_ADVANCED_BATTERY_BOX_TRANSFER_RATE.getValue();

    final LimitingEnergyStorage energyStorage;
    private final EnergizedPowerEnergyStorage internalEnergyStorage;

    public AdvancedBatteryBoxBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.ADVANCED_BATTERY_BOX_ENTITY, blockPos, blockState);

        internalEnergyStorage = new EnergizedPowerEnergyStorage(CAPACITY, CAPACITY, CAPACITY) {
            @Override
            protected void onFinalCommit() {
                markDirty();

                if(world != null && !world.isClient()) {
                    ModMessages.sendServerPacketToPlayersWithinXBlocks(
                            getPos(), (ServerWorld)world, 32,
                            new EnergySyncS2CPacket(amount, capacity, getPos())
                    );
                }
            }
        };
        energyStorage = new LimitingEnergyStorage(internalEnergyStorage, MAX_TRANSFER, MAX_TRANSFER);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("container.energizedpower.advanced_battery_box");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        ModMessages.sendServerPacketToPlayer((ServerPlayerEntity)player,
                new EnergySyncS2CPacket(internalEnergyStorage.amount, internalEnergyStorage.capacity, getPos()));

        return new AdvancedBatteryBoxMenu(id, this, inventory);
    }

    @Override
    public BlockPos getScreenOpeningData(ServerPlayerEntity player) {
        return pos;
    }

    public int getRedstoneOutput() {
        return EnergyUtils.getRedstoneSignalFromEnergyStorage(energyStorage);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        nbt.putLong("energy", internalEnergyStorage.amount);

        super.writeNbt(nbt, registries);
    }

    @Override
    protected void readNbt(@NotNull NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);

        internalEnergyStorage.amount = nbt.getLong("energy");
    }

    public static void tick(World level, BlockPos blockPos, BlockState state, AdvancedBatteryBoxBlockEntity blockEntity) {
        if(level.isClient())
            return;

        transferEnergy(level, blockPos, state, blockEntity);
    }

    private static void transferEnergy(World level, BlockPos blockPos, BlockState state, AdvancedBatteryBoxBlockEntity blockEntity) {
        if(level.isClient())
            return;

        List<EnergyStorage> consumerItems = new LinkedList<>();
        List<Long> consumerEnergyValues = new LinkedList<>();
        int consumptionSum = 0;
        for(Direction direction:Direction.values()) {
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
                long received = energyStorage.insert(Math.min(MAX_TRANSFER, blockEntity.internalEnergyStorage.amount), transaction);

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

        long consumptionLeft = Math.min(MAX_TRANSFER, Math.min(blockEntity.internalEnergyStorage.amount, consumptionSum));
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

    public long getEnergy() {
        return internalEnergyStorage.amount;
    }

    public long getCapacity() {
        return internalEnergyStorage.capacity;
    }

    @Override
    public void setEnergy(long energy) {
        internalEnergyStorage.amount = energy;
    }

    @Override
    public void setCapacity(long capacity) {
        internalEnergyStorage.capacity = capacity;
    }
}