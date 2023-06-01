package me.jddev0.ep.networking.packet;

import me.jddev0.ep.block.entity.AutoCrafterBlockEntity;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public final class SetAutoCrafterCheckboxC2SPacket {
    private SetAutoCrafterCheckboxC2SPacket() {}

    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {
        BlockPos pos = buf.readBlockPos();
        int checkboxId = buf.readInt();
        boolean checked = buf.readBoolean();

        server.execute(() -> {
            BlockEntity blockEntity = player.getWorld().getBlockEntity(pos);
            if(!(blockEntity instanceof AutoCrafterBlockEntity autoCrafterBlockEntity))
                return;

            switch(checkboxId) {
                //Ignore NBT
                case 0 -> autoCrafterBlockEntity.setIgnoreNBT(checked);

                //Secondary extract mode
                case 1 -> autoCrafterBlockEntity.setSecondaryExtractMode(checked);
            }
        });
    }
}
