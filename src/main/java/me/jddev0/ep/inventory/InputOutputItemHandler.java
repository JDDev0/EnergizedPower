package me.jddev0.ep.inventory;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.world.item.ItemStack;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class InputOutputItemHandler implements SlottedStorage<ItemVariant> {
    private final SlottedStorage<ItemVariant> handler;
    private final BiPredicate<Integer, ItemStack> canInput;
    private final Predicate<Integer> canOutput;

    public InputOutputItemHandler(SlottedStorage<ItemVariant> handler, BiPredicate<Integer, ItemStack> canInput, Predicate<Integer> canOutput) {
        this.handler = handler;
        this.canInput = canInput;
        this.canOutput = canOutput;
    }

    @Override
    public int getSlotCount() {
        return handler.getSlotCount();
    }

    @Override
    public SingleSlotStorage<ItemVariant> getSlot(int slot) {
        return new WrappedSingleSlotStorage(handler.getSlot(slot), slot);
    }

    @Override
    public boolean supportsInsertion() {
        return handler.supportsInsertion();
    }

    @Override
    public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notNegative(maxAmount);
        long amount = 0;

        for(int i = 0;i < getSlotCount();i++) {
            amount += getSlot(i).insert(resource, maxAmount - amount, transaction);
            if (amount == maxAmount) break;
        }

        return amount;
    }

    @Override
    public boolean supportsExtraction() {
        return handler.supportsExtraction();
    }

    @Override
    public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notNegative(maxAmount);
        long amount = 0;

        for(int i = 0;i < getSlotCount();i++) {
            amount += getSlot(i).extract(resource, maxAmount - amount, transaction);
            if (amount == maxAmount) break;
        }

        return amount;
    }

    @Override
    public Iterator<StorageView<ItemVariant>> iterator() {
        return new WrappedStorageViewIterator();
    }

    private class WrappedStorageViewIterator implements Iterator<StorageView<ItemVariant>> {
        private int cursor;

        @Override
        public boolean hasNext() {
            return cursor < getSlotCount();
        }

        @Override
        public StorageView<ItemVariant> next() {
            if(!hasNext())
                throw new NoSuchElementException();

            SingleSlotStorage<ItemVariant> slot = getSlot(cursor);
            cursor++;

            return slot;
        }
    }

    private class WrappedSingleSlotStorage implements SingleSlotStorage<ItemVariant> {
        private SingleSlotStorage<ItemVariant> backingStorage;
        private int slot;

        public WrappedSingleSlotStorage(SingleSlotStorage<ItemVariant> backingStorage, int slot) {
            this.backingStorage = backingStorage;
            this.slot = slot;
        }

        @Override
        public boolean supportsInsertion() {
            return backingStorage.supportsInsertion();
        }

        @Override
        public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
            return canInput.test(slot, resource.toStack((int)maxAmount))?backingStorage.insert(resource, maxAmount, transaction):0;
        }

        @Override
        public boolean supportsExtraction() {
            return backingStorage.supportsExtraction();
        }

        @Override
        public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
            return canOutput.test(slot)?backingStorage.extract(resource, maxAmount, transaction):0;
        }

        @Override
        public boolean isResourceBlank() {
            return backingStorage.isResourceBlank();
        }

        @Override
        public ItemVariant getResource() {
            return backingStorage.getResource();
        }

        @Override
        public long getAmount() {
            return backingStorage.getAmount();
        }

        @Override
        public long getCapacity() {
            return backingStorage.getCapacity();
        }

        @Override
        public StorageView<ItemVariant> getUnderlyingView() {
            return backingStorage;
        }
    }
}
