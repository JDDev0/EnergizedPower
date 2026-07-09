package me.jddev0.ep.fluid;

import me.jddev0.ep.codec.CodecFix;
import me.jddev0.ep.util.XPUtils;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.material.Fluids;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class EnergizedPowerXPFluidStorage extends SnapshotParticipant<Long> implements IEnergizedPowerFluidStorage {
    private long xpAmount;
    private final long xpCapacity;

    public EnergizedPowerXPFluidStorage(long xpCapacity) {
        this.xpCapacity = xpCapacity;
    }

    @Override
    protected Long createSnapshot() {
        return xpAmount;
    }

    @Override
    protected void readSnapshot(Long snapshot) {
        xpAmount = snapshot;
    }

    @Override
    public void serialize(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
        nbt.putLong("xp_tank", xpAmount);
    }

    @Override
    public void deserialize(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
        this.xpAmount = nbt.getLong("xp_tank");
    }

    @Override
    public int getSlotCount() {
        return 1;
    }

    @Override
    public SingleSlotStorage<FluidVariant> getSlot(int slot) {
        return new XPFluidSingleSlotStorage();
    }

    @Override
    public FluidVariant getResource(int tank) {
        //Must be overridden, because the SingleSlotStorage returned by getSlot() calls this method
        return FluidVariant.of(getAmount(tank) == 0?Fluids.EMPTY:EPFluids.LIQUID_XP);
    }

    @Override
    public void setFluid(int tank, FluidStack fluidStack) {
        //Does nothing (sync is done via menu only)
    }

    @Override
    public long getAmount(int tank) {
        //Must be overridden, because the SingleSlotStorage returned by getSlot() calls this method
        return xpAmount * XPUtils.XP_TO_LIQUID_RATIO;
    }

    @Override
    public final long getTankCapacity(int tank) {
        return xpCapacity * XPUtils.XP_TO_LIQUID_RATIO;
    }

    @Override
    public final void setTankCapacity(int tank, long capacity) {
        //Does nothing (capacity is final)
    }

    @Override
    public boolean isValid(int index, FluidVariant resource) {
        return resource.isOf(EPFluids.LIQUID_XP);
    }

    public long getXPAmount() {
        return xpAmount;
    }

    public void setXPAmount(long xpAmount) {
        this.xpAmount = xpAmount;
    }

    public long getXpCapacity() {
        return xpCapacity;
    }

    @Override
    public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        if(isValid(0, resource))
            return insertXP(maxAmount / XPUtils.XP_TO_LIQUID_RATIO, transaction) * XPUtils.XP_TO_LIQUID_RATIO;

        return 0;
    }

    public long insertXP(long maxXPAmount, TransactionContext transaction) {
        long inserted = Math.min(maxXPAmount, xpCapacity - xpAmount);
        if(inserted > 0) {
            updateSnapshots(transaction);
            xpAmount += inserted;

            return inserted;
        }

        return 0;
    }

    @Override
    public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        if(isValid(0, resource))
            return extractXP(maxAmount / XPUtils.XP_TO_LIQUID_RATIO, transaction) * XPUtils.XP_TO_LIQUID_RATIO;

        return 0;
    }

    public long extractXP(long maxXPAmount, TransactionContext transaction) {
        long extracted = Math.min(maxXPAmount, xpAmount);
        if(extracted > 0) {
            updateSnapshots(transaction);
            xpAmount -= extracted;

            return extracted;
        }

        return 0;
    }

    @Override
    public Iterator<StorageView<FluidVariant>> iterator() {
        return new XPFluidStorageViewIterator();
    }

    private class XPFluidStorageViewIterator implements Iterator<StorageView<FluidVariant>> {
        private int cursor;

        @Override
        public boolean hasNext() {
            return cursor < getSlotCount();
        }

        @Override
        public StorageView<FluidVariant> next() {
            if(!hasNext())
                throw new NoSuchElementException();

            StorageView<FluidVariant> slot = getSlot(cursor);
            cursor++;

            return slot;
        }
    }

    private class XPFluidSingleSlotStorage implements SingleSlotStorage<FluidVariant> {
        @Override
        public boolean supportsInsertion() {
            return EnergizedPowerXPFluidStorage.this.supportsInsertion();
        }

        @Override
        public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {
            return EnergizedPowerXPFluidStorage.this.insert(resource, maxAmount, transaction);
        }

        @Override
        public boolean supportsExtraction() {
            return EnergizedPowerXPFluidStorage.this.supportsExtraction();
        }

        @Override
        public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
            return EnergizedPowerXPFluidStorage.this.extract(resource, maxAmount, transaction);
        }

        @Override
        public boolean isResourceBlank() {
            return getResource().isBlank();
        }

        @Override
        public FluidVariant getResource() {
            return EnergizedPowerXPFluidStorage.this.getResource(0);
        }

        @Override
        public long getAmount() {
            return EnergizedPowerXPFluidStorage.this.getAmount(0);
        }

        @Override
        public long getCapacity() {
            return EnergizedPowerXPFluidStorage.this.getTankCapacity(0);
        }
    }
}
