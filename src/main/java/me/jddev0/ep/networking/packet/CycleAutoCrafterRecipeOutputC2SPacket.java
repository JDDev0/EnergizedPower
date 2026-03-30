package me.jddev0.ep.networking.packet;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.entity.AutoCrafterBlockEntity;
import me.jddev0.ep.screen.AutoCrafterMenu;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public record CycleAutoCrafterRecipeOutputC2SPacket(BlockPos pos) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<CycleAutoCrafterRecipeOutputC2SPacket> ID =
            new CustomPacketPayload.Type<>(EPAPI.id("cycle_auto_crafter_recipe_output"));
    public static final StreamCodec<RegistryFriendlyByteBuf, CycleAutoCrafterRecipeOutputC2SPacket> STREAM_CODEC =
            StreamCodec.ofMember(CycleAutoCrafterRecipeOutputC2SPacket::write, CycleAutoCrafterRecipeOutputC2SPacket::new);

    public CycleAutoCrafterRecipeOutputC2SPacket(RegistryFriendlyByteBuf buffer) {
        this(buffer.readBlockPos());
    }

    public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void receive(CycleAutoCrafterRecipeOutputC2SPacket data, ServerPlayNetworking.Context context) {
        context.server().execute(() -> {
            if(!context.player().mayBuild())
                return;

            Level level = context.player().level();
            if(!level.hasChunk(SectionPos.blockToSectionCoord(data.pos.getX()), SectionPos.blockToSectionCoord(data.pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(data.pos);
            if(!(blockEntity instanceof AutoCrafterBlockEntity autoCrafterBlockEntity))
                return;

            AbstractContainerMenu menu = context.player().containerMenu;

            if(!(menu instanceof AutoCrafterMenu))
                return;

            autoCrafterBlockEntity.cycleRecipe();

            autoCrafterBlockEntity.resetProgressAndMarkAsChanged();
        });
    }
}
