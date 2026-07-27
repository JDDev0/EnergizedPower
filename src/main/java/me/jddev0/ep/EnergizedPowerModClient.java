package me.jddev0.ep;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.EPBlockEntities;
import me.jddev0.ep.block.entity.renderer.FluidTankBlockEntityRenderer;
import me.jddev0.ep.block.entity.renderer.ItemConveyorBeltBlockEntityRenderer;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.entity.EPEntityTypes;
import me.jddev0.ep.fluid.EPFluids;
import me.jddev0.ep.input.ModKeyBindings;
import me.jddev0.ep.item.ActivatableItem;
import me.jddev0.ep.item.WorkingItem;
import me.jddev0.ep.loading.EnergizedPowerBookReloadListener;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.screen.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;

public class EnergizedPowerModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModConfigs.registerConfigs(false);

        registerEntityRenderers();

        registerBlockRenderers();

        registerBlockEntityRenderers();

        onRegisterFluidModels();

        onRegisterMenuScreens();

        onRegisterConditionalItemModelProperties();

        loadBookPages();

        onRegisterClientPayloadHandlers();

        onKeyRegister();
    }

    public void registerEntityRenderers() {
        EntityRendererRegistry.register(EPEntityTypes.BATTERY_BOX_MINECART,
                entity -> new MinecartRenderer<>(entity, new ModelLayerLocation(
                        ResourceLocation.fromNamespaceAndPath("minecraft", "chest_minecart"), "main")));
        EntityRendererRegistry.register(EPEntityTypes.ADVANCED_BATTERY_BOX_MINECART,
                entity -> new MinecartRenderer<>(entity, new ModelLayerLocation(
                        ResourceLocation.fromNamespaceAndPath("minecraft", "chest_minecart"), "main")));
        EntityRendererRegistry.register(EPEntityTypes.ELITE_BATTERY_BOX_MINECART,
                entity -> new MinecartRenderer(entity, new ModelLayerLocation(
                        ResourceLocation.fromNamespaceAndPath("minecraft", "chest_minecart"), "main")));
    }

    public void registerBlockRenderers() {
        //XP Drain must be of render type cutout
        BlockRenderLayerMap.INSTANCE.putBlock(EPBlocks.XP_DRAIN, RenderType.cutout());
    }

    public void registerBlockEntityRenderers() {
        BlockEntityRenderers.register(EPBlockEntities.BASIC_ITEM_CONVEYOR_BELT_ENTITY, ItemConveyorBeltBlockEntityRenderer::new);
        BlockEntityRenderers.register(EPBlockEntities.FAST_ITEM_CONVEYOR_BELT_ENTITY, ItemConveyorBeltBlockEntityRenderer::new);
        BlockEntityRenderers.register(EPBlockEntities.EXPRESS_ITEM_CONVEYOR_BELT_ENTITY, ItemConveyorBeltBlockEntityRenderer::new);
        BlockEntityRenderers.register(EPBlockEntities.FLUID_TANK_SMALL_ENTITY, FluidTankBlockEntityRenderer::new);
        BlockEntityRenderers.register(EPBlockEntities.FLUID_TANK_MEDIUM_ENTITY, FluidTankBlockEntityRenderer::new);
        BlockEntityRenderers.register(EPBlockEntities.FLUID_TANK_LARGE_ENTITY, FluidTankBlockEntityRenderer::new);
        BlockEntityRenderers.register(EPBlockEntities.CREATIVE_FLUID_TANK_ENTITY, FluidTankBlockEntityRenderer::new);
    }

    public void onRegisterFluidModels() {
        FluidRenderHandlerRegistry.INSTANCE.register(EPFluids.DIRTY_WATER, EPFluids.FLOWING_DIRTY_WATER,
                new SimpleFluidRenderHandler(
                        ResourceLocation.parse("block/water_still"),
                        ResourceLocation.parse("block/water_flow"),
                        0xC86F3900
                ));
        BlockRenderLayerMap.INSTANCE.putFluids(RenderType.translucent(),
                EPFluids.DIRTY_WATER, EPFluids.FLOWING_DIRTY_WATER);

        FluidRenderHandlerRegistry.INSTANCE.register(EPFluids.LIQUID_XP, EPFluids.FLOWING_LIQUID_XP,
                new SimpleFluidRenderHandler(
                        EPAPI.id("block/liquid_xp_still"),
                        EPAPI.id("block/liquid_xp_flow"),
                        0xFFFFFFFF
                ));
        BlockRenderLayerMap.INSTANCE.putFluids(RenderType.translucent(),
                EPFluids.LIQUID_XP, EPFluids.FLOWING_LIQUID_XP);
    }

    public void onRegisterMenuScreens() {
        RegisterMenuScreensEvent event = new RegisterMenuScreensEvent();

        event.register(EPMenuTypes.BASIC_ITEM_CONVEYOR_BELT_LOADER_MENU, ItemConveyorBeltLoaderScreen::new);
        event.register(EPMenuTypes.FAST_ITEM_CONVEYOR_BELT_LOADER_MENU, ItemConveyorBeltLoaderScreen::new);
        event.register(EPMenuTypes.EXPRESS_ITEM_CONVEYOR_BELT_LOADER_MENU, ItemConveyorBeltLoaderScreen::new);
        event.register(EPMenuTypes.BASIC_ITEM_CONVEYOR_BELT_SORTER_MENU, ItemConveyorBeltSorterScreen::new);
        event.register(EPMenuTypes.FAST_ITEM_CONVEYOR_BELT_SORTER_MENU, ItemConveyorBeltSorterScreen::new);
        event.register(EPMenuTypes.EXPRESS_ITEM_CONVEYOR_BELT_SORTER_MENU, ItemConveyorBeltSorterScreen::new);
        event.register(EPMenuTypes.AUTO_CRAFTER_MENU, AutoCrafterScreen::new);
        event.register(EPMenuTypes.ADVANCED_AUTO_CRAFTER_MENU, AdvancedAutoCrafterScreen::new);
        event.register(EPMenuTypes.CRUSHER_MENU, CrusherScreen::new);
        event.register(EPMenuTypes.ADVANCED_CRUSHER_MENU, AdvancedCrusherScreen::new);
        event.register(EPMenuTypes.PULVERIZER_MENU, PulverizerScreen::new);
        event.register(EPMenuTypes.ADVANCED_PULVERIZER_MENU, AdvancedPulverizerScreen::new);
        event.register(EPMenuTypes.SAWMILL_MENU, SawmillScreen::new);
        event.register(EPMenuTypes.COMPRESSOR_MENU, CompressorScreen::new);
        event.register(EPMenuTypes.PLANT_GROWTH_CHAMBER_MENU, PlantGrowthChamberScreen::new);
        event.register(EPMenuTypes.FLUID_FREEZER_MENU, FluidFreezerScreen::new);
        event.register(EPMenuTypes.STONE_LIQUEFIER_MENU, StoneLiquefierScreen::new);
        event.register(EPMenuTypes.STONE_SOLIDIFIER_MENU, StoneSolidifierScreen::new);
        event.register(EPMenuTypes.FILTRATION_PLANT_MENU, FiltrationPlantScreen::new);
        event.register(EPMenuTypes.FLUID_TRANSPOSER_MENU, FluidTransposerScreen::new);
        event.register(EPMenuTypes.BLOCK_PLACER_MENU, BlockPlacerScreen::new);
        event.register(EPMenuTypes.FLUID_FILLER_MENU, FluidFillerScreen::new);
        event.register(EPMenuTypes.FLUID_DRAINER_MENU, FluidDrainerScreen::new);
        event.register(EPMenuTypes.FLUID_PUMP_MENU, FluidPumpScreen::new);
        event.register(EPMenuTypes.ADVANCED_FLUID_PUMP_MENU, AdvancedFluidPumpScreen::new);
        event.register(EPMenuTypes.DRAIN_MENU, DrainScreen::new);
        event.register(EPMenuTypes.CHARGER_MENU, ChargerScreen::new);
        event.register(EPMenuTypes.ADVANCED_CHARGER_MENU, AdvancedChargerScreen::new);
        event.register(EPMenuTypes.ELITE_CHARGER_MENU, EliteChargerScreen::new);
        event.register(EPMenuTypes.UNCHARGER_MENU, UnchargerScreen::new);
        event.register(EPMenuTypes.ADVANCED_UNCHARGER_MENU, AdvancedUnchargerScreen::new);
        event.register(EPMenuTypes.ELITE_UNCHARGER_MENU, EliteUnchargerScreen::new);
        event.register(EPMenuTypes.ENERGIZER_MENU, EnergizerScreen::new);
        event.register(EPMenuTypes.COAL_ENGINE_MENU, CoalEngineScreen::new);
        event.register(EPMenuTypes.POWERED_FURNACE_MENU, PoweredFurnaceScreen::new);
        event.register(EPMenuTypes.ADVANCED_POWERED_FURNACE_MENU, AdvancedPoweredFurnaceScreen::new);
        event.register(EPMenuTypes.WEATHER_CONTROLLER_MENU, WeatherControllerScreen::new);
        event.register(EPMenuTypes.TIME_CONTROLLER_MENU, TimeControllerScreen::new);
        event.register(EPMenuTypes.TELEPORTER_MENU, TeleporterScreen::new);
        event.register(EPMenuTypes.LIGHTNING_GENERATOR_MENU, LightningGeneratorScreen::new);
        event.register(EPMenuTypes.CHARGING_STATION_MENU, ChargingStationScreen::new);
        event.register(EPMenuTypes.CRYSTAL_GROWTH_CHAMBER_MENU, CrystalGrowthChamberScreen::new);
        event.register(EPMenuTypes.HEAT_GENERATOR_MENU, HeatGeneratorScreen::new);
        event.register(EPMenuTypes.THERMAL_GENERATOR_MENU, ThermalGeneratorScreen::new);
        event.register(EPMenuTypes.BATTERY_BOX_MENU, BatteryBoxScreen::new);
        event.register(EPMenuTypes.ADVANCED_BATTERY_BOX_MENU, AdvancedBatteryBoxScreen::new);
        event.register(EPMenuTypes.ELITE_BATTERY_BOX_MENU, EliteBatteryBoxScreen::new);
        event.register(EPMenuTypes.CREATIVE_BATTERY_BOX_MENU, CreativeBatteryBoxScreen::new);
        event.register(EPMenuTypes.MINECART_CHARGER_MENU, MinecartChargerScreen::new);
        event.register(EPMenuTypes.ADVANCED_MINECART_CHARGER_MENU, AdvancedMinecartChargerScreen::new);
        event.register(EPMenuTypes.ELITE_MINECART_CHARGER_MENU, EliteMinecartChargerScreen::new);
        event.register(EPMenuTypes.MINECART_UNCHARGER_MENU, MinecartUnchargerScreen::new);
        event.register(EPMenuTypes.ADVANCED_MINECART_UNCHARGER_MENU, AdvancedMinecartUnchargerScreen::new);
        event.register(EPMenuTypes.SOLAR_PANEL_MENU_1, SolarPanelScreen::new);
        event.register(EPMenuTypes.SOLAR_PANEL_MENU_2, SolarPanelScreen::new);
        event.register(EPMenuTypes.SOLAR_PANEL_MENU_3, SolarPanelScreen::new);
        event.register(EPMenuTypes.SOLAR_PANEL_MENU_4, SolarPanelScreen::new);
        event.register(EPMenuTypes.SOLAR_PANEL_MENU_5, SolarPanelScreen::new);
        event.register(EPMenuTypes.SOLAR_PANEL_MENU_6, SolarPanelScreen::new);
        event.register(EPMenuTypes.SOLAR_PANEL_MENU_7, SolarPanelScreen::new);
        event.register(EPMenuTypes.LV_TRANSFORMER_1_TO_N_MENU, TransformerScreen::new);
        event.register(EPMenuTypes.LV_TRANSFORMER_3_TO_3_MENU, TransformerScreen::new);
        event.register(EPMenuTypes.LV_TRANSFORMER_N_TO_1_MENU, TransformerScreen::new);
        event.register(EPMenuTypes.CONFIGURABLE_LV_TRANSFORMER_MENU, TransformerScreen::new);
        event.register(EPMenuTypes.MV_TRANSFORMER_1_TO_N_MENU, TransformerScreen::new);
        event.register(EPMenuTypes.MV_TRANSFORMER_3_TO_3_MENU, TransformerScreen::new);
        event.register(EPMenuTypes.MV_TRANSFORMER_N_TO_1_MENU, TransformerScreen::new);
        event.register(EPMenuTypes.CONFIGURABLE_MV_TRANSFORMER_MENU, TransformerScreen::new);
        event.register(EPMenuTypes.HV_TRANSFORMER_1_TO_N_MENU, TransformerScreen::new);
        event.register(EPMenuTypes.HV_TRANSFORMER_3_TO_3_MENU, TransformerScreen::new);
        event.register(EPMenuTypes.HV_TRANSFORMER_N_TO_1_MENU, TransformerScreen::new);
        event.register(EPMenuTypes.CONFIGURABLE_HV_TRANSFORMER_MENU, TransformerScreen::new);
        event.register(EPMenuTypes.EHV_TRANSFORMER_1_TO_N_MENU, TransformerScreen::new);
        event.register(EPMenuTypes.EHV_TRANSFORMER_3_TO_3_MENU, TransformerScreen::new);
        event.register(EPMenuTypes.EHV_TRANSFORMER_N_TO_1_MENU, TransformerScreen::new);
        event.register(EPMenuTypes.CONFIGURABLE_EHV_TRANSFORMER_MENU, TransformerScreen::new);
        event.register(EPMenuTypes.UHV_TRANSFORMER_1_TO_N_MENU, TransformerScreen::new);
        event.register(EPMenuTypes.UHV_TRANSFORMER_3_TO_3_MENU, TransformerScreen::new);
        event.register(EPMenuTypes.UHV_TRANSFORMER_N_TO_1_MENU, TransformerScreen::new);
        event.register(EPMenuTypes.CONFIGURABLE_UHV_TRANSFORMER_MENU, TransformerScreen::new);
        event.register(EPMenuTypes.PRESS_MOLD_MAKER_MENU, PressMoldMakerScreen::new);
        event.register(EPMenuTypes.ALLOY_FURNACE_MENU, AlloyFurnaceScreen::new);
        event.register(EPMenuTypes.METAL_PRESS_MENU, MetalPressScreen::new);
        event.register(EPMenuTypes.AUTO_PRESS_MOLD_MAKER_MENU, AutoPressMoldMakerScreen::new);
        event.register(EPMenuTypes.AUTO_STONECUTTER_MENU, AutoStonecutterScreen::new);
        event.register(EPMenuTypes.ASSEMBLING_MACHINE_MENU, AssemblingMachineScreen::new);
        event.register(EPMenuTypes.INDUCTION_SMELTER_MENU, InductionSmelterScreen::new);
        event.register(EPMenuTypes.FLUID_TANK_SMALL, FluidTankScreen::new);
        event.register(EPMenuTypes.FLUID_TANK_MEDIUM, FluidTankScreen::new);
        event.register(EPMenuTypes.FLUID_TANK_LARGE, FluidTankScreen::new);
        event.register(EPMenuTypes.CREATIVE_FLUID_TANK, CreativeFluidTankScreen::new);
        event.register(EPMenuTypes.XP_STORAGE_TINY, XPStorageScreen::new);
        event.register(EPMenuTypes.XP_STORAGE_SMALL, XPStorageScreen::new);
        event.register(EPMenuTypes.XP_STORAGE_MEDIUM, XPStorageScreen::new);
        event.register(EPMenuTypes.XP_STORAGE_LARGE, XPStorageScreen::new);
        event.register(EPMenuTypes.XP_STORAGE_GIANT, XPStorageScreen::new);
        event.register(EPMenuTypes.ITEM_SILO_TINY, ItemSiloScreen::new);
        event.register(EPMenuTypes.ITEM_SILO_SMALL, ItemSiloScreen::new);
        event.register(EPMenuTypes.ITEM_SILO_MEDIUM, ItemSiloScreen::new);
        event.register(EPMenuTypes.ITEM_SILO_LARGE, ItemSiloScreen::new);
        event.register(EPMenuTypes.ITEM_SILO_GIANT, ItemSiloScreen::new);
        event.register(EPMenuTypes.CREATIVE_ITEM_SILO_MENU, CreativeItemSiloScreen::new);

        event.register(EPMenuTypes.INVENTORY_CHARGER_MENU, InventoryChargerScreen::new);
        event.register(EPMenuTypes.INVENTORY_TELEPORTER_MENU, InventoryTeleporterScreen::new);

        event.register(EPMenuTypes.MINECART_BATTERY_BOX_MENU, MinecartBatteryBoxScreen::new);
        event.register(EPMenuTypes.MINECART_ADVANCED_BATTERY_BOX_MENU, MinecartAdvancedBatteryBoxScreen::new);
        event.register(EPMenuTypes.MINECART_ELITE_BATTERY_BOX_MENU, MinecartEliteBatteryBoxScreen::new);
    }

    public void onRegisterConditionalItemModelProperties() {
        ItemProperties.registerGeneric(EPAPI.id("active"), (itemStack, level, entity, seed) -> {
            Item item = itemStack.getItem();
            return (item instanceof ActivatableItem && ((ActivatableItem)item).isActive(itemStack))?1.f:0.f;
        });
        ItemProperties.registerGeneric(EPAPI.id("working"), (itemStack, level, entity, seed) -> {
            Item item = itemStack.getItem();
            return (item instanceof WorkingItem && ((WorkingItem)item).isWorking(itemStack))?1.f:0.f;
        });
    }

    public void loadBookPages() {
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new EnergizedPowerBookReloadListener());
    }

    public void onRegisterClientPayloadHandlers() {
        ModMessages.registerPacketsS2C();
    }

    public void onKeyRegister() {
        ModKeyBindings.register();
    }

    /**
     * Registration adapter to match NeoForge
     */
    private static final class RegisterMenuScreensEvent {
        private RegisterMenuScreensEvent() {}

        public <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void register(
                final MenuType<? extends M> menuType, final MenuScreens.ScreenConstructor<M, U> screenConstructor
        ) {
            MenuScreens.register(menuType, screenConstructor);
        }
    }
}
