package me.jddev0.ep.screen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.screen.base.ConfigurableIOUpgradableEnergyStorageContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class EliteUnchargerScreen
        extends ConfigurableIOUpgradableEnergyStorageContainerScreen<EliteUnchargerMenu> {
    public EliteUnchargerScreen(EliteUnchargerMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component,
                "tooltip.energizedpower.advanced_uncharger.items_energy_left.txt",
                EPAPI.id("textures/gui/container/elite_uncharger.png"),
                EPAPI.id("textures/gui/container/upgrade_view/elite_uncharger.png"));

        imageWidth = 230;
        ioConfigurationViewX = 33;

        energyPerTickBarTooltipComponentID = "tooltip.energizedpower.energy_production_per_tick.txt";
    }
}
