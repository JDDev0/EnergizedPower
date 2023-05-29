package me.jddev0.ep.screen;

public interface EnergyStorageMenu {
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
