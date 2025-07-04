package me.jddev0.ep.screen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.screen.base.UpgradableEnergyStorageContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class HeatGeneratorScreen
        extends UpgradableEnergyStorageContainerScreen<HeatGeneratorMenu> {
    public HeatGeneratorScreen(HeatGeneratorMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component,
                EPAPI.id("textures/gui/container/upgrade_view/1_energy_capacity.png"));

        energyMeterX = 80;
    }
}
