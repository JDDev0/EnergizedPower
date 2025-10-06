package me.jddev0.ep.energy;

import net.neoforged.neoforge.transfer.energy.EnergyHandler;

//TODO remove class if in NeoForge EnergyHandler
public interface IEnergizedPowerEnergyHandler extends EnergyHandler {
    default boolean canInsert() {
        return true;
    }

    default boolean canExtract() {
        return true;
    }
}
