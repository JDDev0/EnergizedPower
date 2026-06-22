package me.jddev0.ep.fluid;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedSlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

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
    public void serialize(ValueOutput view) {
        if(getTankCount() == 1) {
            FluidStack fluid = getFluid(0);
            view.child("fluid").storeNullable("Fluid", FluidStack.CODEC_MILLIBUCKETS, fluid.isEmpty()?null:fluid);
        }else {
            for(int i = 0;i < getTankCount();i++) {
                FluidStack fluid = getFluid(i);
                view.storeNullable("fluid." + i, FluidStack.CODEC_MILLIBUCKETS, fluid.isEmpty()?null:fluid);
            }
        }
    }

    @Override
    public void deserialize(ValueInput view) {
        if(getTankCount() == 1) {
            FluidStack fluid = view.child("fluid").flatMap(subView -> subView.read("Fluid", FluidStack.CODEC_MILLIBUCKETS)).
                    orElseGet(() -> new FluidStack(Fluids.EMPTY, 0));
            setFluid(0, fluid);
        }else {
            for(int i = 0;i < getTankCount();i++) {
                FluidStack fluid = view.read("fluid." + i, FluidStack.CODEC_MILLIBUCKETS).
                        orElseGet(() -> new FluidStack(Fluids.EMPTY, 0));
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
