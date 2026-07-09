package me.jddev0.ep.fluid;

import me.jddev0.ep.util.XPUtils;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public class EnergizedPowerXPFluidStorage implements IEnergizedPowerFluidStorage {
    private long xpAmount;
    private final long xpCapacity;

    public EnergizedPowerXPFluidStorage(long xpCapacity) {
        this.xpCapacity = xpCapacity;
    }

    protected void onFinalCommit() {}

    @Override
    public void serialize(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
        nbt.putLong("xp_tank", xpAmount);
    }

    @Override
    public void deserialize(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
        this.xpAmount = nbt.getLong("xp_tank");
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        return new FluidStack(EPFluids.LIQUID_XP.get(), (int)Math.min(xpAmount * XPUtils.XP_TO_LIQUID_RATIO, Integer.MAX_VALUE));
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
    public boolean isFluidValid(int tank, FluidStack stack) {
        return stack.is(Tags.Fluids.EXPERIENCE);
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
    public int fill(FluidStack resource, FluidAction action) {
        if(isFluidValid(0, resource))
            return (int)insertXP(resource.getAmount() / XPUtils.XP_TO_LIQUID_RATIO, action) * XPUtils.XP_TO_LIQUID_RATIO;

        return 0;
    }

    public long insertXP(long maxXPAmount, FluidAction action) {
        long inserted = Math.min(maxXPAmount, xpCapacity - xpAmount);
        if(inserted > 0) {
            if(action.execute()) {
                xpAmount += inserted;
                onFinalCommit();
            }

            return inserted;
        }

        return 0;
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
        return drain(new FluidStack(EPFluids.LIQUID_XP, maxDrain), action);
    }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
        if(isFluidValid(0, resource)) {
            int drained = (int)extractXP(resource.getAmount() / XPUtils.XP_TO_LIQUID_RATIO, action) * XPUtils.XP_TO_LIQUID_RATIO;
            if(drained > 0) {
                return new FluidStack(resource.getFluidHolder(), drained, resource.getComponentsPatch());
            }
        }

        return FluidStack.EMPTY;
    }

    public long extractXP(long maxXPAmount, FluidAction action) {
        long extracted = Math.min(maxXPAmount, xpAmount);
        if(extracted > 0) {
            if(action.execute()) {
                xpAmount -= extracted;
                onFinalCommit();
            }

            return extracted;
        }

        return 0;
    }

    @Override
    public FluidStack getFluid(int tank) {
        return getFluidInTank(tank);
    }

    @Override
    public void setFluid(int tank, FluidStack fluidStack) {
        //Does nothing (sync is done via menu only)
    }
}
