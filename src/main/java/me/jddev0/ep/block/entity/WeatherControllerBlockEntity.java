package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.UpgradableEnergyStorageBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.energy.ReceiveOnlyEnergyStorage;
import me.jddev0.ep.inventory.CombinedContainerData;
import me.jddev0.ep.inventory.data.*;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.screen.WeatherControllerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

public class WeatherControllerBlockEntity extends UpgradableEnergyStorageBlockEntity<ReceiveOnlyEnergyStorage> {
    private static final int WEATHER_CHANGED_TICKS = ModConfigs.COMMON_WEATHER_CONTROLLER_CONTROL_DURATION.getValue();

    private int selectedWeatherType = -1;

    public WeatherControllerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.WEATHER_CONTROLLER_ENTITY.get(), blockPos, blockState,

                "weather_controller",

                ModConfigs.COMMON_WEATHER_CONTROLLER_CAPACITY.getValue(),
                ModConfigs.COMMON_WEATHER_CONTROLLER_TRANSFER_RATE.getValue(),

                UpgradeModuleModifier.DURATION
        );
    }

    protected ContainerData initContainerData() {
        return new CombinedContainerData(
                new ShortValueContainerData(() -> (short)selectedWeatherType, value -> selectedWeatherType = value),
                new EnergyValueContainerData(() -> (!hasInfiniteWeatherChangedDuration() || selectedWeatherType == -1)?-1:
                        (int)upgradeModuleInventory.getModifierEffectSum(UpgradeModuleModifier.ENERGY_CONSUMPTION), value -> {}),
                new BooleanValueContainerData(this::hasEnoughEnergy, value -> {})
        );
    }

    @Override
    protected ReceiveOnlyEnergyStorage initEnergyStorage() {
        return new ReceiveOnlyEnergyStorage(0, baseEnergyCapacity, baseEnergyTransferRate) {
            @Override
            public int getMaxReceive() {
                return Math.max(1, (int)Math.ceil(maxReceive * upgradeModuleInventory.getModifierEffectProduct(
                        UpgradeModuleModifier.ENERGY_TRANSFER_RATE)));
            }

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

        return new WeatherControllerMenu(id, inventory, this, upgradeModuleInventory, data);
    }

    public @Nullable IEnergyStorage getEnergyStorageCapability(@Nullable Direction side) {
        return energyStorage;
    }

    @Override
    protected void saveAdditional(ValueOutput view) {
        super.saveAdditional(view);

        view.putInt("selected_weather_type", selectedWeatherType);
    }

    @Override
    protected void loadAdditional(ValueInput view) {
        super.loadAdditional(view);

        selectedWeatherType = view.getIntOr("selected_weather_type", -1);
    }


    public static void tick(Level level, BlockPos blockPos, BlockState state, WeatherControllerBlockEntity blockEntity) {
        if(level.isClientSide() || !(level instanceof ServerLevel serverLevel))
            return;

        if(!blockEntity.hasInfiniteWeatherChangedDuration() || blockEntity.selectedWeatherType == -1)
            return;

        double energyConsumptionPerTick = blockEntity.upgradeModuleInventory.getModifierEffectSum(
                UpgradeModuleModifier.ENERGY_CONSUMPTION);

        if(blockEntity.energyStorage.getEnergy() >= energyConsumptionPerTick) {
            blockEntity.energyStorage.setEnergy((int)(blockEntity.energyStorage.getEnergy() - energyConsumptionPerTick));

            //Set weather every 5 seconds instead of for every tick
            if(level.getGameTime() % 100 == 0) {
                int duration = blockEntity.getWeatherChangedDuration();

                switch(blockEntity.selectedWeatherType) {
                    //Clear
                    case 0 -> serverLevel.setWeatherParameters(duration, 0, false, false);
                    //Rain
                    case 1 -> serverLevel.setWeatherParameters(0, duration, true, false);
                    //Thunder
                    case 2 -> serverLevel.setWeatherParameters(0, duration, true, true);
                }
            }
        }else {
            blockEntity.setSelectedWeatherType(-1);
        }
    }

    public void clearEnergy() {
        energyStorage.setEnergy(0);
    }

    public boolean hasEnoughEnergy() {
        if(hasInfiniteWeatherChangedDuration()) {
            double energyConsumptionPerTick = upgradeModuleInventory.getModifierEffectSum(
                    UpgradeModuleModifier.ENERGY_CONSUMPTION);

            return energyStorage.getEnergy() >= energyConsumptionPerTick;
        }

        return energyStorage.getEnergy() >= energyStorage.getCapacity();
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
        setChanged();
    }

    @Override
    protected void updateUpgradeModules() {
        //Reset to no selected weather type if infinite duration upgrade is not present
        if(!hasInfiniteWeatherChangedDuration())
            selectedWeatherType = -1;

        super.updateUpgradeModules();
    }
}