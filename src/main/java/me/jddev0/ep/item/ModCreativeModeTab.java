package me.jddev0.ep.item;

import me.jddev0.ep.EnergizedPowerMod;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class ModCreativeModeTab {
    public static ItemGroup ENERGIZED_POWER_TAB = FabricItemGroupBuilder.build(new Identifier(EnergizedPowerMod.MODID, "tab"),
            () -> new ItemStack(ModItems.ENERGIZED_COPPER_INGOT));

    public static void register() {

    }
}
