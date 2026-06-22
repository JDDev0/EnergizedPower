package me.jddev0.ep.fluid;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class InfinityFluidStorage extends SingleFluidStorage implements IEnergizedPowerFluidStorage {
    public static final Codec<FluidStack> CODEC = FluidStack.CODEC_MILLIBUCKETS;

    public InfinityFluidStorage() {
        amount = Long.MAX_VALUE;
    }

    @Override
    public void serialize(ValueOutput view) {
        FluidStack fluid = getFluid(0);
        view.child("fluid").storeNullable("Fluid", FluidStack.CODEC_MILLIBUCKETS, fluid.isEmpty()?null:fluid);
    }

    @Override
    public void deserialize(ValueInput view) {
        FluidStack fluid = view.child("fluid").flatMap(subView -> subView.read("Fluid", FluidStack.CODEC_MILLIBUCKETS)).
                orElseGet(() -> new FluidStack(Fluids.EMPTY, 0));
        setFluid(0, fluid);
    }

    public void setFluid(FluidStack fluidStack) {
        variant = fluidStack.getFluidVariant();
    }

    @Override
    protected long getCapacity(FluidVariant variant) {
        return Long.MAX_VALUE;
    }

    @Override
    public final long getTankCapacity(int tank) {
        return Long.MAX_VALUE;
    }

    @Override
    public final void setTankCapacity(int tank, long capacity) {}

    @Override
    public boolean isValid(int index, FluidVariant resource) {
        return true;
    }

    @Override
    public void setFluid(int tank, FluidStack fluidStack) {
        variant = fluidStack.getFluidVariant();
    }

    public void setFluid(FluidStack fluidStack, TransactionContext transaction) {
        FluidVariant fluidVariant = fluidStack.getFluidVariant();

        updateSnapshots(transaction);

        variant = fluidVariant;
    }

    @Override
    public long insert(FluidVariant insertedVariant, long maxAmount, TransactionContext transaction) {
        return maxAmount;
    }

    @Override
    public long extract(FluidVariant extractedVariant, long maxAmount, TransactionContext transaction) {
        return !extractedVariant.isBlank() && extractedVariant.isOf(variant.getFluid()) &&
                extractedVariant.componentsMatch(variant.getComponentsPatch())?maxAmount:0;
    }
}
