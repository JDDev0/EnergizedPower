package me.jddev0.ep.networking.packet;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.fluid.FluidStoragePacketUpdate;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import org.jetbrains.annotations.NotNull;

public record FluidSyncS2CPacket(int tank, FluidStack fluidStack, int capacity, BlockPos pos) implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(EnergizedPowerMod.MODID, "fluid_sync");


    public FluidSyncS2CPacket(FriendlyByteBuf buffer) {
        this(buffer.readInt(), buffer.readFluidStack(), buffer.readInt(), buffer.readBlockPos());
    }

    @Override
    public void write(final FriendlyByteBuf buffer) {
        buffer.writeInt(tank);
        buffer.writeFluidStack(fluidStack);
        buffer.writeInt(capacity);
        buffer.writeBlockPos(pos);
    }

    @Override
    @NotNull
    public ResourceLocation id() {
        return ID;
    }

    public static void handle(final FluidSyncS2CPacket data, final PlayPayloadContext context) {
        context.workHandler().execute(() -> {
            if(context.level().isEmpty())
                return;

            BlockEntity blockEntity = context.level().get().getBlockEntity(data.pos);

            //BlockEntity
            if(blockEntity instanceof FluidStoragePacketUpdate) {
                FluidStoragePacketUpdate fluidStorage = (FluidStoragePacketUpdate)blockEntity;
                fluidStorage.setFluid(data.tank, data.fluidStack);
                fluidStorage.setTankCapacity(data.tank, data.capacity);
            }
        });
    }
}
