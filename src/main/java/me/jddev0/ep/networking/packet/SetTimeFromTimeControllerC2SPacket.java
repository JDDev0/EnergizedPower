package me.jddev0.ep.networking.packet;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.entity.TimeControllerBlockEntity;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;
import team.reborn.energy.api.EnergyStorage;

public record SetTimeFromTimeControllerC2SPacket(BlockPos pos, int time) implements CustomPayload {
    public static final CustomPayload.Id<SetTimeFromTimeControllerC2SPacket> ID =
            new CustomPayload.Id<>(EPAPI.id("set_time_from_time_controller"));
    public static final PacketCodec<RegistryByteBuf, SetTimeFromTimeControllerC2SPacket> PACKET_CODEC =
            PacketCodec.of(SetTimeFromTimeControllerC2SPacket::write, SetTimeFromTimeControllerC2SPacket::new);

    public SetTimeFromTimeControllerC2SPacket(RegistryByteBuf buffer) {
        this(buffer.readBlockPos(), buffer.readInt());
    }

    public void write(RegistryByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeInt(time);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(SetTimeFromTimeControllerC2SPacket data, ServerPlayNetworking.Context context) {
        context.server().execute(() -> {
            if(!context.player().canModifyBlocks())
                return;

            World level = context.player().getEntityWorld();
            if(!level.isChunkLoaded(ChunkSectionPos.getSectionCoord(data.pos.getX()), ChunkSectionPos.getSectionCoord(data.pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(data.pos);
            if(!(blockEntity instanceof TimeControllerBlockEntity timeControllerBlockEntity))
                return;

            EnergyStorage energyStorage = EnergyStorage.SIDED.find(context.player().getEntityWorld(), data.pos, null);
            if(energyStorage == null)
                return;

            if(energyStorage.getAmount() < TimeControllerBlockEntity.CAPACITY)
                return;

            timeControllerBlockEntity.clearEnergy();

            if(data.time < 0 || data.time > 24000)
                return;

            long currentTime = context.player().getEntityWorld().getTimeOfDay();

            int currentDayTime = (int)(currentTime % 24000);

            if(currentDayTime <= data.time)
                context.player().getEntityWorld().setTimeOfDay(currentTime - currentDayTime + data.time);
            else
                context.player().getEntityWorld().setTimeOfDay(currentTime + 24000 - currentDayTime + data.time);
        });
    }
}
