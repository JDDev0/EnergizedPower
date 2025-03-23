package me.jddev0.ep.networking.packet;

import me.jddev0.ep.block.entity.AdvancedAutoCrafterBlockEntity;
import me.jddev0.ep.screen.AdvancedAutoCrafterMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CycleAdvancedAutoCrafterRecipeOutputC2SPacket {
    private final BlockPos pos;

    public CycleAdvancedAutoCrafterRecipeOutputC2SPacket(BlockPos pos) {
        this.pos = pos;
    }

    public CycleAdvancedAutoCrafterRecipeOutputC2SPacket(FriendlyByteBuf buffer) {
        pos = buffer.readBlockPos();
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            Level level = context.getSender().level();
            if(!level.hasChunk(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(pos);
            if(!(blockEntity instanceof AdvancedAutoCrafterBlockEntity advancedAutoCrafterBlockEntity))
                return;

            AbstractContainerMenu menu = context.getSender().containerMenu;

            if(!(menu instanceof AdvancedAutoCrafterMenu))
                return;

            int recipeIndex = advancedAutoCrafterBlockEntity.getCurrentRecipeIndex();

            advancedAutoCrafterBlockEntity.cycleRecipe();

            advancedAutoCrafterBlockEntity.resetProgressAndMarkAsChanged(recipeIndex);
        });

        return true;
    }
}
