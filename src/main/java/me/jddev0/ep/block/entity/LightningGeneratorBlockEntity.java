package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.LightningGeneratorBlock;
import me.jddev0.ep.energy.EnergyStoragePacketUpdate;
import me.jddev0.ep.networking.ModMessages;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import team.reborn.energy.api.base.LimitingEnergyStorage;
import team.reborn.energy.api.base.SimpleEnergyStorage;

public class LightningGeneratorBlockEntity extends BlockEntity implements EnergyStoragePacketUpdate {

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
        energyStorage = new LimitingEnergyStorage(internalEnergyStorage, 0, 65536);
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

    @Override
    public void setEnergy(long energy) {
        internalEnergyStorage.amount = energy;
    }

    @Override
    public void setCapacity(long capacity) {
        //Does nothing (capacity is final)
    }
}