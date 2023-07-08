package me.jddev0.ep.util;

import java.util.Locale;

public final class FluidUtils {
    private static final String[] FLUID_PREFIXES = new String[] {
            "", "k", "M", "G", "T", "P"
    };

    private FluidUtils() {}

    public static String getFluidAmountWithPrefix(int energy) {
        if(energy < 1000)
            return String.format(Locale.ENGLISH, "%d mB", energy);

        double energyWithPrefix = energy;

        int prefixIndex = 0;
        while(((int)energyWithPrefix >= 1000) && prefixIndex + 1 < FLUID_PREFIXES.length) {
            energyWithPrefix /= 1000;

            prefixIndex++;
        }

        return String.format(Locale.ENGLISH, "%.2f%s mB", energyWithPrefix, FLUID_PREFIXES[prefixIndex]);
    }
}
