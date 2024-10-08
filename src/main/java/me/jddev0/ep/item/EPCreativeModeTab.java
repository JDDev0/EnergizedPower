package me.jddev0.ep.item;

import me.jddev0.ep.api.EPAPI;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;

public class EPCreativeModeTab {
    public static ItemGroup registerItemGroup(RegistryKey<ItemGroup> key, ItemGroup itemGroup) {
        return Registry.register(Registries.ITEM_GROUP, key.getValue(), itemGroup);
    }

    public static final RegistryKey<ItemGroup> ENERGIZED_POWER_TAB_REG_KEY = RegistryKey.of(RegistryKeys.ITEM_GROUP,
            EPAPI.id("main"));
    public static final ItemGroup ENERGIZED_POWER_TAB = registerItemGroup(ENERGIZED_POWER_TAB_REG_KEY, FabricItemGroup.builder()
            .displayName(Text.translatable("itemGroup.energizedpower.tab"))
            .icon(() -> new ItemStack(EPItems.ENERGIZED_COPPER_INGOT))
            .build());

    public static void register() {

    }
}
