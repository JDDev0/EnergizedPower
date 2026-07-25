package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.MenuEnergyStorageBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.energy.EnergizedPowerEnergyStorage;
import me.jddev0.ep.energy.EnergizedPowerLimitingEnergyStorage;
import me.jddev0.ep.machine.ItemDrop;
import me.jddev0.ep.machine.RedstoneOutput;
import me.jddev0.ep.screen.EliteBatteryBoxMenu;
import me.jddev0.ep.util.EnergyUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

public class EliteBatteryBoxBlockEntity extends MenuEnergyStorageBlockEntity<EnergizedPowerEnergyStorage>
        implements RedstoneOutput, ItemDrop {
    public static final int CAPACITY = ModConfigs.COMMON_ELITE_BATTERY_BOX_CAPACITY.getValue();
    public static final int MAX_TRANSFER = ModConfigs.COMMON_ELITE_BATTERY_BOX_TRANSFER_RATE.getValue();

    public EliteBatteryBoxBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.ELITE_BATTERY_BOX_ENTITY.get(), blockPos, blockState,

                "elite_battery_box",

                CAPACITY, MAX_TRANSFER
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
        return new EnergizedPowerLimitingEnergyStorage(energyStorage, baseEnergyTransferRate, baseEnergyTransferRate);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        syncEnergyToPlayer(player);

        return new EliteBatteryBoxMenu(id, inventory, this);
    }

    @Override
    public int getRedstoneOutput() {
        return EnergyUtils.getRedstoneSignalFromEnergyStorage(energyStorage);
    }

    public @Nullable IEnergyStorage getEnergyStorageCapability(@Nullable Direction side) {
        return limitingEnergyStorage;
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, EliteBatteryBoxBlockEntity blockEntity) {
        if(level.isClientSide())
            return;

        blockEntity.pushEnergyToOutputs(Direction.values());
    }

    @Override
    public void drops(Level level, BlockPos worldPosition) {}
}