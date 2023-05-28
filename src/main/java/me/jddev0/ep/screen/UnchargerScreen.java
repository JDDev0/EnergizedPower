package me.jddev0.ep.screen;

import me.jddev0.ep.EnergizedPowerMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class UnchargerScreen extends AbstractGenericEnergyStorageHandledScreen<UnchargerMenu> {
    public UnchargerScreen(UnchargerMenu menu, PlayerInventory inventory, Text component) {
        super(menu, inventory, component,
                "tooltip.energizedpower.uncharger.item_energy_left.txt",
                new Identifier(EnergizedPowerMod.MODID, "textures/gui/container/uncharger.png"),
                8, 17);
    }
}
