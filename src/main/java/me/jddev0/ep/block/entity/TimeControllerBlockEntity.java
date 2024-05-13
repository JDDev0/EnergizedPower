package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.EnergyStorageBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.screen.TimeControllerMenu;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import me.jddev0.ep.energy.EnergizedPowerEnergyStorage;
import me.jddev0.ep.energy.EnergizedPowerLimitingEnergyStorage;

public class TimeControllerBlockEntity
        extends EnergyStorageBlockEntity<EnergizedPowerEnergyStorage>
        implements ExtendedScreenHandlerFactory<BlockPos> {
    public static final long CAPACITY = ModConfigs.COMMON_TIME_CONTROLLER_CAPACITY.getValue();

    public TimeControllerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                ModBlockEntities.TIME_CONTROLLER_ENTITY, blockPos, blockState,

                CAPACITY,
                ModConfigs.COMMON_TIME_CONTROLLER_TRANSFER_RATE.getValue()
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

    @Override
    public Text getDisplayName() {
        return Text.translatable("container.energizedpower.time_controller");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        syncEnergyToPlayer(player);
        
        return new TimeControllerMenu(id, this, inventory);
    }

    @Override
    public BlockPos getScreenOpeningData(ServerPlayerEntity player) {
        return pos;
    }

    public void clearEnergy() {
        try(Transaction transaction = Transaction.openOuter()) {
            energyStorage.extract(CAPACITY, transaction);

            transaction.commit();
        }
    }
}