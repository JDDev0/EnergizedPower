package me.jddev0.ep.networking.packet;

import me.jddev0.ep.block.entity.TimeControllerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.common.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.LazyOptional;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.network.NetworkEvent;

public class SetTimeFromTimeControllerC2SPacket {
    private final BlockPos pos;
    private final int time;

    public SetTimeFromTimeControllerC2SPacket(BlockPos pos, int time) {
        this.pos = pos;
        this.time = time;
    }

    public SetTimeFromTimeControllerC2SPacket(FriendlyByteBuf buffer) {
        pos = buffer.readBlockPos();
        time = buffer.readInt();
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeInt(time);
    }

    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            Level level = context.getSender().level();
            if(!level.hasChunk(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(pos);
            if(!(blockEntity instanceof TimeControllerBlockEntity timeControllerBlockEntity))
                return;

            LazyOptional<IEnergyStorage> energyStorageLazyOptional = timeControllerBlockEntity.getCapability(Capabilities.ENERGY, null);
            if(!energyStorageLazyOptional.isPresent())
                return;

            IEnergyStorage energyStorage = energyStorageLazyOptional.orElse(null);
            if(energyStorage.getEnergyStored() < TimeControllerBlockEntity.CAPACITY)
                return;

            timeControllerBlockEntity.clearEnergy();

            if(time < 0 || time > 24000)
                return;

            long currentTime = context.getSender().level().getDayTime();

            int currentDayTime = (int)(currentTime % 24000);

            if(currentDayTime <= time)
                context.getSender().serverLevel().setDayTime(currentTime - currentDayTime + time);
            else
                context.getSender().serverLevel().setDayTime(currentTime + 24000 - currentDayTime + time);
        });

        return true;
    }
}
