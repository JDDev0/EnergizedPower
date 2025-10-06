package me.jddev0.ep.energy;

import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class EnergizedPowerEnergyStorage extends SnapshotJournal<Integer> implements IEnergizedPowerEnergyStorage {
    protected int amount;
    protected int capacity;
    protected int maxInsert;
    protected int maxExtract;

    public EnergizedPowerEnergyStorage(int capacity, int maxInsert, int maxExtract) {
        TransferPreconditions.checkNonNegative(capacity);
        TransferPreconditions.checkNonNegative(maxInsert);
        TransferPreconditions.checkNonNegative(maxExtract);

        this.capacity = capacity;
        this.maxInsert = maxInsert;
        this.maxExtract = maxExtract;
    }

    @Override
    protected Integer createSnapshot() {
        return amount;
    }

    @Override
    protected void revertToSnapshot(Integer snapshot) {
        amount = snapshot;
    }

    protected void onFinalCommit() {}

    @Override
    protected void onRootCommit(Integer originalState) {
        int previousAmount = originalState;
        if(amount != previousAmount) {
            onFinalCommit();
        }
    }

    @Override
    public boolean canInsert() {
        return maxInsert > 0;
    }

    @Override
    public int insert(int maxAmount, TransactionContext transaction) {
        TransferPreconditions.checkNonNegative(maxAmount);

        int inserted = Math.min(maxInsert, Math.min(maxAmount, getCapacityAsInt() - amount));

        if(inserted > 0) {
            updateSnapshots(transaction);
            amount += inserted;
            return inserted;
        }

        return 0;
    }

    @Override
    public boolean canExtract() {
        return maxExtract > 0;
    }

    @Override
    public int extract(int maxAmount, TransactionContext transaction) {
        TransferPreconditions.checkNonNegative(maxAmount);

        int extracted = Math.min(maxExtract, Math.min(maxAmount, amount));

        if(extracted > 0) {
            updateSnapshots(transaction);
            amount -= extracted;
            return extracted;
        }

        return 0;
    }

    @Override
    public long getAmountAsLong() {
        return amount;
    }

    @Override
    public long getCapacityAsLong() {
        return capacity;
    }

    @Override
    public void setAmountWithoutUpdate(int amount) {
        this.amount = amount;
    }

    @Override
    public void setCapacityWithoutUpdate(int capacity) {
        this.capacity = capacity;
    }
}
