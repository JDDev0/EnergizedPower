package me.jddev0.ep.networking.packet;

import me.jddev0.ep.block.entity.TeleporterBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UseTeleporterC2SPacket {
    private final BlockPos pos;

    public UseTeleporterC2SPacket(BlockPos pos) {
        this.pos = pos;
    }

    public UseTeleporterC2SPacket(FriendlyByteBuf buffer) {
        pos = buffer.readBlockPos();
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            Level level = context.getSender().getLevel();
            if(!level.hasChunk(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ())))
                return;

            ServerPlayer player = context.getSender();
            if(player == null)
                return;

            BlockEntity blockEntity = level.getBlockEntity(pos);
            if(!(blockEntity instanceof TeleporterBlockEntity teleporterBlockEntity))
                return;

            teleporterBlockEntity.teleportPlayer(player);
        });

        return true;
    }
}
