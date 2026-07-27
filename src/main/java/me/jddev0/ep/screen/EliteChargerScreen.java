package me.jddev0.ep.screen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.screen.base.ConfigurableIOUpgradableEnergyStorageContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class EliteChargerScreen
        extends ConfigurableIOUpgradableEnergyStorageContainerScreen<EliteChargerMenu> {
    public EliteChargerScreen(EliteChargerMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component,
                "tooltip.energizedpower.advanced_charger.items_energy_left.txt",
                EPAPI.id("textures/gui/container/elite_charger.png"),
                EPAPI.id("textures/gui/container/upgrade_view/elite_charger.png"));

        imageWidth = 230;
        ioConfigurationViewX = 33;
    }
}
