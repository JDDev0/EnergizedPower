package me.jddev0.ep.screen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.screen.base.UpgradableEnergyStorageContainerScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SolarPanelScreen extends UpgradableEnergyStorageContainerScreen<SolarPanelMenu> {
    public SolarPanelScreen(SolarPanelMenu menu, PlayerInventory inventory, Text component) {
        super(menu, inventory, component,
                EPAPI.id("textures/gui/container/upgrade_view/1_energy_capacity_1_moon_light.png"));

        energyMeterX = 80;
    }
}
