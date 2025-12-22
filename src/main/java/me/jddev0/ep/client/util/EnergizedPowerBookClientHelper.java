package me.jddev0.ep.client.util;

import me.jddev0.ep.screen.EnergizedPowerBookScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.LecternBlockEntity;

public final class EnergizedPowerBookClientHelper {
    private EnergizedPowerBookClientHelper() {}

    public static void showBookViewScreen(Identifier openOnPageForBlock) {
        Minecraft.getInstance().setScreen(new EnergizedPowerBookScreen(openOnPageForBlock));
    }

    public static void showBookViewScreen(LecternBlockEntity lecternBlockEntity) {
        Minecraft.getInstance().setScreen(new EnergizedPowerBookScreen(lecternBlockEntity));
    }
}
