package me.jddev0.ep.networking.packet;

import me.jddev0.ep.block.entity.TimeControllerBlockEntity;
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

public final class SetTimeFromTimeControllerC2SPacket {
    private SetTimeFromTimeControllerC2SPacket() {}

    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {
        BlockPos pos = buf.readBlockPos();
        int time = buf.readInt();

        server.execute(() -> {
            World level = player.getWorld();
            if(!level.isChunkLoaded(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(pos);
            if(!(blockEntity instanceof TimeControllerBlockEntity timeControllerBlockEntity))
                return;

            EnergyStorage energyStorage = EnergyStorage.SIDED.find(player.getWorld(), pos, null);
            if(energyStorage == null)
                return;

            if(energyStorage.getAmount() < TimeControllerBlockEntity.CAPACITY)
                return;

            timeControllerBlockEntity.clearEnergy();

            if(time < 0 || time > 24000)
                return;

            long currentTime = player.getWorld().getTimeOfDay();

            int currentDayTime = (int)(currentTime % 24000);

            if(currentDayTime <= time)
                player.getWorld().setTimeOfDay(currentTime - currentDayTime + time);
            else
                player.getWorld().setTimeOfDay(currentTime + 24000 - currentDayTime + time);
        });
    }
}
