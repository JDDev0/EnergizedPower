package me.jddev0.ep;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.behavior.ModBlockBehaviors;
import me.jddev0.ep.block.entity.EPBlockEntities;
import me.jddev0.ep.block.entity.renderer.FluidTankBlockEntityRenderer;
import me.jddev0.ep.block.entity.renderer.ItemConveyorBeltBlockEntityRenderer;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.entity.EPEntityTypes;
import me.jddev0.ep.fluid.EPFluidTypes;
import me.jddev0.ep.fluid.EPFluids;
import me.jddev0.ep.input.ModKeyBindings;
import me.jddev0.ep.item.*;
import me.jddev0.ep.loading.EnergizedPowerBookReloadListener;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.paintings.EPPaintings;
import me.jddev0.ep.recipe.EPRecipes;
import me.jddev0.ep.screen.*;
import me.jddev0.ep.villager.EPVillager;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.nbt.IntTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.api.distmarker.Dist;
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
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(EPAPI.MOD_ID)
public class EnergizedPowerMod {
    public EnergizedPowerMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModConfigs.registerConfigs(true);

        EPItems.register(modEventBus);
        EPBlocks.register(modEventBus);
        EPBlockEntities.register(modEventBus);
        EPRecipes.register(modEventBus);
        EPMenuTypes.register(modEventBus);
        EPVillager.register(modEventBus);
        EPEntityTypes.register(modEventBus);
        EPPaintings.register(modEventBus);

        EPFluids.register(modEventBus);
        EPFluidTypes.register(modEventBus);

        ModBlockBehaviors.register();

