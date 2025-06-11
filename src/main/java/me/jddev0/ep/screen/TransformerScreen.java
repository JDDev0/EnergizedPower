package me.jddev0.ep.screen;

import me.jddev0.ep.screen.base.ConfigurableEnergyStorageContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TransformerScreen
        extends ConfigurableEnergyStorageContainerScreen<TransformerMenu> {
    public TransformerScreen(TransformerMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);

        energyMeterX = 80;
    }
}
