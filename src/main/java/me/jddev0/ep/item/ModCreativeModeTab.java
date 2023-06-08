package me.jddev0.ep.item;

import me.jddev0.ep.EnergizedPowerMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModeTab {
    private ModCreativeModeTab() {}

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, EnergizedPowerMod.MODID);

    // public static final RegistryObject<Item> BATTERY_BOX_MINECART = ITEMS.register("battery_box_minecart",
    //            () -> new BatteryBoxMinecartItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<CreativeModeTab> ENERGIZED_POWER_TAB = CREATIVE_MODE_TABS.register("main",
            () -> CreativeModeTab.builder().
                    title(Component.translatable("itemGroup.energizedpower.tab")).
                    icon(() -> new ItemStack(ModItems.ENERGIZED_COPPER_INGOT.get())).
                    build());

    public static void register(IEventBus modEventBus) {
        CREATIVE_MODE_TABS.register(modEventBus);
    }
}
