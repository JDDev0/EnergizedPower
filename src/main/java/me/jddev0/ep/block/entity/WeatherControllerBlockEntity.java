package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.UpgradableEnergyStorageBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.screen.WeatherControllerMenu;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import org.jetbrains.annotations.Nullable;
import me.jddev0.ep.energy.EnergizedPowerEnergyStorage;
import me.jddev0.ep.energy.EnergizedPowerLimitingEnergyStorage;

public class WeatherControllerBlockEntity
        extends UpgradableEnergyStorageBlockEntity<EnergizedPowerEnergyStorage>
        implements ExtendedScreenHandlerFactory {
    public static final long CAPACITY = ModConfigs.COMMON_WEATHER_CONTROLLER_CAPACITY.getValue();

    private static final int WEATHER_CHANGED_TICKS = ModConfigs.COMMON_WEATHER_CONTROLLER_CONTROL_DURATION.getValue();

    public WeatherControllerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                ModBlockEntities.WEATHER_CONTROLLER_ENTITY, blockPos, blockState,

                CAPACITY,
                ModConfigs.COMMON_WEATHER_CONTROLLER_TRANSFER_RATE.getValue(),

                UpgradeModuleModifier.DURATION
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
        return Text.translatable("container.energizedpower.weather_controller");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        syncEnergyToPlayer(player);
        
        return new WeatherControllerMenu(id, this, inventory, upgradeModuleInventory);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    public void clearEnergy() {
        try(Transaction transaction = Transaction.openOuter()) {
            energyStorage.extract(CAPACITY, transaction);

            transaction.commit();
        }
    }

    public int getWeatherChangedDuration() {
        return (int)Math.max(1, WEATHER_CHANGED_TICKS *
                upgradeModuleInventory.getModifierEffectProduct(UpgradeModuleModifier.DURATION));
    }
}