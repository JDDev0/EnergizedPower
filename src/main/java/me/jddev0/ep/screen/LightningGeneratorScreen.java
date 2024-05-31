package me.jddev0.ep.screen;

import me.jddev0.ep.screen.base.EnergyStorageContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LightningGeneratorScreen extends EnergyStorageContainerScreen<LightningGeneratorMenu> {
    public LightningGeneratorScreen(LightningGeneratorMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
    }
}
