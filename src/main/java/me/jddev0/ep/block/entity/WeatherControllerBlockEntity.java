package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.UpgradableEnergyStorageBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.energy.ReceiveOnlyEnergyStorage;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.screen.WeatherControllerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WeatherControllerBlockEntity extends UpgradableEnergyStorageBlockEntity<ReceiveOnlyEnergyStorage>
        implements MenuProvider {
    public static final int CAPACITY = ModConfigs.COMMON_WEATHER_CONTROLLER_CAPACITY.getValue();

    private static final int WEATHER_CHANGED_TICKS = ModConfigs.COMMON_WEATHER_CONTROLLER_CONTROL_DURATION.getValue();

    private LazyOptional<IEnergyStorage> lazyEnergyStorage = LazyOptional.empty();

    public WeatherControllerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                ModBlockEntities.WEATHER_CONTROLLER_ENTITY.get(), blockPos, blockState,

                CAPACITY,
                ModConfigs.COMMON_WEATHER_CONTROLLER_TRANSFER_RATE.getValue(),

                UpgradeModuleModifier.DURATION
        );
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

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.energizedpower.weather_controller");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        syncEnergyToPlayer(player);

        return new WeatherControllerMenu(id, inventory, this, upgradeModuleInventory);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.ENERGY) {
            return lazyEnergyStorage.cast();
        }

        return super.getCapability(cap, side);
    }

    public void clearEnergy() {
        energyStorage.setEnergy(0);
    }

    @Override
    public void onLoad() {
        super.onLoad();

        lazyEnergyStorage = LazyOptional.of(() -> energyStorage);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();

        lazyEnergyStorage.invalidate();
    }

    public int getWeatherChangedDuration() {
        return (int)Math.max(1, WEATHER_CHANGED_TICKS *
                upgradeModuleInventory.getModifierEffectProduct(UpgradeModuleModifier.DURATION));
    }
}