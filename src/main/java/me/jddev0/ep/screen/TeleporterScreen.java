package me.jddev0.ep.screen;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.screen.base.EnergyStorageContainerScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class TeleporterScreen extends EnergyStorageContainerScreen<TeleporterMenu> {
    public TeleporterScreen(TeleporterMenu menu, PlayerInventory inventory, Text component) {
        super(menu, inventory, component,
                new Identifier(EnergizedPowerMod.MODID, "textures/gui/container/teleporter.png"));
    }
}
