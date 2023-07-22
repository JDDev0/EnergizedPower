package me.jddev0.ep.util;

import java.util.Locale;

public final class FluidUtils {
    private static final String[] FLUID_PREFIXES = new String[] {
            "", "k", "M", "G", "T", "P"
    };

    private FluidUtils() {}

    public static String getFluidAmountWithPrefix(int fluidAmount) {
        if(fluidAmount < 1000)
            return String.format(Locale.ENGLISH, "%d mB", fluidAmount);

        double fluidAmountWithPrefix = fluidAmount;

        int prefixIndex = 0;
        while(((int)fluidAmountWithPrefix >= 1000) && prefixIndex + 1 < FLUID_PREFIXES.length) {
            fluidAmountWithPrefix /= 1000;

            prefixIndex++;
        }

        return String.format(Locale.ENGLISH, "%.2f%s mB", fluidAmountWithPrefix, FLUID_PREFIXES[prefixIndex]);
    }
}
