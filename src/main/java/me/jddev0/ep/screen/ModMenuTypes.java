package me.jddev0.ep.screen;

import me.jddev0.ep.EnergizedPowerMod;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModMenuTypes {
    private ModMenuTypes() {}

    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, EnergizedPowerMod.MODID);

    private static <T extends AbstractContainerMenu>RegistryObject<MenuType<T>> registerMenuType(String name, IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IForgeMenuType.create(factory));
    }

    public static final RegistryObject<MenuType<AutoCrafterMenu>> AUTO_CRAFTER_MENU = registerMenuType("auto_crafter",
            AutoCrafterMenu::new);

    public static final RegistryObject<MenuType<CrusherMenu>> CRUSHER_MENU = registerMenuType("crusher",
            CrusherMenu::new);

    public static final RegistryObject<MenuType<SawmillMenu>> SAWMILL_MENU = registerMenuType("sawmill",
            SawmillMenu::new);

    public static final RegistryObject<MenuType<CompressorMenu>> COMPRESSOR_MENU = registerMenuType("compressor",
            CompressorMenu::new);

    public static final RegistryObject<MenuType<PlantGrowthChamberMenu>> PLANT_GROWTH_CHAMBER_MENU = registerMenuType("plant_growth_chamber",
            PlantGrowthChamberMenu::new);

    public static final RegistryObject<MenuType<BlockPlacerMenu>> BLOCK_PLACER_MENU = registerMenuType("block_placer",
            BlockPlacerMenu::new);

    public static final RegistryObject<MenuType<FluidFillerMenu>> FLUID_FILLER_MENU = registerMenuType("fluid_filler",
            FluidFillerMenu::new);

    public static final RegistryObject<MenuType<FluidDrainerMenu>> FLUID_DRAINER_MENU = registerMenuType("fluid_drainer",
            FluidDrainerMenu::new);

    public static final RegistryObject<MenuType<ChargerMenu>> CHARGER_MENU = registerMenuType("charger",
            ChargerMenu::new);

    public static final RegistryObject<MenuType<AdvancedChargerMenu>> ADVANCED_CHARGER_MENU = registerMenuType("advanced_charger",
            AdvancedChargerMenu::new);

    public static final RegistryObject<MenuType<UnchargerMenu>> UNCHARGER_MENU = registerMenuType("uncharger",
            UnchargerMenu::new);

    public static final RegistryObject<MenuType<AdvancedUnchargerMenu>> ADVANCED_UNCHARGER_MENU = registerMenuType("advanced_uncharger",
            AdvancedUnchargerMenu::new);
    public static final RegistryObject<MenuType<EnergizerMenu>> ENERGIZER_MENU = registerMenuType("energizer",
            EnergizerMenu::new);

    public static final RegistryObject<MenuType<CoalEngineMenu>> COAL_ENGINE_MENU = registerMenuType("coal_engine",
            CoalEngineMenu::new);

    public static final RegistryObject<MenuType<PoweredFurnaceMenu>> POWERED_FURNACE_MENU = registerMenuType("powered_furnace",
            PoweredFurnaceMenu::new);

    public static final RegistryObject<MenuType<WeatherControllerMenu>> WEATHER_CONTROLLER_MENU = registerMenuType("weather_controller",
            WeatherControllerMenu::new);

    public static final RegistryObject<MenuType<TimeControllerMenu>> TIME_CONTROLLER_MENU = registerMenuType("time_controller",
            TimeControllerMenu::new);

    public static final RegistryObject<MenuType<LightningGeneratorMenu>> LIGHTNING_GENERATOR_MENU = registerMenuType("lightning_generator",
            LightningGeneratorMenu::new);

    public static final RegistryObject<MenuType<ChargingStationMenu>> CHARGING_STATION_MENU = registerMenuType("charging_station",
            ChargingStationMenu::new);

    public static final RegistryObject<MenuType<HeatGeneratorMenu>> HEAT_GENERATOR_MENU = registerMenuType("heat_generator",
            HeatGeneratorMenu::new);

    public static final RegistryObject<MenuType<ThermalGeneratorMenu>> THERMAL_GENERATOR_MENU = registerMenuType("thermal_generator",
            ThermalGeneratorMenu::new);

    public static final RegistryObject<MenuType<BatteryBoxMenu>> BATTERY_BOX_MENU = registerMenuType("battery_box",
            BatteryBoxMenu::new);
    public static final RegistryObject<MenuType<AdvancedBatteryBoxMenu>> ADVANCED_BATTERY_BOX_MENU = registerMenuType("advanced_battery_box",
            AdvancedBatteryBoxMenu::new);

    public static final RegistryObject<MenuType<MinecartChargerMenu>> MINECART_CHARGER_MENU = registerMenuType("minecart_charger",
            MinecartChargerMenu::new);

    public static final RegistryObject<MenuType<MinecartUnchargerMenu>> MINECART_UNCHARGER_MENU = registerMenuType("minecart_uncharger",
            MinecartUnchargerMenu::new);

    public static final RegistryObject<MenuType<MinecartBatteryBoxMenu>> MINECART_BATTERY_BOX_MENU = registerMenuType("minecart_battery_box",
            MinecartBatteryBoxMenu::new);
    public static final RegistryObject<MenuType<MinecartAdvancedBatteryBoxMenu>> MINECART_ADVANCED_BATTERY_BOX_MENU = registerMenuType("minecart_advanced_battery_box",
            MinecartAdvancedBatteryBoxMenu::new);

    public static final RegistryObject<MenuType<SolarPanelMenu>> SOLAR_PANEL_MENU_1 = registerMenuType("solar_panel_1",
            SolarPanelMenu::new);
    public static final RegistryObject<MenuType<SolarPanelMenu>> SOLAR_PANEL_MENU_2 = registerMenuType("solar_panel_2",
            SolarPanelMenu::new);
    public static final RegistryObject<MenuType<SolarPanelMenu>> SOLAR_PANEL_MENU_3 = registerMenuType("solar_panel_3",
            SolarPanelMenu::new);
    public static final RegistryObject<MenuType<SolarPanelMenu>> SOLAR_PANEL_MENU_4 = registerMenuType("solar_panel_4",
            SolarPanelMenu::new);
    public static final RegistryObject<MenuType<SolarPanelMenu>> SOLAR_PANEL_MENU_5 = registerMenuType("solar_panel_5",
            SolarPanelMenu::new);

    public static void register(IEventBus modEventBus) {
        MENUS.register(modEventBus);
    }
}