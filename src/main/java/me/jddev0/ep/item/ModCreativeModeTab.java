package me.jddev0.ep.item;

import me.jddev0.ep.EnergizedPowerMod;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModCreativeModeTab {
    public static ItemGroup ENERGIZED_POWER_TAB = FabricItemGroup.builder(new Identifier(EnergizedPowerMod.MODID, "main"))
            .displayName(Text.translatable("itemGroup.energizedpower.tab"))
            .icon(() -> new ItemStack(ModItems.ENERGIZED_COPPER_INGOT))
            .build();

    public static void register() {

    }
}
