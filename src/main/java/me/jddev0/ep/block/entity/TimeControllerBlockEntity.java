package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.MenuEnergyStorageBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.screen.TimeControllerMenu;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import me.jddev0.ep.energy.EnergizedPowerEnergyStorage;
import me.jddev0.ep.energy.EnergizedPowerLimitingEnergyStorage;

public class TimeControllerBlockEntity
        extends MenuEnergyStorageBlockEntity<EnergizedPowerEnergyStorage> {
    public static final long CAPACITY = ModConfigs.COMMON_TIME_CONTROLLER_CAPACITY.getValue();

    public TimeControllerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.TIME_CONTROLLER_ENTITY, blockPos, blockState,

                "time_controller",

                CAPACITY,
                ModConfigs.COMMON_TIME_CONTROLLER_TRANSFER_RATE.getValue()
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

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        syncEnergyToPlayer(player);
        
        return new TimeControllerMenu(id, this, inventory);
    }

    public void clearEnergy() {
        try(Transaction transaction = Transaction.openOuter()) {
            energyStorage.extract(CAPACITY, transaction);

            transaction.commit();
        }
    }
}