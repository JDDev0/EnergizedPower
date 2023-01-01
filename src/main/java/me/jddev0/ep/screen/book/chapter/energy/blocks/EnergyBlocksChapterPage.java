package me.jddev0.ep.screen.book.chapter.energy.blocks;

import me.jddev0.ep.screen.EnergizedPowerBookScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EnergyBlocksChapterPage implements EnergizedPowerBookScreen.PageContent {
    @Override
    public Component getTitleComponent() {
        return Component.translatable("book.energizedpower.page.chapter.energy_blocks.title").withStyle(ChatFormatting.GOLD);
    }

    @Override
    public Component getPageContent() {
        return Component.translatable("book.energizedpower.page.chapter.energy_blocks");
    }
}
