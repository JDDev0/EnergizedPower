package me.jddev0.ep.integration.rei;

public final class EnergizedPowerREIUtils {
    private EnergizedPowerREIUtils() {}

    public static boolean isREIAvailable() {
        try {
            Class.forName("me.shedaniel.rei.api.common.plugins.REICommonPlugin");

            return true;
        }catch(ClassNotFoundException e) {
            return false;
        }
    }
}
