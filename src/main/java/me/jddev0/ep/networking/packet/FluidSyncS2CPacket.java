package me.jddev0.ep.networking.packet;

import me.jddev0.ep.fluid.FluidStoragePacketUpdate;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.NetworkEvent;

public class FluidSyncS2CPacket {
    private final int tank;
    private final FluidStack fluidStack;
    private final int capacity;
    private final BlockPos pos;

    public FluidSyncS2CPacket(int tank, FluidStack fluidStack, int capacity, BlockPos pos) {
        this.tank = tank;
        this.fluidStack = fluidStack;
        this.capacity = capacity;
        this.pos = pos;
    }

    public FluidSyncS2CPacket(FriendlyByteBuf buffer) {
        tank = buffer.readInt();
        fluidStack = buffer.readFluidStack();
        capacity = buffer.readInt();
        pos = buffer.readBlockPos();
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeInt(tank);
        buffer.writeFluidStack(fluidStack);
        buffer.writeInt(capacity);
        buffer.writeBlockPos(pos);
    }

    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(pos);

            //BlockEntity
            if(blockEntity instanceof FluidStoragePacketUpdate) {
                FluidStoragePacketUpdate fluidStorage = (FluidStoragePacketUpdate)blockEntity;
                fluidStorage.setFluid(tank, fluidStack);
                fluidStorage.setTankCapacity(tank, capacity);
            }
        });

        return true;
    }
}
