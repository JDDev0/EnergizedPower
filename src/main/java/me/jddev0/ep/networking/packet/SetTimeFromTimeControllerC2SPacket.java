package me.jddev0.ep.networking.packet;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.entity.TimeControllerBlockEntity;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import team.reborn.energy.api.EnergyStorage;

public record SetTimeFromTimeControllerC2SPacket(BlockPos pos, int time) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SetTimeFromTimeControllerC2SPacket> ID =
            new CustomPacketPayload.Type<>(EPAPI.id("set_time_from_time_controller"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SetTimeFromTimeControllerC2SPacket> PACKET_CODEC =
            StreamCodec.ofMember(SetTimeFromTimeControllerC2SPacket::write, SetTimeFromTimeControllerC2SPacket::new);

    public SetTimeFromTimeControllerC2SPacket(RegistryFriendlyByteBuf buffer) {
        this(buffer.readBlockPos(), buffer.readInt());
    }

    public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeInt(time);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void receive(SetTimeFromTimeControllerC2SPacket data, ServerPlayNetworking.Context context) {
        context.server().execute(() -> {
            if(!context.player().mayBuild())
                return;

            Level level = context.player().level();
            if(!level.hasChunk(SectionPos.blockToSectionCoord(data.pos.getX()), SectionPos.blockToSectionCoord(data.pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(data.pos);
            if(!(blockEntity instanceof TimeControllerBlockEntity timeControllerBlockEntity))
                return;

            EnergyStorage energyStorage = EnergyStorage.SIDED.find(context.player().level(), data.pos, null);
            if(energyStorage == null)
                return;

            if(energyStorage.getAmount() < TimeControllerBlockEntity.CAPACITY)
                return;

            timeControllerBlockEntity.clearEnergy();

            if(data.time < 0 || data.time > 24000)
                return;

            long currentTime = context.player().level().getDayTime();

            int currentDayTime = (int)(currentTime % 24000);

            if(currentDayTime <= data.time)
                context.player().level().setDayTime(currentTime - currentDayTime + data.time);
            else
                context.player().level().setDayTime(currentTime + 24000 - currentDayTime + data.time);
        });
    }
}
