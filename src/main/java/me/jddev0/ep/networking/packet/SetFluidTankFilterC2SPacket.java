package me.jddev0.ep.networking.packet;

import me.jddev0.ep.block.entity.FluidTankBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SetFluidTankFilterC2SPacket {
    private final BlockPos pos;
    private final FluidStack fluidFilter;

    public SetFluidTankFilterC2SPacket(BlockPos pos, FluidStack fluidFilter) {
        this.pos = pos;
        this.fluidFilter = fluidFilter;
    }

    public SetFluidTankFilterC2SPacket(FriendlyByteBuf buffer) {
        this.pos = buffer.readBlockPos();
        this.fluidFilter = buffer.readFluidStack();
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeFluidStack(fluidFilter);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            Level level = context.getSender().level();
            if(!level.hasChunk(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(pos);
            if(!(blockEntity instanceof FluidTankBlockEntity fluidTankBlockEntity))
                return;

            fluidTankBlockEntity.setFluidFilter(fluidFilter);
        });
    }
}
