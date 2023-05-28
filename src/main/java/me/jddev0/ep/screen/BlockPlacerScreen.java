package me.jddev0.ep.screen;

import me.jddev0.ep.EnergizedPowerMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class BlockPlacerScreen extends AbstractGenericEnergyStorageHandledScreen<BlockPlacerMenu> {
    public BlockPlacerScreen(BlockPlacerMenu menu, PlayerInventory inventory, Text component) {
        super(menu, inventory, component,
                "tooltip.energizedpower.block_placer.block_energy_left.txt",
                new Identifier(EnergizedPowerMod.MODID, "textures/gui/container/block_placer.png"),
                8, 17);
    }
}
