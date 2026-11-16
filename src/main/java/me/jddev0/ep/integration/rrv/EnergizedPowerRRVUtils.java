package me.jddev0.ep.integration.rrv;

public final class EnergizedPowerRRVUtils {
    private EnergizedPowerRRVUtils() {}

    public static boolean isRRVAvailable() {
        try {
            Class.forName("cc.cassian.rrv.api.ReliableRecipeViewerPlugin");

            return true;
        }catch(ClassNotFoundException e) {
            return false;
        }
    }
}
