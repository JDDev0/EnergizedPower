package me.jddev0.ep.networking.packet;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.entity.CreativeFluidTankBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record SetCreativeFluidTankFluidStackC2SPacket(BlockPos pos, FluidStack fluidStack) implements CustomPacketPayload {
    public static final Type<SetCreativeFluidTankFluidStackC2SPacket> ID =
            new Type<>(EPAPI.id("set_creative_fluid_tank_fluid_stack"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SetCreativeFluidTankFluidStackC2SPacket> STREAM_CODEC =
            StreamCodec.ofMember(SetCreativeFluidTankFluidStackC2SPacket::write, SetCreativeFluidTankFluidStackC2SPacket::new);

    public SetCreativeFluidTankFluidStackC2SPacket(RegistryFriendlyByteBuf buffer) {
        this(buffer.readBlockPos(), FluidStack.OPTIONAL_STREAM_CODEC.decode(buffer));
    }

     public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        FluidStack.OPTIONAL_STREAM_CODEC.encode(buffer, fluidStack);
    }

    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void handle(SetCreativeFluidTankFluidStackC2SPacket data, IPayloadContext context) {
        context.enqueueWork(() -> {
            if(!(context.player().level() instanceof ServerLevel level) || !(context.player() instanceof ServerPlayer player))
                return;

            if(!level.hasChunk(SectionPos.blockToSectionCoord(data.pos.getX()), SectionPos.blockToSectionCoord(data.pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(data.pos);
            if(!(blockEntity instanceof CreativeFluidTankBlockEntity creativeFluidTankBlockEntity))
                return;

            creativeFluidTankBlockEntity.setFluidStack(data.fluidStack);
        });
    }
}
