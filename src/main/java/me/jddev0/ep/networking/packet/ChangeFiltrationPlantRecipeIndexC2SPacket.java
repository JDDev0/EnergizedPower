package me.jddev0.ep.networking.packet;

import me.jddev0.ep.block.entity.FiltrationPlantBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.NetworkEvent;

public class ChangeFiltrationPlantRecipeIndexC2SPacket {
    private final BlockPos pos;
    private final boolean downUp;

    public ChangeFiltrationPlantRecipeIndexC2SPacket(BlockPos pos, boolean downUp) {
        this.pos = pos;
        this.downUp = downUp;
    }

    public ChangeFiltrationPlantRecipeIndexC2SPacket(FriendlyByteBuf buffer) {
        pos = buffer.readBlockPos();
        downUp = buffer.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeBoolean(downUp);
    }

    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            Level level = context.getSender().level();
            if(!level.hasChunk(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(pos);
            if(!(blockEntity instanceof FiltrationPlantBlockEntity filtrationPlantBlockEntity))
                return;

            filtrationPlantBlockEntity.changeRecipeIndex(downUp);
        });

        return true;
    }
}
