package me.jddev0.ep.screen;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MinecartUnchargerScreen extends AbstractGenericEnergyStorageContainerScreen<MinecartUnchargerMenu> {
    public MinecartUnchargerScreen(MinecartUnchargerMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
    }
}
