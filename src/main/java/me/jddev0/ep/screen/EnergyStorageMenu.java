package me.jddev0.ep.screen;

public interface EnergyStorageMenu {
    int getEnergy();
    int getCapacity();

    int getScaledEnergyMeterPos(int energyBarHeight);
}
