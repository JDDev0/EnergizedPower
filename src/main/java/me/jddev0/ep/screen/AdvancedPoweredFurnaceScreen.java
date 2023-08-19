package me.jddev0.ep.screen;

import me.jddev0.ep.EnergizedPowerMod;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AdvancedPoweredFurnaceScreen extends AbstractGenericEnergyStorageContainerScreen<AdvancedPoweredFurnaceMenu> {
    public AdvancedPoweredFurnaceScreen(AdvancedPoweredFurnaceMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component,
                "tooltip.energizedpower.advanced_powered_furnace.energy_required_to_finish.txt",
                new ResourceLocation(EnergizedPowerMod.MODID, "textures/gui/container/advanced_powered_furnace.png"),
                8, 17);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        super.renderBg(guiGraphics, partialTick, mouseX, mouseY);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        renderProgressArrows(guiGraphics, x, y);
    }

    private void renderProgressArrows(GuiGraphics guiGraphics, int x, int y) {
        for(int i = 0;i < 3;i++)
            if(menu.isCraftingActive(i))
                guiGraphics.blit(TEXTURE, x + 45 + 54 * i, y + 35, 176, 53, 12, menu.getScaledProgressArrowSize(i));
    }
}
