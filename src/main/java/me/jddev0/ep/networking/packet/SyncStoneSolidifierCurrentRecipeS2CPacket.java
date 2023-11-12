package me.jddev0.ep.networking.packet;

import me.jddev0.ep.block.entity.StoneSolidifierBlockEntity;
import me.jddev0.ep.recipe.StoneSolidifierRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class SyncStoneSolidifierCurrentRecipeS2CPacket {
    private final BlockPos pos;
    private final StoneSolidifierRecipe currentRecipe;

    public SyncStoneSolidifierCurrentRecipeS2CPacket(BlockPos pos, @Nullable StoneSolidifierRecipe currentRecipe) {
        this.pos = pos;
        this.currentRecipe = currentRecipe;
    }

    public SyncStoneSolidifierCurrentRecipeS2CPacket(FriendlyByteBuf buffer) {
        pos = buffer.readBlockPos();

        currentRecipe = buffer.readBoolean()?StoneSolidifierRecipe.Serializer.INSTANCE.fromNetwork(
                buffer.readResourceLocation(), buffer):null;
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);

        if(currentRecipe == null) {
            buffer.writeBoolean(false);
        }else {
            buffer.writeBoolean(true);

            buffer.writeResourceLocation(currentRecipe.getId());
            StoneSolidifierRecipe.Serializer.INSTANCE.toNetwork(buffer, currentRecipe);
        }
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(pos);

            //BlockEntity
            if(blockEntity instanceof StoneSolidifierBlockEntity stoneSolidifierBlockEntity) {
                stoneSolidifierBlockEntity.setCurrentRecipe(currentRecipe);
            }
        });

        return true;
    }
}
