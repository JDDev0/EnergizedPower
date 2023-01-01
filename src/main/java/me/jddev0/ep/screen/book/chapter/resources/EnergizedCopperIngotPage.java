package me.jddev0.ep.screen.book.chapter.resources;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.screen.EnergizedPowerBookScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EnergizedCopperIngotPage implements EnergizedPowerBookScreen.PageContent {
    private static final ResourceLocation ITEM_TEXTURE = new ResourceLocation(EnergizedPowerMod.MODID, "textures/item/energized_copper_ingot.png");

    @Override
    public Component getPageContent() {
        return Component.translatable("book.energizedpower.page.energized_copper_ingot");
    }

    @Override
    public ResourceLocation getItemImage() {
        return ITEM_TEXTURE;
    }
}
