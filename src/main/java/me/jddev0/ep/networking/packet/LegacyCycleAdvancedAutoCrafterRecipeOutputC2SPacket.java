package me.jddev0.ep.networking.packet;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.entity.AdvancedAutoCrafterBlockEntity;
import me.jddev0.ep.screen.AdvancedAutoCrafterMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record LegacyCycleAdvancedAutoCrafterRecipeOutputC2SPacket(BlockPos pos) implements CustomPacketPayload {
    public static final Type<LegacyCycleAdvancedAutoCrafterRecipeOutputC2SPacket> ID =
            new Type<>(EPAPI.id("cycle_advanced_auto_crafter_recipe_output"));
    public static final StreamCodec<RegistryFriendlyByteBuf, LegacyCycleAdvancedAutoCrafterRecipeOutputC2SPacket> STREAM_CODEC =
            StreamCodec.ofMember(LegacyCycleAdvancedAutoCrafterRecipeOutputC2SPacket::write, LegacyCycleAdvancedAutoCrafterRecipeOutputC2SPacket::new);

    public LegacyCycleAdvancedAutoCrafterRecipeOutputC2SPacket(RegistryFriendlyByteBuf buffer) {
        this(buffer.readBlockPos());
    }

     public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
    }

    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void handle(LegacyCycleAdvancedAutoCrafterRecipeOutputC2SPacket data, IPayloadContext context) {
        context.enqueueWork(() -> {
            if(!(context.player().level() instanceof ServerLevel level) || !(context.player() instanceof ServerPlayer player))
                return;

            if(!level.hasChunk(SectionPos.blockToSectionCoord(data.pos.getX()), SectionPos.blockToSectionCoord(data.pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(data.pos);
            if(!(blockEntity instanceof AdvancedAutoCrafterBlockEntity advancedAutoCrafterBlockEntity))
                return;

            AbstractContainerMenu menu = player.containerMenu;

            if(!(menu instanceof AdvancedAutoCrafterMenu))
                return;

            int recipeIndex = advancedAutoCrafterBlockEntity.getCurrentRecipeIndex();

            advancedAutoCrafterBlockEntity.cycleRecipe();

            advancedAutoCrafterBlockEntity.resetProgressAndMarkAsChanged(recipeIndex);
        });
    }
}
