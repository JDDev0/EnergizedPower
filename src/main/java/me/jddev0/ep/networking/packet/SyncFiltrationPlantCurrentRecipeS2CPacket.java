package me.jddev0.ep.networking.packet;

import me.jddev0.ep.block.entity.FiltrationPlantBlockEntity;
import me.jddev0.ep.recipe.FiltrationPlantRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class SyncFiltrationPlantCurrentRecipeS2CPacket {
    private final BlockPos pos;
    private final FiltrationPlantRecipe currentRecipe;

    public SyncFiltrationPlantCurrentRecipeS2CPacket(BlockPos pos, @Nullable FiltrationPlantRecipe currentRecipe) {
        this.pos = pos;
        this.currentRecipe = currentRecipe;
    }

    public SyncFiltrationPlantCurrentRecipeS2CPacket(FriendlyByteBuf buffer) {
        pos = buffer.readBlockPos();

        currentRecipe = buffer.readBoolean()?FiltrationPlantRecipe.Serializer.INSTANCE.fromNetwork(
                buffer.readResourceLocation(), buffer):null;
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);

        if(currentRecipe == null) {
            buffer.writeBoolean(false);
        }else {
            buffer.writeBoolean(true);

            buffer.writeResourceLocation(currentRecipe.getId());
            FiltrationPlantRecipe.Serializer.INSTANCE.toNetwork(buffer, currentRecipe);
        }
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(pos);

            //BlockEntity
            if(blockEntity instanceof FiltrationPlantBlockEntity filtrationPlantBlockEntity) {
                filtrationPlantBlockEntity.setCurrentRecipe(currentRecipe);
            }
        });

        return true;
    }
}
