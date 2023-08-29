package me.jddev0.ep.networking.packet;

import me.jddev0.ep.block.entity.ItemConveyorBeltSorterBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SetItemConveyorBeltSorterCheckboxC2SPacket {
    private final BlockPos pos;
    private final int checkboxId;
    private final boolean checked;

    public SetItemConveyorBeltSorterCheckboxC2SPacket(BlockPos pos, int checkboxId, boolean checked) {
        this.pos = pos;
        this.checkboxId = checkboxId;
        this.checked = checked;
    }

    public SetItemConveyorBeltSorterCheckboxC2SPacket(FriendlyByteBuf buffer) {
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
            Level level = context.getSender().level();
            if(!level.hasChunk(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ())))
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
