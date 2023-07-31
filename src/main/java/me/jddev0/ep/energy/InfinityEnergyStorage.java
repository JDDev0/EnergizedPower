package me.jddev0.ep.energy;

import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import team.reborn.energy.api.EnergyStorage;

public class InfinityEnergyStorage implements EnergyStorage {
    public InfinityEnergyStorage() {}

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
}
