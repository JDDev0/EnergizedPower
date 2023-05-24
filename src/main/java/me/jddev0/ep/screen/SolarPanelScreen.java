package me.jddev0.ep.screen;

import me.jddev0.ep.block.SolarPanelBlock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class SolarPanelScreen extends AbstractGenericEnergyStorageHandledScreen<SolarPanelMenu> {
    public SolarPanelScreen(SolarPanelMenu menu, PlayerInventory inventory, Text component) {
        super(menu, inventory, component);
    }

    public SolarPanelBlock.Tier getTier() {
        return handler.getTier();
    }
}
