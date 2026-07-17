package me.jddev0.ep.networking.packet;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.machine.configuration.RelativeDirection;
import me.jddev0.ep.machine.configuration.SetIOConfigurationUpdate;
import me.jddev0.ep.machine.configuration.SlotType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

public record SetIOConfigurationC2SPacket(BlockPos pos, SlotType slotType, RelativeDirection direction, int slotGroupId) implements CustomPacketPayload {
    public static final Type<SetIOConfigurationC2SPacket> ID =
            new Type<>(EPAPI.id("set_io_configuration"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SetIOConfigurationC2SPacket> STREAM_CODEC =
            StreamCodec.ofMember(SetIOConfigurationC2SPacket::write, SetIOConfigurationC2SPacket::new);

    public SetIOConfigurationC2SPacket(RegistryFriendlyByteBuf buffer) {
        this(buffer.readBlockPos(), buffer.readEnum(SlotType.class), buffer.readEnum(RelativeDirection.class), buffer.readVarInt());
    }

    public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeEnum(slotType);
        buffer.writeEnum(direction);
        buffer.writeVarInt(slotGroupId);
    }

    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void receive(SetIOConfigurationC2SPacket data, ServerPlayNetworking.Context context) {
        context.server().execute(() -> {
            if(!context.player().mayBuild())
                return;

            Level level = context.player().level();
            if(!level.hasChunk(SectionPos.blockToSectionCoord(data.pos.getX()), SectionPos.blockToSectionCoord(data.pos.getZ())))
                return;

            if(!level.hasChunk(SectionPos.blockToSectionCoord(data.pos.getX()), SectionPos.blockToSectionCoord(data.pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(data.pos);
            if(!(blockEntity instanceof SetIOConfigurationUpdate setIOConfigurationUpdate))
                return;

            setIOConfigurationUpdate.setIOConfigurationByPlayer(data.slotType, data.direction, data.slotGroupId, context.player());
        });
    }
}
