package me.jddev0.ep.networking.packet;

import me.jddev0.ep.block.entity.StoneSolidifierBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ChangeStoneSolidifierRecipeIndexC2SPacket {
    private final BlockPos pos;
    private final boolean downUp;

    public ChangeStoneSolidifierRecipeIndexC2SPacket(BlockPos pos, boolean downUp) {
        this.pos = pos;
        this.downUp = downUp;
    }

    public ChangeStoneSolidifierRecipeIndexC2SPacket(FriendlyByteBuf buffer) {
        pos = buffer.readBlockPos();
        downUp = buffer.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeBoolean(downUp);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            Level level = context.getSender().level();
            if(!level.hasChunk(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(pos);
            if(!(blockEntity instanceof StoneSolidifierBlockEntity stoneSolidifierBlockEntity))
                return;

            stoneSolidifierBlockEntity.changeRecipeIndex(downUp);
        });

        return true;
    }
}
