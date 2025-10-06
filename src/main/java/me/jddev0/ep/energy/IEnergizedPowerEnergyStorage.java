package me.jddev0.ep.energy;

public interface IEnergizedPowerEnergyStorage extends IEnergizedPowerEnergyHandler {
    void setAmountWithoutUpdate(int energy);

    void setCapacityWithoutUpdate(int capacity);
}
