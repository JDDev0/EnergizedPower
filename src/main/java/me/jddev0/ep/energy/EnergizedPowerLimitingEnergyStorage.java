package me.jddev0.ep.energy;

import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.Objects;

public class EnergizedPowerLimitingEnergyStorage implements IEnergyStorage {
    protected IEnergyStorage backingStorage;
    protected int maxInsert, maxExtract;

    public EnergizedPowerLimitingEnergyStorage(IEnergyStorage backingStorage, int maxInsert, int maxExtract) {
        Objects.requireNonNull(backingStorage);

        this.backingStorage = backingStorage;
        this.maxInsert = maxInsert;
        this.maxExtract = maxExtract;
    }

    public int getMaxInsert() {
        return maxInsert;
    }

    public int getMaxExtract() {
        return maxExtract;
    }

    @Override
    public boolean canReceive() {
        return maxInsert > 0;
    }

    @Override
    public int receiveEnergy(int maxAmount, boolean simulate) {
        return backingStorage.receiveEnergy(Math.min(maxAmount, getMaxInsert()), simulate);
    }

    @Override
    public boolean canExtract() {
        return maxExtract > 0;
    }

    @Override
    public int extractEnergy(int maxAmount, boolean simulate) {
        return backingStorage.extractEnergy(Math.min(maxAmount, getMaxExtract()), simulate);
    }

    @Override
    public int getEnergyStored() {
        return backingStorage.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored() {
        return backingStorage.getMaxEnergyStored();
    }
}
