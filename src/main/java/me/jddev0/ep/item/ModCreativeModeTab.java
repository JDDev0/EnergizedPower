package me.jddev0.ep.item;

import me.jddev0.ep.EnergizedPowerMod;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.CreativeModeTabEvent;

public class ModCreativeModeTab {
    public static CreativeModeTab ENERGIZED_POWER_TAB;

    public static void registerCreative(CreativeModeTabEvent.Register event) {
        ENERGIZED_POWER_TAB = event.registerCreativeModeTab(new ResourceLocation(EnergizedPowerMod.MODID, "main"), builder -> {
            builder.title(Component.translatable("itemGroup.energizedpower.tab"))
            .icon(() -> new ItemStack(ModItems.ENERGIZED_COPPER_INGOT.get()));
        });
    }
}
