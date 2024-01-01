package me.jddev0.ep.networking.packet;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.entity.PressMoldMakerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import org.jetbrains.annotations.NotNull;

public record CraftPressMoldMakerRecipeC2SPacket(BlockPos pos, ResourceLocation resourceLocation) implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(EnergizedPowerMod.MODID, "craft_press_mold_maker_recipe");

    public CraftPressMoldMakerRecipeC2SPacket(FriendlyByteBuf buffer) {
        this(buffer.readBlockPos(), buffer.readResourceLocation());
    }

    @Override
    public void write(final FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeResourceLocation(resourceLocation);
    }

    @Override
    @NotNull
    public ResourceLocation id() {
        return ID;
    }

    public static void handle(final CraftPressMoldMakerRecipeC2SPacket data, final PlayPayloadContext context) {
        context.workHandler().execute(() -> {
            if(context.level().isEmpty() || !(context.level().get() instanceof ServerLevel level) ||
                    context.player().isEmpty() || !(context.player().get() instanceof ServerPlayer player))
                return;

            if(!level.hasChunk(SectionPos.blockToSectionCoord(data.pos.getX()), SectionPos.blockToSectionCoord(data.pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(data.pos);
            if(!(blockEntity instanceof PressMoldMakerBlockEntity pressMoldMakerBlockEntity))
                return;

            pressMoldMakerBlockEntity.craftItem(data.resourceLocation);
        });
    }
}
