package me.jddev0.ep.screen;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MinecartAdvancedBatteryBoxScreen extends AbstractGenericEnergyStorageContainerScreen<MinecartAdvancedBatteryBoxMenu> {
    public MinecartAdvancedBatteryBoxScreen(MinecartAdvancedBatteryBoxMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
    }
}
