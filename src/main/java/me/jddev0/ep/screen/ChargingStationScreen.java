package me.jddev0.ep.screen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.screen.base.UpgradableEnergyStorageContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ChargingStationScreen
        extends UpgradableEnergyStorageContainerScreen<ChargingStationMenu> {
    public ChargingStationScreen(ChargingStationMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component,
                EPAPI.id("textures/gui/container/upgrade_view/1_energy_capacity_1_range.png"));

        energyMeterX = 80;
    }
}
