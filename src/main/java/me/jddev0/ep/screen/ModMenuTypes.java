package me.jddev0.ep.screen;

import me.jddev0.ep.EnergizedPowerMod;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public final class ModMenuTypes {
    private ModMenuTypes() {}

    public static final ScreenHandlerType<ItemConveyorBeltLoaderMenu> ITEM_CONVEYOR_BELT_LOADER_MENU = createScreenHandlerType("item_conveyor_belt_loader",
            new ExtendedScreenHandlerType<>(ItemConveyorBeltLoaderMenu::new));

    public static final ScreenHandlerType<AutoCrafterMenu> AUTO_CRAFTER_MENU = createScreenHandlerType("auto_crafter",
            new ExtendedScreenHandlerType<>(AutoCrafterMenu::new));

    public static final ScreenHandlerType<CrusherMenu> CRUSHER_MENU = createScreenHandlerType("crusher",
            new ExtendedScreenHandlerType<>(CrusherMenu::new));

    public static final ScreenHandlerType<PulverizerMenu> PULVERIZER_MENU = createScreenHandlerType("pulverizer",
            new ExtendedScreenHandlerType<>(PulverizerMenu::new));

    public static final ScreenHandlerType<SawmillMenu> SAWMILL_MENU = createScreenHandlerType("sawmill",
            new ExtendedScreenHandlerType<>(SawmillMenu::new));

    public static final ScreenHandlerType<BlockPlacerMenu> BLOCK_PLACER_MENU = createScreenHandlerType("block_placer",
            new ExtendedScreenHandlerType<>(BlockPlacerMenu::new));

    public static final ScreenHandlerType<FluidFillerMenu> FLUID_FILLER_MENU = createScreenHandlerType("fluid_filler",
            new ExtendedScreenHandlerType<>(FluidFillerMenu::new));

    public static final ScreenHandlerType<FluidDrainerMenu> FLUID_DRAINER_MENU = createScreenHandlerType("fluid_drainer",
            new ExtendedScreenHandlerType<>(FluidDrainerMenu::new));

    public static final ScreenHandlerType<ChargerMenu> CHARGER_MENU = createScreenHandlerType("charger",
            new ExtendedScreenHandlerType<>(ChargerMenu::new));

    public static final ScreenHandlerType<AdvancedChargerMenu> ADVANCED_CHARGER_MENU = createScreenHandlerType("advanced_charger",
            new ExtendedScreenHandlerType<>(AdvancedChargerMenu::new));

    public static final ScreenHandlerType<UnchargerMenu> UNCHARGER_MENU = createScreenHandlerType("uncharger",
            new ExtendedScreenHandlerType<>(UnchargerMenu::new));

    public static final ScreenHandlerType<AdvancedUnchargerMenu> ADVANCED_UNCHARGER_MENU = createScreenHandlerType("advanced_uncharger",
            new ExtendedScreenHandlerType<>(AdvancedUnchargerMenu::new));

    public static final ScreenHandlerType<EnergizerMenu> ENERGIZER_MENU = createScreenHandlerType("energizer",
            new ExtendedScreenHandlerType<>(EnergizerMenu::new));

    public static final ScreenHandlerType<CompressorMenu> COMPRESSOR_MENU = createScreenHandlerType("compressor",
            new ExtendedScreenHandlerType<>(CompressorMenu::new));

    public static final ScreenHandlerType<PlantGrowthChamberMenu> PLANT_GROWTH_CHAMBER_MENU = createScreenHandlerType("plant_growth_chamber",
            new ExtendedScreenHandlerType<>(PlantGrowthChamberMenu::new));

    public static final ScreenHandlerType<CoalEngineMenu> COAL_ENGINE_MENU = createScreenHandlerType("coal_engine",
            new ExtendedScreenHandlerType<>(CoalEngineMenu::new));

    public static final ScreenHandlerType<PoweredFurnaceMenu> POWERED_FURNACE_MENU = createScreenHandlerType("powered_furnace",
            new ExtendedScreenHandlerType<>(PoweredFurnaceMenu::new));

    public static final ScreenHandlerType<AdvancedPoweredFurnaceMenu> ADVANCED_POWERED_FURNACE_MENU = createScreenHandlerType("advanced_powered_furnace",
            new ExtendedScreenHandlerType<>(AdvancedPoweredFurnaceMenu::new));

    public static final ScreenHandlerType<WeatherControllerMenu> WEATHER_CONTROLLER_MENU = createScreenHandlerType("weather_controller",
            new ExtendedScreenHandlerType<>(WeatherControllerMenu::new));

    public static final ScreenHandlerType<TimeControllerMenu> TIME_CONTROLLER_MENU = createScreenHandlerType("time_controller",
            new ExtendedScreenHandlerType<>(TimeControllerMenu::new));

    public static final ScreenHandlerType<LightningGeneratorMenu> LIGHTNING_GENERATOR_MENU = createScreenHandlerType("lightning_generator",
            new ExtendedScreenHandlerType<>(LightningGeneratorMenu::new));

    public static final ScreenHandlerType<ChargingStationMenu> CHARGING_STATION_MENU = createScreenHandlerType("charging_station",
            new ExtendedScreenHandlerType<>(ChargingStationMenu::new));

    public static final ScreenHandlerType<HeatGeneratorMenu> HEAT_GENERATOR_MENU = createScreenHandlerType("heat_generator",
            new ExtendedScreenHandlerType<>(HeatGeneratorMenu::new));

    public static final ScreenHandlerType<ThermalGeneratorMenu> THERMAL_GENERATOR_MENU = createScreenHandlerType("thermal_generator",
            new ExtendedScreenHandlerType<>(ThermalGeneratorMenu::new));

    public static final ScreenHandlerType<BatteryBoxMenu> BATTERY_BOX_MENU = createScreenHandlerType("battery_box",
            new ExtendedScreenHandlerType<>(BatteryBoxMenu::new));
    public static final ScreenHandlerType<AdvancedBatteryBoxMenu> ADVANCED_BATTERY_BOX_MENU = createScreenHandlerType("advanced_battery_box",
            new ExtendedScreenHandlerType<>(AdvancedBatteryBoxMenu::new));

    public static final ScreenHandlerType<MinecartChargerMenu> MINECART_CHARGER_MENU = createScreenHandlerType("minecart_charger",
            new ExtendedScreenHandlerType<>(MinecartChargerMenu::new));

    public static final ScreenHandlerType<AdvancedMinecartChargerMenu> ADVANCED_MINECART_CHARGER_MENU = createScreenHandlerType("advanced_minecart_charger",
            new ExtendedScreenHandlerType<>(AdvancedMinecartChargerMenu::new));

    public static final ScreenHandlerType<MinecartUnchargerMenu> MINECART_UNCHARGER_MENU = createScreenHandlerType("minecart_uncharger",
            new ExtendedScreenHandlerType<>(MinecartUnchargerMenu::new));

    public static final ScreenHandlerType<AdvancedMinecartUnchargerMenu> ADVANCED_MINECART_UNCHARGER_MENU = createScreenHandlerType("advanced_minecart_uncharger",
            new ExtendedScreenHandlerType<>(AdvancedMinecartUnchargerMenu::new));

    public static final ScreenHandlerType<SolarPanelMenu> SOLAR_PANEL_MENU_1 = createScreenHandlerType("solar_panel_1",
            new ExtendedScreenHandlerType<>(SolarPanelMenu::new));
    public static final ScreenHandlerType<SolarPanelMenu> SOLAR_PANEL_MENU_2 = createScreenHandlerType("solar_panel_2",
            new ExtendedScreenHandlerType<>(SolarPanelMenu::new));
    public static final ScreenHandlerType<SolarPanelMenu> SOLAR_PANEL_MENU_3 = createScreenHandlerType("solar_panel_3",
            new ExtendedScreenHandlerType<>(SolarPanelMenu::new));
    public static final ScreenHandlerType<SolarPanelMenu> SOLAR_PANEL_MENU_4 = createScreenHandlerType("solar_panel_4",
            new ExtendedScreenHandlerType<>(SolarPanelMenu::new));
    public static final ScreenHandlerType<SolarPanelMenu> SOLAR_PANEL_MENU_5 = createScreenHandlerType("solar_panel_5",
            new ExtendedScreenHandlerType<>(SolarPanelMenu::new));

    public static final ScreenHandlerType<InventoryChargerMenu> INVENTORY_CHARGER_MENU = createScreenHandlerType("inventory_charger",
            new ScreenHandlerType<>(InventoryChargerMenu::new, FeatureFlags.VANILLA_FEATURES));

    public static final ScreenHandlerType<MinecartBatteryBoxMenu> MINECART_BATTERY_BOX_MENU = createScreenHandlerType("minecart_battery_box",
            new ScreenHandlerType<>(MinecartBatteryBoxMenu::new, FeatureFlags.VANILLA_FEATURES));
    public static final ScreenHandlerType<MinecartAdvancedBatteryBoxMenu> MINECART_ADVANCED_BATTERY_BOX_MENU = createScreenHandlerType("minecart_advanced_battery_box",
            new ScreenHandlerType<>(MinecartAdvancedBatteryBoxMenu::new, FeatureFlags.VANILLA_FEATURES));

    private static <T extends ScreenHandler> ScreenHandlerType<T> createScreenHandlerType(String name, ScreenHandlerType<T> screenHandlerType) {
        return Registry.register(Registries.SCREEN_HANDLER, new Identifier(EnergizedPowerMod.MODID, name), screenHandlerType);
    }

    public static void register() {

    }
}