package me.jddev0.ep;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.entity.EPBlockEntities;
import me.jddev0.ep.block.entity.renderer.FluidTankBlockEntityRenderer;
import me.jddev0.ep.block.entity.renderer.ItemConveyorBeltBlockEntityRenderer;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.entity.EPEntityTypes;
import me.jddev0.ep.fluid.EPFluidTypes;
import me.jddev0.ep.fluid.EPFluids;
import me.jddev0.ep.input.ModKeyBindings;
import me.jddev0.ep.item.ActivatableItem;
import me.jddev0.ep.item.WorkingItem;
import me.jddev0.ep.loading.EnergizedPowerBookReloadListener;
import me.jddev0.ep.screen.*;
import net.minecraft.client.Camera;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

@EventBusSubscriber(modid = EPAPI.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class EnergizedPowerModClient {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        ModConfigs.registerConfigs(false);

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
                        ResourceLocation.fromNamespaceAndPath("minecraft", "chest_minecart"), "main")));
        EntityRenderers.register(EPEntityTypes.ADVANCED_BATTERY_BOX_MINECART.get(),
                entity -> new MinecartRenderer<>(entity, new ModelLayerLocation(
                        ResourceLocation.fromNamespaceAndPath("minecraft", "chest_minecart"), "main")));

        ItemBlockRenderTypes.setRenderLayer(EPFluids.DIRTY_WATER.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(EPFluids.FLOWING_DIRTY_WATER.get(), RenderType.translucent());

        BlockEntityRenderers.register(EPBlockEntities.BASIC_ITEM_CONVEYOR_BELT_ENTITY.get(), ItemConveyorBeltBlockEntityRenderer::new);
        BlockEntityRenderers.register(EPBlockEntities.FAST_ITEM_CONVEYOR_BELT_ENTITY.get(), ItemConveyorBeltBlockEntityRenderer::new);
        BlockEntityRenderers.register(EPBlockEntities.EXPRESS_ITEM_CONVEYOR_BELT_ENTITY.get(), ItemConveyorBeltBlockEntityRenderer::new);
        BlockEntityRenderers.register(EPBlockEntities.FLUID_TANK_SMALL_ENTITY.get(), FluidTankBlockEntityRenderer::new);
        BlockEntityRenderers.register(EPBlockEntities.FLUID_TANK_MEDIUM_ENTITY.get(), FluidTankBlockEntityRenderer::new);
        BlockEntityRenderers.register(EPBlockEntities.FLUID_TANK_LARGE_ENTITY.get(), FluidTankBlockEntityRenderer::new);
        BlockEntityRenderers.register(EPBlockEntities.CREATIVE_FLUID_TANK_ENTITY.get(), FluidTankBlockEntityRenderer::new);
    }

    @SubscribeEvent
    static void onRegisterClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerFluidType(new IClientFluidTypeExtensions() {
            @Override
            public int getTintColor() {
                return EPFluidTypes.DIRTY_WATER_FLUID_TYPE.get().getTintColor();
            }

            @Override
            public ResourceLocation getStillTexture() {
                return EPFluidTypes.DIRTY_WATER_FLUID_TYPE.get().getStillTexture();
            }

            @Override
            public ResourceLocation getFlowingTexture() {
                return EPFluidTypes.DIRTY_WATER_FLUID_TYPE.get().getFlowingTexture();
            }

            @Override
            public @Nullable ResourceLocation getOverlayTexture() {
                return EPFluidTypes.DIRTY_WATER_FLUID_TYPE.get().getOverlayTexture();
            }

            @Override
            public @NotNull Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor) {
                return EPFluidTypes.DIRTY_WATER_FLUID_TYPE.get().getFogColor();
            }

            @Override
            public void modifyFogRender(Camera camera, FogRenderer.FogMode mode, float renderDistance, float partialTick, float nearDistance, float farDistance, FogShape shape) {
                RenderSystem.setShaderFogStart(.25f);
                RenderSystem.setShaderFogEnd(3.f);
            }
        }, EPFluidTypes.DIRTY_WATER_FLUID_TYPE.get());

        event.registerFluidType(new IClientFluidTypeExtensions() {
            @Override
            public ResourceLocation getStillTexture() {
                return EPFluidTypes.LIQUID_XP_FLUID_TYPE.get().getStillTexture();
            }

            @Override
            public ResourceLocation getFlowingTexture() {
                return EPFluidTypes.LIQUID_XP_FLUID_TYPE.get().getFlowingTexture();
            }

            @Override
            public @Nullable ResourceLocation getOverlayTexture() {
                return EPFluidTypes.LIQUID_XP_FLUID_TYPE.get().getOverlayTexture();
            }

            @Override
            public @NotNull Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor) {
                return EPFluidTypes.LIQUID_XP_FLUID_TYPE.get().getFogColor();
            }

            @Override
            public void modifyFogRender(Camera camera, FogRenderer.FogMode mode, float renderDistance, float partialTick, float nearDistance, float farDistance, FogShape shape) {
                RenderSystem.setShaderFogStart(.125f);
                RenderSystem.setShaderFogEnd(1.f);
            }
        }, EPFluidTypes.LIQUID_XP_FLUID_TYPE.get());
    }

    @SubscribeEvent
    public static void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
        event.register(EPMenuTypes.BASIC_ITEM_CONVEYOR_BELT_LOADER_MENU.get(), ItemConveyorBeltLoaderScreen::new);
        event.register(EPMenuTypes.FAST_ITEM_CONVEYOR_BELT_LOADER_MENU.get(), ItemConveyorBeltLoaderScreen::new);
        event.register(EPMenuTypes.EXPRESS_ITEM_CONVEYOR_BELT_LOADER_MENU.get(), ItemConveyorBeltLoaderScreen::new);
        event.register(EPMenuTypes.BASIC_ITEM_CONVEYOR_BELT_SORTER_MENU.get(), ItemConveyorBeltSorterScreen::new);
        event.register(EPMenuTypes.FAST_ITEM_CONVEYOR_BELT_SORTER_MENU.get(), ItemConveyorBeltSorterScreen::new);
        event.register(EPMenuTypes.EXPRESS_ITEM_CONVEYOR_BELT_SORTER_MENU.get(), ItemConveyorBeltSorterScreen::new);
        event.register(EPMenuTypes.AUTO_CRAFTER_MENU.get(), AutoCrafterScreen::new);
        event.register(EPMenuTypes.ADVANCED_AUTO_CRAFTER_MENU.get(), AdvancedAutoCrafterScreen::new);
        event.register(EPMenuTypes.CRUSHER_MENU.get(), CrusherScreen::new);
        event.register(EPMenuTypes.ADVANCED_CRUSHER_MENU.get(), AdvancedCrusherScreen::new);
        event.register(EPMenuTypes.PULVERIZER_MENU.get(), PulverizerScreen::new);
        event.register(EPMenuTypes.ADVANCED_PULVERIZER_MENU.get(), AdvancedPulverizerScreen::new);
        event.register(EPMenuTypes.SAWMILL_MENU.get(), SawmillScreen::new);
        event.register(EPMenuTypes.COMPRESSOR_MENU.get(), CompressorScreen::new);
        event.register(EPMenuTypes.PLANT_GROWTH_CHAMBER_MENU.get(), PlantGrowthChamberScreen::new);
        event.register(EPMenuTypes.FLUID_FREEZER_MENU.get(), FluidFreezerScreen::new);
        event.register(EPMenuTypes.STONE_LIQUEFIER_MENU.get(), StoneLiquefierScreen::new);
        event.register(EPMenuTypes.STONE_SOLIDIFIER_MENU.get(), StoneSolidifierScreen::new);
        event.register(EPMenuTypes.FILTRATION_PLANT_MENU.get(), FiltrationPlantScreen::new);
        event.register(EPMenuTypes.FLUID_TRANSPOSER_MENU.get(), FluidTransposerScreen::new);
        event.register(EPMenuTypes.BLOCK_PLACER_MENU.get(), BlockPlacerScreen::new);
        event.register(EPMenuTypes.FLUID_FILLER_MENU.get(), FluidFillerScreen::new);
        event.register(EPMenuTypes.FLUID_DRAINER_MENU.get(), FluidDrainerScreen::new);
        event.register(EPMenuTypes.FLUID_PUMP_MENU.get(), FluidPumpScreen::new);
        event.register(EPMenuTypes.ADVANCED_FLUID_PUMP_MENU.get(), AdvancedFluidPumpScreen::new);
        event.register(EPMenuTypes.DRAIN_MENU.get(), DrainScreen::new);
        event.register(EPMenuTypes.CHARGER_MENU.get(), ChargerScreen::new);
        event.register(EPMenuTypes.ADVANCED_CHARGER_MENU.get(), AdvancedChargerScreen::new);
        event.register(EPMenuTypes.UNCHARGER_MENU.get(), UnchargerScreen::new);
        event.register(EPMenuTypes.ADVANCED_UNCHARGER_MENU.get(), AdvancedUnchargerScreen::new);
        event.register(EPMenuTypes.ENERGIZER_MENU.get(), EnergizerScreen::new);
        event.register(EPMenuTypes.COAL_ENGINE_MENU.get(), CoalEngineScreen::new);
        event.register(EPMenuTypes.POWERED_FURNACE_MENU.get(), PoweredFurnaceScreen::new);
        event.register(EPMenuTypes.ADVANCED_POWERED_FURNACE_MENU.get(), AdvancedPoweredFurnaceScreen::new);
        event.register(EPMenuTypes.WEATHER_CONTROLLER_MENU.get(), WeatherControllerScreen::new);
        event.register(EPMenuTypes.TIME_CONTROLLER_MENU.get(), TimeControllerScreen::new);
        event.register(EPMenuTypes.TELEPORTER_MENU.get(), TeleporterScreen::new);
        event.register(EPMenuTypes.LIGHTNING_GENERATOR_MENU.get(), LightningGeneratorScreen::new);
        event.register(EPMenuTypes.CHARGING_STATION_MENU.get(), ChargingStationScreen::new);
        event.register(EPMenuTypes.CRYSTAL_GROWTH_CHAMBER_MENU.get(), CrystalGrowthChamberScreen::new);
        event.register(EPMenuTypes.HEAT_GENERATOR_MENU.get(), HeatGeneratorScreen::new);
        event.register(EPMenuTypes.THERMAL_GENERATOR_MENU.get(), ThermalGeneratorScreen::new);
        event.register(EPMenuTypes.BATTERY_BOX_MENU.get(), BatteryBoxScreen::new);
        event.register(EPMenuTypes.ADVANCED_BATTERY_BOX_MENU.get(), AdvancedBatteryBoxScreen::new);
        event.register(EPMenuTypes.CREATIVE_BATTERY_BOX_MENU.get(), CreativeBatteryBoxScreen::new);
        event.register(EPMenuTypes.MINECART_CHARGER_MENU.get(), MinecartChargerScreen::new);
        event.register(EPMenuTypes.ADVANCED_MINECART_CHARGER_MENU.get(), AdvancedMinecartChargerScreen::new);
        event.register(EPMenuTypes.MINECART_UNCHARGER_MENU.get(), MinecartUnchargerScreen::new);
        event.register(EPMenuTypes.ADVANCED_MINECART_UNCHARGER_MENU.get(), AdvancedMinecartUnchargerScreen::new);
        event.register(EPMenuTypes.SOLAR_PANEL_MENU_1.get(), SolarPanelScreen::new);
        event.register(EPMenuTypes.SOLAR_PANEL_MENU_2.get(), SolarPanelScreen::new);
        event.register(EPMenuTypes.SOLAR_PANEL_MENU_3.get(), SolarPanelScreen::new);
        event.register(EPMenuTypes.SOLAR_PANEL_MENU_4.get(), SolarPanelScreen::new);
        event.register(EPMenuTypes.SOLAR_PANEL_MENU_5.get(), SolarPanelScreen::new);
        event.register(EPMenuTypes.SOLAR_PANEL_MENU_6.get(), SolarPanelScreen::new);
        event.register(EPMenuTypes.SOLAR_PANEL_MENU_7.get(), SolarPanelScreen::new);
        event.register(EPMenuTypes.LV_TRANSFORMER_1_TO_N_MENU.get(), TransformerScreen::new);
        event.register(EPMenuTypes.LV_TRANSFORMER_3_TO_3_MENU.get(), TransformerScreen::new);
        event.register(EPMenuTypes.LV_TRANSFORMER_N_TO_1_MENU.get(), TransformerScreen::new);
        event.register(EPMenuTypes.CONFIGURABLE_LV_TRANSFORMER_MENU.get(), TransformerScreen::new);
        event.register(EPMenuTypes.MV_TRANSFORMER_1_TO_N_MENU.get(), TransformerScreen::new);
        event.register(EPMenuTypes.MV_TRANSFORMER_3_TO_3_MENU.get(), TransformerScreen::new);
        event.register(EPMenuTypes.MV_TRANSFORMER_N_TO_1_MENU.get(), TransformerScreen::new);
        event.register(EPMenuTypes.CONFIGURABLE_MV_TRANSFORMER_MENU.get(), TransformerScreen::new);
        event.register(EPMenuTypes.HV_TRANSFORMER_1_TO_N_MENU.get(), TransformerScreen::new);
        event.register(EPMenuTypes.HV_TRANSFORMER_3_TO_3_MENU.get(), TransformerScreen::new);
        event.register(EPMenuTypes.HV_TRANSFORMER_N_TO_1_MENU.get(), TransformerScreen::new);
        event.register(EPMenuTypes.CONFIGURABLE_HV_TRANSFORMER_MENU.get(), TransformerScreen::new);
        event.register(EPMenuTypes.EHV_TRANSFORMER_1_TO_N_MENU.get(), TransformerScreen::new);
        event.register(EPMenuTypes.EHV_TRANSFORMER_3_TO_3_MENU.get(), TransformerScreen::new);
        event.register(EPMenuTypes.EHV_TRANSFORMER_N_TO_1_MENU.get(), TransformerScreen::new);
        event.register(EPMenuTypes.CONFIGURABLE_EHV_TRANSFORMER_MENU.get(), TransformerScreen::new);
        event.register(EPMenuTypes.PRESS_MOLD_MAKER_MENU.get(), PressMoldMakerScreen::new);
        event.register(EPMenuTypes.ALLOY_FURNACE_MENU.get(), AlloyFurnaceScreen::new);
        event.register(EPMenuTypes.METAL_PRESS_MENU.get(), MetalPressScreen::new);
        event.register(EPMenuTypes.AUTO_PRESS_MOLD_MAKER_MENU.get(), AutoPressMoldMakerScreen::new);
        event.register(EPMenuTypes.AUTO_STONECUTTER_MENU.get(), AutoStonecutterScreen::new);
        event.register(EPMenuTypes.ASSEMBLING_MACHINE_MENU.get(), AssemblingMachineScreen::new);
        event.register(EPMenuTypes.INDUCTION_SMELTER_MENU.get(), InductionSmelterScreen::new);
        event.register(EPMenuTypes.FLUID_TANK_SMALL.get(), FluidTankScreen::new);
        event.register(EPMenuTypes.FLUID_TANK_MEDIUM.get(), FluidTankScreen::new);
        event.register(EPMenuTypes.FLUID_TANK_LARGE.get(), FluidTankScreen::new);
        event.register(EPMenuTypes.CREATIVE_FLUID_TANK.get(), CreativeFluidTankScreen::new);
        event.register(EPMenuTypes.XP_STORAGE_TINY.get(), XPStorageScreen::new);
        event.register(EPMenuTypes.XP_STORAGE_SMALL.get(), XPStorageScreen::new);
        event.register(EPMenuTypes.XP_STORAGE_MEDIUM.get(), XPStorageScreen::new);
        event.register(EPMenuTypes.XP_STORAGE_LARGE.get(), XPStorageScreen::new);
        event.register(EPMenuTypes.XP_STORAGE_GIANT.get(), XPStorageScreen::new);
        event.register(EPMenuTypes.ITEM_SILO_TINY.get(), ItemSiloScreen::new);
        event.register(EPMenuTypes.ITEM_SILO_SMALL.get(), ItemSiloScreen::new);
        event.register(EPMenuTypes.ITEM_SILO_MEDIUM.get(), ItemSiloScreen::new);
        event.register(EPMenuTypes.ITEM_SILO_LARGE.get(), ItemSiloScreen::new);
        event.register(EPMenuTypes.ITEM_SILO_GIANT.get(), ItemSiloScreen::new);
        event.register(EPMenuTypes.CREATIVE_ITEM_SILO_MENU.get(), CreativeItemSiloScreen::new);

        event.register(EPMenuTypes.INVENTORY_CHARGER_MENU.get(), InventoryChargerScreen::new);
        event.register(EPMenuTypes.INVENTORY_TELEPORTER_MENU.get(), InventoryTeleporterScreen::new);

        event.register(EPMenuTypes.MINECART_BATTERY_BOX_MENU.get(), MinecartBatteryBoxScreen::new);
        event.register(EPMenuTypes.MINECART_ADVANCED_BATTERY_BOX_MENU.get(), MinecartAdvancedBatteryBoxScreen::new);
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
