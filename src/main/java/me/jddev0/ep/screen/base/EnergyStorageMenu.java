package me.jddev0.ep.screen.base;

public interface EnergyStorageMenu extends UpgradeModuleMenu {
    long getEnergy();
    long getCapacity();

    default int getScaledEnergyMeterPos(int energyMeterHeight) {
        long energy = getEnergy();
        long capacity = getCapacity();

        return (int)((energy == 0 || capacity == 0)?0:Math.max(1, energy * energyMeterHeight / capacity));
    }

    default long getEnergyIndicatorBarValue() {
        return 0;
    }

    default int getScaledEnergyIndicatorBarPos(int energyMeterHeight) {
        return 0;
    }
}
