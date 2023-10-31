package me.jddev0.ep.screen;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MinecartBatteryBoxScreen extends AbstractGenericEnergyStorageContainerScreen<MinecartBatteryBoxMenu> {
    public MinecartBatteryBoxScreen(MinecartBatteryBoxMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
    }
}
