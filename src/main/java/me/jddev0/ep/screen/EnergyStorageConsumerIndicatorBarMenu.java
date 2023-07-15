package me.jddev0.ep.screen;

public interface EnergyStorageConsumerIndicatorBarMenu extends EnergyStorageMenu {
    @Override
    int getEnergyIndicatorBarValue();

    @Override
    default int getScaledEnergyIndicatorBarPos(int energyMeterHeight) {
        int energyRequirement = getEnergyIndicatorBarValue();
        int capacity = getCapacity();

        return (energyRequirement <= 0 || capacity == 0)?0:
                (Math.min(energyRequirement, capacity - 1) *energyMeterHeight / capacity + 1);
    }
}
