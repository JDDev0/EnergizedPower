package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.MinecartChargerBlock;
import me.jddev0.ep.block.MinecartUnchargerBlock;
import me.jddev0.ep.energy.EnergyStoragePacketUpdate;
import me.jddev0.ep.entity.MinecartBatteryBox;
import me.jddev0.ep.networking.ModMessages;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import team.reborn.energy.api.base.LimitingEnergyStorage;
import team.reborn.energy.api.base.SimpleEnergyStorage;

import java.util.List;

public class MinecartChargerBlockEntity extends BlockEntity implements EnergyStoragePacketUpdate {
    public static final long CAPACITY = 16384;
    public static final long MAX_TRANSFER = 512;

    final LimitingEnergyStorage energyStorage;
    private final SimpleEnergyStorage internalEnergyStorage;

    public MinecartChargerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.MINECART_CHARGER_ENTITY, blockPos, blockState);

        internalEnergyStorage = new SimpleEnergyStorage(CAPACITY, CAPACITY, CAPACITY) {
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
        energyStorage = new LimitingEnergyStorage(internalEnergyStorage, MAX_TRANSFER, 0);
    }

    public int getRedstoneOutput() {
        BlockPos blockPosFacing = getPos().offset(getCachedState().get(MinecartChargerBlock.FACING));
        List<MinecartBatteryBox> minecarts = world.getEntitiesByType(TypeFilter.instanceOf(MinecartBatteryBox.class),
                new Box(blockPosFacing.getX(), blockPosFacing.getY(),
                        blockPosFacing.getZ(), blockPosFacing.getX() + 1,
                        blockPosFacing.getY() + 1, blockPosFacing.getZ() + 1),
                EntityPredicates.VALID_ENTITY);
        if(minecarts.isEmpty())
            return 0;

        MinecartBatteryBox minecart = minecarts.get(0);

        long minecartEnergy = minecart.getEnergy();
        boolean isEmptyFlag = minecartEnergy == 0;

        return Math.min(MathHelper.floor((float)minecartEnergy / MinecartBatteryBox.CAPACITY * 14.f) + (isEmptyFlag?0:1), 15);
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

    public static void tick(World level, BlockPos blockPos, BlockState state, MinecartChargerBlockEntity blockEntity) {
        if(level.isClient())
            return;

        BlockPos blockPosFacing = blockEntity.getPos().offset(blockEntity.getCachedState().get(MinecartChargerBlock.FACING));
        List<MinecartBatteryBox> minecarts = level.getEntitiesByType(TypeFilter.instanceOf(MinecartBatteryBox.class),
                new Box(blockPosFacing.getX(), blockPosFacing.getY(),
                        blockPosFacing.getZ(), blockPosFacing.getX() + 1,
                        blockPosFacing.getY() + 1, blockPosFacing.getZ() + 1),
                EntityPredicates.VALID_ENTITY);
        if(minecarts.isEmpty())
            return;

        MinecartBatteryBox minecart = minecarts.get(0);
        long transferred = Math.min(Math.min(blockEntity.energyStorage.getAmount(), MAX_TRANSFER),
                Math.min(MinecartBatteryBox.MAX_TRANSFER, MinecartBatteryBox.CAPACITY - minecart.getEnergy()));
        minecart.setEnergy(minecart.getEnergy() + transferred);

        try(Transaction transaction = Transaction.openOuter()) {
            blockEntity.internalEnergyStorage.extract(transferred, transaction);
            transaction.commit();
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