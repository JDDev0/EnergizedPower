package me.jddev0.ep.input;

import com.mojang.blaze3d.platform.InputConstants;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.UseTeleporterC2SPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public final class ModKeyBindings {
    private ModKeyBindings() {}

    public static final KeyMapping.Category KEY_CATEGORY_ENERGIZED_POWER = KeyMapping.Category.register(EPAPI.id("main"));
    public static final String KEY_TELEPORTER_USE = "key.energizedpower.teleporter.use";

    public static final KeyMapping TELEPORTER_USE_KEY = KeyMappingHelper.registerKeyMapping(new KeyMapping(
            KEY_TELEPORTER_USE, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_V, KEY_CATEGORY_ENERGIZED_POWER));

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(ModKeyBindings::checkTeleporterUseKey);
    }

    private static void checkTeleporterUseKey(Minecraft client) {
        if(ModKeyBindings.TELEPORTER_USE_KEY.consumeClick()) {
            Player player = client.player;
            if(player == null)
                return;

            Level level = client.level;
            if(level == null)
                return;

            BlockPos blockPos = player.getOnPos();
            BlockState state = level.getBlockState(blockPos);

            if(!state.is(EPBlocks.TELEPORTER))
                return;

            ModMessages.sendClientPacketToServer(new UseTeleporterC2SPacket(blockPos));
        }
    }
}
