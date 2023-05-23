package me.jddev0.ep.screen;

import me.jddev0.ep.block.SolarPanelBlock;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SolarPanelScreen extends AbstractGenericEnergyStorageContainerScreen<SolarPanelMenu> {
    public SolarPanelScreen(SolarPanelMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
    }

    public SolarPanelBlock.Tier getTier() {
        return menu.getTier();
    }
}
