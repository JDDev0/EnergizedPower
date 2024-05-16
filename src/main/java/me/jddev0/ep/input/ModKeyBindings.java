package me.jddev0.ep.input;

import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.UseTeleporterC2SPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public final class ModKeyBindings {
    private ModKeyBindings() {}

    public static final String KEY_CATEGORY_ENERGIZED_POWER = "key.category.energizedpower";
    public static final String KEY_TELEPORTER_USE = "key.energizedpower.teleporter.use";

    public static final KeyBinding TELEPORTER_USE_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            KEY_TELEPORTER_USE, InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_V, KEY_CATEGORY_ENERGIZED_POWER));

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(ModKeyBindings::checkTeleporterUseKey);
    }

    private static void checkTeleporterUseKey(MinecraftClient client) {
        if(ModKeyBindings.TELEPORTER_USE_KEY.wasPressed()) {
            PlayerEntity player = client.player;
            if(player == null)
                return;

            World level = client.world;
            if(level == null)
                return;

            BlockPos blockPos = player.getSteppingPos();
            BlockState state = level.getBlockState(blockPos);

            if(!state.isOf(ModBlocks.TELEPORTER))
                return;

            ModMessages.sendClientPacketToServer(new UseTeleporterC2SPacket(blockPos));
        }
    }
}
