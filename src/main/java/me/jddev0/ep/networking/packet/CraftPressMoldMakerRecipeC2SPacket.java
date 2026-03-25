package me.jddev0.ep.networking.packet;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.entity.PressMoldMakerBlockEntity;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public record CraftPressMoldMakerRecipeC2SPacket(BlockPos pos, Identifier recipeId) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<CraftPressMoldMakerRecipeC2SPacket> ID =
            new CustomPacketPayload.Type<>(EPAPI.id("craft_press_mold_maker_recipe"));
    public static final StreamCodec<RegistryFriendlyByteBuf, CraftPressMoldMakerRecipeC2SPacket> PACKET_CODEC =
            StreamCodec.ofMember(CraftPressMoldMakerRecipeC2SPacket::write, CraftPressMoldMakerRecipeC2SPacket::new);

    public CraftPressMoldMakerRecipeC2SPacket(RegistryFriendlyByteBuf buffer) {
        this(buffer.readBlockPos(), buffer.readIdentifier());
    }

    public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeIdentifier(recipeId);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void receive(CraftPressMoldMakerRecipeC2SPacket data, ServerPlayNetworking.Context context) {
        context.server().execute(() -> {
            if(!context.player().mayBuild())
                return;

            Level level = context.player().level();
            if(!level.hasChunk(SectionPos.blockToSectionCoord(data.pos.getX()), SectionPos.blockToSectionCoord(data.pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(data.pos);
            if(!(blockEntity instanceof PressMoldMakerBlockEntity pressMoldMakerBlockEntity))
                return;

            pressMoldMakerBlockEntity.craftItem(data.recipeId);
        });
    }
}
