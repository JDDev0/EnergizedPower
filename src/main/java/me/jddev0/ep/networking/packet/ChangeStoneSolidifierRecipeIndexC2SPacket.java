package me.jddev0.ep.networking.packet;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.entity.StoneSolidifierBlockEntity;
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

public record ChangeStoneSolidifierRecipeIndexC2SPacket(BlockPos pos, boolean downUp) implements CustomPacketPayload {
    public static final Type<ChangeStoneSolidifierRecipeIndexC2SPacket> ID =
            new Type<>(new ResourceLocation(EnergizedPowerMod.MODID, "change_stone_solidifer_recipe_index"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ChangeStoneSolidifierRecipeIndexC2SPacket> STREAM_CODEC =
            StreamCodec.ofMember(ChangeStoneSolidifierRecipeIndexC2SPacket::write, ChangeStoneSolidifierRecipeIndexC2SPacket::new);

    public ChangeStoneSolidifierRecipeIndexC2SPacket(RegistryFriendlyByteBuf buffer) {
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

    public static void handle(ChangeStoneSolidifierRecipeIndexC2SPacket data, IPayloadContext context) {
        context.enqueueWork(() -> {
            if(!(context.player().level() instanceof ServerLevel level) || !(context.player() instanceof ServerPlayer player))
                return;

            if(!level.hasChunk(SectionPos.blockToSectionCoord(data.pos.getX()), SectionPos.blockToSectionCoord(data.pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(data.pos);
            if(!(blockEntity instanceof StoneSolidifierBlockEntity stoneSolidifierBlockEntity))
                return;

            stoneSolidifierBlockEntity.changeRecipeIndex(data.downUp);
        });
    }
}
