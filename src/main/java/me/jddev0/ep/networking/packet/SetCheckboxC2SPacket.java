package me.jddev0.ep.networking.packet;

import me.jddev0.ep.machine.CheckboxUpdate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.NetworkEvent;

public class SetCheckboxC2SPacket {
    private final BlockPos pos;
    private final int checkboxId;
    private final boolean checked;

    public SetCheckboxC2SPacket(BlockPos pos, int checkboxId, boolean checked) {
        this.pos = pos;
        this.checkboxId = checkboxId;
        this.checked = checked;
    }

    public SetCheckboxC2SPacket(FriendlyByteBuf buffer) {
        pos = buffer.readBlockPos();
        checkboxId = buffer.readInt();
        checked = buffer.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeInt(checkboxId);
        buffer.writeBoolean(checked);
    }

    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            Level level = context.getSender().level();
            if(!level.hasChunk(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(pos);
            if(!(blockEntity instanceof CheckboxUpdate checkboxUpdate))
                return;

            checkboxUpdate.setCheckbox(checkboxId, checked);
        });

        return true;
    }
}
