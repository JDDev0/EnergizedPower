package me.jddev0.ep.networking.packet;

import me.jddev0.ep.api.EPAPI;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LecternBlockEntity;

public record PopEnergizedPowerBookFromLecternC2SPacket(BlockPos pos) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PopEnergizedPowerBookFromLecternC2SPacket> ID =
            new CustomPacketPayload.Type<>(EPAPI.id("pop_energized_power_book_from_lectern"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PopEnergizedPowerBookFromLecternC2SPacket> STREAM_CODEC =
            StreamCodec.ofMember(PopEnergizedPowerBookFromLecternC2SPacket::write, PopEnergizedPowerBookFromLecternC2SPacket::new);

    public PopEnergizedPowerBookFromLecternC2SPacket(RegistryFriendlyByteBuf buffer) {
        this(buffer.readBlockPos());
    }

    public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void receive(PopEnergizedPowerBookFromLecternC2SPacket data, ServerPlayNetworking.Context context) {
        context.server().execute(() -> {
            if(!context.player().mayBuild())
                return;

            Level level = context.player().level();
            if(!level.hasChunk(SectionPos.blockToSectionCoord(data.pos.getX()), SectionPos.blockToSectionCoord(data.pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(data.pos);
            if(!(blockEntity instanceof LecternBlockEntity lecternBlockEntity))
                return;

            ItemStack itemStack = lecternBlockEntity.getBook();

            lecternBlockEntity.setBook(ItemStack.EMPTY);
            LecternBlock.resetBookState(context.player(), context.player().level(), data.pos, context.player().level().getBlockState(data.pos), false);
            if(!context.player().getInventory().add(itemStack))
                context.player().drop(itemStack, false);
        });
    }
}
