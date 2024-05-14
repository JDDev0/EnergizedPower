package me.jddev0.ep.energy;

import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

public class InfinityEnergyStorage implements IEnergizedPowerEnergyStorage {
    @Override
    public long insert(long maxAmount, TransactionContext transaction) {
        return maxAmount;
    }

    @Override
    public long extract(long maxAmount, TransactionContext transaction) {
        return maxAmount;
    }

    @Override
    public long getAmount() {
        return Long.MAX_VALUE;
    }

    @Override
    public long getCapacity() {
        return Long.MAX_VALUE;
    }

    @Override
    public void setAmountWithoutUpdate(long amount) {}

    @Override
    public void setCapacityWithoutUpdate(long capacity) {}
}
