package me.jddev0.ep.screen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.screen.base.ConfigurableUpgradableEnergyStorageContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;


@OnlyIn(Dist.CLIENT)
public class AdvancedChargerScreen
        extends ConfigurableUpgradableEnergyStorageContainerScreen<AdvancedChargerMenu> {
    public AdvancedChargerScreen(AdvancedChargerMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component,
                "tooltip.energizedpower.advanced_charger.items_energy_left.txt",
                EPAPI.id("textures/gui/container/advanced_charger.png"),
                EPAPI.id("textures/gui/container/upgrade_view/1_energy_capacity.png"));
    }
}
