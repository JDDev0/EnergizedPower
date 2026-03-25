package me.jddev0.ep.networking.packet;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.entity.WeatherControllerBlockEntity;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public record SetWeatherFromWeatherControllerC2SPacket(BlockPos pos, int weatherType) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SetWeatherFromWeatherControllerC2SPacket> ID =
            new CustomPacketPayload.Type<>(EPAPI.id("set_weather_from_weather_controller"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SetWeatherFromWeatherControllerC2SPacket> PACKET_CODEC =
            StreamCodec.ofMember(SetWeatherFromWeatherControllerC2SPacket::write, SetWeatherFromWeatherControllerC2SPacket::new);

    public SetWeatherFromWeatherControllerC2SPacket(RegistryFriendlyByteBuf buffer) {
        this(buffer.readBlockPos(), buffer.readInt());
    }

    public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeInt(weatherType);
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void receive(SetWeatherFromWeatherControllerC2SPacket data, ServerPlayNetworking.Context context) {
        context.server().execute(() -> {
            if(!context.player().mayBuild())
                return;

            Level level = context.player().level();
            if(!level.hasChunk(SectionPos.blockToSectionCoord(data.pos.getX()), SectionPos.blockToSectionCoord(data.pos.getZ())))
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
                case 0 -> context.player().level().setWeatherParameters(duration, 0, false, false);
                //Rain
                case 1 -> context.player().level().setWeatherParameters(0, duration, true, false);
                //Thunder
                case 2 -> context.player().level().setWeatherParameters(0, duration, true, true);
            }
        });
    }
}
