package me.jddev0.ep.networking.packet;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.entity.CreativeFluidTankBlockEntity;
import me.jddev0.ep.fluid.FluidStack;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

public record SetCreativeFluidTankFluidStackC2SPacket(BlockPos pos, FluidStack fluidStack) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SetCreativeFluidTankFluidStackC2SPacket> ID =
            new CustomPacketPayload.Type<>(EPAPI.id("set_creative_fluid_tank_fluid_stack"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SetCreativeFluidTankFluidStackC2SPacket> PACKET_CODEC =
            StreamCodec.ofMember(SetCreativeFluidTankFluidStackC2SPacket::write, SetCreativeFluidTankFluidStackC2SPacket::new);

    public SetCreativeFluidTankFluidStackC2SPacket(RegistryFriendlyByteBuf buffer) {
        this(buffer.readBlockPos(), FluidStack.PACKET_CODEC.decode(buffer));
    }

     public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        FluidStack.PACKET_CODEC.encode(buffer, fluidStack);
    }

    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void receive(SetCreativeFluidTankFluidStackC2SPacket data, ServerPlayNetworking.Context context) {
        context.server().execute(() -> {
            if(!context.player().mayBuild())
                return;

            Level level = context.player().level();
            if(!level.hasChunk(SectionPos.blockToSectionCoord(data.pos.getX()), SectionPos.blockToSectionCoord(data.pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(data.pos);
            if(!(blockEntity instanceof CreativeFluidTankBlockEntity creativeFluidTankBlockEntity))
                return;

            creativeFluidTankBlockEntity.setFluidStack(data.fluidStack);
        });
    }
}
