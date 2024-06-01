package me.jddev0.ep.networking.packet;

import me.jddev0.ep.block.entity.WeatherControllerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SetWeatherFromWeatherControllerC2SPacket {
    private final BlockPos pos;
    private final int weatherType;

    public SetWeatherFromWeatherControllerC2SPacket(BlockPos pos, int weatherType) {
        this.pos = pos;
        this.weatherType = weatherType;
    }

    public SetWeatherFromWeatherControllerC2SPacket(FriendlyByteBuf buffer) {
        pos = buffer.readBlockPos();
        weatherType = buffer.readInt();
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeInt(weatherType);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            Level level = context.getSender().getLevel();
            if(!level.hasChunk(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(pos);
            if(!(blockEntity instanceof WeatherControllerBlockEntity weatherControllerBlockEntity))
                return;

            if(!weatherControllerBlockEntity.hasEnoughEnergy())
                return;

            if(weatherControllerBlockEntity.hasInfiniteWeatherChangedDuration()) {
                if(weatherControllerBlockEntity.getSelectedWeatherType() == weatherType) {
                    weatherControllerBlockEntity.setSelectedWeatherType(-1);

                    return;
                }

                weatherControllerBlockEntity.setSelectedWeatherType(weatherType);
            }else {
                weatherControllerBlockEntity.clearEnergy();
            }

            int duration = weatherControllerBlockEntity.getWeatherChangedDuration();

            switch(weatherType) {
                //Clear
                case 0 -> context.getSender().getLevel().setWeatherParameters(duration, 0, false, false);
                //Rain
                case 1 -> context.getSender().getLevel().setWeatherParameters(0, duration, true, false);
                //Thunder
                case 2 -> context.getSender().getLevel().setWeatherParameters(0, duration, true, true);
            }
        });

        return true;
    }
}
