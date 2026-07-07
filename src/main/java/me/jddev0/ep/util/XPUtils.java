package me.jddev0.ep.util;

import me.jddev0.ep.config.ModConfigs;
import net.minecraft.world.entity.player.Player;

public final class XPUtils {
    public static final int XP_TO_LIQUID_RATIO = ModConfigs.COMMON_XP_TO_LIQUID_RATIO.getValue();

    private XPUtils() {}

    public static int getXpNeededForNextLevel(int currentLevel) {
        if(currentLevel >= 30) {
            return 112 + (currentLevel - 30) * 9;
        }else {
            return currentLevel >= 15 ? 37 + (currentLevel - 15) * 5 : 7 + currentLevel * 2;
        }
    }

    public static long getTotalXPFromLevel(int level) {
        if(level <= 16) {
            //delta: + 7 + level * 2
            //total: level * 7 + 2 * (sum from 0 to level - 1 of level)
            return (long)level * 6 + (long)level * level;
        }else if(level <= 31) {
            //delta: + 37 + (level - 16) * 5
            //total: 352 + (level - 16) * 37 + 5 * (sum from 0 to level - 16 of level)
            return 360 + (long)(level * -40.5 + 2.5 * level * level);
        }else {
            //delta: + 112 + (level - 31) * 9
            //total: 1507 + (level - 31) * 112 + 9 * (sum from 0 to level - 31 of level)
            return 2220 + (long)(level * -162.5 + 4.5 * level * level);
        }
    }

    private static final long TOTAL_XP_FROM_LEVEL_16 = getTotalXPFromLevel(16);
    private static final long TOTAL_XP_FROM_LEVEL_31 = getTotalXPFromLevel(31);
    public static int getLevelFromTotalXP(long totalXP) {
        if(totalXP <= TOTAL_XP_FROM_LEVEL_16) {
            return (int)Math.floor(Math.sqrt(totalXP + 9) - 3);
        }else if(totalXP <= TOTAL_XP_FROM_LEVEL_31) {
            return (int)Math.floor(Math.sqrt(1640.25 + 10 * (totalXP - 360)) / 5. + 8.1);
        }else {
            return (int)Math.floor((162.5 + Math.sqrt(26406.25 + 18 * (totalXP - 2220))) / 9.);
        }
    }

    public static long getTotalXPFromPlayer(Player player) {
        return getTotalXPFromLevel(player.experienceLevel) + (long)(player.experienceProgress * player.getXpNeededForNextLevel());
    }
}
