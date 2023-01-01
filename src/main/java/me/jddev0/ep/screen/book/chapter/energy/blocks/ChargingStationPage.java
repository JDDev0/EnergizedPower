package me.jddev0.ep.screen.book.chapter.energy.blocks;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.screen.EnergizedPowerBookScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChargingStationPage implements EnergizedPowerBookScreen.PageContent {
    private static final ResourceLocation BLOCK_ID = new ResourceLocation(EnergizedPowerMod.MODID, "charging_station");

    @Override
    public Component getPageContent() {
        return Component.translatable("book.energizedpower.page.charging_station");
    }

    @Override
    public ResourceLocation getBlockId() {
        return BLOCK_ID;
    }
}
