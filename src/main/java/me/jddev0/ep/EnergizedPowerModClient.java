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
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.entity.EntityRendererFactories;
import net.minecraft.client.render.entity.MinecartEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.item.property.bool.BooleanProperties;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

public class EnergizedPowerModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModConfigs.registerConfigs(false);

        HandledScreens.register(EPMenuTypes.BASIC_ITEM_CONVEYOR_BELT_LOADER_MENU, ItemConveyorBeltLoaderScreen::new);
        HandledScreens.register(EPMenuTypes.FAST_ITEM_CONVEYOR_BELT_LOADER_MENU, ItemConveyorBeltLoaderScreen::new);
        HandledScreens.register(EPMenuTypes.EXPRESS_ITEM_CONVEYOR_BELT_LOADER_MENU, ItemConveyorBeltLoaderScreen::new);
        HandledScreens.register(EPMenuTypes.BASIC_ITEM_CONVEYOR_BELT_SORTER_MENU, ItemConveyorBeltSorterScreen::new);
        HandledScreens.register(EPMenuTypes.FAST_ITEM_CONVEYOR_BELT_SORTER_MENU, ItemConveyorBeltSorterScreen::new);
        HandledScreens.register(EPMenuTypes.EXPRESS_ITEM_CONVEYOR_BELT_SORTER_MENU, ItemConveyorBeltSorterScreen::new);
        HandledScreens.register(EPMenuTypes.AUTO_CRAFTER_MENU, AutoCrafterScreen::new);
        HandledScreens.register(EPMenuTypes.ADVANCED_AUTO_CRAFTER_MENU, AdvancedAutoCrafterScreen::new);
        HandledScreens.register(EPMenuTypes.CRUSHER_MENU, CrusherScreen::new);
        HandledScreens.register(EPMenuTypes.ADVANCED_CRUSHER_MENU, AdvancedCrusherScreen::new);
        HandledScreens.register(EPMenuTypes.PULVERIZER_MENU, PulverizerScreen::new);
        HandledScreens.register(EPMenuTypes.ADVANCED_PULVERIZER_MENU, AdvancedPulverizerScreen::new);
        HandledScreens.register(EPMenuTypes.SAWMILL_MENU, SawmillScreen::new);
        HandledScreens.register(EPMenuTypes.COMPRESSOR_MENU, CompressorScreen::new);
        HandledScreens.register(EPMenuTypes.PLANT_GROWTH_CHAMBER_MENU, PlantGrowthChamberScreen::new);
        HandledScreens.register(EPMenuTypes.STONE_LIQUEFIER_MENU, StoneLiquefierScreen::new);
        HandledScreens.register(EPMenuTypes.STONE_SOLIDIFIER_MENU, StoneSolidifierScreen::new);
        HandledScreens.register(EPMenuTypes.FILTRATION_PLANT_MENU, FiltrationPlantScreen::new);
        HandledScreens.register(EPMenuTypes.FLUID_TRANSPOSER_MENU, FluidTransposerScreen::new);
        HandledScreens.register(EPMenuTypes.BLOCK_PLACER_MENU, BlockPlacerScreen::new);
        HandledScreens.register(EPMenuTypes.FLUID_FILLER_MENU, FluidFillerScreen::new);
        HandledScreens.register(EPMenuTypes.FLUID_DRAINER_MENU, FluidDrainerScreen::new);
        HandledScreens.register(EPMenuTypes.FLUID_PUMP_MENU, FluidPumpScreen::new);
        HandledScreens.register(EPMenuTypes.ADVANCED_FLUID_PUMP_MENU, AdvancedFluidPumpScreen::new);
        HandledScreens.register(EPMenuTypes.DRAIN_MENU, DrainScreen::new);
        HandledScreens.register(EPMenuTypes.CHARGER_MENU, ChargerScreen::new);
        HandledScreens.register(EPMenuTypes.ADVANCED_CHARGER_MENU, AdvancedChargerScreen::new);
        HandledScreens.register(EPMenuTypes.UNCHARGER_MENU, UnchargerScreen::new);
        HandledScreens.register(EPMenuTypes.ADVANCED_UNCHARGER_MENU, AdvancedUnchargerScreen::new);
        HandledScreens.register(EPMenuTypes.ENERGIZER_MENU, EnergizerScreen::new);
        HandledScreens.register(EPMenuTypes.COAL_ENGINE_MENU, CoalEngineScreen::new);
        HandledScreens.register(EPMenuTypes.POWERED_FURNACE_MENU, PoweredFurnaceScreen::new);
        HandledScreens.register(EPMenuTypes.ADVANCED_POWERED_FURNACE_MENU, AdvancedPoweredFurnaceScreen::new);
        HandledScreens.register(EPMenuTypes.WEATHER_CONTROLLER_MENU, WeatherControllerScreen::new);
        HandledScreens.register(EPMenuTypes.TIME_CONTROLLER_MENU, TimeControllerScreen::new);
        HandledScreens.register(EPMenuTypes.TELEPORTER_MENU, TeleporterScreen::new);
        HandledScreens.register(EPMenuTypes.LIGHTNING_GENERATOR_MENU, LightningGeneratorScreen::new);
        HandledScreens.register(EPMenuTypes.CHARGING_STATION_MENU, ChargingStationScreen::new);
        HandledScreens.register(EPMenuTypes.CRYSTAL_GROWTH_CHAMBER_MENU, CrystalGrowthChamberScreen::new);
        HandledScreens.register(EPMenuTypes.HEAT_GENERATOR_MENU, HeatGeneratorScreen::new);
        HandledScreens.register(EPMenuTypes.THERMAL_GENERATOR_MENU, ThermalGeneratorScreen::new);
        HandledScreens.register(EPMenuTypes.BATTERY_BOX_MENU, BatteryBoxScreen::new);
        HandledScreens.register(EPMenuTypes.ADVANCED_BATTERY_BOX_MENU, AdvancedBatteryBoxScreen::new);
        HandledScreens.register(EPMenuTypes.CREATIVE_BATTERY_BOX_MENU, CreativeBatteryBoxScreen::new);
        HandledScreens.register(EPMenuTypes.MINECART_CHARGER_MENU, MinecartChargerScreen::new);
        HandledScreens.register(EPMenuTypes.ADVANCED_MINECART_CHARGER_MENU, AdvancedMinecartChargerScreen::new);
        HandledScreens.register(EPMenuTypes.MINECART_UNCHARGER_MENU, MinecartUnchargerScreen::new);
        HandledScreens.register(EPMenuTypes.ADVANCED_MINECART_UNCHARGER_MENU, AdvancedMinecartUnchargerScreen::new);
        HandledScreens.register(EPMenuTypes.SOLAR_PANEL_MENU_1, SolarPanelScreen::new);
        HandledScreens.register(EPMenuTypes.SOLAR_PANEL_MENU_2, SolarPanelScreen::new);
        HandledScreens.register(EPMenuTypes.SOLAR_PANEL_MENU_3, SolarPanelScreen::new);
        HandledScreens.register(EPMenuTypes.SOLAR_PANEL_MENU_4, SolarPanelScreen::new);
        HandledScreens.register(EPMenuTypes.SOLAR_PANEL_MENU_5, SolarPanelScreen::new);
        HandledScreens.register(EPMenuTypes.SOLAR_PANEL_MENU_6, SolarPanelScreen::new);
        HandledScreens.register(EPMenuTypes.LV_TRANSFORMER_1_TO_N_MENU, TransformerScreen::new);
        HandledScreens.register(EPMenuTypes.LV_TRANSFORMER_3_TO_3_MENU, TransformerScreen::new);
        HandledScreens.register(EPMenuTypes.LV_TRANSFORMER_N_TO_1_MENU, TransformerScreen::new);
        HandledScreens.register(EPMenuTypes.CONFIGURABLE_LV_TRANSFORMER_MENU, TransformerScreen::new);
        HandledScreens.register(EPMenuTypes.MV_TRANSFORMER_1_TO_N_MENU, TransformerScreen::new);
        HandledScreens.register(EPMenuTypes.MV_TRANSFORMER_3_TO_3_MENU, TransformerScreen::new);
        HandledScreens.register(EPMenuTypes.MV_TRANSFORMER_N_TO_1_MENU, TransformerScreen::new);
        HandledScreens.register(EPMenuTypes.CONFIGURABLE_MV_TRANSFORMER_MENU, TransformerScreen::new);
        HandledScreens.register(EPMenuTypes.HV_TRANSFORMER_1_TO_N_MENU, TransformerScreen::new);
        HandledScreens.register(EPMenuTypes.HV_TRANSFORMER_3_TO_3_MENU, TransformerScreen::new);
        HandledScreens.register(EPMenuTypes.HV_TRANSFORMER_N_TO_1_MENU, TransformerScreen::new);
        HandledScreens.register(EPMenuTypes.CONFIGURABLE_HV_TRANSFORMER_MENU, TransformerScreen::new);
        HandledScreens.register(EPMenuTypes.EHV_TRANSFORMER_1_TO_N_MENU, TransformerScreen::new);
        HandledScreens.register(EPMenuTypes.EHV_TRANSFORMER_3_TO_3_MENU, TransformerScreen::new);
        HandledScreens.register(EPMenuTypes.EHV_TRANSFORMER_N_TO_1_MENU, TransformerScreen::new);
        HandledScreens.register(EPMenuTypes.CONFIGURABLE_EHV_TRANSFORMER_MENU, TransformerScreen::new);
        HandledScreens.register(EPMenuTypes.PRESS_MOLD_MAKER_MENU, PressMoldMakerScreen::new);
        HandledScreens.register(EPMenuTypes.ALLOY_FURNACE_MENU, AlloyFurnaceScreen::new);
        HandledScreens.register(EPMenuTypes.METAL_PRESS_MENU, MetalPressScreen::new);
        HandledScreens.register(EPMenuTypes.AUTO_PRESS_MOLD_MAKER_MENU, AutoPressMoldMakerScreen::new);
        HandledScreens.register(EPMenuTypes.AUTO_STONECUTTER_MENU, AutoStonecutterScreen::new);
        HandledScreens.register(EPMenuTypes.ASSEMBLING_MACHINE_MENU, AssemblingMachineScreen::new);
        HandledScreens.register(EPMenuTypes.INDUCTION_SMELTER_MENU, InductionSmelterScreen::new);
        HandledScreens.register(EPMenuTypes.FLUID_TANK_SMALL, FluidTankScreen::new);
        HandledScreens.register(EPMenuTypes.FLUID_TANK_MEDIUM, FluidTankScreen::new);
        HandledScreens.register(EPMenuTypes.FLUID_TANK_LARGE, FluidTankScreen::new);
        HandledScreens.register(EPMenuTypes.CREATIVE_FLUID_TANK, CreativeFluidTankScreen::new);
        HandledScreens.register(EPMenuTypes.ITEM_SILO_TINY, ItemSiloScreen::new);
        HandledScreens.register(EPMenuTypes.ITEM_SILO_SMALL, ItemSiloScreen::new);
        HandledScreens.register(EPMenuTypes.ITEM_SILO_MEDIUM, ItemSiloScreen::new);
        HandledScreens.register(EPMenuTypes.ITEM_SILO_LARGE, ItemSiloScreen::new);
        HandledScreens.register(EPMenuTypes.ITEM_SILO_GIANT, ItemSiloScreen::new);
        HandledScreens.register(EPMenuTypes.CREATIVE_ITEM_SILO_MENU, CreativeItemSiloScreen::new);

        HandledScreens.register(EPMenuTypes.INVENTORY_CHARGER_MENU, InventoryChargerScreen::new);
        HandledScreens.register(EPMenuTypes.INVENTORY_TELEPORTER_MENU, InventoryTeleporterScreen::new);

        HandledScreens.register(EPMenuTypes.MINECART_BATTERY_BOX_MENU, MinecartBatteryBoxScreen::new);
        HandledScreens.register(EPMenuTypes.MINECART_ADVANCED_BATTERY_BOX_MENU, MinecartAdvancedBatteryBoxScreen::new);

        BooleanProperties.ID_MAPPER.put(EPAPI.id("active"), ActiveProperty.CODEC);
        BooleanProperties.ID_MAPPER.put(EPAPI.id("working"), WorkingProperty.CODEC);

        ResourceLoader.get(ResourceType.CLIENT_RESOURCES).registerReloader(EPAPI.id("energizedpowerbook"), new EnergizedPowerBookReloadListener());

        ModMessages.registerPacketsS2C();

        ModKeyBindings.register();

        EntityRendererFactories.register(EPEntityTypes.BATTERY_BOX_MINECART,
                entity -> new MinecartEntityRenderer(entity, new EntityModelLayer(
                        Identifier.of("minecraft", "chest_minecart"), "main")));
        EntityRendererFactories.register(EPEntityTypes.ADVANCED_BATTERY_BOX_MINECART,
                entity -> new MinecartEntityRenderer(entity, new EntityModelLayer(
                        Identifier.of("minecraft", "chest_minecart"), "main")));

        FluidRenderHandlerRegistry.INSTANCE.register(EPFluids.DIRTY_WATER, EPFluids.FLOWING_DIRTY_WATER,
                new SimpleFluidRenderHandler(
                        Identifier.of("block/water_still"),
                        Identifier.of("block/water_flow"),
                        0xC86F3900
                ));

        BlockRenderLayerMap.putFluids(BlockRenderLayer.TRANSLUCENT,
                EPFluids.DIRTY_WATER, EPFluids.FLOWING_DIRTY_WATER);

        BlockEntityRendererFactories.register(EPBlockEntities.BASIC_ITEM_CONVEYOR_BELT_ENTITY, ItemConveyorBeltBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(EPBlockEntities.FAST_ITEM_CONVEYOR_BELT_ENTITY, ItemConveyorBeltBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(EPBlockEntities.EXPRESS_ITEM_CONVEYOR_BELT_ENTITY, ItemConveyorBeltBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(EPBlockEntities.FLUID_TANK_SMALL_ENTITY, FluidTankBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(EPBlockEntities.FLUID_TANK_MEDIUM_ENTITY, FluidTankBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(EPBlockEntities.FLUID_TANK_LARGE_ENTITY, FluidTankBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(EPBlockEntities.CREATIVE_FLUID_TANK_ENTITY, FluidTankBlockEntityRenderer::new);

        ClientRecipeSynchronizedEvent.EVENT.register((minecraft, synchronizedRecipes) -> {
            if(EnergizedPowerJEIUtils.isJEIAvailable()) {
                EnergizedPowerJEIPlugin.recipeMap = synchronizedRecipes;
            }
        });
    }
}
