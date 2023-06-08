package me.jddev0.ep.networking.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PopEnergizedPowerBookFromLecternC2SPacket {
    private final BlockPos pos;

    public PopEnergizedPowerBookFromLecternC2SPacket(BlockPos pos) {
        this.pos = pos;
    }

    public PopEnergizedPowerBookFromLecternC2SPacket(FriendlyByteBuf buffer) {
        pos = buffer.readBlockPos();
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if(!player.mayBuild())
                return;

            BlockEntity blockEntity = player.level().getBlockEntity(pos);

            if(blockEntity instanceof LecternBlockEntity) {
                LecternBlockEntity lecternBlockEntity = (LecternBlockEntity)blockEntity;

                ItemStack itemStack = lecternBlockEntity.getBook();

                lecternBlockEntity.setBook(ItemStack.EMPTY);
                LecternBlock.resetBookState(player, player.level(), pos, player.level().getBlockState(pos), false);
                if(!player.getInventory().add(itemStack))
                    player.drop(itemStack, false);
            }
        });

        return true;
    }
}
