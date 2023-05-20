package me.jddev0.ep.screen;

public interface EnergyStorageMenu {
    long getEnergy();
    long getCapacity();

    int getScaledEnergyMeterPos(int energyBarHeight);
}
