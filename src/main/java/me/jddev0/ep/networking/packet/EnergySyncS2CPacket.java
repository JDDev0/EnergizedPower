package me.jddev0.ep.networking.packet;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.energy.EnergyStoragePacketUpdate;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record EnergySyncS2CPacket(int energy, int capacity, BlockPos pos) implements CustomPacketPayload {
    public static final Type<EnergySyncS2CPacket> ID =
            new Type<>(ResourceLocation.fromNamespaceAndPath(EnergizedPowerMod.MODID, "energy_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, EnergySyncS2CPacket> STREAM_CODEC =
            StreamCodec.ofMember(EnergySyncS2CPacket::write, EnergySyncS2CPacket::new);

    public EnergySyncS2CPacket(RegistryFriendlyByteBuf buffer) {
        this(buffer.readInt(), buffer.readInt(), buffer.readBlockPos());
    }

    public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeInt(energy);
        buffer.writeInt(capacity);
        buffer.writeBlockPos(pos);
    }

    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void handle(EnergySyncS2CPacket data, IPayloadContext context) {
        context.enqueueWork(() -> {
            BlockEntity blockEntity = context.player().level().getBlockEntity(data.pos);

            //BlockEntity
            if(blockEntity instanceof EnergyStoragePacketUpdate) {
                EnergyStoragePacketUpdate energyStorage = (EnergyStoragePacketUpdate)blockEntity;
                energyStorage.setCapacity(data.capacity);
                energyStorage.setEnergy(data.energy);
            }
        });
    }
}
