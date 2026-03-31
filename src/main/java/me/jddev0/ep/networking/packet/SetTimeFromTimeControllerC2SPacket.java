package me.jddev0.ep.networking.packet;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.entity.TimeControllerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.clock.ServerClockManager;
import net.minecraft.world.clock.WorldClock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.dimension.DimensionType;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record SetTimeFromTimeControllerC2SPacket(BlockPos pos, int time) implements CustomPacketPayload {
    public static final Type<SetTimeFromTimeControllerC2SPacket> ID =
            new Type<>(EPAPI.id("set_time_from_time_controller"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SetTimeFromTimeControllerC2SPacket> STREAM_CODEC =
            StreamCodec.ofMember(SetTimeFromTimeControllerC2SPacket::write, SetTimeFromTimeControllerC2SPacket::new);

    public SetTimeFromTimeControllerC2SPacket(RegistryFriendlyByteBuf buffer) {
        this(buffer.readBlockPos(), buffer.readInt());
    }

     public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeInt(time);
    }

    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }


    public static void handle(SetTimeFromTimeControllerC2SPacket data, IPayloadContext context) {
        context.enqueueWork(() -> {
            if(!(context.player().level() instanceof ServerLevel level) || !(context.player() instanceof ServerPlayer player))
                return;

            if(!level.hasChunk(SectionPos.blockToSectionCoord(data.pos.getX()), SectionPos.blockToSectionCoord(data.pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(data.pos);
            if(!(blockEntity instanceof TimeControllerBlockEntity timeControllerBlockEntity))
                return;

            EnergyHandler energyStorage = level.getCapability(Capabilities.Energy.BLOCK, data.pos,
                    level.getBlockState(data.pos), timeControllerBlockEntity, null);
            if(energyStorage == null)
                return;

            if(energyStorage.getAmountAsInt() < TimeControllerBlockEntity.CAPACITY)
                return;

            timeControllerBlockEntity.clearEnergy();

            if(data.time < 0 || data.time > 24000)
                return;

            Holder<DimensionType> dimensionType = context.player().level().dimensionTypeRegistration();
            Optional<Holder<WorldClock>> defaultClockOptional = dimensionType.value().defaultClock();
            if(defaultClockOptional.isEmpty()) {
                //TODO send error to player (There is no time in dimension ...)

                return;
            }

            Holder<WorldClock> defaultClock = defaultClockOptional.get();
            ServerClockManager clockManager = level.clockManager();

            //TODO dynamically get days timeline
            long ticksPerDay = 24000;

            long currentTime = clockManager.getTotalTicks(defaultClock);

            int currentDayTime = (int)(currentTime % ticksPerDay);

            if(currentDayTime <= data.time)
                clockManager.setTotalTicks(defaultClock, currentTime - currentDayTime + data.time);
            else
                clockManager.setTotalTicks(defaultClock, currentTime + ticksPerDay - currentDayTime + data.time);
        });
    }
}
