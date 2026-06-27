package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.UpgradableEnergyStorageBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.energy.EnergizedPowerEnergyStorage;
import me.jddev0.ep.energy.EnergizedPowerLimitingEnergyStorage;
import me.jddev0.ep.inventory.CombinedContainerData;
import me.jddev0.ep.inventory.data.BooleanValueContainerData;
import me.jddev0.ep.inventory.data.EnergyValueContainerData;
import me.jddev0.ep.inventory.data.ShortValueContainerData;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.screen.WeatherControllerMenu;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

public class WeatherControllerBlockEntity extends UpgradableEnergyStorageBlockEntity<EnergizedPowerEnergyStorage> {
    private static final int WEATHER_CHANGED_TICKS = ModConfigs.COMMON_WEATHER_CONTROLLER_CONTROL_DURATION.getValue();
    private static final int WEATHER_RESET_TIMEOUT_TICKS = ModConfigs.COMMON_WEATHER_CONTROLLER_INFINITE_WEATHER_RESET_TIMEOUT.getValue();
    private static final int WEATHER_CONTROL_TIMEOUT_TICKS = ModConfigs.COMMON_WEATHER_CONTROLLER_INFINITE_WEATHER_CONTROL_TIMEOUT.getValue();

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
    protected ContainerData initContainerData() {
        return new CombinedContainerData(
                new ShortValueContainerData(() -> (short)selectedWeatherType, value -> selectedWeatherType = value),
                new EnergyValueContainerData(() -> (!hasInfiniteWeatherChangedDuration() || selectedWeatherType == -1)?-1:
                        (long)upgradeModuleInventory.getModifierEffectSum(UpgradeModuleModifier.ENERGY_CONSUMPTION), value -> {}),
                new BooleanValueContainerData(this::hasEnoughEnergy, value -> {})
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
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        syncEnergyToPlayer(player);

        return new WeatherControllerMenu(id, inventory, this, upgradeModuleInventory, data);
    }

    public @Nullable EnergyStorage getEnergyStorageCapability(@Nullable Direction side) {
        return limitingEnergyStorage;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(nbt, registries);

        nbt.put("selected_weather_type", IntTag.valueOf(selectedWeatherType));
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag nbt, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(nbt, registries);

        selectedWeatherType = nbt.contains("selected_weather_type")?nbt.getInt("selected_weather_type"):-1;
    }


    public static void tick(Level level, BlockPos blockPos, BlockState state, WeatherControllerBlockEntity blockEntity) {
        if(level.isClientSide || !(level instanceof ServerLevel serverLevel))
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

            if(level.getGameTime() % WEATHER_RESET_TIMEOUT_TICKS == 0) {
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
        if(hasInfiniteWeatherChangedDuration())
            return WEATHER_CONTROL_TIMEOUT_TICKS;

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