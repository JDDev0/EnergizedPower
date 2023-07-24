package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.LightningGeneratorBlock;
import me.jddev0.ep.energy.EnergyStoragePacketUpdate;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.screen.LightningGeneratorMenu;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.base.LimitingEnergyStorage;
import team.reborn.energy.api.base.SimpleEnergyStorage;

import java.util.LinkedList;
import java.util.List;

public class LightningGeneratorBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, EnergyStoragePacketUpdate {
    public static final long MAX_EXTRACT = 65536;

    final LimitingEnergyStorage energyStorage;
    private final SimpleEnergyStorage internalEnergyStorage;

    public LightningGeneratorBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.LIGHTING_GENERATOR_ENTITY, blockPos, blockState);

        internalEnergyStorage = new SimpleEnergyStorage(LightningGeneratorBlock.ENERGY_PER_LIGHTNING_STRIKE,
                LightningGeneratorBlock.ENERGY_PER_LIGHTNING_STRIKE, LightningGeneratorBlock.ENERGY_PER_LIGHTNING_STRIKE) {
            @Override
            protected void onFinalCommit() {
                markDirty();

                if(world != null && !world.isClient()) {
                    PacketByteBuf buffer = PacketByteBufs.create();
                    buffer.writeLong(amount);
                    buffer.writeLong(capacity);
                    buffer.writeBlockPos(getPos());

                    ModMessages.broadcastServerPacket(world.getServer(), ModMessages.ENERGY_SYNC_ID, buffer);
                }
            }
        };
        energyStorage = new LimitingEnergyStorage(internalEnergyStorage, 0, MAX_EXTRACT);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("container.energizedpower.lightning_generator");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeLong(internalEnergyStorage.amount);
        buffer.writeLong(internalEnergyStorage.capacity);
        buffer.writeBlockPos(getPos());

        ModMessages.sendServerPacketToPlayer((ServerPlayerEntity)player, ModMessages.ENERGY_SYNC_ID, buffer);
        
        return new LightningGeneratorMenu(id, this, inventory);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    public void onLightningStrike() {
        try(Transaction transaction = Transaction.openOuter()) {
            internalEnergyStorage.insert(LightningGeneratorBlock.ENERGY_PER_LIGHTNING_STRIKE, transaction);
            transaction.commit();
        }
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

    public static void tick(World level, BlockPos blockPos, BlockState state, LightningGeneratorBlockEntity blockEntity) {
        if(level.isClient())
            return;

        transferEnergy(level, blockPos, state, blockEntity);
    }

    private static void transferEnergy(World level, BlockPos blockPos, BlockState state, LightningGeneratorBlockEntity blockEntity) {
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
                long received = energyStorage.insert(Math.min(MAX_EXTRACT, blockEntity.internalEnergyStorage.amount), transaction);

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

        long consumptionLeft = Math.min(MAX_EXTRACT, Math.min(blockEntity.internalEnergyStorage.amount, consumptionSum));
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
        //Does nothing (capacity is final)
    }
}