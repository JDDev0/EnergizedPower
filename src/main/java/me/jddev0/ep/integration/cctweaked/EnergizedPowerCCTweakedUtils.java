package me.jddev0.ep.integration.cctweaked;

public final class EnergizedPowerCCTweakedUtils {
    private EnergizedPowerCCTweakedUtils() {}

    public static boolean isCCTweakedAvailable() {
        try {
            Class.forName("dan200.computercraft.api.ComputerCraftAPI");

            return true;
        }catch(ClassNotFoundException e) {
            return false;
        }
    }
}
