package me.jddev0.ep;

import com.mojang.logging.LogUtils;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.block.behavior.ModBlockBehaviors;
import me.jddev0.ep.block.entity.ModBlockEntities;
import me.jddev0.ep.block.entity.renderer.FluidTankBlockEntityRenderer;
import me.jddev0.ep.block.entity.renderer.ItemConveyorBeltBlockEntityRenderer;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.entity.ModEntityTypes;
import me.jddev0.ep.fluid.ModFluidTypes;
import me.jddev0.ep.fluid.ModFluids;
import me.jddev0.ep.input.ModKeyBindings;
import me.jddev0.ep.item.*;
import me.jddev0.ep.item.energy.EnergizedPowerEnergyItem;
import me.jddev0.ep.item.energy.ItemCapabilityEnergy;
import me.jddev0.ep.loading.EnergizedPowerBookReloadListener;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.painting.ModPaintings;
import me.jddev0.ep.recipe.ModRecipes;
import me.jddev0.ep.screen.*;
import me.jddev0.ep.villager.ModVillager;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.IntTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

@Mod(EnergizedPowerMod.MODID)
public class EnergizedPowerMod {
    public static final String MODID = "energizedpower";
    private static final Logger LOGGER = LogUtils.getLogger();

    public EnergizedPowerMod(IEventBus modEventBus) {
        ModConfigs.registerConfigs(true);

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModRecipes.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModVillager.register(modEventBus);
        ModEntityTypes.register(modEventBus);
        ModPaintings.register(modEventBus);

        ModFluids.register(modEventBus);
        ModFluidTypes.register(modEventBus);

        ModBlockBehaviors.register();

        ModCreativeModeTab.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addCreativeTab);
        modEventBus.addListener(this::registerCapabilities);

        modEventBus.addListener(ModMessages::register);

        NeoForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {

    }

    private ItemStack getChargedItemStack(Item item, int energy) {
        ItemStack itemStack = new ItemStack(item);
        itemStack.getOrCreateTag().put("energy", IntTag.valueOf(energy));

        return itemStack;
    }

    private void addEmptyAndFullyChargedItem(BuildCreativeModeTabContentsEvent event, ItemLike item, int capacity) {
        event.accept(item);
        event.accept(getChargedItemStack(item.asItem(), capacity));
    }

