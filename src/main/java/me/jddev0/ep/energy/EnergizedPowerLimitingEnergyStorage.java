package me.jddev0.ep.energy;

import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import team.reborn.energy.api.EnergyStorage;

import java.util.Objects;

public class EnergizedPowerLimitingEnergyStorage implements EnergyStorage {
    protected EnergyStorage backingStorage;
    protected long maxInsert, maxExtract;

    public EnergizedPowerLimitingEnergyStorage(EnergyStorage backingStorage, long maxInsert, long maxExtract) {
        Objects.requireNonNull(backingStorage);
        StoragePreconditions.notNegative(maxInsert);
        StoragePreconditions.notNegative(maxExtract);

        this.backingStorage = backingStorage;
        this.maxInsert = maxInsert;
        this.maxExtract = maxExtract;
    }

    public long getMaxInsert() {
        return maxInsert;
    }

    public long getMaxExtract() {
        return maxExtract;
    }

    @Override
    public boolean supportsInsertion() {
        return getMaxInsert() > 0 && backingStorage.supportsInsertion();
    }

    @Override
    public long insert(long maxAmount, TransactionContext transaction) {
        return backingStorage.insert(Math.min(maxAmount, getMaxInsert()), transaction);
    }

    @Override
    public boolean supportsExtraction() {
        return getMaxExtract() > 0 && backingStorage.supportsExtraction();
    }

    @Override
    public long extract(long maxAmount, TransactionContext transaction) {
        return backingStorage.extract(Math.min(maxAmount, getMaxExtract()), transaction);
    }

    @Override
    public long getAmount() {
        return backingStorage.getAmount();
    }

    @Override
    public long getCapacity() {
        return backingStorage.getCapacity();
    }
}
