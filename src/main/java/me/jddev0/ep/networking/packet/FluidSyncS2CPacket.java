package me.jddev0.ep.networking.packet;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.fluid.FluidStack;
import me.jddev0.ep.fluid.FluidStoragePacketUpdate;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record FluidSyncS2CPacket(int tank, FluidStack fluidStack, long capacity, BlockPos pos) implements IEnergizedPowerPacket {
    public static final Identifier ID = EPAPI.id("fluid_sync");

    public FluidSyncS2CPacket(PacketByteBuf buffer) {
        this(buffer.readInt(), FluidStack.fromPacket(buffer), buffer.readLong(), buffer.readBlockPos());
    }

    public void write(PacketByteBuf buffer) {
        buffer.writeInt(tank);
        fluidStack.toPacket(buffer);
        buffer.writeLong(capacity);
        buffer.writeBlockPos(pos);
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    public static void receive(FluidSyncS2CPacket data, MinecraftClient client, ClientPlayNetworkHandler handler,
                               PacketSender responseSender) {
        client.execute(() -> {
            if(client.world == null)
                return;

            BlockEntity blockEntity = client.world.getBlockEntity(data.pos);
            if(blockEntity instanceof FluidStoragePacketUpdate) {
                FluidStoragePacketUpdate fluidStorage = (FluidStoragePacketUpdate)blockEntity;
                fluidStorage.setTankCapacity(data.tank, data.capacity);
                fluidStorage.setFluid(data.tank, data.fluidStack);
            }
        });
    }
}
