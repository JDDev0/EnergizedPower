package me.jddev0.ep.networking.packet;

import me.jddev0.ep.block.entity.AdvancedAutoCrafterBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SetAdvancedAutoCrafterRecipeIndexC2SPacket {
    private final BlockPos pos;
    private final int recipeIndex;

    public SetAdvancedAutoCrafterRecipeIndexC2SPacket(BlockPos pos, int recipeId) {
        this.pos = pos;
        this.recipeIndex = recipeId;
    }

    public SetAdvancedAutoCrafterRecipeIndexC2SPacket(FriendlyByteBuf buffer) {
        pos = buffer.readBlockPos();
        recipeIndex = buffer.readInt();
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeInt(recipeIndex);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            Level level = context.getSender().level;
            if(!level.hasChunk(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(pos);
            if(!(blockEntity instanceof AdvancedAutoCrafterBlockEntity advancedAutoCrafterBlockEntity))
                return;

            advancedAutoCrafterBlockEntity.setCurrentRecipeIndex(recipeIndex);
        });

        return true;
    }
}
