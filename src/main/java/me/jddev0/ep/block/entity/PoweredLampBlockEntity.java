package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.PoweredLampBlock;
import me.jddev0.ep.block.entity.base.EnergyStorageBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
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
                setChanged();
                syncEnergyToPlayers(32);
            }
        };
    }

    @Override
    protected EnergizedPowerLimitingEnergyStorage initLimitingEnergyStorage() {
        return new EnergizedPowerLimitingEnergyStorage(energyStorage, baseEnergyTransferRate, 0);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, PoweredLampBlockEntity blockEntity) {
        if(level.isClientSide())
            return;

        boolean isEmptyFlag = blockEntity.energyStorage.getAmount() == 0;
        int levelValue = Math.min(Mth.floor((float)blockEntity.energyStorage.getAmount() /
                blockEntity.limitingEnergyStorage.getCapacity() * 14.f) + (isEmptyFlag?0:1), 15);

        if(state.getValue(PoweredLampBlock.LEVEL) != levelValue)
            level.setBlock(blockPos, state.setValue(PoweredLampBlock.LEVEL, levelValue), 3);

        try(Transaction transaction = Transaction.openOuter()) {
            blockEntity.energyStorage.extract(blockEntity.energyStorage.getAmount(), transaction);
            transaction.commit();
        }
    }
}