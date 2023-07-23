package me.jddev0.ep.util;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.nbt.NbtCompound;

import java.util.Locale;

public final class FluidUtils {
    private static final String[] FLUID_PREFIXES = new String[] {
            "", "k", "M", "G", "T", "P"
    };

    private FluidUtils() {}

    public static String getFluidAmountWithPrefix(long milliBuckets) {
        if(milliBuckets < 1000)
            return String.format(Locale.ENGLISH, "%d mB", milliBuckets);

        double milliBucketsWithPrefix = milliBuckets;

        int prefixIndex = 0;
        while(((long)milliBucketsWithPrefix >= 1000) && prefixIndex + 1 < FLUID_PREFIXES.length) {
            milliBucketsWithPrefix /= 1000;

            prefixIndex++;
        }

        return String.format(Locale.ENGLISH, "%.2f%s mB", milliBucketsWithPrefix, FLUID_PREFIXES[prefixIndex]);
    }

    public static long convertDropletsToMilliBuckets(long droplets) {
        return droplets * 1000 / FluidConstants.BUCKET;
    }

    public static long convertMilliBucketsToDroplets(long milliBuckets) {
        return milliBuckets * FluidConstants.BUCKET / 1000;
    }

    /**
     * For compatibility with "Forge"
     */
    public static long readFluidAmountInMilliBucketsWithLeftover(String milliBucketsKey, String leftoverKey,
                                                                 NbtCompound nbtCompound) {
        long milliBucketsAmount = nbtCompound.getLong(milliBucketsKey);
        if(milliBucketsAmount == -1)
            return -1;

        long dropletsLeftOverAmount = nbtCompound.contains(leftoverKey)?nbtCompound.getLong(leftoverKey):0;

        return FluidUtils.convertMilliBucketsToDroplets(milliBucketsAmount) + dropletsLeftOverAmount;
    }

    /**
     * For compatibility with "Forge"
     */
    public static void writeFluidAmountInMilliBucketsWithLeftover(long droplets, String milliBucketsKey,
                                                                 String leftoverKey, NbtCompound nbtCompound) {
        if(droplets == -1) {
            nbtCompound.putLong(milliBucketsKey, -1);

            return;
        }

        long milliBucketsAmount = FluidUtils.convertDropletsToMilliBuckets(droplets);
        long dropletsLeftOverAmount = droplets - FluidUtils.convertMilliBucketsToDroplets(milliBucketsAmount);

        nbtCompound.putLong(milliBucketsKey, milliBucketsAmount);
        if(dropletsLeftOverAmount > 0)
            nbtCompound.putLong(leftoverKey, dropletsLeftOverAmount);
    }
}
