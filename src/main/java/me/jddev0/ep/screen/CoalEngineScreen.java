package me.jddev0.ep.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import me.jddev0.ep.EnergizedPowerMod;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CoalEngineScreen extends AbstractGenericEnergyStorageContainerScreen<CoalEngineMenu> {
    public CoalEngineScreen(CoalEngineMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component,
                "tooltip.energizedpower.coal_engine.txt",
                new ResourceLocation(EnergizedPowerMod.MODID, "textures/gui/container/coal_engine.png"),
                8, 17);
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
        super.renderBg(poseStack, partialTick, mouseX, mouseY);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        renderProgressFlame(poseStack, x, y);
    }

    private void renderProgressFlame(PoseStack poseStack, int x, int y) {
        if(menu.isProducingActive()) {
            int pos = menu.getScaledProgressFlameSize();
            blit(poseStack, x + 81, y + 28 + pos, 176, 53 + pos, 14, 14 - pos);
        }
    }
}
