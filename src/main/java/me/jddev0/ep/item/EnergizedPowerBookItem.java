package me.jddev0.ep.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EnergizedPowerBookItem extends WrittenBookItem {
    public EnergizedPowerBookItem(Item.Properties props) {
        super(props);
    }

    public static ItemStack createWrittenBook() {
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

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag tooltipFlag) {
        super.appendHoverText(createWrittenBook(), level, components, tooltipFlag);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);

        if(level.isClientSide)
            showBookViewScreen();

        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
    }

    @OnlyIn(Dist.CLIENT)
    private void showBookViewScreen() {
        Minecraft.getInstance().setScreen(new BookViewScreen(new BookViewScreen.WrittenBookAccess(EnergizedPowerBookItem.createWrittenBook())));
    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        return false;
    }
}
