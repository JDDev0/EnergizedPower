package me.jddev0.ep.screen;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.screen.base.ConfigurableUpgradableEnergyStorageContainerScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class AdvancedChargerScreen extends ConfigurableUpgradableEnergyStorageContainerScreen<AdvancedChargerMenu> {
    public AdvancedChargerScreen(AdvancedChargerMenu menu, PlayerInventory inventory, Text component) {
        super(menu, inventory, component,
                "tooltip.energizedpower.advanced_charger.items_energy_left.txt",
                Identifier.of(EnergizedPowerMod.MODID, "textures/gui/container/advanced_charger.png"),
                Identifier.of(EnergizedPowerMod.MODID, "textures/gui/container/upgrade_view/1_energy_capacity.png"));
    }
}
