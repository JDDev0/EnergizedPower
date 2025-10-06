package me.jddev0.ep.util;

import net.minecraft.util.Mth;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

import java.util.Locale;

public final class FluidUtils {
    private static final String[] FLUID_PREFIXES = new String[] {
            "", "k", "M", "G", "T", "P", "E"
    };

    private FluidUtils() {}

    public static String getFluidAmountWithPrefix(long fluidAmount) {
        if(fluidAmount < 1000)
            return String.format(Locale.ENGLISH, "%d mB", fluidAmount);

        double fluidAmountWithPrefix = fluidAmount;

        int prefixIndex = 0;
        while(((long)fluidAmountWithPrefix >= 1000) && prefixIndex + 1 < FLUID_PREFIXES.length) {
            fluidAmountWithPrefix /= 1000;

            prefixIndex++;
        }

        return String.format(Locale.ENGLISH, "%.2f%s mB", fluidAmountWithPrefix, FLUID_PREFIXES[prefixIndex]);
    }

    public static int getRedstoneSignalFromFluidHandler(ResourceHandler<FluidResource> fluidHandler) {
        double fullnessPercentSum = 0;
        boolean isEmptyFlag = true;

        int size = fluidHandler.size();

        for(int i = 0;i < size;i++) {
            if(!fluidHandler.getResource(i).isEmpty()) {
                fullnessPercentSum += (double)fluidHandler.getAmountAsLong(i) / fluidHandler.getCapacityAsLong(i, FluidResource.EMPTY);
                isEmptyFlag = false;
            }
        }

        return Math.min(Mth.floor(fullnessPercentSum / size * 14.f) + (isEmptyFlag?0:1), 15);
    }
}
