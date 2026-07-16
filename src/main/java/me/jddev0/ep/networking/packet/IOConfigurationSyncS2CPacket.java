package me.jddev0.ep.networking.packet;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.machine.configuration.IOConfiguration;
import me.jddev0.ep.machine.configuration.IOConfigurationUpdate;
import me.jddev0.ep.machine.configuration.SlotType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record IOConfigurationSyncS2CPacket(SlotType slotType, IOConfiguration configuration, BlockPos pos) implements CustomPacketPayload {
    public static final Type<IOConfigurationSyncS2CPacket> ID =
            new Type<>(EPAPI.id("io_configuration_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, IOConfigurationSyncS2CPacket> STREAM_CODEC =
            StreamCodec.ofMember(IOConfigurationSyncS2CPacket::write, IOConfigurationSyncS2CPacket::new);

    public IOConfigurationSyncS2CPacket(RegistryFriendlyByteBuf buffer) {
        this(buffer.readEnum(SlotType.class), IOConfiguration.STREAM_CODEC.decode(buffer), buffer.readBlockPos());
    }

    public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeEnum(slotType);
        IOConfiguration.STREAM_CODEC.encode(buffer, configuration);
        buffer.writeBlockPos(pos);
    }

    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void handle(IOConfigurationSyncS2CPacket data, IPayloadContext context) {
        context.enqueueWork(() -> {
            BlockEntity blockEntity = context.player().level().getBlockEntity(data.pos);

            //BlockEntity
            if(blockEntity instanceof IOConfigurationUpdate ioConfigurationUpdate) {
                ioConfigurationUpdate.setIOConfiguration(data.slotType, data.configuration);
            }
        });
    }
}
