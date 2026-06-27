package me.jddev0.ep.fluid;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedSlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.stream.LongStream;

public class EnergizedPowerFluidStorage extends CombinedSlottedStorage<FluidVariant, SingleFluidStorage>
        implements IEnergizedPowerFluidStorage {
    private final long[] capacities;

    /**
     * Constructor for a single tank
     */
    public EnergizedPowerFluidStorage(long capacity) {
        this(1, capacity);
    }

    /**
     * Constructor for multiple tanks with the same capacity
     */
    public EnergizedPowerFluidStorage(int size, long capacity) {
        this(LongStream.range(0, size).map(i -> capacity).toArray());
    }

    public EnergizedPowerFluidStorage(long[] capacities) {
        super(new ArrayList<>(capacities.length));

        this.capacities = capacities;

        for(int i = 0;i < capacities.length;i++) {
            final int index = i;
            this.parts.add(new SingleFluidStorage() {
                @Override
                protected long getCapacity(FluidVariant variant) {
                    return EnergizedPowerFluidStorage.this.capacities[index];
                }

                @Override
                protected boolean canInsert(FluidVariant resource) {
                    return EnergizedPowerFluidStorage.this.isValid(index, resource);
                }

                @Override
                public void updateSnapshots(TransactionContext transaction) {
                    super.updateSnapshots(transaction);
                }

                @Override
                protected void onFinalCommit() {
                    EnergizedPowerFluidStorage.this.onFinalCommit();
                }
            });
        }
    }

    @Override
    public void serialize(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
        if(getTankCount() == 1) {
            FluidStack fluid = getFluid(0);
            CompoundTag tag = new CompoundTag();
            if(!fluid.isEmpty()) {
                tag.put("Fluid", fluid.toNBT(new CompoundTag(), lookupProvider));
            }
            nbt.put("fluid", tag);
        }else {
            for(int i = 0;i < getTankCount();i++) {
                FluidStack fluid = getFluid(i);
                Tag tag = new CompoundTag();
                if(!fluid.isEmpty()) {
                    tag = fluid.toNBT(new CompoundTag(), lookupProvider);
                }
                nbt.put("fluid." + i, tag);
            }
        }
    }

    @Override
    public void deserialize(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
        if(getTankCount() == 1) {
            CompoundTag tag = nbt.getCompound("fluid");
            FluidStack fluid = FluidStack.fromNbt(tag.getCompound("Fluid"), lookupProvider);
            setFluid(0, fluid);
        }else {
            for(int i = 0;i < getTankCount();i++) {
                FluidStack fluid = FluidStack.fromNbt(nbt.getCompound("fluid." + i), lookupProvider);
                setFluid(i, fluid);
            }
        }
    }

    @Override
    public void setFluid(int tank, FluidStack fluidStack) {
        parts.get(tank).variant = fluidStack.getFluidVariant();
        parts.get(tank).amount = fluidStack.getDropletsAmount();
        onFinalCommit();
    }

    @Override
    public final long getTankCapacity(int tank) {
        return capacities[tank];
    }

    @Override
    public final void setTankCapacity(int tank, long capacity) {
        //Does nothing (capacity is final)
    }

    @Override
    public boolean isValid(int index, FluidVariant resource) {
        return true;
    }

    protected void onFinalCommit() {}

    @Override
    public int getSlotCount() {
        return parts.size();
    }

    @Override
    public SingleSlotStorage<FluidVariant> getSlot(int slot) {
        return parts.get(slot);
    }
}
