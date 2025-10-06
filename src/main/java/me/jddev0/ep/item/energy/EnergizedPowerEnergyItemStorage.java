package me.jddev0.ep.item.energy;

import me.jddev0.ep.energy.IEnergizedPowerEnergyHandler;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class EnergizedPowerEnergyItemStorage implements IEnergizedPowerEnergyHandler {
    private final ItemAccess ctx;
    private final int capacity;
    private final int maxInsert, maxExtract;

    public EnergizedPowerEnergyItemStorage(ItemAccess ctx, int capacity, int maxInsert, int maxExtract) {
        this.ctx = ctx;
        this.capacity = capacity;
        this.maxInsert = maxInsert;
        this.maxExtract = maxExtract;
    }

    private boolean trySetEnergy(ItemStack itemStack, int amount, TransactionContext transaction) {
        EnergizedPowerEnergyItem.setStoredEnergyUnchecked(itemStack, amount);
        ItemResource newVariant = ItemResource.of(itemStack);

        try(Transaction nested = Transaction.open(transaction)) {
            if(ctx.extract(ctx.getResource(), 1, nested) == 1 &&
                    ctx.insert(newVariant, 1, nested) == 1) {
                nested.commit();
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean canInsert() {
        return maxInsert > 0;
    }

    @Override
    public int insert(int maxAmount, TransactionContext transaction) {
        TransferPreconditions.checkNonNegative(maxAmount);

        ItemStack itemStack = ctx.getResource().toStack();

        int amount = EnergizedPowerEnergyItem.getStoredEnergyUnchecked(itemStack);
        int inserted = Math.min(maxInsert, Math.min(maxAmount, capacity - amount));

        if(inserted > 0 && trySetEnergy(itemStack, amount + inserted, transaction)) {
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

        ItemStack itemStack = ctx.getResource().toStack();

        int amount = EnergizedPowerEnergyItem.getStoredEnergyUnchecked(itemStack);
        int extracted = Math.min(maxExtract, Math.min(maxAmount, amount));

        if(extracted > 0 && trySetEnergy(itemStack, amount - extracted, transaction)) {
            return extracted;
        }

        return 0;
    }

    @Override
    public long getAmountAsLong() {
        return EnergizedPowerEnergyItem.getStoredEnergyUnchecked(ctx.getResource().toStack());
    }

    @Override
    public long getCapacityAsLong() {
        return capacity;
    }
}
