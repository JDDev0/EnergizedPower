package me.jddev0.ep.util;

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
}
