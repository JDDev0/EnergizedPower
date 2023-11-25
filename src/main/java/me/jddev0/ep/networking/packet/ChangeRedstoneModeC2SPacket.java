package me.jddev0.ep.networking.packet;

import me.jddev0.ep.machine.configuration.RedstoneModeUpdate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class ChangeRedstoneModeC2SPacket {
    private final BlockPos pos;

    public ChangeRedstoneModeC2SPacket(BlockPos pos) {
        this.pos = pos;
    }

    public ChangeRedstoneModeC2SPacket(FriendlyByteBuf buffer) {
        pos = buffer.readBlockPos();
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
    }

    public boolean handle(CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            Level level = context.getSender().level();
            if(!level.hasChunk(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(pos);
            if(!(blockEntity instanceof RedstoneModeUpdate redstoneModeUpdate))
                return;

            redstoneModeUpdate.setNextRedstoneMode();
        });

        return true;
    }
}
