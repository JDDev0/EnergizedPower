package me.jddev0.ep.networking.packet;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.entity.WeatherControllerBlockEntity;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;
import team.reborn.energy.api.EnergyStorage;

public record SetWeatherFromWeatherControllerC2SPacket(BlockPos pos, int weatherType) implements IEnergizedPowerPacket {
    public static final Identifier ID = new Identifier(EnergizedPowerMod.MODID, "set_weather_from_weather_controller");

    public SetWeatherFromWeatherControllerC2SPacket(PacketByteBuf buffer) {
        this(buffer.readBlockPos(), buffer.readInt());
    }

    public void write(PacketByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeInt(weatherType);
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    public static void receive(SetWeatherFromWeatherControllerC2SPacket data, MinecraftServer server, ServerPlayerEntity player,
                               ServerPlayNetworkHandler handler, PacketSender responseSender) {
        player.server.execute(() -> {
            if(!player.canModifyBlocks())
                return;

            World level = player.getWorld();
            if(!level.isChunkLoaded(ChunkSectionPos.getSectionCoord(data.pos.getX()), ChunkSectionPos.getSectionCoord(data.pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(data.pos);
            if(!(blockEntity instanceof WeatherControllerBlockEntity weatherControllerBlockEntity))
                return;

            EnergyStorage energyStorage = EnergyStorage.SIDED.find(player.getWorld(), data.pos, null);
            if(energyStorage == null)
                return;

            if(energyStorage.getAmount() < WeatherControllerBlockEntity.CAPACITY)
                return;

            weatherControllerBlockEntity.clearEnergy();

            int duration = weatherControllerBlockEntity.getWeatherChangedDuration();

            switch(data.weatherType) {
                //Clear
                case 0 -> player.getServerWorld().setWeather(duration, 0, false, false);
                //Rain
                case 1 -> player.getServerWorld().setWeather(0, duration, true, false);
                //Thunder
                case 2 -> player.getServerWorld().setWeather(0, duration, true, true);
            }
        });
    }
}
