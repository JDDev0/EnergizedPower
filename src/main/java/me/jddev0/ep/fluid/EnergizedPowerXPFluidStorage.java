package me.jddev0.ep.fluid;

import me.jddev0.ep.codec.CodecFix;
import me.jddev0.ep.util.XPUtils;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class EnergizedPowerXPFluidStorage implements IEnergizedPowerFluidStorage {
    private long xpAmount;
    private final SnapshotJournal<Long> xpValueSnapshots = new SnapshotJournal<>() {
        @Override
        protected Long createSnapshot() {
            return xpAmount;
        }

        @Override
        protected void revertToSnapshot(Long snapshot) {
            xpAmount = snapshot;
        }

        @Override
        protected void onRootCommit(Long originalState) {
            onFinalCommit();
        }
    };
    private final long xpCapacity;

    public EnergizedPowerXPFluidStorage(long xpCapacity) {
        this.xpCapacity = xpCapacity;
    }

    protected void onFinalCommit() {}

    @Override
    public void serialize(ValueOutput view) {
        view.store("xp_tank", CodecFix.NON_NEGATIVE_LONG, xpAmount);
    }

    @Override
    public void deserialize(ValueInput view) {
        this.xpAmount = view.read("xp_tank", CodecFix.NON_NEGATIVE_LONG).orElse(0L);
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public FluidResource getResource(int tank) {
        return FluidResource.of(getAmountAsLong(tank) == 0?Fluids.EMPTY:EPFluids.LIQUID_XP.get());
    }

    @Override
    public void setFluid(int tank, FluidStack fluidStack) {
        //Does nothing (sync is done via menu only)
    }

    @Override
    public long getAmountAsLong(int tank) {
        return xpAmount * XPUtils.XP_TO_LIQUID_RATIO;
    }

    @Override
    public long getCapacityAsLong(int tank, FluidResource resource) {
        return xpCapacity * XPUtils.XP_TO_LIQUID_RATIO;
    }

    @Override
    public final int getTankCapacity(int tank) {
        return (int)Math.min(xpCapacity * XPUtils.XP_TO_LIQUID_RATIO, Integer.MAX_VALUE);
    }

    @Override
    public final void setTankCapacity(int tank, int capacity) {
        //Does nothing (capacity is final)
    }

    @Override
    public final boolean isValid(int tank, FluidResource resource) {
        return resource.is(Tags.Fluids.EXPERIENCE);
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
    public int insert(int index, FluidResource resource, int maxAmount, TransactionContext transaction) {
        if(isValid(index, resource))
            return (int)insertXP(maxAmount / XPUtils.XP_TO_LIQUID_RATIO, transaction) * XPUtils.XP_TO_LIQUID_RATIO;

        return 0;
    }

    public long insertXP(long maxXPAmount, TransactionContext transaction) {
        long inserted = Math.min(maxXPAmount, xpCapacity - xpAmount);
        if(inserted > 0) {
            xpValueSnapshots.updateSnapshots(transaction);
            xpAmount += inserted;

            return inserted;
        }

        return 0;
    }

    @Override
    public int extract(int index, FluidResource resource, int maxAmount, TransactionContext transaction) {
        if(isValid(index, resource))
            return (int)extractXP(maxAmount / XPUtils.XP_TO_LIQUID_RATIO, transaction) * XPUtils.XP_TO_LIQUID_RATIO;

        return 0;
    }

    public long extractXP(long maxXPAmount, TransactionContext transaction) {
        long extracted = Math.min(maxXPAmount, xpAmount);
        if(extracted > 0) {
            xpValueSnapshots.updateSnapshots(transaction);
            xpAmount -= extracted;

            return extracted;
        }

        return 0;
    }
}
