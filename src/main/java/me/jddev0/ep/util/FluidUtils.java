package me.jddev0.ep.util;

import net.minecraft.util.Mth;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

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

    public static int getRedstoneSignalFromFluidHandler(IFluidHandler fluidHandler) {
        float fullnessPercentSum = 0;
        boolean isEmptyFlag = true;

        int size = fluidHandler.getTanks();

        for(int i = 0;i < size;i++) {
            FluidStack fluid = fluidHandler.getFluidInTank(i);
            if(!fluid.isEmpty()) {
                fullnessPercentSum += (float) fluid.getAmount() / fluidHandler.getTankCapacity(i);
                isEmptyFlag = false;
            }
        }

        return Math.min(Mth.floor(fullnessPercentSum / size * 14.f) + (isEmptyFlag?0:1), 15);
    }
}
