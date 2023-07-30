package me.jddev0.ep.block.entity;

import me.jddev0.ep.energy.EnergyStoragePacketUpdate;
import me.jddev0.ep.energy.InfinityEnergyStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import team.reborn.energy.api.EnergyStorage;

public class CreativeBatteryBoxBlockEntity extends BlockEntity implements EnergyStoragePacketUpdate {
    final InfinityEnergyStorage energyStorage;

    public CreativeBatteryBoxBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.CREATIVE_BATTERY_BOX_ENTITY, blockPos, blockState);

        energyStorage = new InfinityEnergyStorage();
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.put("energy", new NbtCompound());

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(@NotNull NbtCompound nbt) {
        super.readNbt(nbt);

        if(!(nbt.get("energy") instanceof NbtCompound))
            throw new IllegalArgumentException("Tag must be of type IntTag!");
    }

    public static void tick(World level, BlockPos blockPos, BlockState state, CreativeBatteryBoxBlockEntity blockEntity) {
        if(level.isClient())
            return;

        transferInfiniteEnergy(level, blockPos, state, blockEntity);
    }

    private static void transferInfiniteEnergy(World level, BlockPos blockPos, BlockState state, CreativeBatteryBoxBlockEntity blockEntity) {
        if(level.isClient())
            return;

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

            long received;
            try(Transaction transaction = Transaction.openOuter()) {
                received = energyStorage.insert(energyStorage.getCapacity(), transaction);
            }

            try(Transaction transaction = Transaction.openOuter()) {
                energyStorage.insert(received, transaction);
                transaction.commit();
            }
        }
    }

    public long getEnergy() {
        return Long.MAX_VALUE;
    }

    public long getCapacity() {
        return Long.MAX_VALUE;
    }

    @Override
    public void setEnergy(long energy) {
        //Does nothing (energy is final)
    }

    @Override
    public void setCapacity(long capacity) {
        //Does nothing (capacity is final)
    }
}