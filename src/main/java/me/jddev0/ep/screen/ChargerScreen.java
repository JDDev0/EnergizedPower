package me.jddev0.ep.screen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.screen.base.ConfigurableUpgradableEnergyStorageContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ChargerScreen
        extends ConfigurableUpgradableEnergyStorageContainerScreen<ChargerMenu> {
    public ChargerScreen(ChargerMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component,
                "tooltip.energizedpower.charger.item_energy_left.txt",
                EPAPI.id("textures/gui/container/charger.png"),
                EPAPI.id("textures/gui/container/upgrade_view/1_energy_capacity.png"));
    }
}
