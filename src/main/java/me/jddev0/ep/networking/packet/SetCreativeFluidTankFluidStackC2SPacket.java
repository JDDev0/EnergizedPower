package me.jddev0.ep.networking.packet;

import me.jddev0.ep.block.entity.CreativeFluidTankBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record SetCreativeFluidTankFluidStackC2SPacket(BlockPos pos, FluidStack fluidStack) {
    public SetCreativeFluidTankFluidStackC2SPacket(FriendlyByteBuf buffer) {
        this(buffer.readBlockPos(), buffer.readFluidStack());
    }

     public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeFluidStack(fluidStack);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            Level level = context.getSender().level;
            if(!level.hasChunk(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(pos);
            if(!(blockEntity instanceof CreativeFluidTankBlockEntity creativeFluidTankBlockEntity))
                return;

            creativeFluidTankBlockEntity.setFluidStack(fluidStack);
        });

        return true;
    }
}
