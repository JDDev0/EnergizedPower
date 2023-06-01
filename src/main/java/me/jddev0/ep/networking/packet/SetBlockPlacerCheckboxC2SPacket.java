package me.jddev0.ep.networking.packet;

import me.jddev0.ep.block.entity.BlockPlacerBlockEntity;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public final class SetBlockPlacerCheckboxC2SPacket {
    private SetBlockPlacerCheckboxC2SPacket() {}

    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {
        BlockPos pos = buf.readBlockPos();
        int checkboxId = buf.readInt();
        boolean checked = buf.readBoolean();

        server.execute(() -> {
            BlockEntity blockEntity = player.getWorld().getBlockEntity(pos);
            if(!(blockEntity instanceof BlockPlacerBlockEntity blockPlacerBlockEntity))
                return;

            switch(checkboxId) {
                //Inverse rotation
                case 0 -> blockPlacerBlockEntity.setInverseRotation(checked);
            }
        });
    }
}
