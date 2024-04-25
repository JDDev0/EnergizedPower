package me.jddev0.ep.networking.packet;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.entity.FiltrationPlantBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record ChangeFiltrationPlantRecipeIndexC2SPacket(BlockPos pos, boolean downUp) implements CustomPacketPayload {
    public static final Type<ChangeFiltrationPlantRecipeIndexC2SPacket> ID =
            new Type<>(new ResourceLocation(EnergizedPowerMod.MODID, "change_filtration_plant_recipe_index"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ChangeFiltrationPlantRecipeIndexC2SPacket> STREAM_CODEC =
            StreamCodec.ofMember(ChangeFiltrationPlantRecipeIndexC2SPacket::write, ChangeFiltrationPlantRecipeIndexC2SPacket::new);

    public ChangeFiltrationPlantRecipeIndexC2SPacket(RegistryFriendlyByteBuf buffer) {
        this(buffer.readBlockPos(), buffer.readBoolean());
    }

     public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeBoolean(downUp);
    }

    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void handle(ChangeFiltrationPlantRecipeIndexC2SPacket data, IPayloadContext context) {
        context.enqueueWork(() -> {
            if(!(context.player().level() instanceof ServerLevel level) || !(context.player() instanceof ServerPlayer player))
                return;

            if(!level.hasChunk(SectionPos.blockToSectionCoord(data.pos.getX()), SectionPos.blockToSectionCoord(data.pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(data.pos);
            if(!(blockEntity instanceof FiltrationPlantBlockEntity filtrationPlantBlockEntity))
                return;

            filtrationPlantBlockEntity.changeRecipeIndex(data.downUp);
        });
    }
}
