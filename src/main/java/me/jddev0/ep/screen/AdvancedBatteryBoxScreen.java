package me.jddev0.ep.screen;

import me.jddev0.ep.screen.base.EnergyStorageContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AdvancedBatteryBoxScreen extends EnergyStorageContainerScreen<AdvancedBatteryBoxMenu> {
    public AdvancedBatteryBoxScreen(AdvancedBatteryBoxMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);

        energyMeterX = 80;
    }
}
