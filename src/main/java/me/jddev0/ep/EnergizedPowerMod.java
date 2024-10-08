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
import me.jddev0.ep.integration.cctweaked.EnergizedPowerCCTweakedIntegration;
import me.jddev0.ep.integration.cctweaked.EnergizedPowerCCTweakedUtils;
import me.jddev0.ep.item.*;
import me.jddev0.ep.loading.EnergizedPowerBookReloadListener;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.painting.EPPaintings;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

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

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::onLoadComplete);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        ModMessages.register();
    }

    private void onLoadComplete(final FMLLoadCompleteEvent event) {
        if(EnergizedPowerCCTweakedUtils.isCCTweakedAvailable())
            EnergizedPowerCCTweakedIntegration.register();
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
