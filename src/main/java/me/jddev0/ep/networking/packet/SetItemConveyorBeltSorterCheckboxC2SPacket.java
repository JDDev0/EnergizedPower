package me.jddev0.ep.networking.packet;

import me.jddev0.ep.block.entity.ItemConveyorBeltSorterBlockEntity;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;

public class SetItemConveyorBeltSorterCheckboxC2SPacket {
    public SetItemConveyorBeltSorterCheckboxC2SPacket() {}

    public static boolean receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
                           PacketByteBuf buf, PacketSender responseSender) {
        BlockPos pos = buf.readBlockPos();
        int checkboxId = buf.readInt();
        boolean checked = buf.readBoolean();

        server.execute(() -> {
            World level = player.getWorld();
            if(!level.isChunkLoaded(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(pos);
            if(!(blockEntity instanceof ItemConveyorBeltSorterBlockEntity itemConveyorBeltSorterBlockEntity))
                return;

            switch(checkboxId) {
                //Whitelist [3x]
                case 0, 1, 2 -> itemConveyorBeltSorterBlockEntity.setWhitelist(checkboxId, checked);

                //Ignore NBT [3x]
                case 3, 4, 5 -> itemConveyorBeltSorterBlockEntity.setIgnoreNBT(checkboxId - 3, checked);
            }
        });

        return true;
    }
}
