package me.jddev0.ep.networking.packet;

import me.jddev0.ep.fluid.FluidStoragePacketUpdate;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class FluidSyncS2CPacket {
    private final FluidStack fluidStack;
    private final int capacity;
    private final BlockPos pos;

    public FluidSyncS2CPacket(FluidStack fluidStack, int capacity, BlockPos pos) {
        this.fluidStack = fluidStack;
        this.capacity = capacity;
        this.pos = pos;
    }

    public FluidSyncS2CPacket(FriendlyByteBuf buffer) {
        fluidStack = buffer.readFluidStack();
        capacity = buffer.readInt();
        pos = buffer.readBlockPos();
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeFluidStack(fluidStack);
        buffer.writeInt(capacity);
        buffer.writeBlockPos(pos);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(pos);

            //BlockEntity
            if(blockEntity instanceof FluidStoragePacketUpdate) {
                FluidStoragePacketUpdate fluidStorage = (FluidStoragePacketUpdate)blockEntity;
                fluidStorage.setFluid(fluidStack);
                fluidStorage.setTankCapacity(capacity);
            }
        });

        return true;
    }
}
