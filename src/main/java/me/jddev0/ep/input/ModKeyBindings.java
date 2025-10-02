package me.jddev0.ep.input;

import com.mojang.blaze3d.platform.InputConstants;
import me.jddev0.ep.api.EPAPI;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public final class ModKeyBindings {
    private ModKeyBindings() {}

    public static final KeyMapping.Category KEY_CATEGORY_ENERGIZED_POWER = new KeyMapping.Category(EPAPI.id("main"));
    public static final String KEY_TELEPORTER_USE = "key.energizedpower.teleporter.use";

    public static final KeyMapping TELEPORTER_USE_KEY = new KeyMapping(KEY_TELEPORTER_USE,
            KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_V, KEY_CATEGORY_ENERGIZED_POWER);
}
