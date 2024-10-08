package me.jddev0.ep.networking.packet;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.entity.TimeControllerBlockEntity;
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

public record SetTimeFromTimeControllerC2SPacket(BlockPos pos, int time) implements IEnergizedPowerPacket {
    public static final Identifier ID = EPAPI.id("set_time_from_time_controller");

    public SetTimeFromTimeControllerC2SPacket(PacketByteBuf buffer) {
        this(buffer.readBlockPos(), buffer.readInt());
    }

    public void write(PacketByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeInt(time);
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    public static void receive(SetTimeFromTimeControllerC2SPacket data, MinecraftServer server, ServerPlayerEntity player,
                               ServerPlayNetworkHandler handler, PacketSender responseSender) {
        player.server.execute(() -> {
            if(!player.canModifyBlocks())
                return;

            World level = player.getWorld();
            if(!level.isChunkLoaded(ChunkSectionPos.getSectionCoord(data.pos.getX()), ChunkSectionPos.getSectionCoord(data.pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(data.pos);
            if(!(blockEntity instanceof TimeControllerBlockEntity timeControllerBlockEntity))
                return;

            EnergyStorage energyStorage = EnergyStorage.SIDED.find(player.getWorld(), data.pos, null);
            if(energyStorage == null)
                return;

            if(energyStorage.getAmount() < TimeControllerBlockEntity.CAPACITY)
                return;

            timeControllerBlockEntity.clearEnergy();

            if(data.time < 0 || data.time > 24000)
                return;

            long currentTime = player.getWorld().getTimeOfDay();

            int currentDayTime = (int)(currentTime % 24000);

            if(currentDayTime <= data.time)
                player.getServerWorld().setTimeOfDay(currentTime - currentDayTime + data.time);
            else
                player.getServerWorld().setTimeOfDay(currentTime + 24000 - currentDayTime + data.time);
        });
    }
}
