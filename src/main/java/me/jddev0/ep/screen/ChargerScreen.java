package me.jddev0.ep.screen;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.screen.base.ConfigurableUpgradableEnergyStorageContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;


@OnlyIn(Dist.CLIENT)
public class ChargerScreen
        extends ConfigurableUpgradableEnergyStorageContainerScreen<ChargerMenu> {
    public ChargerScreen(ChargerMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component,
                "tooltip.energizedpower.charger.item_energy_left.txt",
                new ResourceLocation(EnergizedPowerMod.MODID, "textures/gui/container/charger.png"),
                8, 17,
                new ResourceLocation(EnergizedPowerMod.MODID, "textures/gui/container/upgrade_view/1_energy_capacity.png"));
    }
}
