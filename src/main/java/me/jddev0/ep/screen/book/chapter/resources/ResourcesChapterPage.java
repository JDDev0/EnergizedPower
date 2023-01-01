package me.jddev0.ep.screen.book.chapter.resources;

import me.jddev0.ep.screen.EnergizedPowerBookScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ResourcesChapterPage implements EnergizedPowerBookScreen.PageContent {
    @Override
    public Component getTitleComponent() {
        return Component.translatable("book.energizedpower.page.chapter.resources.title").withStyle(ChatFormatting.GOLD);
    }

    @Override
    public Component getPageContent() {
        return Component.translatable("book.energizedpower.page.chapter.resources");
    }
}
