package me.jddev0.ep.screen.base;

public interface IEnergyStorageMenu extends IUpgradeModuleMenu {
    int getEnergy();
    int getCapacity();

    default int getScaledEnergyMeterPos(int energyMeterHeight) {
        int energy = getEnergy();
        int capacity = getCapacity();

        return Math.min(energyMeterHeight, (energy == 0 || capacity == 0)?0:Math.max(1, energy * energyMeterHeight / capacity));
    }

    default int getEnergyIndicatorBarValue() {
        return 0;
    }

    default int getScaledEnergyIndicatorBarPos(int energyMeterHeight) {
        return 0;
    }

    default int getEnergyPerTickBarValue() {
        return 0;
    }

    default int getScaledEnergyPerTickBarPos(int energyMeterHeight) {
        int energyPerTick = getEnergyPerTickBarValue();
        int capacity = getCapacity();

        return (energyPerTick <= 0 || capacity == 0)?0:(Math.min(energyPerTick, capacity - 1) *energyMeterHeight / capacity + 1);
    }
}
