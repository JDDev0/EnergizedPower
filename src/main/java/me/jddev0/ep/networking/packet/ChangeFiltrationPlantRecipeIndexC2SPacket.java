package me.jddev0.ep.networking.packet;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.entity.FiltrationPlantBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ChangeFiltrationPlantRecipeIndexC2SPacket {
    private final BlockPos pos;
    private final boolean downUp;

    public static final ResourceLocation ID = new ResourceLocation(EnergizedPowerMod.MODID, "change_filtration_plant_recipe_index");

    public ChangeFiltrationPlantRecipeIndexC2SPacket(BlockPos pos, boolean downUp) {
        this.pos = pos;
        this.downUp = downUp;
    }

    public ChangeFiltrationPlantRecipeIndexC2SPacket(FriendlyByteBuf buffer) {
        this.pos = buffer.readBlockPos();
        this.downUp = buffer.readBoolean();
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
            if(!(blockEntity instanceof FiltrationPlantBlockEntity filtrationPlantBlockEntity))
                return;

            filtrationPlantBlockEntity.changeRecipeIndex(downUp);
        });

        return true;
    }
}
