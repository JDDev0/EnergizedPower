package me.jddev0.ep.screen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.screen.base.ConfigurableUpgradableEnergyStorageContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class AdvancedUnchargerScreen
        extends ConfigurableUpgradableEnergyStorageContainerScreen<AdvancedUnchargerMenu> {
    public AdvancedUnchargerScreen(AdvancedUnchargerMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component,
                "tooltip.energizedpower.advanced_uncharger.items_energy_left.txt",
                EPAPI.id("textures/gui/container/advanced_uncharger.png"),
                EPAPI.id("textures/gui/container/upgrade_view/1_energy_capacity.png"));

        energyPerTickBarTooltipComponentID = "tooltip.energizedpower.energy_production_per_tick.txt";
    }
}
