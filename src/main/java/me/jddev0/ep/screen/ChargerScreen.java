package me.jddev0.ep.screen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.screen.base.ConfigurableUpgradableEnergyStorageContainerScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ChargerScreen extends ConfigurableUpgradableEnergyStorageContainerScreen<ChargerMenu> {
    public ChargerScreen(ChargerMenu menu, PlayerInventory inventory, Text component) {
        super(menu, inventory, component,
                "tooltip.energizedpower.charger.item_energy_left.txt",
                EPAPI.id("textures/gui/container/charger.png"),
                EPAPI.id("textures/gui/container/upgrade_view/1_energy_capacity.png"));
    }
}
