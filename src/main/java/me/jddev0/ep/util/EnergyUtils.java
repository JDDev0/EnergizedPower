package me.jddev0.ep.util;

import net.minecraft.util.Mth;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.Locale;

public final class EnergyUtils {
    private static final String[] ENERGY_PREFIXES = new String[] {
            "", "k", "M", "G", "T", "P"
    };

    private EnergyUtils() {}

    public static String getEnergyWithPrefix(int energy) {
        if(energy < 1000)
            return String.format(Locale.ENGLISH, "%d FE", energy);

        double energyWithPrefix = energy;

        int prefixIndex = 0;
        while(((int)energyWithPrefix >= 1000) && prefixIndex + 1 < ENERGY_PREFIXES.length) {
            energyWithPrefix /= 1000;

            prefixIndex++;
        }

        return String.format(Locale.ENGLISH, "%.2f%s FE", energyWithPrefix, ENERGY_PREFIXES[prefixIndex]);
    }

    public static int getRedstoneSignalFromEnergyStorage(IEnergyStorage energyStorage) {
        boolean isEmptyFlag = energyStorage.getEnergyStored() == 0;

        return Math.min(Mth.floor((float)energyStorage.getEnergyStored() / energyStorage.getMaxEnergyStored() * 14.f) + (isEmptyFlag?0:1), 15);
    }
}
