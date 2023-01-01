package me.jddev0.ep.screen.book.chapter.energy.items;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.screen.EnergizedPowerBookScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BatteriesPage implements EnergizedPowerBookScreen.PageContent {
    private static final ResourceLocation ITEM_TEXTURE = new ResourceLocation(EnergizedPowerMod.MODID, "textures/item/battery_4.png");

    @Override
    public Component getPageContent() {
        return Component.translatable("book.energizedpower.page.batteries");
    }

    @Override
    public ResourceLocation getItemImage() {
        return ITEM_TEXTURE;
    }
}
