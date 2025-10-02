package me.jddev0.ep.networking.packet;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.entity.WeatherControllerBlockEntity;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;

public record SetWeatherFromWeatherControllerC2SPacket(BlockPos pos, int weatherType) implements CustomPayload {
    public static final CustomPayload.Id<SetWeatherFromWeatherControllerC2SPacket> ID =
            new CustomPayload.Id<>(EPAPI.id("set_weather_from_weather_controller"));
    public static final PacketCodec<RegistryByteBuf, SetWeatherFromWeatherControllerC2SPacket> PACKET_CODEC =
            PacketCodec.of(SetWeatherFromWeatherControllerC2SPacket::write, SetWeatherFromWeatherControllerC2SPacket::new);

    public SetWeatherFromWeatherControllerC2SPacket(RegistryByteBuf buffer) {
        this(buffer.readBlockPos(), buffer.readInt());
    }

    public void write(RegistryByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeInt(weatherType);
    }

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(SetWeatherFromWeatherControllerC2SPacket data, ServerPlayNetworking.Context context) {
        context.server().execute(() -> {
            if(!context.player().canModifyBlocks())
                return;

            World level = context.player().getEntityWorld();
            if(!level.isChunkLoaded(ChunkSectionPos.getSectionCoord(data.pos.getX()), ChunkSectionPos.getSectionCoord(data.pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(data.pos);
            if(!(blockEntity instanceof WeatherControllerBlockEntity weatherControllerBlockEntity))
                return;

            if(!weatherControllerBlockEntity.hasEnoughEnergy())
                return;

            if(weatherControllerBlockEntity.hasInfiniteWeatherChangedDuration()) {
                if(weatherControllerBlockEntity.getSelectedWeatherType() == data.weatherType) {
                    weatherControllerBlockEntity.setSelectedWeatherType(-1);

                    return;
                }

                weatherControllerBlockEntity.setSelectedWeatherType(data.weatherType);
            }else {
                weatherControllerBlockEntity.clearEnergy();
            }

            int duration = weatherControllerBlockEntity.getWeatherChangedDuration();

            switch(data.weatherType) {
                //Clear
                case 0 -> context.player().getEntityWorld().setWeather(duration, 0, false, false);
                //Rain
                case 1 -> context.player().getEntityWorld().setWeather(0, duration, true, false);
                //Thunder
                case 2 -> context.player().getEntityWorld().setWeather(0, duration, true, true);
            }
        });
    }
}
