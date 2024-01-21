package me.jddev0.ep.networking.packet;

import me.jddev0.ep.block.entity.FluidTankBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class SetFluidTankCheckboxC2SPacket {
    private final BlockPos pos;
    private final int checkboxId;
    private final boolean checked;

    public SetFluidTankCheckboxC2SPacket(BlockPos pos, int checkboxId, boolean checked) {
        this.pos = pos;
        this.checkboxId = checkboxId;
        this.checked = checked;
    }

    public SetFluidTankCheckboxC2SPacket(FriendlyByteBuf buffer) {
        this.pos = buffer.readBlockPos();
        this.checkboxId = buffer.readInt();
        this.checked = buffer.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeInt(checkboxId);
        buffer.writeBoolean(checked);
    }

    public void handle(CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            Level level = context.getSender().level();
            if(!level.hasChunk(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(pos);
            if(!(blockEntity instanceof FluidTankBlockEntity fluidTankBlockEntity))
                return;

            switch(checkboxId) {
                //Ignore NBT
                case 0 -> fluidTankBlockEntity.setIgnoreNBT(checked);
            }
        });
    }
}
