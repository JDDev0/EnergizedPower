package me.jddev0.ep.screen;

public interface EnergyStorageMenu {
    long getEnergy();
    long getCapacity();

    int getScaledEnergyMeterPos(int energyMeterHeight);

    default long getEnergyIndicatorBarValue() {
        return 0;
    }

    default int getScaledEnergyIndicatorBarPos(int energyMeterHeight) {
        return 0;
    }
}
