package me.jddev0.ep.util;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;

import java.util.Locale;

public final class FluidUtils {
    private static final String[] FLUID_PREFIXES = new String[] {
            "", "k", "M", "G", "T", "P"
    };

    private FluidUtils() {}

    public static String getFluidAmountWithPrefix(long energy) {
        if(energy < 1000)
            return String.format(Locale.ENGLISH, "%d mB", energy);

        double energyWithPrefix = energy;

        int prefixIndex = 0;
        while(((long)energyWithPrefix >= 1000) && prefixIndex + 1 < FLUID_PREFIXES.length) {
            energyWithPrefix /= 1000;

            prefixIndex++;
        }

        return String.format(Locale.ENGLISH, "%.2f%s mB", energyWithPrefix, FLUID_PREFIXES[prefixIndex]);
    }

    public static long convertDropletsToMilliBuckets(long droplets) {
        return droplets * 1000 / FluidConstants.BUCKET;
    }

    public static long convertMilliBucketsToDroplets(long milliBuckets) {
        return milliBuckets * FluidConstants.BUCKET / 1000;
    }
}
