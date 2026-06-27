package me.jddev0.ep.fluid;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class InputOutputFluidStorage implements SlottedStorage<FluidVariant> {
    private final SlottedStorage<FluidVariant> handler;
    private final BiPredicate<Integer, FluidStack> canInput;
    private final Predicate<Integer> canOutput;

    public InputOutputFluidStorage(SlottedStorage<FluidVariant> handler, BiPredicate<Integer, FluidStack> canInput, Predicate<Integer> canOutput) {
        this.handler = handler;
        this.canInput = canInput;
        this.canOutput = canOutput;
    }

    @Override
    public int getSlotCount() {
        return handler.getSlotCount();
    }

    @Override
    public SingleSlotStorage<FluidVariant> getSlot(int slot) {
        return new WrappedSingleSlotStorage(handler.getSlot(slot), slot);
    }

    @Override
    public boolean supportsInsertion() {
        return handler.supportsInsertion();
    }

    @Override
    public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {
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
    public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notNegative(maxAmount);
        long amount = 0;

        for(int i = 0;i < getSlotCount();i++) {
            amount += getSlot(i).extract(resource, maxAmount - amount, transaction);
            if (amount == maxAmount) break;
        }

        return amount;
    }

    @Override
    public Iterator<StorageView<FluidVariant>> iterator() {
        return new WrappedStorageViewIterator();
    }

    private class WrappedStorageViewIterator implements Iterator<StorageView<FluidVariant>> {
        private int cursor;

        @Override
        public boolean hasNext() {
            return cursor < getSlotCount();
        }

        @Override
        public StorageView<FluidVariant> next() {
            if(!hasNext())
                throw new NoSuchElementException();

            SingleSlotStorage<FluidVariant> slot = getSlot(cursor);
            cursor++;

            return slot;
        }
    }

    private class WrappedSingleSlotStorage implements SingleSlotStorage<FluidVariant> {
        private SingleSlotStorage<FluidVariant> backingStorage;
        private int slot;

        public WrappedSingleSlotStorage(SingleSlotStorage<FluidVariant> backingStorage, int slot) {
            this.backingStorage = backingStorage;
            this.slot = slot;
        }

        @Override
        public boolean supportsInsertion() {
            return backingStorage.supportsInsertion();
        }

        @Override
        public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {
            return canInput.test(slot, new FluidStack(resource, maxAmount))?backingStorage.insert(resource, maxAmount, transaction):0;
        }

        @Override
        public boolean supportsExtraction() {
            return backingStorage.supportsExtraction();
        }

        @Override
        public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
            return canOutput.test(slot)?backingStorage.extract(resource, maxAmount, transaction):0;
        }

        @Override
        public boolean isResourceBlank() {
            return backingStorage.isResourceBlank();
        }

        @Override
        public FluidVariant getResource() {
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
        public StorageView<FluidVariant> getUnderlyingView() {
            return backingStorage;
        }
    }
}
