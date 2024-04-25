package me.jddev0.ep.networking.packet;

import me.jddev0.ep.EnergizedPowerMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record PopEnergizedPowerBookFromLecternC2SPacket(BlockPos pos) implements CustomPacketPayload {
    public static final Type<PopEnergizedPowerBookFromLecternC2SPacket> ID =
            new Type<>(new ResourceLocation(EnergizedPowerMod.MODID, "pop_energized_power_book_from_lectern"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PopEnergizedPowerBookFromLecternC2SPacket> STREAM_CODEC =
            StreamCodec.ofMember(PopEnergizedPowerBookFromLecternC2SPacket::write, PopEnergizedPowerBookFromLecternC2SPacket::new);

    public PopEnergizedPowerBookFromLecternC2SPacket(RegistryFriendlyByteBuf buffer) {
        this(buffer.readBlockPos());
    }

     public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
    }

    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void handle(PopEnergizedPowerBookFromLecternC2SPacket data, IPayloadContext context) {
        context.enqueueWork(() -> {
            if(!(context.player().level() instanceof ServerLevel level) || !(context.player() instanceof ServerPlayer player))
                return;

            if(!player.mayBuild())
                return;

            if(!level.hasChunk(SectionPos.blockToSectionCoord(data.pos.getX()), SectionPos.blockToSectionCoord(data.pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(data.pos);
            if(!(blockEntity instanceof LecternBlockEntity lecternBlockEntity))
                return;

            ItemStack itemStack = lecternBlockEntity.getBook();

            lecternBlockEntity.setBook(ItemStack.EMPTY);
            LecternBlock.resetBookState(player, player.level(), data.pos, player.level().getBlockState(data.pos), false);
            if(!player.getInventory().add(itemStack))
                player.drop(itemStack, false);
        });
    }
}
