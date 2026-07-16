package me.jddev0.ep.screen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.screen.base.ConfigurableIOUpgradableEnergyStorageContainerScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PoweredFurnaceScreen
        extends ConfigurableIOUpgradableEnergyStorageContainerScreen<PoweredFurnaceMenu> {
    public PoweredFurnaceScreen(PoweredFurnaceMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component,
                "tooltip.energizedpower.recipe.energy_required_to_finish.txt",
                EPAPI.id("textures/gui/container/powered_furnace.png"),
                EPAPI.id("textures/gui/container/upgrade_view/1_speed_1_energy_efficiency_1_energy_capacity_1_furnace_mode_1_item_ejector_1_item_pulling_1_xp_extraction.png"));
    }

    @Override
    protected void renderBgNormalView(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        super.renderBgNormalView(guiGraphics, partialTick, mouseX, mouseY);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        if(menu.hasXPExtractionUpgradeModule()) {
            guiGraphics.blit(MACHINE_SPRITES_TEXTURE, x + 151, y + 16, 116, 0, 18, 54, 256, 256);
            renderFluidMeterContent(guiGraphics, menu.getFluid(), menu.getTankCapacity(), x + 152, y + 17, 16, 52);
            renderFluidMeterOverlay(guiGraphics, x, y);
        }

        renderProgressArrow(guiGraphics, x, y);
    }

    private void renderFluidMeterOverlay(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.blit(MACHINE_SPRITES_TEXTURE, x + 152, y + 17, 16, 0, 16, 52, 256, 256);
    }

    private void renderProgressArrow(GuiGraphics guiGraphics, int x, int y) {
        if(menu.isCraftingActive())
            guiGraphics.blit(MACHINE_SPRITES_TEXTURE, x + 80, y + 34, 0, 58, menu.getScaledProgressArrowSize(), 17);
    }

    @Override
    protected void renderTooltipNormalView(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltipNormalView(guiGraphics, mouseX, mouseY);

        if(isHovering(152, 17, 16, 52, mouseX, mouseY) && menu.hasXPExtractionUpgradeModule()) {
            renderFluidMeterContentTooltip(guiGraphics, menu.getFluid(), menu.getTankCapacity(), mouseX, mouseY);
        }
    }

    @Override
    protected Rect getTankCords(int tank) {
        if(tank == 0)
            return new Rect(152, 17, 16, 52);

        return super.getTankCords(tank);
    }
}
