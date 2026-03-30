package me.jddev0.ep.item;

import me.jddev0.ep.api.EPAPI;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public final class EPCreativeModeTab {
    public static CreativeModeTab registerItemGroup(ResourceKey<CreativeModeTab> key, CreativeModeTab itemGroup) {
        return Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, key.identifier(), itemGroup);
    }

    public static final ResourceKey<CreativeModeTab> ENERGIZED_POWER_TAB_REG_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB,
            EPAPI.id("main"));
    public static final CreativeModeTab ENERGIZED_POWER_TAB = registerItemGroup(ENERGIZED_POWER_TAB_REG_KEY, FabricCreativeModeTab.builder()
            .title(Component.translatable("itemGroup.energizedpower.tab"))
            .icon(() -> new ItemStack(EPItems.ENERGIZED_COPPER_INGOT))
            .build());

    public static void register() {

    }
}
