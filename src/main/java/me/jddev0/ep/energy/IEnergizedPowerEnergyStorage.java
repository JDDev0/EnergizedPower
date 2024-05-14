package me.jddev0.ep.energy;

import team.reborn.energy.api.EnergyStorage;

public interface IEnergizedPowerEnergyStorage extends EnergyStorage {
    void setAmountWithoutUpdate(long amount);

    void setCapacityWithoutUpdate(long capacity);
}
