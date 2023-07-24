package me.jddev0.ep;

import me.jddev0.ep.entity.ModEntityTypes;
import me.jddev0.ep.item.ActivatableItem;
import me.jddev0.ep.item.WorkingItem;
import me.jddev0.ep.loading.EnergizedPowerBookReloadListener;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.screen.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.entity.MinecartEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.item.Item;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

public class EnergizedPowerModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HandledScreens.register(ModMenuTypes.AUTO_CRAFTER_MENU, AutoCrafterScreen::new);
        HandledScreens.register(ModMenuTypes.CRUSHER_MENU, CrusherScreen::new);
        HandledScreens.register(ModMenuTypes.SAWMILL_MENU, SawmillScreen::new);
        HandledScreens.register(ModMenuTypes.COMPRESSOR_MENU, CompressorScreen::new);
        HandledScreens.register(ModMenuTypes.PLANT_GROWTH_CHAMBER_MENU, PlantGrowthChamberScreen::new);
        HandledScreens.register(ModMenuTypes.BLOCK_PLACER_MENU, BlockPlacerScreen::new);
        HandledScreens.register(ModMenuTypes.CHARGER_MENU, ChargerScreen::new);
        HandledScreens.register(ModMenuTypes.UNCHARGER_MENU, UnchargerScreen::new);
        HandledScreens.register(ModMenuTypes.ENERGIZER_MENU, EnergizerScreen::new);
        HandledScreens.register(ModMenuTypes.COAL_ENGINE_MENU, CoalEngineScreen::new);
        HandledScreens.register(ModMenuTypes.POWERED_FURNACE_MENU, PoweredFurnaceScreen::new);
        HandledScreens.register(ModMenuTypes.WEATHER_CONTROLLER_MENU, WeatherControllerScreen::new);
        HandledScreens.register(ModMenuTypes.TIME_CONTROLLER_MENU, TimeControllerScreen::new);
        HandledScreens.register(ModMenuTypes.LIGHTNING_GENERATOR_MENU, LightningGeneratorScreen::new);
        HandledScreens.register(ModMenuTypes.CHARGING_STATION_MENU, ChargingStationScreen::new);
        HandledScreens.register(ModMenuTypes.HEAT_GENERATOR_MENU, HeatGeneratorScreen::new);
        HandledScreens.register(ModMenuTypes.THERMAL_GENERATOR_MENU, ThermalGeneratorScreen::new);
        HandledScreens.register(ModMenuTypes.BATTERY_BOX_MENU, BatteryBoxScreen::new);
        HandledScreens.register(ModMenuTypes.ADVANCED_BATTERY_BOX_MENU, AdvancedBatteryBoxScreen::new);
        HandledScreens.register(ModMenuTypes.MINECART_CHARGER_MENU, MinecartChargerScreen::new);
        HandledScreens.register(ModMenuTypes.MINECART_UNCHARGER_MENU, MinecartUnchargerScreen::new);
        HandledScreens.register(ModMenuTypes.SOLAR_PANEL_MENU_1, SolarPanelScreen::new);
        HandledScreens.register(ModMenuTypes.SOLAR_PANEL_MENU_2, SolarPanelScreen::new);
        HandledScreens.register(ModMenuTypes.SOLAR_PANEL_MENU_3, SolarPanelScreen::new);
        HandledScreens.register(ModMenuTypes.SOLAR_PANEL_MENU_4, SolarPanelScreen::new);
        HandledScreens.register(ModMenuTypes.SOLAR_PANEL_MENU_5, SolarPanelScreen::new);

        HandledScreens.register(ModMenuTypes.MINECART_BATTERY_BOX_MENU, MinecartBatteryBoxScreen::new);
        HandledScreens.register(ModMenuTypes.MINECART_ADVANCED_BATTERY_BOX_MENU, MinecartAdvancedBatteryBoxScreen::new);

        ModelPredicateProviderRegistry.register(new Identifier(EnergizedPowerMod.MODID, "active"), (itemStack, level, entity, seed) -> {
            Item item = itemStack.getItem();
            return (item instanceof ActivatableItem && ((ActivatableItem)item).isActive(itemStack))?1.f:0.f;
        });
        ModelPredicateProviderRegistry.register(new Identifier(EnergizedPowerMod.MODID, "working"), (itemStack, level, entity, seed) -> {
            Item item = itemStack.getItem();
            return (item instanceof WorkingItem && ((WorkingItem)item).isWorking(itemStack))?1.f:0.f;
        });

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new EnergizedPowerBookReloadListener());

        ModMessages.registerPacketsS2C();

        EntityRendererRegistry.register(ModEntityTypes.BATTERY_BOX_MINECART,
                entity -> new MinecartEntityRenderer<>(entity, new EntityModelLayer(
                        new Identifier("minecraft", "chest_minecart"), "main")));
        EntityRendererRegistry.register(ModEntityTypes.ADVANCED_BATTERY_BOX_MINECART,
                entity -> new MinecartEntityRenderer<>(entity, new EntityModelLayer(
                        new Identifier("minecraft", "chest_minecart"), "main")));
    }
}
