package me.jddev0.ep.item;

import me.jddev0.ep.api.EPAPI;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class EPCreativeModeTab {
    private EPCreativeModeTab() {}

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, EPAPI.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ENERGIZED_POWER_TAB = CREATIVE_MODE_TABS.register("main",
            () -> CreativeModeTab.builder().
                    title(Component.translatable("itemGroup.energizedpower.tab")).
                    icon(() -> new ItemStack(EPItems.ENERGIZED_COPPER_INGOT.get())).
                    build());

    public static void register(IEventBus modEventBus) {
        CREATIVE_MODE_TABS.register(modEventBus);
    }
}