    private void addCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if(event.getTab() == ModCreativeModeTab.ENERGIZED_POWER_TAB.get()) {
            event.accept(ModItems.ENERGIZED_POWER_BOOK);
            addEmptyAndFullyChargedItem(event, ModItems.ENERGY_ANALYZER, EnergyAnalyzerItem.ENERGY_CAPACITY);
            addEmptyAndFullyChargedItem(event, ModItems.FLUID_ANALYZER, FluidAnalyzerItem.ENERGY_CAPACITY);

            event.accept(ModItems.WOODEN_HAMMER);
            event.accept(ModItems.STONE_HAMMER);
            event.accept(ModItems.IRON_HAMMER);
            event.accept(ModItems.GOLDEN_HAMMER);
            event.accept(ModItems.DIAMOND_HAMMER);
            event.accept(ModItems.NETHERITE_HAMMER);

            event.accept(ModItems.CUTTER);

            event.accept(ModItems.WRENCH);

            event.accept(ModBlocks.ITEM_CONVEYOR_BELT_ITEM);
            event.accept(ModBlocks.ITEM_CONVEYOR_BELT_LOADER_ITEM);
            event.accept(ModBlocks.ITEM_CONVEYOR_BELT_SORTER_ITEM);
            event.accept(ModBlocks.ITEM_CONVEYOR_BELT_SWITCH_ITEM);
            event.accept(ModBlocks.ITEM_CONVEYOR_BELT_SPLITTER_ITEM);
            event.accept(ModBlocks.ITEM_CONVEYOR_BELT_MERGER_ITEM);

            event.accept(ModBlocks.IRON_FLUID_PIPE_ITEM);
            event.accept(ModBlocks.GOLDEN_FLUID_PIPE_ITEM);

            event.accept(ModBlocks.COPPER_CABLE_ITEM);
            event.accept(ModBlocks.GOLD_CABLE_ITEM);
            event.accept(ModBlocks.ENERGIZED_COPPER_CABLE_ITEM);
            event.accept(ModBlocks.ENERGIZED_GOLD_CABLE_ITEM);
            event.accept(ModBlocks.ENERGIZED_CRYSTAL_MATRIX_CABLE_ITEM);

            event.accept(ModBlocks.LV_TRANSFORMER_1_TO_N_ITEM);
            event.accept(ModBlocks.LV_TRANSFORMER_3_TO_3_ITEM);
            event.accept(ModBlocks.LV_TRANSFORMER_N_TO_1_ITEM);
            event.accept(ModBlocks.MV_TRANSFORMER_1_TO_N_ITEM);
            event.accept(ModBlocks.MV_TRANSFORMER_3_TO_3_ITEM);
            event.accept(ModBlocks.MV_TRANSFORMER_N_TO_1_ITEM);
            event.accept(ModBlocks.HV_TRANSFORMER_1_TO_N_ITEM);
            event.accept(ModBlocks.HV_TRANSFORMER_3_TO_3_ITEM);
            event.accept(ModBlocks.HV_TRANSFORMER_N_TO_1_ITEM);
            event.accept(ModBlocks.EHV_TRANSFORMER_1_TO_N_ITEM);
            event.accept(ModBlocks.EHV_TRANSFORMER_3_TO_3_ITEM);
            event.accept(ModBlocks.EHV_TRANSFORMER_N_TO_1_ITEM);

            event.accept(ModBlocks.PRESS_MOLD_MAKER_ITEM);

            event.accept(ModBlocks.COAL_ENGINE_ITEM);
            event.accept(ModBlocks.HEAT_GENERATOR_ITEM);
            event.accept(ModBlocks.THERMAL_GENERATOR_ITEM);
            event.accept(ModBlocks.LIGHTNING_GENERATOR_ITEM);
            event.accept(ModBlocks.SOLAR_PANEL_ITEM_1);
            event.accept(ModBlocks.SOLAR_PANEL_ITEM_2);
            event.accept(ModBlocks.SOLAR_PANEL_ITEM_3);
            event.accept(ModBlocks.SOLAR_PANEL_ITEM_4);
            event.accept(ModBlocks.SOLAR_PANEL_ITEM_5);
            event.accept(ModBlocks.SOLAR_PANEL_ITEM_6);

            event.accept(ModBlocks.BATTERY_BOX_ITEM);
            event.accept(ModBlocks.ADVANCED_BATTERY_BOX_ITEM);
            event.accept(ModBlocks.CREATIVE_BATTERY_BOX_ITEM);

            event.accept(ModBlocks.POWERED_LAMP_ITEM);
            event.accept(ModBlocks.POWERED_FURNACE_ITEM);
            event.accept(ModBlocks.ADVANCED_POWERED_FURNACE_ITEM);
            event.accept(ModBlocks.AUTO_CRAFTER_ITEM);
            event.accept(ModBlocks.ADVANCED_AUTO_CRAFTER_ITEM);
            event.accept(ModBlocks.CRUSHER_ITEM);
            event.accept(ModBlocks.ADVANCED_CRUSHER_ITEM);
            event.accept(ModBlocks.PULVERIZER_ITEM);
            event.accept(ModBlocks.SAWMILL_ITEM);
            event.accept(ModBlocks.COMPRESSOR_ITEM);
            event.accept(ModBlocks.METAL_PRESS_ITEM);
            event.accept(ModBlocks.ASSEMBLING_MACHINE_ITEM);
            event.accept(ModBlocks.PLANT_GROWTH_CHAMBER_ITEM);
            event.accept(ModBlocks.STONE_SOLIDIFIER_ITEM);
            event.accept(ModBlocks.FILTRATION_PLANT_ITEM);
            event.accept(ModBlocks.BLOCK_PLACER_ITEM);
            event.accept(ModBlocks.FLUID_TANK_SMALL_ITEM);
            event.accept(ModBlocks.FLUID_TANK_MEDIUM_ITEM);
            event.accept(ModBlocks.FLUID_TANK_LARGE_ITEM);
            event.accept(ModBlocks.FLUID_FILLER_ITEM);
            event.accept(ModBlocks.FLUID_DRAINER_ITEM);
            event.accept(ModBlocks.DRAIN_ITEM);
            event.accept(ModBlocks.CHARGER_ITEM);
            event.accept(ModBlocks.ADVANCED_CHARGER_ITEM);
            event.accept(ModBlocks.UNCHARGER_ITEM);
            event.accept(ModBlocks.ADVANCED_UNCHARGER_ITEM);
            event.accept(ModBlocks.MINECART_CHARGER_ITEM);
            event.accept(ModBlocks.ADVANCED_MINECART_CHARGER_ITEM);
            event.accept(ModBlocks.MINECART_UNCHARGER_ITEM);
            event.accept(ModBlocks.ADVANCED_MINECART_UNCHARGER_ITEM);

            event.accept(ModBlocks.ENERGIZER_ITEM);
            event.accept(ModBlocks.CHARGING_STATION_ITEM);
            event.accept(ModBlocks.CRYSTAL_GROWTH_CHAMBER);

            event.accept(ModBlocks.WEATHER_CONTROLLER_ITEM);
            event.accept(ModBlocks.TIME_CONTROLLER_ITEM);
            event.accept(ModBlocks.TELEPORTER_ITEM);

            addEmptyAndFullyChargedItem(event, ModItems.INVENTORY_COAL_ENGINE, InventoryCoalEngineItem.CAPACITY);
            event.accept(ModItems.INVENTORY_CHARGER);

            addEmptyAndFullyChargedItem(event, ModItems.BATTERY_1, BatteryItem.Tier.BATTERY_1.getCapacity());
            addEmptyAndFullyChargedItem(event, ModItems.BATTERY_2, BatteryItem.Tier.BATTERY_2.getCapacity());
            addEmptyAndFullyChargedItem(event, ModItems.BATTERY_3, BatteryItem.Tier.BATTERY_3.getCapacity());
            addEmptyAndFullyChargedItem(event, ModItems.BATTERY_4, BatteryItem.Tier.BATTERY_4.getCapacity());
            addEmptyAndFullyChargedItem(event, ModItems.BATTERY_5, BatteryItem.Tier.BATTERY_5.getCapacity());
            addEmptyAndFullyChargedItem(event, ModItems.BATTERY_6, BatteryItem.Tier.BATTERY_6.getCapacity());
            addEmptyAndFullyChargedItem(event, ModItems.BATTERY_7, BatteryItem.Tier.BATTERY_7.getCapacity());
            addEmptyAndFullyChargedItem(event, ModItems.BATTERY_8, BatteryItem.Tier.BATTERY_8.getCapacity());
            event.accept(ModItems.CREATIVE_BATTERY);

            event.accept(ModItems.BATTERY_BOX_MINECART);
            event.accept(ModItems.ADVANCED_BATTERY_BOX_MINECART);

            event.accept(ModBlocks.BASIC_MACHINE_FRAME_ITEM);
            event.accept(ModBlocks.HARDENED_MACHINE_FRAME_ITEM);
            event.accept(ModBlocks.ADVANCED_MACHINE_FRAME_ITEM);
            event.accept(ModBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM);

            event.accept(ModItems.BASIC_SOLAR_CELL);
            event.accept(ModItems.ADVANCED_SOLAR_CELL);
            event.accept(ModItems.REINFORCED_ADVANCED_SOLAR_CELL);

            event.accept(ModItems.BASIC_CIRCUIT);
            event.accept(ModItems.ADVANCED_CIRCUIT);
            event.accept(ModItems.PROCESSING_UNIT);

            event.accept(ModItems.TELEPORTER_PROCESSING_UNIT);
            event.accept(ModItems.TELEPORTER_MATRIX);

            event.accept(ModBlocks.SILICON_BLOCK_ITEM);
            event.accept(ModBlocks.SAWDUST_BLOCK_ITEM);
            event.accept(ModItems.CABLE_INSULATOR);
            event.accept(ModItems.CHARCOAL_FILTER);
            event.accept(ModItems.SAW_BLADE);
            event.accept(ModItems.CRYSTAL_MATRIX);
            event.accept(ModItems.SAWDUST);
            event.accept(ModItems.CHARCOAL_DUST);
            event.accept(ModItems.BASIC_FERTILIZER);
            event.accept(ModItems.GOOD_FERTILIZER);
            event.accept(ModItems.ADVANCED_FERTILIZER);
            event.accept(ModItems.RAW_GEAR_PRESS_MOLD);
            event.accept(ModItems.RAW_ROD_PRESS_MOLD);
            event.accept(ModItems.RAW_WIRE_PRESS_MOLD);
            event.accept(ModItems.GEAR_PRESS_MOLD);
            event.accept(ModItems.ROD_PRESS_MOLD);
            event.accept(ModItems.WIRE_PRESS_MOLD);
            event.accept(ModItems.SILICON);
            event.accept(ModItems.COPPER_DUST);
            event.accept(ModItems.IRON_DUST);
            event.accept(ModItems.GOLD_DUST);
            event.accept(ModItems.COPPER_PLATE);
            event.accept(ModItems.IRON_PLATE);
            event.accept(ModItems.GOLD_PLATE);
            event.accept(ModItems.IRON_GEAR);
            event.accept(ModItems.IRON_ROD);
            event.accept(ModItems.COPPER_WIRE);
            event.accept(ModItems.GOLD_WIRE);
            event.accept(ModItems.ENERGIZED_COPPER_INGOT);
            event.accept(ModItems.ENERGIZED_GOLD_INGOT);
            event.accept(ModItems.ENERGIZED_COPPER_PLATE);
            event.accept(ModItems.ENERGIZED_GOLD_PLATE);
            event.accept(ModItems.ENERGIZED_COPPER_WIRE);
            event.accept(ModItems.ENERGIZED_GOLD_WIRE);
            event.accept(ModItems.ENERGIZED_CRYSTAL_MATRIX);

            event.accept(ModFluids.DIRTY_WATER_BUCKET_ITEM);
        }
    }

    public void registerCapabilities(RegisterCapabilitiesEvent event) {
        //Items
        for(Item item:BuiltInRegistries.ITEM) {
            if(item instanceof EnergizedPowerEnergyItem energizedPowerEnergyItem) {
                event.registerItem(Capabilities.EnergyStorage.ITEM, (stack, ctx) -> {
                    return new ItemCapabilityEnergy(stack, stack.getTag(), energizedPowerEnergyItem.getEnergyStorageProvider().get());
                }, item);
            }
        }

        //Block Entities
        ModBlockEntities.registerCapabilities(event);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            ModConfigs.registerConfigs(false);

            MenuScreens.register(ModMenuTypes.ITEM_CONVEYOR_BELT_LOADER_MENU.get(), ItemConveyorBeltLoaderScreen::new);
            MenuScreens.register(ModMenuTypes.ITEM_CONVEYOR_BELT_SORTER_MENU.get(), ItemConveyorBeltSorterScreen::new);
            MenuScreens.register(ModMenuTypes.AUTO_CRAFTER_MENU.get(), AutoCrafterScreen::new);
            MenuScreens.register(ModMenuTypes.ADVANCED_AUTO_CRAFTER_MENU.get(), AdvancedAutoCrafterScreen::new);
            MenuScreens.register(ModMenuTypes.CRUSHER_MENU.get(), CrusherScreen::new);
            MenuScreens.register(ModMenuTypes.ADVANCED_CRUSHER_MENU.get(), AdvancedCrusherScreen::new);
            MenuScreens.register(ModMenuTypes.PULVERIZER_MENU.get(), PulverizerScreen::new);
            MenuScreens.register(ModMenuTypes.SAWMILL_MENU.get(), SawmillScreen::new);
            MenuScreens.register(ModMenuTypes.COMPRESSOR_MENU.get(), CompressorScreen::new);
            MenuScreens.register(ModMenuTypes.PLANT_GROWTH_CHAMBER_MENU.get(), PlantGrowthChamberScreen::new);
            MenuScreens.register(ModMenuTypes.STONE_SOLIDIFIER_MENU.get(), StoneSolidifierScreen::new);
            MenuScreens.register(ModMenuTypes.FILTRATION_PLANT_MENU.get(), FiltrationPlantScreen::new);
            MenuScreens.register(ModMenuTypes.BLOCK_PLACER_MENU.get(), BlockPlacerScreen::new);
            MenuScreens.register(ModMenuTypes.FLUID_FILLER_MENU.get(), FluidFillerScreen::new);
            MenuScreens.register(ModMenuTypes.FLUID_DRAINER_MENU.get(), FluidDrainerScreen::new);
            MenuScreens.register(ModMenuTypes.DRAIN_MENU.get(), DrainScreen::new);
            MenuScreens.register(ModMenuTypes.CHARGER_MENU.get(), ChargerScreen::new);
            MenuScreens.register(ModMenuTypes.ADVANCED_CHARGER_MENU.get(), AdvancedChargerScreen::new);
            MenuScreens.register(ModMenuTypes.UNCHARGER_MENU.get(), UnchargerScreen::new);
            MenuScreens.register(ModMenuTypes.ADVANCED_UNCHARGER_MENU.get(), AdvancedUnchargerScreen::new);
            MenuScreens.register(ModMenuTypes.ENERGIZER_MENU.get(), EnergizerScreen::new);
            MenuScreens.register(ModMenuTypes.COAL_ENGINE_MENU.get(), CoalEngineScreen::new);
            MenuScreens.register(ModMenuTypes.POWERED_FURNACE_MENU.get(), PoweredFurnaceScreen::new);
            MenuScreens.register(ModMenuTypes.ADVANCED_POWERED_FURNACE_MENU.get(), AdvancedPoweredFurnaceScreen::new);
            MenuScreens.register(ModMenuTypes.WEATHER_CONTROLLER_MENU.get(), WeatherControllerScreen::new);
            MenuScreens.register(ModMenuTypes.TIME_CONTROLLER_MENU.get(), TimeControllerScreen::new);
            MenuScreens.register(ModMenuTypes.TELEPORTER_MENU.get(), TeleporterScreen::new);
            MenuScreens.register(ModMenuTypes.LIGHTNING_GENERATOR_MENU.get(), LightningGeneratorScreen::new);
            MenuScreens.register(ModMenuTypes.CHARGING_STATION_MENU.get(), ChargingStationScreen::new);
            MenuScreens.register(ModMenuTypes.CRYSTAL_GROWTH_CHAMBER_MENU.get(), CrystalGrowthChamberScreen::new);
            MenuScreens.register(ModMenuTypes.HEAT_GENERATOR_MENU.get(), HeatGeneratorScreen::new);
            MenuScreens.register(ModMenuTypes.THERMAL_GENERATOR_MENU.get(), ThermalGeneratorScreen::new);
            MenuScreens.register(ModMenuTypes.BATTERY_BOX_MENU.get(), BatteryBoxScreen::new);
            MenuScreens.register(ModMenuTypes.ADVANCED_BATTERY_BOX_MENU.get(), AdvancedBatteryBoxScreen::new);
            MenuScreens.register(ModMenuTypes.MINECART_CHARGER_MENU.get(), MinecartChargerScreen::new);
            MenuScreens.register(ModMenuTypes.ADVANCED_MINECART_CHARGER_MENU.get(), AdvancedMinecartChargerScreen::new);
            MenuScreens.register(ModMenuTypes.MINECART_UNCHARGER_MENU.get(), MinecartUnchargerScreen::new);
            MenuScreens.register(ModMenuTypes.ADVANCED_MINECART_UNCHARGER_MENU.get(), AdvancedMinecartUnchargerScreen::new);
            MenuScreens.register(ModMenuTypes.SOLAR_PANEL_MENU_1.get(), SolarPanelScreen::new);
            MenuScreens.register(ModMenuTypes.SOLAR_PANEL_MENU_2.get(), SolarPanelScreen::new);
            MenuScreens.register(ModMenuTypes.SOLAR_PANEL_MENU_3.get(), SolarPanelScreen::new);
            MenuScreens.register(ModMenuTypes.SOLAR_PANEL_MENU_4.get(), SolarPanelScreen::new);
            MenuScreens.register(ModMenuTypes.SOLAR_PANEL_MENU_5.get(), SolarPanelScreen::new);
            MenuScreens.register(ModMenuTypes.SOLAR_PANEL_MENU_6.get(), SolarPanelScreen::new);
            MenuScreens.register(ModMenuTypes.PRESS_MOLD_MAKER_MENU.get(), PressMoldMakerScreen::new);
            MenuScreens.register(ModMenuTypes.METAL_PRESS_MENU.get(), MetalPressScreen::new);
            MenuScreens.register(ModMenuTypes.ASSEMBLING_MACHINE_MENU.get(), AssemblingMachineScreen::new);
            MenuScreens.register(ModMenuTypes.FLUID_TANK_SMALL.get(), FluidTankScreen::new);
            MenuScreens.register(ModMenuTypes.FLUID_TANK_MEDIUM.get(), FluidTankScreen::new);
            MenuScreens.register(ModMenuTypes.FLUID_TANK_LARGE.get(), FluidTankScreen::new);

            MenuScreens.register(ModMenuTypes.INVENTORY_CHARGER_MENU.get(), InventoryChargerScreen::new);

            MenuScreens.register(ModMenuTypes.MINECART_BATTERY_BOX_MENU.get(), MinecartBatteryBoxScreen::new);
            MenuScreens.register(ModMenuTypes.MINECART_ADVANCED_BATTERY_BOX_MENU.get(), MinecartAdvancedBatteryBoxScreen::new);

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
                            new ResourceLocation("minecraft", "chest_minecart"), "main")));
            EntityRenderers.register(ModEntityTypes.ADVANCED_BATTERY_BOX_MINECART.get(),
                    entity -> new MinecartRenderer<>(entity, new ModelLayerLocation(
                            new ResourceLocation("minecraft", "chest_minecart"), "main")));

            ItemBlockRenderTypes.setRenderLayer(ModFluids.DIRTY_WATER.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.FLOWING_DIRTY_WATER.get(), RenderType.translucent());

            BlockEntityRenderers.register(ModBlockEntities.ITEM_CONVEYOR_BELT_ENTITY.get(), ItemConveyorBeltBlockEntityRenderer::new);
            BlockEntityRenderers.register(ModBlockEntities.FLUID_TANK_SMALL_ENTITY.get(), FluidTankBlockEntityRenderer::new);
            BlockEntityRenderers.register(ModBlockEntities.FLUID_TANK_MEDIUM_ENTITY.get(), FluidTankBlockEntityRenderer::new);
            BlockEntityRenderers.register(ModBlockEntities.FLUID_TANK_LARGE_ENTITY.get(), FluidTankBlockEntityRenderer::new);
        }

        @SubscribeEvent
        public static void loadBookPages(RegisterClientReloadListenersEvent event) {
            event.registerReloadListener(new EnergizedPowerBookReloadListener());
        }

        @SubscribeEvent
        public static void onKeyRegister(RegisterKeyMappingsEvent event) {
            event.register(ModKeyBindings.TELEPORTER_USE_KEY);
        }
    }
}
