package me.jddev0.ep.networking.packet;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.entity.FluidTankBlockEntity;
import me.jddev0.ep.fluid.FluidStack;
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

public record SetFluidTankFilterC2SPacket(BlockPos pos, FluidStack fluidFilter) implements IEnergizedPowerPacket {
    public static final Identifier ID = EPAPI.id("set_fluid_tank_filter");

    public SetFluidTankFilterC2SPacket(PacketByteBuf buffer) {
        this(buffer.readBlockPos(), FluidStack.fromPacket(buffer));
    }

    public void write(PacketByteBuf buffer) {
        buffer.writeBlockPos(pos);
        fluidFilter.toPacket(buffer);
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    public static void receive(SetFluidTankFilterC2SPacket data, MinecraftServer server, ServerPlayerEntity player,
                               ServerPlayNetworkHandler handler, PacketSender responseSender) {
        player.server.execute(() -> {
            if(!player.canModifyBlocks())
                return;

            World level = player.getWorld();
            if(!level.isChunkLoaded(ChunkSectionPos.getSectionCoord(data.pos.getX()), ChunkSectionPos.getSectionCoord(data.pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(data.pos);
            if(!(blockEntity instanceof FluidTankBlockEntity fluidTankBlockEntity))
                return;

            fluidTankBlockEntity.setFluidFilter(data.fluidFilter);
        });
    }
}
