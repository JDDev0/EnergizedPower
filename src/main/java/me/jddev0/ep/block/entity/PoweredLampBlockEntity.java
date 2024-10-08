package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.PoweredLampBlock;
import me.jddev0.ep.block.entity.base.EnergyStorageBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import me.jddev0.ep.energy.EnergizedPowerEnergyStorage;
import me.jddev0.ep.energy.EnergizedPowerLimitingEnergyStorage;

public class PoweredLampBlockEntity
        extends EnergyStorageBlockEntity<EnergizedPowerEnergyStorage> {
    public static final long MAX_RECEIVE = ModConfigs.COMMON_POWERED_LAMP_TRANSFER_RATE.getValue();

    public PoweredLampBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.POWERED_LAMP_ENTITY, blockPos, blockState,

                MAX_RECEIVE,
                MAX_RECEIVE
        );
    }

    @Override
    protected EnergizedPowerEnergyStorage initEnergyStorage() {
        return new EnergizedPowerEnergyStorage(baseEnergyCapacity, baseEnergyCapacity, baseEnergyCapacity) {
            @Override
            protected void onFinalCommit() {
                markDirty();
                syncEnergyToPlayers(32);
            }
        };
    }

    @Override
    protected EnergizedPowerLimitingEnergyStorage initLimitingEnergyStorage() {
        return new EnergizedPowerLimitingEnergyStorage(energyStorage, baseEnergyTransferRate, 0);
    }

    public static void tick(World level, BlockPos blockPos, BlockState state, PoweredLampBlockEntity blockEntity) {
        if(level.isClient())
            return;

        boolean isEmptyFlag = blockEntity.energyStorage.getAmount() == 0;
        int levelValue = Math.min(MathHelper.floor((float)blockEntity.energyStorage.getAmount() /
                blockEntity.limitingEnergyStorage.getCapacity() * 14.f) + (isEmptyFlag?0:1), 15);

        if(state.get(PoweredLampBlock.LEVEL) != levelValue)
            level.setBlockState(blockPos, state.with(PoweredLampBlock.LEVEL, levelValue), 3);

        try(Transaction transaction = Transaction.openOuter()) {
            blockEntity.energyStorage.extract(blockEntity.energyStorage.getAmount(), transaction);
            transaction.commit();
        }
    }
}