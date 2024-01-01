package me.jddev0.ep.networking.packet;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.screen.EnergizedPowerBookScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import org.jetbrains.annotations.NotNull;

public record OpenEnergizedPowerBookS2CPacket(BlockPos pos) implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(EnergizedPowerMod.MODID, "open_energized_power_book");

    public OpenEnergizedPowerBookS2CPacket(FriendlyByteBuf buffer) {
        this(buffer.readBlockPos());
    }

    @Override
    public void write(final FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
    }

    @Override
    @NotNull
    public ResourceLocation id() {
        return ID;
    }

    public static void handle(final OpenEnergizedPowerBookS2CPacket data, final PlayPayloadContext context) {
        context.workHandler().execute(() -> {
            if(context.level().isEmpty())
                return;

            BlockEntity blockEntity = context.level().get().getBlockEntity(data.pos);

            if(blockEntity instanceof LecternBlockEntity) {
                LecternBlockEntity lecternBlockEntity = (LecternBlockEntity)blockEntity;

                showBookViewScreen(lecternBlockEntity);
            }
        });
    }

    @OnlyIn(Dist.CLIENT)
    private static void showBookViewScreen(LecternBlockEntity lecternBlockEntity) {
        Minecraft.getInstance().setScreen(new EnergizedPowerBookScreen(lecternBlockEntity));
    }
}
