package me.jddev0.ep.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class BatteryBoxScreen extends AbstractGenericEnergyStorageHandledScreen<BatteryBoxMenu> {
    public BatteryBoxScreen(BatteryBoxMenu menu, PlayerInventory inventory, Text component) {
        super(menu, inventory, component);
    }
}
