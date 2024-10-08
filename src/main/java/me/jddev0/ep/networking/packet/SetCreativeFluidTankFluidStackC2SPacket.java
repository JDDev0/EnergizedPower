package me.jddev0.ep.networking.packet;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.entity.CreativeFluidTankBlockEntity;
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

public record SetCreativeFluidTankFluidStackC2SPacket(BlockPos pos, FluidStack fluidStack) implements IEnergizedPowerPacket {
    public static final Identifier ID = EPAPI.id("set_creative_fluid_tank_fluid_stack");

    public SetCreativeFluidTankFluidStackC2SPacket(PacketByteBuf buffer) {
        this(buffer.readBlockPos(), FluidStack.fromPacket(buffer));
    }

     public void write(PacketByteBuf buffer) {
        buffer.writeBlockPos(pos);
        fluidStack.toPacket(buffer);
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    public static void receive(SetCreativeFluidTankFluidStackC2SPacket data, MinecraftServer server, ServerPlayerEntity player,
                               ServerPlayNetworkHandler handler, PacketSender responseSender) {
        server.execute(() -> {
            if(!player.canModifyBlocks())
                return;

            World level = player.getWorld();
            if(!level.isChunkLoaded(ChunkSectionPos.getSectionCoord(data.pos.getX()), ChunkSectionPos.getSectionCoord(data.pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(data.pos);
            if(!(blockEntity instanceof CreativeFluidTankBlockEntity creativeFluidTankBlockEntity))
                return;

            creativeFluidTankBlockEntity.setFluidStack(data.fluidStack);
        });
    }
}
