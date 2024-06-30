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
import me.jddev0.ep.integration.cctweaked.EnergizedPowerCCTweakedIntegration;
import me.jddev0.ep.integration.cctweaked.EnergizedPowerCCTweakedUtils;
import me.jddev0.ep.item.*;
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
import org.slf4j.Logger;

@Mod(EnergizedPowerMod.MODID)
public class EnergizedPowerMod {
    public static final String MODID = "energizedpower";
    private static final Logger LOGGER = LogUtils.getLogger();

    public EnergizedPowerMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

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
            MenuScreens.register(ModMenuTypes.ADVANCED_PULVERIZER_MENU.get(), AdvancedPulverizerScreen::new);
            MenuScreens.register(ModMenuTypes.SAWMILL_MENU.get(), SawmillScreen::new);
            MenuScreens.register(ModMenuTypes.COMPRESSOR_MENU.get(), CompressorScreen::new);
            MenuScreens.register(ModMenuTypes.PLANT_GROWTH_CHAMBER_MENU.get(), PlantGrowthChamberScreen::new);
            MenuScreens.register(ModMenuTypes.STONE_SOLIDIFIER_MENU.get(), StoneSolidifierScreen::new);
            MenuScreens.register(ModMenuTypes.FILTRATION_PLANT_MENU.get(), FiltrationPlantScreen::new);
            MenuScreens.register(ModMenuTypes.FLUID_TRANSPOSER_MENU.get(), FluidTransposerScreen::new);
            MenuScreens.register(ModMenuTypes.BLOCK_PLACER_MENU.get(), BlockPlacerScreen::new);
            MenuScreens.register(ModMenuTypes.FLUID_FILLER_MENU.get(), FluidFillerScreen::new);
            MenuScreens.register(ModMenuTypes.FLUID_DRAINER_MENU.get(), FluidDrainerScreen::new);
            MenuScreens.register(ModMenuTypes.FLUID_PUMP_MENU.get(), FluidPumpScreen::new);
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
            MenuScreens.register(ModMenuTypes.CREATIVE_BATTERY_BOX_MENU.get(), CreativeBatteryBoxScreen::new);
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
            MenuScreens.register(ModMenuTypes.ALLOY_FURNACE_MENU.get(), AlloyFurnaceScreen::new);
            MenuScreens.register(ModMenuTypes.METAL_PRESS_MENU.get(), MetalPressScreen::new);
            MenuScreens.register(ModMenuTypes.AUTO_PRESS_MOLD_MAKER_MENU.get(), AutoPressMoldMakerScreen::new);
            MenuScreens.register(ModMenuTypes.AUTO_STONECUTTER_MENU.get(), AutoStonecutterScreen::new);
            MenuScreens.register(ModMenuTypes.ASSEMBLING_MACHINE_MENU.get(), AssemblingMachineScreen::new);
            MenuScreens.register(ModMenuTypes.INDUCTION_SMELTER_MENU.get(), InductionSmelterScreen::new);
            MenuScreens.register(ModMenuTypes.FLUID_TANK_SMALL.get(), FluidTankScreen::new);
            MenuScreens.register(ModMenuTypes.FLUID_TANK_MEDIUM.get(), FluidTankScreen::new);
            MenuScreens.register(ModMenuTypes.FLUID_TANK_LARGE.get(), FluidTankScreen::new);
            MenuScreens.register(ModMenuTypes.CREATIVE_FLUID_TANK.get(), CreativeFluidTankScreen::new);

            MenuScreens.register(ModMenuTypes.INVENTORY_CHARGER_MENU.get(), InventoryChargerScreen::new);
            MenuScreens.register(ModMenuTypes.INVENTORY_TELEPORTER_MENU.get(), InventoryTeleporterScreen::new);

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
            BlockEntityRenderers.register(ModBlockEntities.CREATIVE_FLUID_TANK_ENTITY.get(), FluidTankBlockEntityRenderer::new);
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
