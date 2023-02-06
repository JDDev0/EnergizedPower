package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.TransformerBlock;
import me.jddev0.ep.energy.EnergyStoragePacketUpdate;
import me.jddev0.ep.networking.ModMessages;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.base.LimitingEnergyStorage;
import team.reborn.energy.api.base.SimpleEnergyStorage;

public class TransformerBlockEntity extends BlockEntity implements EnergyStoragePacketUpdate {
    public static final long MAX_ENERGY_TRANSFER = 1048576;

    private final TransformerBlock.Type type;

    final LimitingEnergyStorage energyStorageInsert;
    final LimitingEnergyStorage energyStorageExtract;
    private final SimpleEnergyStorage internalEnergyStorage;

    public static BlockEntityType<TransformerBlockEntity> getEntityTypeFromType(TransformerBlock.Type type) {
        return switch(type) {
            case TYPE_1_TO_N -> ModBlockEntities.TRANSFORMER_1_TO_N_ENTITY;
            case TYPE_N_TO_1 -> ModBlockEntities.TRANSFORMER_N_TO_1_ENTITY;
        };
    }

    public TransformerBlockEntity(BlockPos blockPos, BlockState blockState, TransformerBlock.Type type) {
        super(getEntityTypeFromType(type), blockPos, blockState);

        this.type = type;

        internalEnergyStorage = new SimpleEnergyStorage(MAX_ENERGY_TRANSFER, MAX_ENERGY_TRANSFER, MAX_ENERGY_TRANSFER) {
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
        energyStorageInsert = new LimitingEnergyStorage(internalEnergyStorage, MAX_ENERGY_TRANSFER, 0);
        energyStorageExtract = new LimitingEnergyStorage(internalEnergyStorage, 0, MAX_ENERGY_TRANSFER);
    }

    public TransformerBlock.Type getTransformerType() {
        return type;
    }

    EnergyStorage getEnergyStorageForDirection(Direction side) {
        if(side == null)
            return internalEnergyStorage;

        Direction facing = getCachedState().get(TransformerBlock.FACING);

        EnergyStorage singleSide = type == TransformerBlock.Type.TYPE_1_TO_N?energyStorageInsert:energyStorageExtract;
        EnergyStorage multipleSide = type == TransformerBlock.Type.TYPE_1_TO_N?energyStorageExtract:energyStorageInsert;

        if(facing == side)
            return singleSide;

        return multipleSide;
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

    @Override
    public void setEnergy(long energy) {
        internalEnergyStorage.amount = energy;
    }

    @Override
    public void setCapacity(long capacity) {
        //Does nothing (capacity is final)
    }
}