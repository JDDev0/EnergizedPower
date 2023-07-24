package me.jddev0.ep.networking.packet;

import me.jddev0.ep.fluid.FluidStack;
import me.jddev0.ep.fluid.FluidStoragePacketUpdate;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

public class FluidSyncS2CPacket {
   private FluidSyncS2CPacket() {}

    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        FluidStack fluidStack = FluidStack.fromPacket(buf);
        long capacity = buf.readLong();
        BlockPos pos = buf.readBlockPos();

        client.execute(() -> {
            if(client.world == null)
                return;

            BlockEntity blockEntity = client.world.getBlockEntity(pos);

            //BlockEntity
            if(blockEntity instanceof FluidStoragePacketUpdate) {
                FluidStoragePacketUpdate fluidStorage = (FluidStoragePacketUpdate)blockEntity;
                fluidStorage.setFluid(fluidStack);
                fluidStorage.setTankCapacity(capacity);
            }
        });
    }
}
