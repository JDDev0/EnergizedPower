package me.jddev0.ep.networking.packet;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.entity.FluidTankBlockEntity;
import me.jddev0.ep.fluid.FluidStack;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public record SetFluidTankFilterC2SPacket(BlockPos pos, FluidStack fluidFilter) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SetFluidTankFilterC2SPacket> ID =
            new CustomPacketPayload.Type<>(EPAPI.id("set_fluid_tank_filter"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SetFluidTankFilterC2SPacket> STREAM_CODEC =
            StreamCodec.ofMember(SetFluidTankFilterC2SPacket::write, SetFluidTankFilterC2SPacket::new);

    public SetFluidTankFilterC2SPacket(RegistryFriendlyByteBuf buffer) {
        this(buffer.readBlockPos(), FluidStack.STREAM_CODEC.decode(buffer));
    }

    public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        FluidStack.STREAM_CODEC.encode(buffer, fluidFilter);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void receive(SetFluidTankFilterC2SPacket data, ServerPlayNetworking.Context context) {
        context.server().execute(() -> {
            if(!context.player().mayBuild())
                return;

            Level level = context.player().level();
            if(!level.hasChunk(SectionPos.blockToSectionCoord(data.pos.getX()), SectionPos.blockToSectionCoord(data.pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(data.pos);
            if(!(blockEntity instanceof FluidTankBlockEntity fluidTankBlockEntity))
                return;

            fluidTankBlockEntity.setFluidFilter(data.fluidFilter, context.player().registryAccess());
        });
    }
}
