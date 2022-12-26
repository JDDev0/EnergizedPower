package me.jddev0.ep.screen;

import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.PopEnergizedPowerBookFromLecternC2SPacket;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EnergizedPowerBookScreen extends BookViewScreen {
    private final LecternBlockEntity lecternBlockEntity;

    private static ItemStack createWrittenBook() {
        ItemStack itemStack = new ItemStack(Items.WRITTEN_BOOK);

        itemStack.addTagElement("author", StringTag.valueOf("JDDev0"));
        itemStack.addTagElement("filtered_title", StringTag.valueOf("Energized Power Book"));
        itemStack.addTagElement("title", StringTag.valueOf("Energized Power Book"));

        ListTag pages = new ListTag();
        for(int i = 0;i < 2;i++)
            pages.add(StringTag.valueOf(Component.Serializer.toJson(Component.translatable("book.energizedpower.page." + i))));

        itemStack.addTagElement("filtered_pages", pages);
        itemStack.addTagElement("pages", pages);

        return itemStack;
    }

    public EnergizedPowerBookScreen() {
        this(null);
    }

    public EnergizedPowerBookScreen(LecternBlockEntity lecternBlockEntity) {
        super(new BookViewScreen.WrittenBookAccess(createWrittenBook()));

        this.lecternBlockEntity = lecternBlockEntity;
    }

    @Override
    protected void createMenuControls() {
        if(lecternBlockEntity == null || !minecraft.player.mayBuild()) {
            super.createMenuControls();

            return;
        }

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> onClose()).
                bounds(this.width / 2 - 100, 196, 98, 20).build());
        this.addRenderableWidget(Button.builder(Component.translatable("lectern.take_book"), button -> {
            ModMessages.sendToServer(new PopEnergizedPowerBookFromLecternC2SPacket(lecternBlockEntity.getBlockPos()));
            onClose();
        }).bounds(this.width / 2 + 2, 196, 98, 20).build());
    }
}
