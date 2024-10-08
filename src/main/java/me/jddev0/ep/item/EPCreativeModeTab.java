package me.jddev0.ep.item;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public final class EPCreativeModeTab {
    private EPCreativeModeTab() {}

    public static CreativeModeTab ENERGIZED_POWER_TAB = new CreativeModeTab("energizedpower.tab") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(EPItems.ENERGIZED_COPPER_INGOT.get());
        }
    };
}
