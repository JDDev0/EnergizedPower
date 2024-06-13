package me.jddev0.ep.networking.packet;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.entity.WeatherControllerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record SetWeatherFromWeatherControllerC2SPacket(BlockPos pos, int weatherType) implements CustomPacketPayload {
    public static final Type<SetWeatherFromWeatherControllerC2SPacket> ID =
            new Type<>(ResourceLocation.fromNamespaceAndPath(EnergizedPowerMod.MODID, "set_weather_from_weather_controller"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SetWeatherFromWeatherControllerC2SPacket> STREAM_CODEC =
            StreamCodec.ofMember(SetWeatherFromWeatherControllerC2SPacket::write, SetWeatherFromWeatherControllerC2SPacket::new);

    public SetWeatherFromWeatherControllerC2SPacket(RegistryFriendlyByteBuf buffer) {
        this(buffer.readBlockPos(), buffer.readInt());
    }

     public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeInt(weatherType);
    }

    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void handle(SetWeatherFromWeatherControllerC2SPacket data, IPayloadContext context) {
        context.enqueueWork(() -> {
            if(!(context.player().level() instanceof ServerLevel level) || !(context.player() instanceof ServerPlayer player))
                return;

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
                case 0 -> level.setWeatherParameters(duration, 0, false, false);
                //Rain
                case 1 -> level.setWeatherParameters(0, duration, true, false);
                //Thunder
                case 2 -> level.setWeatherParameters(0, duration, true, true);
            }
        });
    }
}
