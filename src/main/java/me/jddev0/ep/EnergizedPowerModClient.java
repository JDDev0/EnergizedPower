package me.jddev0.ep;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.entity.EPBlockEntities;
import me.jddev0.ep.block.entity.renderer.FluidTankBlockEntityRenderer;
import me.jddev0.ep.block.entity.renderer.ItemConveyorBeltBlockEntityRenderer;
import me.jddev0.ep.client.item.property.bool.ActiveProperty;
import me.jddev0.ep.client.item.property.bool.WorkingProperty;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.entity.EPEntityTypes;
import me.jddev0.ep.fluid.EPFluids;
import me.jddev0.ep.input.ModKeyBindings;
import me.jddev0.ep.integration.jei.EnergizedPowerJEIPlugin;
import me.jddev0.ep.integration.jei.EnergizedPowerJEIUtils;
import me.jddev0.ep.loading.EnergizedPowerBookReloadListener;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.screen.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.recipe.v1.sync.ClientRecipeSynchronizedEvent;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperties;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;

public class EnergizedPowerModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModConfigs.registerConfigs(false);

        MenuScreens.register(EPMenuTypes.BASIC_ITEM_CONVEYOR_BELT_LOADER_MENU, ItemConveyorBeltLoaderScreen::new);
        MenuScreens.register(EPMenuTypes.FAST_ITEM_CONVEYOR_BELT_LOADER_MENU, ItemConveyorBeltLoaderScreen::new);
        MenuScreens.register(EPMenuTypes.EXPRESS_ITEM_CONVEYOR_BELT_LOADER_MENU, ItemConveyorBeltLoaderScreen::new);
        MenuScreens.register(EPMenuTypes.BASIC_ITEM_CONVEYOR_BELT_SORTER_MENU, ItemConveyorBeltSorterScreen::new);
        MenuScreens.register(EPMenuTypes.FAST_ITEM_CONVEYOR_BELT_SORTER_MENU, ItemConveyorBeltSorterScreen::new);
        MenuScreens.register(EPMenuTypes.EXPRESS_ITEM_CONVEYOR_BELT_SORTER_MENU, ItemConveyorBeltSorterScreen::new);
        MenuScreens.register(EPMenuTypes.AUTO_CRAFTER_MENU, AutoCrafterScreen::new);
        MenuScreens.register(EPMenuTypes.ADVANCED_AUTO_CRAFTER_MENU, AdvancedAutoCrafterScreen::new);
        MenuScreens.register(EPMenuTypes.CRUSHER_MENU, CrusherScreen::new);
        MenuScreens.register(EPMenuTypes.ADVANCED_CRUSHER_MENU, AdvancedCrusherScreen::new);
        MenuScreens.register(EPMenuTypes.PULVERIZER_MENU, PulverizerScreen::new);
        MenuScreens.register(EPMenuTypes.ADVANCED_PULVERIZER_MENU, AdvancedPulverizerScreen::new);
        MenuScreens.register(EPMenuTypes.SAWMILL_MENU, SawmillScreen::new);
        MenuScreens.register(EPMenuTypes.COMPRESSOR_MENU, CompressorScreen::new);
        MenuScreens.register(EPMenuTypes.PLANT_GROWTH_CHAMBER_MENU, PlantGrowthChamberScreen::new);
        MenuScreens.register(EPMenuTypes.STONE_LIQUEFIER_MENU, StoneLiquefierScreen::new);
        MenuScreens.register(EPMenuTypes.STONE_SOLIDIFIER_MENU, StoneSolidifierScreen::new);
        MenuScreens.register(EPMenuTypes.FILTRATION_PLANT_MENU, FiltrationPlantScreen::new);
        MenuScreens.register(EPMenuTypes.FLUID_TRANSPOSER_MENU, FluidTransposerScreen::new);
        MenuScreens.register(EPMenuTypes.BLOCK_PLACER_MENU, BlockPlacerScreen::new);
        MenuScreens.register(EPMenuTypes.FLUID_FILLER_MENU, FluidFillerScreen::new);
        MenuScreens.register(EPMenuTypes.FLUID_DRAINER_MENU, FluidDrainerScreen::new);
        MenuScreens.register(EPMenuTypes.FLUID_PUMP_MENU, FluidPumpScreen::new);
        MenuScreens.register(EPMenuTypes.ADVANCED_FLUID_PUMP_MENU, AdvancedFluidPumpScreen::new);
        MenuScreens.register(EPMenuTypes.DRAIN_MENU, DrainScreen::new);
        MenuScreens.register(EPMenuTypes.CHARGER_MENU, ChargerScreen::new);
        MenuScreens.register(EPMenuTypes.ADVANCED_CHARGER_MENU, AdvancedChargerScreen::new);
        MenuScreens.register(EPMenuTypes.UNCHARGER_MENU, UnchargerScreen::new);
        MenuScreens.register(EPMenuTypes.ADVANCED_UNCHARGER_MENU, AdvancedUnchargerScreen::new);
        MenuScreens.register(EPMenuTypes.ENERGIZER_MENU, EnergizerScreen::new);
        MenuScreens.register(EPMenuTypes.COAL_ENGINE_MENU, CoalEngineScreen::new);
        MenuScreens.register(EPMenuTypes.POWERED_FURNACE_MENU, PoweredFurnaceScreen::new);
        MenuScreens.register(EPMenuTypes.ADVANCED_POWERED_FURNACE_MENU, AdvancedPoweredFurnaceScreen::new);
        MenuScreens.register(EPMenuTypes.WEATHER_CONTROLLER_MENU, WeatherControllerScreen::new);
        MenuScreens.register(EPMenuTypes.TIME_CONTROLLER_MENU, TimeControllerScreen::new);
        MenuScreens.register(EPMenuTypes.TELEPORTER_MENU, TeleporterScreen::new);
        MenuScreens.register(EPMenuTypes.LIGHTNING_GENERATOR_MENU, LightningGeneratorScreen::new);
        MenuScreens.register(EPMenuTypes.CHARGING_STATION_MENU, ChargingStationScreen::new);
        MenuScreens.register(EPMenuTypes.CRYSTAL_GROWTH_CHAMBER_MENU, CrystalGrowthChamberScreen::new);
        MenuScreens.register(EPMenuTypes.HEAT_GENERATOR_MENU, HeatGeneratorScreen::new);
        MenuScreens.register(EPMenuTypes.THERMAL_GENERATOR_MENU, ThermalGeneratorScreen::new);
        MenuScreens.register(EPMenuTypes.BATTERY_BOX_MENU, BatteryBoxScreen::new);
        MenuScreens.register(EPMenuTypes.ADVANCED_BATTERY_BOX_MENU, AdvancedBatteryBoxScreen::new);
        MenuScreens.register(EPMenuTypes.CREATIVE_BATTERY_BOX_MENU, CreativeBatteryBoxScreen::new);
        MenuScreens.register(EPMenuTypes.MINECART_CHARGER_MENU, MinecartChargerScreen::new);
        MenuScreens.register(EPMenuTypes.ADVANCED_MINECART_CHARGER_MENU, AdvancedMinecartChargerScreen::new);
        MenuScreens.register(EPMenuTypes.MINECART_UNCHARGER_MENU, MinecartUnchargerScreen::new);
        MenuScreens.register(EPMenuTypes.ADVANCED_MINECART_UNCHARGER_MENU, AdvancedMinecartUnchargerScreen::new);
        MenuScreens.register(EPMenuTypes.SOLAR_PANEL_MENU_1, SolarPanelScreen::new);
        MenuScreens.register(EPMenuTypes.SOLAR_PANEL_MENU_2, SolarPanelScreen::new);
        MenuScreens.register(EPMenuTypes.SOLAR_PANEL_MENU_3, SolarPanelScreen::new);
        MenuScreens.register(EPMenuTypes.SOLAR_PANEL_MENU_4, SolarPanelScreen::new);
        MenuScreens.register(EPMenuTypes.SOLAR_PANEL_MENU_5, SolarPanelScreen::new);
        MenuScreens.register(EPMenuTypes.SOLAR_PANEL_MENU_6, SolarPanelScreen::new);
        MenuScreens.register(EPMenuTypes.LV_TRANSFORMER_1_TO_N_MENU, TransformerScreen::new);
        MenuScreens.register(EPMenuTypes.LV_TRANSFORMER_3_TO_3_MENU, TransformerScreen::new);
        MenuScreens.register(EPMenuTypes.LV_TRANSFORMER_N_TO_1_MENU, TransformerScreen::new);
        MenuScreens.register(EPMenuTypes.CONFIGURABLE_LV_TRANSFORMER_MENU, TransformerScreen::new);
        MenuScreens.register(EPMenuTypes.MV_TRANSFORMER_1_TO_N_MENU, TransformerScreen::new);
        MenuScreens.register(EPMenuTypes.MV_TRANSFORMER_3_TO_3_MENU, TransformerScreen::new);
        MenuScreens.register(EPMenuTypes.MV_TRANSFORMER_N_TO_1_MENU, TransformerScreen::new);
        MenuScreens.register(EPMenuTypes.CONFIGURABLE_MV_TRANSFORMER_MENU, TransformerScreen::new);
        MenuScreens.register(EPMenuTypes.HV_TRANSFORMER_1_TO_N_MENU, TransformerScreen::new);
        MenuScreens.register(EPMenuTypes.HV_TRANSFORMER_3_TO_3_MENU, TransformerScreen::new);
        MenuScreens.register(EPMenuTypes.HV_TRANSFORMER_N_TO_1_MENU, TransformerScreen::new);
        MenuScreens.register(EPMenuTypes.CONFIGURABLE_HV_TRANSFORMER_MENU, TransformerScreen::new);
        MenuScreens.register(EPMenuTypes.EHV_TRANSFORMER_1_TO_N_MENU, TransformerScreen::new);
        MenuScreens.register(EPMenuTypes.EHV_TRANSFORMER_3_TO_3_MENU, TransformerScreen::new);
        MenuScreens.register(EPMenuTypes.EHV_TRANSFORMER_N_TO_1_MENU, TransformerScreen::new);
        MenuScreens.register(EPMenuTypes.CONFIGURABLE_EHV_TRANSFORMER_MENU, TransformerScreen::new);
        MenuScreens.register(EPMenuTypes.PRESS_MOLD_MAKER_MENU, PressMoldMakerScreen::new);
        MenuScreens.register(EPMenuTypes.ALLOY_FURNACE_MENU, AlloyFurnaceScreen::new);
        MenuScreens.register(EPMenuTypes.METAL_PRESS_MENU, MetalPressScreen::new);
        MenuScreens.register(EPMenuTypes.AUTO_PRESS_MOLD_MAKER_MENU, AutoPressMoldMakerScreen::new);
        MenuScreens.register(EPMenuTypes.AUTO_STONECUTTER_MENU, AutoStonecutterScreen::new);
        MenuScreens.register(EPMenuTypes.ASSEMBLING_MACHINE_MENU, AssemblingMachineScreen::new);
        MenuScreens.register(EPMenuTypes.INDUCTION_SMELTER_MENU, InductionSmelterScreen::new);
        MenuScreens.register(EPMenuTypes.FLUID_TANK_SMALL, FluidTankScreen::new);
        MenuScreens.register(EPMenuTypes.FLUID_TANK_MEDIUM, FluidTankScreen::new);
        MenuScreens.register(EPMenuTypes.FLUID_TANK_LARGE, FluidTankScreen::new);
        MenuScreens.register(EPMenuTypes.CREATIVE_FLUID_TANK, CreativeFluidTankScreen::new);
        MenuScreens.register(EPMenuTypes.ITEM_SILO_TINY, ItemSiloScreen::new);
        MenuScreens.register(EPMenuTypes.ITEM_SILO_SMALL, ItemSiloScreen::new);
        MenuScreens.register(EPMenuTypes.ITEM_SILO_MEDIUM, ItemSiloScreen::new);
        MenuScreens.register(EPMenuTypes.ITEM_SILO_LARGE, ItemSiloScreen::new);
        MenuScreens.register(EPMenuTypes.ITEM_SILO_GIANT, ItemSiloScreen::new);
        MenuScreens.register(EPMenuTypes.CREATIVE_ITEM_SILO_MENU, CreativeItemSiloScreen::new);

        MenuScreens.register(EPMenuTypes.INVENTORY_CHARGER_MENU, InventoryChargerScreen::new);
        MenuScreens.register(EPMenuTypes.INVENTORY_TELEPORTER_MENU, InventoryTeleporterScreen::new);

        MenuScreens.register(EPMenuTypes.MINECART_BATTERY_BOX_MENU, MinecartBatteryBoxScreen::new);
        MenuScreens.register(EPMenuTypes.MINECART_ADVANCED_BATTERY_BOX_MENU, MinecartAdvancedBatteryBoxScreen::new);

        ConditionalItemModelProperties.ID_MAPPER.put(EPAPI.id("active"), ActiveProperty.CODEC);
        ConditionalItemModelProperties.ID_MAPPER.put(EPAPI.id("working"), WorkingProperty.CODEC);

        ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloader(EPAPI.id("energizedpowerbook"), new EnergizedPowerBookReloadListener());

        ModMessages.registerPacketsS2C();

        ModKeyBindings.register();

        EntityRenderers.register(EPEntityTypes.BATTERY_BOX_MINECART,
                entity -> new MinecartRenderer(entity, new ModelLayerLocation(
                        Identifier.fromNamespaceAndPath("minecraft", "chest_minecart"), "main")));
        EntityRenderers.register(EPEntityTypes.ADVANCED_BATTERY_BOX_MINECART,
                entity -> new MinecartRenderer(entity, new ModelLayerLocation(
                        Identifier.fromNamespaceAndPath("minecraft", "chest_minecart"), "main")));

        FluidRenderHandlerRegistry.INSTANCE.register(EPFluids.DIRTY_WATER, EPFluids.FLOWING_DIRTY_WATER,
                new SimpleFluidRenderHandler(
                        Identifier.parse("block/water_still"),
                        Identifier.parse("block/water_flow"),
                        0xC86F3900
                ));

        BlockRenderLayerMap.putFluids(ChunkSectionLayer.TRANSLUCENT,
                EPFluids.DIRTY_WATER, EPFluids.FLOWING_DIRTY_WATER);

        BlockEntityRenderers.register(EPBlockEntities.BASIC_ITEM_CONVEYOR_BELT_ENTITY, ItemConveyorBeltBlockEntityRenderer::new);
        BlockEntityRenderers.register(EPBlockEntities.FAST_ITEM_CONVEYOR_BELT_ENTITY, ItemConveyorBeltBlockEntityRenderer::new);
        BlockEntityRenderers.register(EPBlockEntities.EXPRESS_ITEM_CONVEYOR_BELT_ENTITY, ItemConveyorBeltBlockEntityRenderer::new);
        BlockEntityRenderers.register(EPBlockEntities.FLUID_TANK_SMALL_ENTITY, FluidTankBlockEntityRenderer::new);
        BlockEntityRenderers.register(EPBlockEntities.FLUID_TANK_MEDIUM_ENTITY, FluidTankBlockEntityRenderer::new);
        BlockEntityRenderers.register(EPBlockEntities.FLUID_TANK_LARGE_ENTITY, FluidTankBlockEntityRenderer::new);
        BlockEntityRenderers.register(EPBlockEntities.CREATIVE_FLUID_TANK_ENTITY, FluidTankBlockEntityRenderer::new);

        ClientRecipeSynchronizedEvent.EVENT.register((minecraft, synchronizedRecipes) -> {
            if(EnergizedPowerJEIUtils.isJEIAvailable()) {
                EnergizedPowerJEIPlugin.recipeMap = synchronizedRecipes;
            }
        });
    }
}
