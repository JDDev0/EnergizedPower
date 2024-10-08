package me.jddev0.ep.item;

import me.jddev0.ep.api.EPAPI;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public final class EPCreativeModeTab {
    private EPCreativeModeTab() {}

    public static ItemGroup ENERGIZED_POWER_TAB = FabricItemGroupBuilder.build(EPAPI.id("tab"),
            () -> new ItemStack(EPItems.ENERGIZED_COPPER_INGOT));

    public static void register() {

    }
}
