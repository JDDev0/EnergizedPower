package me.jddev0.ep.networking.packet;

import me.jddev0.ep.block.entity.AutoCrafterBlockEntity;
import me.jddev0.ep.screen.AutoCrafterMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CycleAutoCrafterRecipeOutputC2SPacket {
    private final BlockPos pos;

    public CycleAutoCrafterRecipeOutputC2SPacket(BlockPos pos) {
        this.pos = pos;
    }

    public CycleAutoCrafterRecipeOutputC2SPacket(FriendlyByteBuf buffer) {
        pos = buffer.readBlockPos();
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            BlockEntity blockEntity = context.getSender().level.getBlockEntity(pos);
            if(!(blockEntity instanceof AutoCrafterBlockEntity autoCrafterBlockEntity))
                return;

            AbstractContainerMenu menu = context.getSender().containerMenu;

            if(!(menu instanceof AutoCrafterMenu))
                return;

            autoCrafterBlockEntity.cycleRecipe();

            autoCrafterBlockEntity.resetProgressAndMarkAsChanged();
        });

        return true;
    }
}
