package me.jddev0.ep.screen;

public interface EnergyStorageProducerIndicatorBarMenu extends EnergyStorageMenu {
    @Override
    long getEnergyIndicatorBarValue();

    @Override
    default int getScaledEnergyIndicatorBarPos(int energyMeterHeight) {
        long energyProduction = getEnergyIndicatorBarValue();
        long energy = getEnergy();
        long capacity = getCapacity();

        return (int)((energyProduction <= 0 || capacity == 0)?0:
                (Math.min(energy + energyProduction, capacity - 1) * energyMeterHeight / capacity + 1));
    }
}
