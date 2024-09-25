package me.jddev0.ep.networking.packet;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.entity.FluidTankBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record SetFluidTankFilterC2SPacket(BlockPos pos, FluidStack fluidFilter) implements CustomPacketPayload {
    public static final Type<SetFluidTankFilterC2SPacket> ID =
            new Type<>(EPAPI.id("set_fluid_tank_filter"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SetFluidTankFilterC2SPacket> STREAM_CODEC =
            StreamCodec.ofMember(SetFluidTankFilterC2SPacket::write, SetFluidTankFilterC2SPacket::new);

    public SetFluidTankFilterC2SPacket(RegistryFriendlyByteBuf buffer) {
        this(buffer.readBlockPos(), FluidStack.OPTIONAL_STREAM_CODEC.decode(buffer));
    }

     public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        FluidStack.OPTIONAL_STREAM_CODEC.encode(buffer, fluidFilter);
    }

    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void handle(SetFluidTankFilterC2SPacket data, IPayloadContext context) {
        context.enqueueWork(() -> {
            if(!(context.player().level() instanceof ServerLevel level) || !(context.player() instanceof ServerPlayer player))
                return;

            if(!level.hasChunk(SectionPos.blockToSectionCoord(data.pos.getX()), SectionPos.blockToSectionCoord(data.pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(data.pos);
            if(!(blockEntity instanceof FluidTankBlockEntity fluidTankBlockEntity))
                return;

            fluidTankBlockEntity.setFluidFilter(data.fluidFilter);
        });
    }
}
