package me.jddev0.ep.networking.packet;

import me.jddev0.ep.block.entity.PressMoldMakerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class CraftPressMoldMakerRecipeC2SPacket {
    private final BlockPos pos;
    private final ResourceLocation resourceLocation;

    public CraftPressMoldMakerRecipeC2SPacket(BlockPos pos, ResourceLocation resourceLocation) {
        this.pos = pos;
        this.resourceLocation = resourceLocation;
    }

    public CraftPressMoldMakerRecipeC2SPacket(FriendlyByteBuf buffer) {
        pos = buffer.readBlockPos();
        resourceLocation = buffer.readResourceLocation();
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeResourceLocation(resourceLocation);
    }

    public boolean handle(CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            Level level = context.getSender().level();
            if(!level.hasChunk(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(pos);
            if(!(blockEntity instanceof PressMoldMakerBlockEntity pressMoldMakerBlockEntity))
                return;

            pressMoldMakerBlockEntity.craftItem(resourceLocation);
        });

        return true;
    }
}
