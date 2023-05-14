package me.jddev0.ep;

import com.mojang.logging.LogUtils;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.block.behavior.ModBlockBehaviors;
import me.jddev0.ep.block.entity.ModBlockEntities;
import me.jddev0.ep.entity.ModEntityTypes;
import me.jddev0.ep.item.*;
import me.jddev0.ep.loading.EnergizedPowerBookReloadListener;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.recipe.ModRecipes;
import me.jddev0.ep.screen.*;
import me.jddev0.ep.villager.ModVillager;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.nbt.IntTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

@Mod(EnergizedPowerMod.MODID)
public class EnergizedPowerMod {
    public static final String MODID = "energizedpower";
    private static final Logger LOGGER = LogUtils.getLogger();

    public EnergizedPowerMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModRecipes.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModVillager.register(modEventBus);
        ModEntityTypes.register(modEventBus);

        ModBlockBehaviors.register();

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::registerCreativeTab);
        modEventBus.addListener(this::addCreativeTab);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        ModMessages.register();
    }

    private void registerCreativeTab(CreativeModeTabEvent.Register event) {
        ModCreativeModeTab.registerCreative(event);
    }

    private ItemStack getChargedItemStack(Item item, int energy) {
        ItemStack itemStack = new ItemStack(item);
        itemStack.getOrCreateTag().put("energy", IntTag.valueOf(energy));

        return itemStack;
    }

    private void addEmptyAndFullyChargedItem(CreativeModeTabEvent.BuildContents event, RegistryObject<Item> item, int capacity) {
        event.accept(item);
        event.accept(getChargedItemStack(item.get(), capacity));
    }

    private void addCreativeTab(CreativeModeTabEvent.BuildContents event) {
        if(event.getTab() == ModCreativeModeTab.ENERGIZED_POWER_TAB) {
            event.accept(ModItems.ENERGIZED_POWER_BOOK);
            addEmptyAndFullyChargedItem(event, ModItems.ENERGY_ANALYZER, EnergyAnalyzerItem.ENERGY_CAPACITY);

            event.accept(ModItems.WOODEN_HAMMER);
            event.accept(ModItems.STONE_HAMMER);
            event.accept(ModItems.IRON_HAMMER);
            event.accept(ModItems.GOLDEN_HAMMER);
            event.accept(ModItems.DIAMOND_HAMMER);
            event.accept(ModItems.NETHERITE_HAMMER);

            event.accept(ModBlocks.COPPER_CABLE_ITEM);
            event.accept(ModBlocks.GOLD_CABLE_ITEM);
            event.accept(ModBlocks.ENERGIZED_COPPER_CABLE_ITEM);
            event.accept(ModBlocks.ENERGIZED_GOLD_CABLE_ITEM);

            event.accept(ModBlocks.TRANSFORMER_1_TO_N_ITEM);
            event.accept(ModBlocks.TRANSFORMER_3_TO_3_ITEM);
            event.accept(ModBlocks.TRANSFORMER_N_TO_1_ITEM);

            event.accept(ModBlocks.COAL_ENGINE_ITEM);
            event.accept(ModBlocks.HEAT_GENERATOR_ITEM);
            event.accept(ModBlocks.LIGHTNING_GENERATOR_ITEM);
            event.accept(ModBlocks.SOLAR_PANEL_ITEM_1);
            event.accept(ModBlocks.SOLAR_PANEL_ITEM_2);
            event.accept(ModBlocks.SOLAR_PANEL_ITEM_3);
            event.accept(ModBlocks.SOLAR_PANEL_ITEM_4);
            event.accept(ModBlocks.SOLAR_PANEL_ITEM_5);

            event.accept(ModBlocks.BATTERY_BOX_ITEM);

            event.accept(ModBlocks.POWERED_FURNACE_ITEM);
            event.accept(ModBlocks.AUTO_CRAFTER_ITEM);
            event.accept(ModBlocks.CRUSHER_ITEM);
            event.accept(ModBlocks.SAWMILL_ITEM);
            event.accept(ModBlocks.COMPRESSOR_ITEM);
            event.accept(ModBlocks.PLANT_GROWTH_CHAMBER_ITEM);
            event.accept(ModBlocks.BLOCK_PLACER_ITEM);
            event.accept(ModBlocks.CHARGER_ITEM);
            event.accept(ModBlocks.UNCHARGER_ITEM);
            event.accept(ModBlocks.MINECART_CHARGER_ITEM);
            event.accept(ModBlocks.ENERGIZER_ITEM);
            event.accept(ModBlocks.CHARGING_STATION_ITEM);
            event.accept(ModBlocks.WEATHER_CONTROLLER_ITEM);
            event.accept(ModBlocks.TIME_CONTROLLER_ITEM);

            event.accept(ModItems.BASIC_SOLAR_CELL);
            event.accept(ModItems.ADVANCED_SOLAR_CELL);

            addEmptyAndFullyChargedItem(event, ModItems.INVENTORY_COAL_ENGINE, InventoryCoalEngine.CAPACITY);

            addEmptyAndFullyChargedItem(event, ModItems.BATTERY_1, BatteryItem.Tier.BATTERY_1.getCapacity());
            addEmptyAndFullyChargedItem(event, ModItems.BATTERY_2, BatteryItem.Tier.BATTERY_2.getCapacity());
            addEmptyAndFullyChargedItem(event, ModItems.BATTERY_3, BatteryItem.Tier.BATTERY_3.getCapacity());
            addEmptyAndFullyChargedItem(event, ModItems.BATTERY_4, BatteryItem.Tier.BATTERY_4.getCapacity());
            addEmptyAndFullyChargedItem(event, ModItems.BATTERY_5, BatteryItem.Tier.BATTERY_5.getCapacity());
            addEmptyAndFullyChargedItem(event, ModItems.BATTERY_6, BatteryItem.Tier.BATTERY_6.getCapacity());
            addEmptyAndFullyChargedItem(event, ModItems.BATTERY_7, BatteryItem.Tier.BATTERY_7.getCapacity());
            addEmptyAndFullyChargedItem(event, ModItems.BATTERY_8, BatteryItem.Tier.BATTERY_8.getCapacity());

            event.accept(ModItems.BATTERY_BOX_MINECART);

            event.accept(ModBlocks.BASIC_MACHINE_FRAME_ITEM);
            event.accept(ModBlocks.ADVANCED_MACHINE_FRAME_ITEM);

            event.accept(ModBlocks.SILICON_BLOCK_ITEM);
            event.accept(ModBlocks.SAWDUST_BLOCK_ITEM);
            event.accept(ModItems.CABLE_INSULATOR);
            event.accept(ModItems.SAW_BLADE);
            event.accept(ModItems.SAWDUST);
            event.accept(ModItems.BASIC_FERTILIZER);
            event.accept(ModItems.GOOD_FERTILIZER);
            event.accept(ModItems.ADVANCED_FERTILIZER);
            event.accept(ModItems.SILICON);
            event.accept(ModItems.COPPER_PLATE);
            event.accept(ModItems.IRON_PLATE);
            event.accept(ModItems.GOLD_PLATE);
            event.accept(ModItems.ENERGIZED_COPPER_INGOT);
            event.accept(ModItems.ENERGIZED_GOLD_INGOT);
            event.accept(ModItems.ENERGIZED_COPPER_PLATE);
            event.accept(ModItems.ENERGIZED_GOLD_PLATE);
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            MenuScreens.register(ModMenuTypes.AUTO_CRAFTER_MENU.get(), AutoCrafterScreen::new);
            MenuScreens.register(ModMenuTypes.CRUSHER_MENU.get(), CrusherScreen::new);
            MenuScreens.register(ModMenuTypes.SAWMILL_MENU.get(), SawmillScreen::new);
            MenuScreens.register(ModMenuTypes.COMPRESSOR_MENU.get(), CompressorScreen::new);
            MenuScreens.register(ModMenuTypes.PLANT_GROWTH_CHAMBER_MENU.get(), PlantGrowthChamberScreen::new);
            MenuScreens.register(ModMenuTypes.BLOCK_PLACER_MENU.get(), BlockPlacerScreen::new);
            MenuScreens.register(ModMenuTypes.CHARGER_MENU.get(), ChargerScreen::new);
            MenuScreens.register(ModMenuTypes.UNCHARGER_MENU.get(), UnchargerScreen::new);
            MenuScreens.register(ModMenuTypes.ENERGIZER_MENU.get(), EnergizerScreen::new);
            MenuScreens.register(ModMenuTypes.COAL_ENGINE_MENU.get(), CoalEngineScreen::new);
            MenuScreens.register(ModMenuTypes.POWERED_FURNACE_MENU.get(), PoweredFurnaceScreen::new);
            MenuScreens.register(ModMenuTypes.WEATHER_CONTROLLER_MENU.get(), WeatherControllerScreen::new);
            MenuScreens.register(ModMenuTypes.TIME_CONTROLLER_MENU.get(), TimeControllerScreen::new);

            event.enqueueWork(() -> {
                ItemProperties.registerGeneric(new ResourceLocation(MODID, "active"), (itemStack, level, entity, seed) -> {
                    Item item = itemStack.getItem();
                    return (item instanceof ActivatableItem && ((ActivatableItem)item).isActive(itemStack))?1.f:0.f;
                });
                ItemProperties.registerGeneric(new ResourceLocation(MODID, "working"), (itemStack, level, entity, seed) -> {
                    Item item = itemStack.getItem();
                    return (item instanceof WorkingItem && ((WorkingItem)item).isWorking(itemStack))?1.f:0.f;
                });
            });

            EntityRenderers.register(ModEntityTypes.BATTERY_BOX_MINECART.get(),
                    entity -> new MinecartRenderer<>(entity, new ModelLayerLocation(
                            new ResourceLocation("minecraft", "tnt_minecart"), "main")));
        }

        @SubscribeEvent
        public static void loadBookPages(RegisterClientReloadListenersEvent event) {
            event.registerReloadListener(new EnergizedPowerBookReloadListener());
        }
    }
}
