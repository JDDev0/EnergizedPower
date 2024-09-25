package me.jddev0.ep.event;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.input.ModKeyBindings;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.UseTeleporterC2SPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = EPAPI.MOD_ID, value = Dist.CLIENT)
public class ModClientEvents {
    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        if(ModKeyBindings.TELEPORTER_USE_KEY.consumeClick()) {
            Player player = Minecraft.getInstance().player;
            if(player == null)
                return;

            Level level = Minecraft.getInstance().level;
            if(level == null)
                return;

            BlockPos blockPos = player.getOnPos();
            BlockState state = level.getBlockState(blockPos);

            if(!state.is(ModBlocks.TELEPORTER.get()))
                return;

            ModMessages.sendToServer(new UseTeleporterC2SPacket(blockPos));
        }
    }
}
