package me.jddev0.ep.networking.packet;

import me.jddev0.ep.item.EnergizedPowerBookItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenEnergizedPowerBookS2CPacket {
    public OpenEnergizedPowerBookS2CPacket() {}

    public OpenEnergizedPowerBookS2CPacket(FriendlyByteBuf buffer) {}

    public void toBytes(FriendlyByteBuf buffer) {}

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(this::showBookViewScreen);

        return true;
    }

    @OnlyIn(Dist.CLIENT)
    private void showBookViewScreen() {
        Minecraft.getInstance().setScreen(new BookViewScreen(new BookViewScreen.WrittenBookAccess(EnergizedPowerBookItem.createWrittenBook())));
    }
}
