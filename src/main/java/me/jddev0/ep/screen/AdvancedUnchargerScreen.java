package me.jddev0.ep.screen;

import me.jddev0.ep.EnergizedPowerMod;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AdvancedUnchargerScreen extends AbstractGenericEnergyStorageContainerScreen<AdvancedUnchargerMenu> {
    public AdvancedUnchargerScreen(AdvancedUnchargerMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component,
                "tooltip.energizedpower.advanced_uncharger.items_energy_left.txt",
                new ResourceLocation(EnergizedPowerMod.MODID, "textures/gui/container/advanced_uncharger.png"),
                8, 17);
    }
}
