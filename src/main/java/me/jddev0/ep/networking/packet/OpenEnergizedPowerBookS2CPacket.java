package me.jddev0.ep.networking.packet;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.screen.EnergizedPowerBookScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record OpenEnergizedPowerBookS2CPacket(BlockPos pos) implements CustomPacketPayload {
    public static final Type<OpenEnergizedPowerBookS2CPacket> ID =
            new Type<>(ResourceLocation.fromNamespaceAndPath(EnergizedPowerMod.MODID, "open_energized_power_book"));
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenEnergizedPowerBookS2CPacket> STREAM_CODEC =
            StreamCodec.ofMember(OpenEnergizedPowerBookS2CPacket::write, OpenEnergizedPowerBookS2CPacket::new);

    public OpenEnergizedPowerBookS2CPacket(RegistryFriendlyByteBuf buffer) {
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

    public static void handle(OpenEnergizedPowerBookS2CPacket data, IPayloadContext context) {
        context.enqueueWork(() -> {
            BlockEntity blockEntity = context.player().level().getBlockEntity(data.pos);

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
