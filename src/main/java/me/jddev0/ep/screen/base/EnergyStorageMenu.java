package me.jddev0.ep.screen.base;

import me.jddev0.ep.screen.UpgradeModuleMenu;

public interface EnergyStorageMenu extends UpgradeModuleMenu {
    int getEnergy();
    int getCapacity();

    default int getScaledEnergyMeterPos(int energyMeterHeight) {
        int energy = getEnergy();
        int capacity = getCapacity();

        return (energy == 0 || capacity == 0)?0:Math.max(1, energy * energyMeterHeight / capacity);
    }

    default int getEnergyIndicatorBarValue() {
        return 0;
    }

    default int getScaledEnergyIndicatorBarPos(int energyMeterHeight) {
        return 0;
    }
}
