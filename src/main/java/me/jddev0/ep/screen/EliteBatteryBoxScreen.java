package me.jddev0.ep.screen;

import me.jddev0.ep.screen.base.EnergyStorageContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class EliteBatteryBoxScreen extends EnergyStorageContainerScreen<EliteBatteryBoxMenu> {
    public EliteBatteryBoxScreen(EliteBatteryBoxMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);

        energyMeterX = 80;
    }
}
