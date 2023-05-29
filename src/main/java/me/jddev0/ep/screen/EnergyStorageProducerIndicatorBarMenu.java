package me.jddev0.ep.screen;

public interface EnergyStorageProducerIndicatorBarMenu extends EnergyStorageMenu {
    @Override
    default int getScaledEnergyIndicatorBarPos(int energyMeterHeight) {
        int energyProduction = getEnergyIndicatorBarValue();
        int energy = getEnergy();
        int capacity = getCapacity();

        return (energyProduction <= 0 || capacity == 0)?0:
                (Math.min(energy + energyProduction, capacity - 1) * energyMeterHeight / capacity + 1);
    }
}
