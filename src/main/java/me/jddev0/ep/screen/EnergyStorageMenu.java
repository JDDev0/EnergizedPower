package me.jddev0.ep.screen;

public interface EnergyStorageMenu {
    int getEnergy();
    int getCapacity();

    int getScaledEnergyMeterPos(int energyMeterHeight);

    default int getEnergyIndicatorBarValue() {
        return 0;
    };

    default int getScaledEnergyIndicatorBarPos(int energyMeterHeight) {
        return 0;
    }
}
