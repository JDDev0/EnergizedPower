package me.jddev0.ep.screen;

import me.jddev0.ep.screen.base.EnergyStorageContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MinecartChargerScreen extends EnergyStorageContainerScreen<MinecartChargerMenu> {
    public MinecartChargerScreen(MinecartChargerMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
    }
}
