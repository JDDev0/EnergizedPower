package me.jddev0.ep.networking.packet;

import me.jddev0.ep.block.entity.AutoCrafterBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SetAutoCrafterCheckboxC2SPacket {
    private final BlockPos pos;
    private final int checkboxId;
    private final boolean checked;

    public SetAutoCrafterCheckboxC2SPacket(BlockPos pos, int checkboxId, boolean checked) {
        this.pos = pos;
        this.checkboxId = checkboxId;
        this.checked = checked;
    }

    public SetAutoCrafterCheckboxC2SPacket(FriendlyByteBuf buffer) {
        pos = buffer.readBlockPos();
        checkboxId = buffer.readInt();
        checked = buffer.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeInt(checkboxId);
        buffer.writeBoolean(checked);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            Level level = context.getSender().getLevel();
            if(!level.hasChunk(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(pos);
            if(!(blockEntity instanceof AutoCrafterBlockEntity autoCrafterBlockEntity))
                return;

            switch(checkboxId) {
                //Ignore NBT
                case 0 -> autoCrafterBlockEntity.setIgnoreNBT(checked);

                //Secondary extract mode
                case 1 -> autoCrafterBlockEntity.setSecondaryExtractMode(checked);
            }
        });

        return true;
    }
}
