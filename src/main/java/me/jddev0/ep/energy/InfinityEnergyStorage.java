package me.jddev0.ep.energy;

import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class InfinityEnergyStorage implements IEnergizedPowerEnergyStorage {
    public InfinityEnergyStorage() {}

    @Override
    public int insert(int maxAmount, TransactionContext transaction) {
        return maxAmount;
    }

    @Override
    public int extract(int maxAmount, TransactionContext transaction) {
        return maxAmount;
    }

    @Override
    public long getAmountAsLong() {
        return Integer.MAX_VALUE;
    }

    @Override
    public long getCapacityAsLong() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void setCapacityWithoutUpdate(int capacity) {}

    @Override
    public void setAmountWithoutUpdate(int energy) {}
}
