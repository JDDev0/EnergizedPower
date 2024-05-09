package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.PoweredLampBlock;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.energy.EnergyStoragePacketUpdate;
import me.jddev0.ep.networking.ModMessages;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import me.jddev0.ep.energy.EnergizedPowerEnergyStorage;
import me.jddev0.ep.energy.EnergizedPowerLimitingEnergyStorage;

public class PoweredLampBlockEntity extends BlockEntity implements EnergyStoragePacketUpdate {
    public static final long MAX_RECEIVE = ModConfigs.COMMON_POWERED_LAMP_TRANSFER_RATE.getValue();

    final EnergizedPowerLimitingEnergyStorage energyStorage;
    private final EnergizedPowerEnergyStorage internalEnergyStorage;

    public PoweredLampBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.POWERED_LAMP_ENTITY, blockPos, blockState);

        internalEnergyStorage = new EnergizedPowerEnergyStorage(MAX_RECEIVE, MAX_RECEIVE, MAX_RECEIVE) {
            @Override
            protected void onFinalCommit() {
                markDirty();

                if(world != null && !world.isClient()) {
                    PacketByteBuf buffer = PacketByteBufs.create();
                    buffer.writeLong(getAmount());
                    buffer.writeLong(getCapacity());
                    buffer.writeBlockPos(getPos());

                    ModMessages.sendServerPacketToPlayersWithinXBlocks(
                            getPos(), (ServerWorld)world, 32,
                            ModMessages.ENERGY_SYNC_ID, buffer
                    );
                }
            }
        };
        energyStorage = new EnergizedPowerLimitingEnergyStorage(internalEnergyStorage, MAX_RECEIVE, 0);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putLong("energy", internalEnergyStorage.getAmount());

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(@NotNull NbtCompound nbt) {
        super.readNbt(nbt);

        internalEnergyStorage.setAmountWithoutUpdate(nbt.getLong("energy"));
    }

    public static void tick(World level, BlockPos blockPos, BlockState state, PoweredLampBlockEntity blockEntity) {
        if(level.isClient())
            return;

        boolean isEmptyFlag = blockEntity.internalEnergyStorage.getAmount() == 0;
        int levelValue = Math.min(MathHelper.floor((float)blockEntity.internalEnergyStorage.getAmount() / blockEntity.energyStorage.getCapacity() * 14.f) + (isEmptyFlag?0:1), 15);

        if(state.get(PoweredLampBlock.LEVEL) != levelValue)
            level.setBlockState(blockPos, state.with(PoweredLampBlock.LEVEL, levelValue), 3);

        try(Transaction transaction = Transaction.openOuter()) {
            blockEntity.internalEnergyStorage.extract(blockEntity.internalEnergyStorage.getAmount(), transaction);
            transaction.commit();
        }
    }

    public long getEnergy() {
        return internalEnergyStorage.getAmount();
    }

    public long getCapacity() {
        return internalEnergyStorage.getCapacity();
    }

    @Override
    public void setEnergy(long energy) {
        internalEnergyStorage.setAmountWithoutUpdate(energy);
    }

    @Override
    public void setCapacity(long capacity) {
        internalEnergyStorage.setCapacityWithoutUpdate(capacity);
    }
}