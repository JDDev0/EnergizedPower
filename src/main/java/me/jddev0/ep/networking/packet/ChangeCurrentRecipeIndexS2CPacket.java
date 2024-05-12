package me.jddev0.ep.networking.packet;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.recipe.ChangeCurrentRecipeIndexPacketUpdate;
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

public record ChangeCurrentRecipeIndexS2CPacket(BlockPos pos, boolean downUp) implements CustomPacketPayload {
    public static final Type<ChangeCurrentRecipeIndexS2CPacket> ID =
            new Type<>(new ResourceLocation(EnergizedPowerMod.MODID, "change_current_recipe_index"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ChangeCurrentRecipeIndexS2CPacket> STREAM_CODEC =
            StreamCodec.ofMember(ChangeCurrentRecipeIndexS2CPacket::write, ChangeCurrentRecipeIndexS2CPacket::new);

    public ChangeCurrentRecipeIndexS2CPacket(RegistryFriendlyByteBuf buffer) {
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

    public static void handle(ChangeCurrentRecipeIndexS2CPacket data, IPayloadContext context) {
        context.enqueueWork(() -> {
            if(!(context.player().level() instanceof ServerLevel level) || !(context.player() instanceof ServerPlayer player))
                return;

            if(!level.hasChunk(SectionPos.blockToSectionCoord(data.pos.getX()), SectionPos.blockToSectionCoord(data.pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(data.pos);
            if(!(blockEntity instanceof ChangeCurrentRecipeIndexPacketUpdate changeCurrentRecipeIndexPacketUpdate))
                return;

            changeCurrentRecipeIndexPacketUpdate.changeRecipeIndex(data.downUp);
        });
    }
}
