package me.jddev0.ep.fluid;

import me.jddev0.ep.util.FluidUtils;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class FluidStack {
    private FluidVariant fluidVariant;
    private long dropletsAmount;

    public static FluidStack fromNbt(NbtCompound nbtCompound) {
        Fluid fluid = Registry.FLUID.get(new Identifier(nbtCompound.getString("FluidName")));

        //Save milli buckets amount in "Amount" for compatibility with Forge
        //Save leftover droplets amount in "LeftoverDropletsAmount" for preventing rounding errors
        long milliBucketsAmount = nbtCompound.getLong("Amount");
        long dropletsLeftOverAmount = nbtCompound.contains("LeftoverDropletsAmount")?
                nbtCompound.getLong("LeftoverDropletsAmount"):0;
        long dropletsAmount = FluidUtils.convertMilliBucketsToDroplets(milliBucketsAmount) + dropletsLeftOverAmount;

        NbtCompound fluidNbt = nbtCompound.contains("Tag")?nbtCompound.getCompound("Tag"):null;

        return new FluidStack(fluid, fluidNbt, dropletsAmount);
    }

    public static FluidStack fromPacket(PacketByteBuf buf) {
        return new FluidStack(FluidVariant.fromPacket(buf), buf.readLong());
    }

    public FluidStack(Fluid fluid, long dropletsAmount) {
        this(fluid, null, dropletsAmount);
    }

    public FluidStack(Fluid fluid, NbtCompound fluidNbt, long dropletsAmount) {
        this(FluidVariant.of(fluid, fluidNbt), dropletsAmount);
    }

    public FluidStack(FluidVariant fluidVariant, long dropletsAmount) {
        this.fluidVariant = fluidVariant;
        this.dropletsAmount = dropletsAmount;
    }

    public FluidVariant getFluidVariant() {
        return fluidVariant;
    }

    public void setFluidVariant(FluidVariant fluidVariant) {
        this.fluidVariant = fluidVariant;
    }

    public long getDropletsAmount() {
        return dropletsAmount;
    }

    public void setDropletsAmount(long dropletsAmount) {
        this.dropletsAmount = dropletsAmount;
    }

    public long getMilliBucketsAmount() {
        return FluidUtils.convertDropletsToMilliBuckets(dropletsAmount);
    }

    public boolean isEmpty() {
        return fluidVariant.isBlank();
    }

    public Fluid getFluid() {
        return fluidVariant.getFluid();
    }

    public String getTranslationKey() {
        return fluidVariant.getFluid().getDefaultState().getBlockState().getBlock().getTranslationKey();
    }

    public NbtCompound toNBT(NbtCompound nbtCompound) {
        nbtCompound.putString("FluidName", Registry.FLUID.getId(fluidVariant.getFluid()).toString());

        //Save milli buckets amount in "Amount" for compatibility with Forge
        //Save leftover droplets amount in "LeftoverDropletsAmount" for preventing rounding errors
        long milliBucketsAmount = FluidUtils.convertDropletsToMilliBuckets(dropletsAmount);
        long dropletsLeftOverAmount = dropletsAmount - FluidUtils.convertMilliBucketsToDroplets(milliBucketsAmount);
        nbtCompound.putLong("Amount", milliBucketsAmount);
        if(dropletsLeftOverAmount > 0)
            nbtCompound.putLong("LeftoverDropletsAmount", dropletsLeftOverAmount);

        if(fluidVariant.getNbt() != null)
            nbtCompound.put("Tag", fluidVariant.getNbt().copy());

        return nbtCompound;
    }

    public void toPacket(PacketByteBuf buf) {
        fluidVariant.toPacket(buf);
        buf.writeLong(dropletsAmount);
    }
}
