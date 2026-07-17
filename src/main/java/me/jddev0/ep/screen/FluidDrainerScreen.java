package me.jddev0.ep.screen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.screen.base.ConfigurableIOUpgradableEnergyStorageContainerScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FluidDrainerScreen
        extends ConfigurableIOUpgradableEnergyStorageContainerScreen<FluidDrainerMenu> {
    public FluidDrainerScreen(FluidDrainerMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component,
                EPAPI.id("textures/gui/container/fluid_drainer.png"),
                EPAPI.id("textures/gui/container/upgrade_view/1_energy_efficiency_1_energy_capacity_1_item_ejector_1_item_pulling.png"));
    }

    @Override
    protected void renderBgNormalView(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        super.renderBgNormalView(guiGraphics, partialTick, mouseX, mouseY);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        renderFluidMeterContent(guiGraphics, menu.getFluid(), menu.getTankCapacity(), x + 152, y + 17, 16, 52);
        renderFluidMeterOverlay(guiGraphics, x, y);
    }

    private void renderFluidMeterOverlay(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.blit(MACHINE_SPRITES_TEXTURE, x + 152, y + 17, 16, 0, 16, 52);
    }

    @Override
    protected void renderTooltipNormalView(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltipNormalView(guiGraphics, mouseX, mouseY);

        if(isHovering(152, 17, 16, 52, mouseX, mouseY)) {
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
