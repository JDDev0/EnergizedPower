package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.MenuEnergyStorageBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.energy.EnergizedPowerEnergyStorage;
import me.jddev0.ep.energy.EnergizedPowerLimitingEnergyStorage;
import me.jddev0.ep.screen.TimeControllerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

public class TimeControllerBlockEntity extends MenuEnergyStorageBlockEntity<EnergizedPowerEnergyStorage> {
    public static final int CAPACITY = ModConfigs.COMMON_TIME_CONTROLLER_CAPACITY.getValue();

    public TimeControllerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.TIME_CONTROLLER_ENTITY.get(), blockPos, blockState,

                "time_controller",

                CAPACITY,
                ModConfigs.COMMON_TIME_CONTROLLER_TRANSFER_RATE.getValue()
        );
    }

    @Override
    protected EnergizedPowerEnergyStorage initEnergyStorage() {
        return new EnergizedPowerEnergyStorage(baseEnergyCapacity) {
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

        return new TimeControllerMenu(id, inventory, this);
    }

    public @Nullable IEnergyStorage getEnergyStorageCapability(@Nullable Direction side) {
        return limitingEnergyStorage;
    }

    public void clearEnergy() {
        energyStorage.setEnergy(0);
    }

    public void onRedstoneTriggered(int signalStrength) {
        if(energyStorage.getEnergyStored() < TimeControllerBlockEntity.CAPACITY || !(level instanceof ServerLevel serverLevel))
            return;

        clearEnergy();

        long ticksPerDay = 24000;

        long time = Math.clamp(signalStrength - 1, 0, 14) * ticksPerDay / 15; //"15" instead of "14": signal strength 14 should set time to 14/15 of ticksPerDay

        long currentTime = level.getDayTime();

        int currentDayTime = (int)(currentTime % 24000);

        if(currentDayTime <= time)
            serverLevel.setDayTime(currentTime - currentDayTime + time);
        else
            serverLevel.setDayTime(currentTime + 24000 - currentDayTime + time);
    }
}