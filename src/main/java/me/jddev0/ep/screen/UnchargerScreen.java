package me.jddev0.ep.screen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.screen.base.ConfigurableUpgradableEnergyStorageContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class UnchargerScreen
        extends ConfigurableUpgradableEnergyStorageContainerScreen<UnchargerMenu> {
    public UnchargerScreen(UnchargerMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component,
                "tooltip.energizedpower.uncharger.item_energy_left.txt",
                EPAPI.id("textures/gui/container/uncharger.png"),
                EPAPI.id("textures/gui/container/upgrade_view/1_energy_capacity.png"));
    }
}
