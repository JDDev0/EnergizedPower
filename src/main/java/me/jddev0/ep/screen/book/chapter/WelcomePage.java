package me.jddev0.ep.screen.book.chapter;

import me.jddev0.ep.screen.EnergizedPowerBookScreen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WelcomePage implements EnergizedPowerBookScreen.PageContent {
    @Override
    public Component getPageContent() {
        return Component.translatable("book.energizedpower.page.welcome");
    }
}
