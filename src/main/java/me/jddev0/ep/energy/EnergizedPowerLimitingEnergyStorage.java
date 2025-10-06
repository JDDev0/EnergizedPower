package me.jddev0.ep.energy;

import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import java.util.Objects;

public class EnergizedPowerLimitingEnergyStorage implements IEnergizedPowerEnergyHandler {
    protected EnergyHandler backingStorage;
    protected int maxInsert, maxExtract;

    public EnergizedPowerLimitingEnergyStorage(EnergyHandler backingStorage, int maxInsert, int maxExtract) {
        Objects.requireNonNull(backingStorage);
        TransferPreconditions.checkNonNegative(maxInsert);
        TransferPreconditions.checkNonNegative(maxExtract);

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
    public boolean canInsert() {
        return maxInsert > 0;
    }

    @Override
    public int insert(int maxAmount, TransactionContext transaction) {
        return backingStorage.insert(Math.min(maxAmount, getMaxInsert()), transaction);
    }

    @Override
    public boolean canExtract() {
        return maxExtract > 0;
    }

    @Override
    public int extract(int maxAmount, TransactionContext transaction) {
        return backingStorage.extract(Math.min(maxAmount, getMaxExtract()), transaction);
    }

    @Override
    public long getAmountAsLong() {
        return backingStorage.getAmountAsLong();
    }

    @Override
    public long getCapacityAsLong() {
        return backingStorage.getCapacityAsLong();
    }
}
