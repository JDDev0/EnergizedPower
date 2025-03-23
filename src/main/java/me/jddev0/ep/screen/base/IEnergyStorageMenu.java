package me.jddev0.ep.screen.base;

public interface IEnergyStorageMenu extends IUpgradeModuleMenu {
    long getEnergy();
    long getCapacity();

    default int getScaledEnergyMeterPos(int energyMeterHeight) {
        long energy = getEnergy();
        long capacity = getCapacity();

        return (int)Math.min(energyMeterHeight, (energy == 0 || capacity == 0)?0:Math.max(1, energy * energyMeterHeight / capacity));
    }

    default long getEnergyIndicatorBarValue() {
        return 0;
    }

    default int getScaledEnergyIndicatorBarPos(int energyMeterHeight) {
        return 0;
    }

    default long getEnergyPerTickBarValue() {
        return 0;
    }

    default int getScaledEnergyPerTickBarPos(int energyMeterHeight) {
        long energyPerTick = getEnergyPerTickBarValue();
        long capacity = getCapacity();

        return (int)((energyPerTick <= 0 || capacity == 0)?0:(Math.min(energyPerTick, capacity - 1) *energyMeterHeight / capacity + 1));
    }
}
