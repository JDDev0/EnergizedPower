package me.jddev0.ep.util;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.minecraft.util.Mth;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import java.util.Locale;

public final class FluidUtils {
    private static final String[] FLUID_PREFIXES = new String[] {
            "", "k", "M", "G", "T", "P", "E"
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
        if(droplets == Long.MAX_VALUE)
            return Long.MAX_VALUE;

        return droplets * 1000 / FluidConstants.BUCKET;
    }

    public static long convertMilliBucketsToDroplets(long milliBuckets) {
        if(milliBuckets == Long.MAX_VALUE)
            return Long.MAX_VALUE;

        return milliBuckets * FluidConstants.BUCKET / 1000;
    }

    /**
     * For compatibility with "Forge"
     */
    public static long readFluidAmountInMilliBucketsWithLeftover(String milliBucketsKey, String leftoverKey,
                                                                 ValueInput view) {
        long milliBucketsAmount = view.getLongOr(milliBucketsKey, 0);
        if(milliBucketsAmount == -1)
            return -1;

        long dropletsLeftOverAmount = view.getLongOr(leftoverKey, 0);

        return FluidUtils.convertMilliBucketsToDroplets(milliBucketsAmount) + dropletsLeftOverAmount;
    }

    /**
     * For compatibility with "Forge"
     */
    public static void writeFluidAmountInMilliBucketsWithLeftover(long droplets, String milliBucketsKey,
                                                                 String leftoverKey, ValueOutput view) {
        if(droplets == -1) {
            view.putLong(milliBucketsKey, -1);

            return;
        }

        long milliBucketsAmount = FluidUtils.convertDropletsToMilliBuckets(droplets);
        long dropletsLeftOverAmount = droplets - FluidUtils.convertMilliBucketsToDroplets(milliBucketsAmount);

        view.putLong(milliBucketsKey, milliBucketsAmount);
        if(dropletsLeftOverAmount > 0)
            view.putLong(leftoverKey, dropletsLeftOverAmount);
    }

    public static int getRedstoneSignalFromFluidHandler(Storage<FluidVariant> fluidStorage) {
        double fullnessPercentSum = 0;
        boolean isEmptyFlag = true;

        int size = 0;
        for(StorageView<FluidVariant> fluidView:fluidStorage) {
            if(++size > 100) //Limit max iterations to 100
                break;

            if(!fluidView.isResourceBlank()) {
                fullnessPercentSum += (double)fluidView.getAmount() / fluidView.getCapacity();
                isEmptyFlag = false;
            }
        }

        return Math.min(Mth.floor(fullnessPercentSum / size * 14.f) + (isEmptyFlag?0:1), 15);
    }
}
