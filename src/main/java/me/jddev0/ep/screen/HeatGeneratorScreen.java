package me.jddev0.ep.screen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.screen.base.UpgradableEnergyStorageContainerScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class HeatGeneratorScreen
        extends UpgradableEnergyStorageContainerScreen<HeatGeneratorMenu> {
    public HeatGeneratorScreen(HeatGeneratorMenu menu, PlayerInventory inventory, Text component) {
        super(menu, inventory, component,
                EPAPI.id("textures/gui/container/upgrade_view/1_energy_capacity.png"));

        energyMeterX = 80;
    }
}
