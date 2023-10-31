package me.jddev0.ep.networking.packet;

import me.jddev0.ep.screen.EnergizedPowerBookScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.NetworkEvent;

public class OpenEnergizedPowerBookS2CPacket {
    private final BlockPos pos;

    public OpenEnergizedPowerBookS2CPacket(BlockPos pos) {
        this.pos = pos;
    }

    public OpenEnergizedPowerBookS2CPacket(FriendlyByteBuf buffer) {
        pos = buffer.readBlockPos();
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
    }

    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(pos);

            if(blockEntity instanceof LecternBlockEntity) {
                LecternBlockEntity lecternBlockEntity = (LecternBlockEntity)blockEntity;

                showBookViewScreen(lecternBlockEntity);
            }
        });

        return true;
    }

    @OnlyIn(Dist.CLIENT)
    private void showBookViewScreen(LecternBlockEntity lecternBlockEntity) {
        Minecraft.getInstance().setScreen(new EnergizedPowerBookScreen(lecternBlockEntity));
    }
}
