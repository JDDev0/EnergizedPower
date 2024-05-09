package me.jddev0.ep.energy;

import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import team.reborn.energy.api.EnergyStorage;

public class EnergizedPowerEnergyStorage extends SnapshotParticipant<Long> implements EnergyStorage {
    protected long amount;
    protected long capacity;
    protected long maxInsert;
    protected long maxExtract;

    public EnergizedPowerEnergyStorage(long capacity, long maxInsert, long maxExtract) {
        StoragePreconditions.notNegative(capacity);
        StoragePreconditions.notNegative(maxInsert);
        StoragePreconditions.notNegative(maxExtract);

        this.capacity = capacity;
        this.maxInsert = maxInsert;
        this.maxExtract = maxExtract;
    }

    @Override
    protected Long createSnapshot() {
        return amount;
    }

    @Override
    protected void readSnapshot(Long snapshot) {
        amount = snapshot;
    }

    @Override
    public boolean supportsInsertion() {
        return maxInsert > 0;
    }

    @Override
    public long insert(long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notNegative(maxAmount);

        long inserted = Math.min(maxInsert, Math.min(maxAmount, getCapacity() - amount));

        if(inserted > 0) {
            updateSnapshots(transaction);
            amount += inserted;
            return inserted;
        }

        return 0;
    }

    @Override
    public boolean supportsExtraction() {
        return maxExtract > 0;
    }

    @Override
    public long extract(long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notNegative(maxAmount);

        long extracted = Math.min(maxExtract, Math.min(maxAmount, amount));

        if(extracted > 0) {
            updateSnapshots(transaction);
            amount -= extracted;
            return extracted;
        }

        return 0;
    }

    @Override
    public long getAmount() {
        return amount;
    }

    @Override
    public long getCapacity() {
        return capacity;
    }

    public void setAmountWithoutUpdate(long amount) {
        this.amount = amount;
    }

    public void setCapacityWithoutUpdate(long capacity) {
        this.capacity = capacity;
    }
}
