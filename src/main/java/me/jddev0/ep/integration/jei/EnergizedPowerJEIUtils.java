package me.jddev0.ep.integration.jei;

public final class EnergizedPowerJEIUtils {
    private EnergizedPowerJEIUtils() {}

    public static boolean isJEIAvailable() {
        try {
            Class.forName("mezz.jei.api.IModPlugin");

            return true;
        }catch(ClassNotFoundException e) {
            return false;
        }
    }
}
