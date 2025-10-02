package me.jddev0.ep.networking.packet;

import me.jddev0.ep.api.EPAPI;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.LecternBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LecternBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;

public record PopEnergizedPowerBookFromLecternC2SPacket(BlockPos pos) implements CustomPayload {
    public static final CustomPayload.Id<PopEnergizedPowerBookFromLecternC2SPacket> ID =
            new CustomPayload.Id<>(EPAPI.id("pop_energized_power_book_from_lectern"));
    public static final PacketCodec<RegistryByteBuf, PopEnergizedPowerBookFromLecternC2SPacket> PACKET_CODEC =
            PacketCodec.of(PopEnergizedPowerBookFromLecternC2SPacket::write, PopEnergizedPowerBookFromLecternC2SPacket::new);

    public PopEnergizedPowerBookFromLecternC2SPacket(RegistryByteBuf buffer) {
        this(buffer.readBlockPos());
    }

    public void write(RegistryByteBuf buffer) {
        buffer.writeBlockPos(pos);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(PopEnergizedPowerBookFromLecternC2SPacket data, ServerPlayNetworking.Context context) {
        context.server().execute(() -> {
            if(!context.player().canModifyBlocks())
                return;

            World level = context.player().getEntityWorld();
            if(!level.isChunkLoaded(ChunkSectionPos.getSectionCoord(data.pos.getX()), ChunkSectionPos.getSectionCoord(data.pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(data.pos);
            if(!(blockEntity instanceof LecternBlockEntity lecternBlockEntity))
                return;

            ItemStack itemStack = lecternBlockEntity.getBook();

            lecternBlockEntity.setBook(ItemStack.EMPTY);
            LecternBlock.setHasBook(context.player(), context.player().getEntityWorld(), data.pos, context.player().getEntityWorld().getBlockState(data.pos), false);
            if(!context.player().getInventory().insertStack(itemStack))
                context.player().dropItem(itemStack, false);
        });
    }
}