        EPCreativeModeTab.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addCreativeTab);

        NeoForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        ModMessages.register();
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
        if(event.getTab() == EPCreativeModeTab.ENERGIZED_POWER_TAB.get()) {
            event.accept(EPItems.ENERGIZED_POWER_BOOK);
            addEmptyAndFullyChargedItem(event, EPItems.ENERGY_ANALYZER, EnergyAnalyzerItem.ENERGY_CAPACITY);
            addEmptyAndFullyChargedItem(event, EPItems.FLUID_ANALYZER, FluidAnalyzerItem.ENERGY_CAPACITY);

            event.accept(EPItems.WOODEN_HAMMER);
            event.accept(EPItems.STONE_HAMMER);
            event.accept(EPItems.IRON_HAMMER);
            event.accept(EPItems.GOLDEN_HAMMER);
            event.accept(EPItems.DIAMOND_HAMMER);
            event.accept(EPItems.NETHERITE_HAMMER);

            event.accept(EPItems.CUTTER);

            event.accept(EPItems.WRENCH);

            event.accept(EPBlocks.ITEM_CONVEYOR_BELT_ITEM);
            event.accept(EPBlocks.ITEM_CONVEYOR_BELT_LOADER_ITEM);
            event.accept(EPBlocks.ITEM_CONVEYOR_BELT_SORTER_ITEM);
            event.accept(EPBlocks.ITEM_CONVEYOR_BELT_SWITCH_ITEM);
            event.accept(EPBlocks.ITEM_CONVEYOR_BELT_SPLITTER_ITEM);
            event.accept(EPBlocks.ITEM_CONVEYOR_BELT_MERGER_ITEM);

            event.accept(EPBlocks.IRON_FLUID_PIPE_ITEM);
            event.accept(EPBlocks.GOLDEN_FLUID_PIPE_ITEM);

            event.accept(EPBlocks.TIN_CABLE_ITEM);
            event.accept(EPBlocks.COPPER_CABLE_ITEM);
            event.accept(EPBlocks.GOLD_CABLE_ITEM);
            event.accept(EPBlocks.ENERGIZED_COPPER_CABLE_ITEM);
            event.accept(EPBlocks.ENERGIZED_GOLD_CABLE_ITEM);
            event.accept(EPBlocks.ENERGIZED_CRYSTAL_MATRIX_CABLE_ITEM);

            event.accept(EPBlocks.LV_TRANSFORMER_1_TO_N_ITEM);
            event.accept(EPBlocks.LV_TRANSFORMER_3_TO_3_ITEM);
            event.accept(EPBlocks.LV_TRANSFORMER_N_TO_1_ITEM);
            event.accept(EPBlocks.MV_TRANSFORMER_1_TO_N_ITEM);
            event.accept(EPBlocks.MV_TRANSFORMER_3_TO_3_ITEM);
            event.accept(EPBlocks.MV_TRANSFORMER_N_TO_1_ITEM);
            event.accept(EPBlocks.HV_TRANSFORMER_1_TO_N_ITEM);
            event.accept(EPBlocks.HV_TRANSFORMER_3_TO_3_ITEM);
            event.accept(EPBlocks.HV_TRANSFORMER_N_TO_1_ITEM);
            event.accept(EPBlocks.EHV_TRANSFORMER_1_TO_N_ITEM);
            event.accept(EPBlocks.EHV_TRANSFORMER_3_TO_3_ITEM);
            event.accept(EPBlocks.EHV_TRANSFORMER_N_TO_1_ITEM);

            event.accept(EPBlocks.PRESS_MOLD_MAKER_ITEM);
            event.accept(EPBlocks.ALLOY_FURNACE_ITEM);

            event.accept(EPBlocks.COAL_ENGINE_ITEM);
            event.accept(EPBlocks.HEAT_GENERATOR_ITEM);
            event.accept(EPBlocks.THERMAL_GENERATOR_ITEM);
            event.accept(EPBlocks.LIGHTNING_GENERATOR_ITEM);
            event.accept(EPBlocks.SOLAR_PANEL_ITEM_1);
            event.accept(EPBlocks.SOLAR_PANEL_ITEM_2);
            event.accept(EPBlocks.SOLAR_PANEL_ITEM_3);
            event.accept(EPBlocks.SOLAR_PANEL_ITEM_4);
            event.accept(EPBlocks.SOLAR_PANEL_ITEM_5);
            event.accept(EPBlocks.SOLAR_PANEL_ITEM_6);

            event.accept(EPBlocks.BATTERY_BOX_ITEM);
            event.accept(EPBlocks.ADVANCED_BATTERY_BOX_ITEM);
            event.accept(EPBlocks.CREATIVE_BATTERY_BOX_ITEM);

            event.accept(EPBlocks.POWERED_LAMP_ITEM);
            event.accept(EPBlocks.POWERED_FURNACE_ITEM);
            event.accept(EPBlocks.ADVANCED_POWERED_FURNACE_ITEM);
            event.accept(EPBlocks.AUTO_CRAFTER_ITEM);
            event.accept(EPBlocks.ADVANCED_AUTO_CRAFTER_ITEM);
            event.accept(EPBlocks.CRUSHER_ITEM);
            event.accept(EPBlocks.ADVANCED_CRUSHER_ITEM);
            event.accept(EPBlocks.PULVERIZER_ITEM);
            event.accept(EPBlocks.ADVANCED_PULVERIZER_ITEM);
            event.accept(EPBlocks.SAWMILL_ITEM);
            event.accept(EPBlocks.COMPRESSOR_ITEM);
            event.accept(EPBlocks.METAL_PRESS_ITEM);
            event.accept(EPBlocks.AUTO_PRESS_MOLD_MAKER_ITEM);
            event.accept(EPBlocks.AUTO_STONECUTTER_ITEM);
            event.accept(EPBlocks.ASSEMBLING_MACHINE_ITEM);
            event.accept(EPBlocks.INDUCTION_SMELTER_ITEM);
            event.accept(EPBlocks.PLANT_GROWTH_CHAMBER_ITEM);
            event.accept(EPBlocks.STONE_SOLIDIFIER_ITEM);
            event.accept(EPBlocks.FILTRATION_PLANT_ITEM);
            event.accept(EPBlocks.FLUID_TRANSPOSER_ITEM);
            event.accept(EPBlocks.BLOCK_PLACER_ITEM);
            event.accept(EPBlocks.FLUID_TANK_SMALL_ITEM);
            event.accept(EPBlocks.FLUID_TANK_MEDIUM_ITEM);
            event.accept(EPBlocks.FLUID_TANK_LARGE_ITEM);
            event.accept(EPBlocks.CREATIVE_FLUID_TANK_ITEM);
            event.accept(EPBlocks.FLUID_FILLER_ITEM);
            event.accept(EPBlocks.FLUID_DRAINER_ITEM);
            event.accept(EPBlocks.FLUID_PUMP_ITEM);
            event.accept(EPBlocks.DRAIN_ITEM);
            event.accept(EPBlocks.CHARGER_ITEM);
            event.accept(EPBlocks.ADVANCED_CHARGER_ITEM);
            event.accept(EPBlocks.UNCHARGER_ITEM);
            event.accept(EPBlocks.ADVANCED_UNCHARGER_ITEM);
            event.accept(EPBlocks.MINECART_CHARGER_ITEM);
            event.accept(EPBlocks.ADVANCED_MINECART_CHARGER_ITEM);
            event.accept(EPBlocks.MINECART_UNCHARGER_ITEM);
            event.accept(EPBlocks.ADVANCED_MINECART_UNCHARGER_ITEM);

            event.accept(EPBlocks.ENERGIZER_ITEM);
            event.accept(EPBlocks.CHARGING_STATION_ITEM);
            event.accept(EPBlocks.CRYSTAL_GROWTH_CHAMBER);

            event.accept(EPBlocks.WEATHER_CONTROLLER_ITEM);
            event.accept(EPBlocks.TIME_CONTROLLER_ITEM);
            event.accept(EPBlocks.TELEPORTER_ITEM);

            addEmptyAndFullyChargedItem(event, EPItems.INVENTORY_COAL_ENGINE, InventoryCoalEngineItem.CAPACITY);
            event.accept(EPItems.INVENTORY_CHARGER);

            addEmptyAndFullyChargedItem(event, EPItems.INVENTORY_TELEPORTER, InventoryTeleporterItem.CAPACITY);

            addEmptyAndFullyChargedItem(event, EPItems.BATTERY_1, BatteryItem.Tier.BATTERY_1.getCapacity());
            addEmptyAndFullyChargedItem(event, EPItems.BATTERY_2, BatteryItem.Tier.BATTERY_2.getCapacity());
            addEmptyAndFullyChargedItem(event, EPItems.BATTERY_3, BatteryItem.Tier.BATTERY_3.getCapacity());
            addEmptyAndFullyChargedItem(event, EPItems.BATTERY_4, BatteryItem.Tier.BATTERY_4.getCapacity());
            addEmptyAndFullyChargedItem(event, EPItems.BATTERY_5, BatteryItem.Tier.BATTERY_5.getCapacity());
            addEmptyAndFullyChargedItem(event, EPItems.BATTERY_6, BatteryItem.Tier.BATTERY_6.getCapacity());
            addEmptyAndFullyChargedItem(event, EPItems.BATTERY_7, BatteryItem.Tier.BATTERY_7.getCapacity());
            addEmptyAndFullyChargedItem(event, EPItems.BATTERY_8, BatteryItem.Tier.BATTERY_8.getCapacity());
            event.accept(EPItems.CREATIVE_BATTERY);

            event.accept(EPItems.BATTERY_BOX_MINECART);
            event.accept(EPItems.ADVANCED_BATTERY_BOX_MINECART);

            event.accept(EPBlocks.BASIC_MACHINE_FRAME_ITEM);
            event.accept(EPBlocks.HARDENED_MACHINE_FRAME_ITEM);
            event.accept(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM);
            event.accept(EPBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM);

            event.accept(EPItems.BASIC_SOLAR_CELL);
            event.accept(EPItems.ADVANCED_SOLAR_CELL);
            event.accept(EPItems.REINFORCED_ADVANCED_SOLAR_CELL);

            event.accept(EPItems.BASIC_CIRCUIT);
            event.accept(EPItems.ADVANCED_CIRCUIT);
            event.accept(EPItems.PROCESSING_UNIT);

            event.accept(EPItems.TELEPORTER_PROCESSING_UNIT);
            event.accept(EPItems.TELEPORTER_MATRIX);

            event.accept(EPItems.BASIC_UPGRADE_MODULE);
            event.accept(EPItems.ADVANCED_UPGRADE_MODULE);
            event.accept(EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE);

            event.accept(EPItems.SPEED_UPGRADE_MODULE_1);
            event.accept(EPItems.SPEED_UPGRADE_MODULE_2);
            event.accept(EPItems.SPEED_UPGRADE_MODULE_3);
            event.accept(EPItems.SPEED_UPGRADE_MODULE_4);
            event.accept(EPItems.SPEED_UPGRADE_MODULE_5);

            event.accept(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_1);
            event.accept(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_2);
            event.accept(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_3);
            event.accept(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_4);
            event.accept(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_5);

            event.accept(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_1);
            event.accept(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_2);
            event.accept(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_3);
            event.accept(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_4);
            event.accept(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_5);

            event.accept(EPItems.DURATION_UPGRADE_MODULE_1);
            event.accept(EPItems.DURATION_UPGRADE_MODULE_2);
            event.accept(EPItems.DURATION_UPGRADE_MODULE_3);
            event.accept(EPItems.DURATION_UPGRADE_MODULE_4);
            event.accept(EPItems.DURATION_UPGRADE_MODULE_5);
            event.accept(EPItems.DURATION_UPGRADE_MODULE_6);

            event.accept(EPItems.RANGE_UPGRADE_MODULE_1);
            event.accept(EPItems.RANGE_UPGRADE_MODULE_2);
            event.accept(EPItems.RANGE_UPGRADE_MODULE_3);

            event.accept(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_1);
            event.accept(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_2);
            event.accept(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_3);
            event.accept(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_4);
            event.accept(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_5);

            event.accept(EPItems.BLAST_FURNACE_UPGRADE_MODULE);
            event.accept(EPItems.SMOKER_UPGRADE_MODULE);

            event.accept(EPItems.MOON_LIGHT_UPGRADE_MODULE_1);
            event.accept(EPItems.MOON_LIGHT_UPGRADE_MODULE_2);
            event.accept(EPItems.MOON_LIGHT_UPGRADE_MODULE_3);

            event.accept(EPBlocks.SILICON_BLOCK_ITEM);
            event.accept(EPBlocks.TIN_BLOCK_ITEM);
            event.accept(EPBlocks.SAWDUST_BLOCK_ITEM);
            event.accept(EPItems.CABLE_INSULATOR);
            event.accept(EPItems.CHARCOAL_FILTER);
            event.accept(EPItems.SAW_BLADE);
            event.accept(EPItems.CRYSTAL_MATRIX);
            event.accept(EPItems.SAWDUST);
            event.accept(EPItems.CHARCOAL_DUST);
            event.accept(EPItems.BASIC_FERTILIZER);
            event.accept(EPItems.GOOD_FERTILIZER);
            event.accept(EPItems.ADVANCED_FERTILIZER);
            event.accept(EPItems.RAW_GEAR_PRESS_MOLD);
            event.accept(EPItems.RAW_ROD_PRESS_MOLD);
            event.accept(EPItems.RAW_WIRE_PRESS_MOLD);
            event.accept(EPItems.GEAR_PRESS_MOLD);
            event.accept(EPItems.ROD_PRESS_MOLD);
            event.accept(EPItems.WIRE_PRESS_MOLD);
            event.accept(EPItems.SILICON);
            event.accept(EPItems.TIN_DUST);
            event.accept(EPItems.COPPER_DUST);
            event.accept(EPItems.IRON_DUST);
            event.accept(EPItems.GOLD_DUST);
            event.accept(EPItems.TIN_NUGGET);
            event.accept(EPItems.TIN_INGOT);
            event.accept(EPItems.TIN_PLATE);
            event.accept(EPItems.COPPER_PLATE);
            event.accept(EPItems.IRON_PLATE);
            event.accept(EPItems.GOLD_PLATE);
            event.accept(EPItems.STEEL_INGOT);
            event.accept(EPItems.REDSTONE_ALLOY_INGOT);
            event.accept(EPItems.ADVANCED_ALLOY_INGOT);
            event.accept(EPItems.ADVANCED_ALLOY_PLATE);
            event.accept(EPItems.IRON_GEAR);
            event.accept(EPItems.IRON_ROD);
            event.accept(EPItems.TIN_WIRE);
            event.accept(EPItems.COPPER_WIRE);
            event.accept(EPItems.GOLD_WIRE);
            event.accept(EPItems.ENERGIZED_COPPER_INGOT);
            event.accept(EPItems.ENERGIZED_GOLD_INGOT);
            event.accept(EPItems.ENERGIZED_COPPER_PLATE);
            event.accept(EPItems.ENERGIZED_GOLD_PLATE);
            event.accept(EPItems.ENERGIZED_COPPER_WIRE);
            event.accept(EPItems.ENERGIZED_GOLD_WIRE);
            event.accept(EPItems.ENERGIZED_CRYSTAL_MATRIX);

            event.accept(EPFluids.DIRTY_WATER_BUCKET_ITEM);

            event.accept(EPItems.STONE_PEBBLE);

            event.accept(EPItems.RAW_TIN);
            event.accept(EPBlocks.TIN_ORE_ITEM);
            event.accept(EPBlocks.DEEPSLATE_TIN_ORE_ITEM);
            event.accept(EPBlocks.RAW_TIN_BLOCK_ITEM);
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    @Mod.EventBusSubscriber(modid = EPAPI.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            ModConfigs.registerConfigs(false);

            MenuScreens.register(EPMenuTypes.ITEM_CONVEYOR_BELT_LOADER_MENU.get(), ItemConveyorBeltLoaderScreen::new);
            MenuScreens.register(EPMenuTypes.ITEM_CONVEYOR_BELT_SORTER_MENU.get(), ItemConveyorBeltSorterScreen::new);
            MenuScreens.register(EPMenuTypes.AUTO_CRAFTER_MENU.get(), AutoCrafterScreen::new);
            MenuScreens.register(EPMenuTypes.ADVANCED_AUTO_CRAFTER_MENU.get(), AdvancedAutoCrafterScreen::new);
            MenuScreens.register(EPMenuTypes.CRUSHER_MENU.get(), CrusherScreen::new);
            MenuScreens.register(EPMenuTypes.ADVANCED_CRUSHER_MENU.get(), AdvancedCrusherScreen::new);
            MenuScreens.register(EPMenuTypes.PULVERIZER_MENU.get(), PulverizerScreen::new);
            MenuScreens.register(EPMenuTypes.ADVANCED_PULVERIZER_MENU.get(), AdvancedPulverizerScreen::new);
            MenuScreens.register(EPMenuTypes.SAWMILL_MENU.get(), SawmillScreen::new);
            MenuScreens.register(EPMenuTypes.COMPRESSOR_MENU.get(), CompressorScreen::new);
            MenuScreens.register(EPMenuTypes.PLANT_GROWTH_CHAMBER_MENU.get(), PlantGrowthChamberScreen::new);
            MenuScreens.register(EPMenuTypes.STONE_SOLIDIFIER_MENU.get(), StoneSolidifierScreen::new);
            MenuScreens.register(EPMenuTypes.FILTRATION_PLANT_MENU.get(), FiltrationPlantScreen::new);
            MenuScreens.register(EPMenuTypes.FLUID_TRANSPOSER_MENU.get(), FluidTransposerScreen::new);
            MenuScreens.register(EPMenuTypes.BLOCK_PLACER_MENU.get(), BlockPlacerScreen::new);
            MenuScreens.register(EPMenuTypes.FLUID_FILLER_MENU.get(), FluidFillerScreen::new);
            MenuScreens.register(EPMenuTypes.FLUID_DRAINER_MENU.get(), FluidDrainerScreen::new);
            MenuScreens.register(EPMenuTypes.FLUID_PUMP_MENU.get(), FluidPumpScreen::new);
            MenuScreens.register(EPMenuTypes.DRAIN_MENU.get(), DrainScreen::new);
            MenuScreens.register(EPMenuTypes.CHARGER_MENU.get(), ChargerScreen::new);
            MenuScreens.register(EPMenuTypes.ADVANCED_CHARGER_MENU.get(), AdvancedChargerScreen::new);
            MenuScreens.register(EPMenuTypes.UNCHARGER_MENU.get(), UnchargerScreen::new);
            MenuScreens.register(EPMenuTypes.ADVANCED_UNCHARGER_MENU.get(), AdvancedUnchargerScreen::new);
            MenuScreens.register(EPMenuTypes.ENERGIZER_MENU.get(), EnergizerScreen::new);
            MenuScreens.register(EPMenuTypes.COAL_ENGINE_MENU.get(), CoalEngineScreen::new);
            MenuScreens.register(EPMenuTypes.POWERED_FURNACE_MENU.get(), PoweredFurnaceScreen::new);
            MenuScreens.register(EPMenuTypes.ADVANCED_POWERED_FURNACE_MENU.get(), AdvancedPoweredFurnaceScreen::new);
            MenuScreens.register(EPMenuTypes.WEATHER_CONTROLLER_MENU.get(), WeatherControllerScreen::new);
            MenuScreens.register(EPMenuTypes.TIME_CONTROLLER_MENU.get(), TimeControllerScreen::new);
            MenuScreens.register(EPMenuTypes.TELEPORTER_MENU.get(), TeleporterScreen::new);
            MenuScreens.register(EPMenuTypes.LIGHTNING_GENERATOR_MENU.get(), LightningGeneratorScreen::new);
            MenuScreens.register(EPMenuTypes.CHARGING_STATION_MENU.get(), ChargingStationScreen::new);
            MenuScreens.register(EPMenuTypes.CRYSTAL_GROWTH_CHAMBER_MENU.get(), CrystalGrowthChamberScreen::new);
            MenuScreens.register(EPMenuTypes.HEAT_GENERATOR_MENU.get(), HeatGeneratorScreen::new);
            MenuScreens.register(EPMenuTypes.THERMAL_GENERATOR_MENU.get(), ThermalGeneratorScreen::new);
            MenuScreens.register(EPMenuTypes.BATTERY_BOX_MENU.get(), BatteryBoxScreen::new);
            MenuScreens.register(EPMenuTypes.ADVANCED_BATTERY_BOX_MENU.get(), AdvancedBatteryBoxScreen::new);
            MenuScreens.register(EPMenuTypes.CREATIVE_BATTERY_BOX_MENU.get(), CreativeBatteryBoxScreen::new);
            MenuScreens.register(EPMenuTypes.MINECART_CHARGER_MENU.get(), MinecartChargerScreen::new);
            MenuScreens.register(EPMenuTypes.ADVANCED_MINECART_CHARGER_MENU.get(), AdvancedMinecartChargerScreen::new);
            MenuScreens.register(EPMenuTypes.MINECART_UNCHARGER_MENU.get(), MinecartUnchargerScreen::new);
            MenuScreens.register(EPMenuTypes.ADVANCED_MINECART_UNCHARGER_MENU.get(), AdvancedMinecartUnchargerScreen::new);
            MenuScreens.register(EPMenuTypes.SOLAR_PANEL_MENU_1.get(), SolarPanelScreen::new);
            MenuScreens.register(EPMenuTypes.SOLAR_PANEL_MENU_2.get(), SolarPanelScreen::new);
            MenuScreens.register(EPMenuTypes.SOLAR_PANEL_MENU_3.get(), SolarPanelScreen::new);
            MenuScreens.register(EPMenuTypes.SOLAR_PANEL_MENU_4.get(), SolarPanelScreen::new);
            MenuScreens.register(EPMenuTypes.SOLAR_PANEL_MENU_5.get(), SolarPanelScreen::new);
            MenuScreens.register(EPMenuTypes.SOLAR_PANEL_MENU_6.get(), SolarPanelScreen::new);
            MenuScreens.register(EPMenuTypes.PRESS_MOLD_MAKER_MENU.get(), PressMoldMakerScreen::new);
            MenuScreens.register(EPMenuTypes.ALLOY_FURNACE_MENU.get(), AlloyFurnaceScreen::new);
            MenuScreens.register(EPMenuTypes.METAL_PRESS_MENU.get(), MetalPressScreen::new);
            MenuScreens.register(EPMenuTypes.AUTO_PRESS_MOLD_MAKER_MENU.get(), AutoPressMoldMakerScreen::new);
            MenuScreens.register(EPMenuTypes.AUTO_STONECUTTER_MENU.get(), AutoStonecutterScreen::new);
            MenuScreens.register(EPMenuTypes.ASSEMBLING_MACHINE_MENU.get(), AssemblingMachineScreen::new);
            MenuScreens.register(EPMenuTypes.INDUCTION_SMELTER_MENU.get(), InductionSmelterScreen::new);
            MenuScreens.register(EPMenuTypes.FLUID_TANK_SMALL.get(), FluidTankScreen::new);
            MenuScreens.register(EPMenuTypes.FLUID_TANK_MEDIUM.get(), FluidTankScreen::new);
            MenuScreens.register(EPMenuTypes.FLUID_TANK_LARGE.get(), FluidTankScreen::new);
            MenuScreens.register(EPMenuTypes.CREATIVE_FLUID_TANK.get(), CreativeFluidTankScreen::new);

            MenuScreens.register(EPMenuTypes.INVENTORY_CHARGER_MENU.get(), InventoryChargerScreen::new);
            MenuScreens.register(EPMenuTypes.INVENTORY_TELEPORTER_MENU.get(), InventoryTeleporterScreen::new);

            MenuScreens.register(EPMenuTypes.MINECART_BATTERY_BOX_MENU.get(), MinecartBatteryBoxScreen::new);
            MenuScreens.register(EPMenuTypes.MINECART_ADVANCED_BATTERY_BOX_MENU.get(), MinecartAdvancedBatteryBoxScreen::new);

            event.enqueueWork(() -> {
                ItemProperties.registerGeneric(EPAPI.id("active"), (itemStack, level, entity, seed) -> {
                    Item item = itemStack.getItem();
                    return (item instanceof ActivatableItem && ((ActivatableItem)item).isActive(itemStack))?1.f:0.f;
                });
                ItemProperties.registerGeneric(EPAPI.id("working"), (itemStack, level, entity, seed) -> {
                    Item item = itemStack.getItem();
                    return (item instanceof WorkingItem && ((WorkingItem)item).isWorking(itemStack))?1.f:0.f;
                });
            });

            EntityRenderers.register(EPEntityTypes.BATTERY_BOX_MINECART.get(),
                    entity -> new MinecartRenderer<>(entity, new ModelLayerLocation(
                            new ResourceLocation("minecraft", "chest_minecart"), "main")));
            EntityRenderers.register(EPEntityTypes.ADVANCED_BATTERY_BOX_MINECART.get(),
                    entity -> new MinecartRenderer<>(entity, new ModelLayerLocation(
                            new ResourceLocation("minecraft", "chest_minecart"), "main")));

            ItemBlockRenderTypes.setRenderLayer(EPFluids.DIRTY_WATER.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(EPFluids.FLOWING_DIRTY_WATER.get(), RenderType.translucent());

            BlockEntityRenderers.register(EPBlockEntities.ITEM_CONVEYOR_BELT_ENTITY.get(), ItemConveyorBeltBlockEntityRenderer::new);
            BlockEntityRenderers.register(EPBlockEntities.FLUID_TANK_SMALL_ENTITY.get(), FluidTankBlockEntityRenderer::new);
            BlockEntityRenderers.register(EPBlockEntities.FLUID_TANK_MEDIUM_ENTITY.get(), FluidTankBlockEntityRenderer::new);
            BlockEntityRenderers.register(EPBlockEntities.FLUID_TANK_LARGE_ENTITY.get(), FluidTankBlockEntityRenderer::new);
            BlockEntityRenderers.register(EPBlockEntities.CREATIVE_FLUID_TANK_ENTITY.get(), FluidTankBlockEntityRenderer::new);
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
