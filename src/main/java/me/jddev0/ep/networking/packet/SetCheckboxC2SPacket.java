package me.jddev0.ep.networking.packet;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.machine.CheckboxUpdate;
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

public record SetCheckboxC2SPacket(BlockPos pos, int checkboxId, boolean checked) implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(EnergizedPowerMod.MODID, "set_checkbox");


    public SetCheckboxC2SPacket(FriendlyByteBuf buffer) {
        this(buffer.readBlockPos(), buffer.readInt(), buffer.readBoolean());
    }

    @Override
    public void write(final FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeInt(checkboxId);
        buffer.writeBoolean(checked);
    }

    @Override
    @NotNull
    public ResourceLocation id() {
        return ID;
    }

    public static void handle(final SetCheckboxC2SPacket data, final PlayPayloadContext context) {
        context.workHandler().execute(() -> {
            if(context.level().isEmpty() || !(context.level().get() instanceof ServerLevel level) ||
                    context.player().isEmpty() || !(context.player().get() instanceof ServerPlayer player))
                return;

            if(!level.hasChunk(SectionPos.blockToSectionCoord(data.pos.getX()), SectionPos.blockToSectionCoord(data.pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(data.pos);
            if(!(blockEntity instanceof CheckboxUpdate checkboxUpdate))
                return;

            checkboxUpdate.setCheckbox(data.checkboxId, data.checked);
        });
    }
}
