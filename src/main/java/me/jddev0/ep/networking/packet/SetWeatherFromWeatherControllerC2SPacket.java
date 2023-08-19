package me.jddev0.ep.networking.packet;

import me.jddev0.ep.block.WeatherControllerBlock;
import me.jddev0.ep.block.entity.WeatherControllerBlockEntity;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;
import team.reborn.energy.api.EnergyStorage;

public final class SetWeatherFromWeatherControllerC2SPacket {
    private SetWeatherFromWeatherControllerC2SPacket() {}

    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {
        BlockPos pos = buf.readBlockPos();
        int weatherType = buf.readInt();

        server.execute(() -> {
            World level = player.getWorld();
            if(!level.isChunkLoaded(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(pos);
            if(!(blockEntity instanceof WeatherControllerBlockEntity weatherControllerBlockEntity))
                return;

            EnergyStorage energyStorage = EnergyStorage.SIDED.find(player.getWorld(), pos, null);
            if(energyStorage == null)
                return;

            if(energyStorage.getAmount() < WeatherControllerBlockEntity.CAPACITY)
                return;

            weatherControllerBlockEntity.clearEnergy();

            switch(weatherType) {
                //Clear
                case 0 -> player.getServerWorld().setWeather(WeatherControllerBlock.WEATHER_CHANGED_TICKS, 0, false, false);
                //Rain
                case 1 -> player.getServerWorld().setWeather(0, WeatherControllerBlock.WEATHER_CHANGED_TICKS, true, false);
                //Thunder
                case 2 -> player.getServerWorld().setWeather(0, WeatherControllerBlock.WEATHER_CHANGED_TICKS, true, true);
            }
        });
    }
}
