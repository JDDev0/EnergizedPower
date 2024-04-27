package me.jddev0.ep.item.energy;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.item.ItemStack;
import team.reborn.energy.api.EnergyStorage;

public class EnergizedPowerEnergyItemStorage implements EnergyStorage {
    private final ContainerItemContext ctx;
    private final long capacity;
    private final long maxInsert, maxExtract;

    public EnergizedPowerEnergyItemStorage(ContainerItemContext ctx, long capacity, long maxInsert, long maxExtract) {
        this.ctx = ctx;
        this.capacity = capacity;
        this.maxInsert = maxInsert;
        this.maxExtract = maxExtract;
    }

    private boolean trySetEnergy(ItemStack itemStack, long amount, TransactionContext transaction) {
        EnergizedPowerEnergyItem.setStoredEnergyUnchecked(itemStack, amount);
        ItemVariant newVariant = ItemVariant.of(itemStack);

        try(Transaction nested = transaction.openNested()) {
            if(ctx.extract(ctx.getItemVariant(), 1, nested) == 1 &&
                    ctx.insert(newVariant, 1, nested) == 1) {
                nested.commit();
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean supportsInsertion() {
        return maxInsert > 0;
    }

    @Override
    public long insert(long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notNegative(maxAmount);

        ItemStack itemStack = ctx.getItemVariant().toStack();

        long amount = EnergizedPowerEnergyItem.getStoredEnergyUnchecked(itemStack);
        long inserted = Math.min(maxInsert, Math.min(maxAmount, capacity - amount));

        if(inserted > 0 && trySetEnergy(itemStack, amount + inserted, transaction)) {
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

        ItemStack itemStack = ctx.getItemVariant().toStack();

        long amount = EnergizedPowerEnergyItem.getStoredEnergyUnchecked(itemStack);
        long extracted = Math.min(maxExtract, Math.min(maxAmount, amount));

        if(extracted > 0 && trySetEnergy(itemStack, amount - extracted, transaction)) {
            return extracted;
        }

        return 0;
    }

    @Override
    public long getAmount() {
        return EnergizedPowerEnergyItem.getStoredEnergyUnchecked(ctx.getItemVariant().toStack());
    }

    @Override
    public long getCapacity() {
        return capacity;
    }
}
