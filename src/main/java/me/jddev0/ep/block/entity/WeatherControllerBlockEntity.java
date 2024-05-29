package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.UpgradableEnergyStorageBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.energy.ReceiveOnlyEnergyStorage;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.screen.WeatherControllerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.capabilities.Capabilities;
import net.neoforged.neoforge.common.capabilities.Capability;
import net.neoforged.neoforge.common.util.LazyOptional;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WeatherControllerBlockEntity extends UpgradableEnergyStorageBlockEntity<ReceiveOnlyEnergyStorage> {
    public static final int CAPACITY = ModConfigs.COMMON_WEATHER_CONTROLLER_CAPACITY.getValue();

    private static final int WEATHER_CHANGED_TICKS = ModConfigs.COMMON_WEATHER_CONTROLLER_CONTROL_DURATION.getValue();

    private final LazyOptional<IEnergyStorage> lazyEnergyStorage;

    public WeatherControllerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                ModBlockEntities.WEATHER_CONTROLLER_ENTITY.get(), blockPos, blockState,

                "weather_controller",

                CAPACITY,
                ModConfigs.COMMON_WEATHER_CONTROLLER_TRANSFER_RATE.getValue(),

                UpgradeModuleModifier.DURATION
        );

        lazyEnergyStorage = LazyOptional.of(() -> energyStorage);
    }

    @Override
    protected ReceiveOnlyEnergyStorage initEnergyStorage() {
        return new ReceiveOnlyEnergyStorage(0, baseEnergyCapacity, baseEnergyTransferRate) {
            @Override
            protected void onChange() {
                setChanged();
                syncEnergyToPlayers(32);
            }
        };
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        syncEnergyToPlayer(player);

        return new WeatherControllerMenu(id, inventory, this, upgradeModuleInventory);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == Capabilities.ENERGY) {
            return lazyEnergyStorage.cast();
        }

        return super.getCapability(cap, side);
    }

    public void clearEnergy() {
        energyStorage.setEnergy(0);
    }

    public int getWeatherChangedDuration() {
        return (int)Math.max(1, WEATHER_CHANGED_TICKS *
                upgradeModuleInventory.getModifierEffectProduct(UpgradeModuleModifier.DURATION));
    }
}