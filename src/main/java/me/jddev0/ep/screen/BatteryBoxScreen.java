package me.jddev0.ep.screen;

import me.jddev0.ep.screen.base.EnergyStorageContainerScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class BatteryBoxScreen extends EnergyStorageContainerScreen<BatteryBoxMenu> {
    public BatteryBoxScreen(BatteryBoxMenu menu, PlayerInventory inventory, Text component) {
        super(menu, inventory, component);

        energyMeterX = 80;
    }
}
