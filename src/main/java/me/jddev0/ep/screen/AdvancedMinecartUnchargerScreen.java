package me.jddev0.ep.screen;

import me.jddev0.ep.screen.base.EnergyStorageContainerScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

@Environment(EnvType.CLIENT)
public class AdvancedMinecartUnchargerScreen extends EnergyStorageContainerScreen<AdvancedMinecartUnchargerMenu> {
    public AdvancedMinecartUnchargerScreen(AdvancedMinecartUnchargerMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);

        energyMeterX = 80;
    }
}
