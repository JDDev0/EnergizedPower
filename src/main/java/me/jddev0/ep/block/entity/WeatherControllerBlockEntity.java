package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.UpgradableEnergyStorageBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.inventory.CombinedContainerData;
import me.jddev0.ep.inventory.data.*;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.screen.WeatherControllerMenu;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import me.jddev0.ep.energy.EnergizedPowerEnergyStorage;
import me.jddev0.ep.energy.EnergizedPowerLimitingEnergyStorage;

public class WeatherControllerBlockEntity
        extends UpgradableEnergyStorageBlockEntity<EnergizedPowerEnergyStorage> {
    private static final int WEATHER_CHANGED_TICKS = ModConfigs.COMMON_WEATHER_CONTROLLER_CONTROL_DURATION.getValue();

    private int selectedWeatherType = -1;

    public WeatherControllerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.WEATHER_CONTROLLER_ENTITY, blockPos, blockState,

                "weather_controller",

                ModConfigs.COMMON_WEATHER_CONTROLLER_CAPACITY.getValue(),
                ModConfigs.COMMON_WEATHER_CONTROLLER_TRANSFER_RATE.getValue(),

                UpgradeModuleModifier.DURATION
        );
    }


    @Override
    protected PropertyDelegate initContainerData() {
        return new CombinedContainerData(
                new ShortValueContainerData(() -> (short)selectedWeatherType, value -> selectedWeatherType = value),
                new EnergyValueContainerData(() -> (!hasInfiniteWeatherChangedDuration() || selectedWeatherType == -1)?-1:
                        (long)upgradeModuleInventory.getModifierEffectSum(UpgradeModuleModifier.ENERGY_CONSUMPTION), value -> {}),
                new BooleanValueContainerData(this::hasEnoughEnergy, value -> {})
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
        return new EnergizedPowerLimitingEnergyStorage(energyStorage, baseEnergyTransferRate, 0) {
            @Override
            public long getMaxInsert() {
                return Math.max(1, (long)Math.ceil(maxInsert * upgradeModuleInventory.getModifierEffectProduct(
                        UpgradeModuleModifier.ENERGY_TRANSFER_RATE)));
            }

        };
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        syncEnergyToPlayer(player);
        
        return new WeatherControllerMenu(id, this, inventory, upgradeModuleInventory, data);
    }

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);

        view.putInt("selected_weather_type", selectedWeatherType);
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);

        selectedWeatherType = view.getInt("selected_weather_type", -1);
    }


    public static void tick(World level, BlockPos blockPos, BlockState state, WeatherControllerBlockEntity blockEntity) {
        if(level.isClient() || !(level instanceof ServerWorld serverLevel))
            return;

        if(!blockEntity.hasInfiniteWeatherChangedDuration() || blockEntity.selectedWeatherType == -1)
            return;

        double energyConsumptionPerTick = blockEntity.upgradeModuleInventory.getModifierEffectSum(
                UpgradeModuleModifier.ENERGY_CONSUMPTION);

        if(blockEntity.energyStorage.getAmount() >= energyConsumptionPerTick) {
            try(Transaction transaction = Transaction.openOuter()) {
                blockEntity.energyStorage.extract((long)energyConsumptionPerTick, transaction);
                transaction.commit();
            }

            //Set weather every 5 seconds instead of for every tick
            if(level.getTime() % 100 == 0) {
                int duration = blockEntity.getWeatherChangedDuration();

                switch(blockEntity.selectedWeatherType) {
                    //Clear
                    case 0 -> serverLevel.setWeather(duration, 0, false, false);
                    //Rain
                    case 1 -> serverLevel.setWeather(0, duration, true, false);
                    //Thunder
                    case 2 -> serverLevel.setWeather(0, duration, true, true);
                }
            }
        }else {
            blockEntity.setSelectedWeatherType(-1);
        }
    }

    public void clearEnergy() {
        try(Transaction transaction = Transaction.openOuter()) {
            energyStorage.extract(energyStorage.getCapacity(), transaction);

            transaction.commit();
        }
    }

    public boolean hasEnoughEnergy() {
        if(hasInfiniteWeatherChangedDuration()) {
            double energyConsumptionPerTick = upgradeModuleInventory.getModifierEffectSum(
                    UpgradeModuleModifier.ENERGY_CONSUMPTION);

            return energyStorage.getAmount() >= energyConsumptionPerTick;
        }

        return energyStorage.getAmount() >= energyStorage.getCapacity();
    }

    public boolean hasInfiniteWeatherChangedDuration() {
        return upgradeModuleInventory.getModifierEffectProduct(UpgradeModuleModifier.DURATION) == Double.POSITIVE_INFINITY;
    }

    public int getWeatherChangedDuration() {
        //15 seconds for infinite duration upgrade
        if(hasInfiniteWeatherChangedDuration())
            return 300;

        return (int)Math.max(1, WEATHER_CHANGED_TICKS *
                upgradeModuleInventory.getModifierEffectProduct(UpgradeModuleModifier.DURATION));
    }

    public int getSelectedWeatherType() {
        return selectedWeatherType;
    }

    public void setSelectedWeatherType(int selectedWeatherType) {
        this.selectedWeatherType = selectedWeatherType;
        markDirty();
    }

    @Override
    protected void updateUpgradeModules() {
        //Reset to no selected weather type if infinite duration upgrade is not present
        if(!hasInfiniteWeatherChangedDuration())
            selectedWeatherType = -1;

        super.updateUpgradeModules();
    }
}