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

    public static final RegistryObject<MenuType<ItemConveyorBeltLoaderMenu>> ITEM_CONVEYOR_BELT_LOADER_MENU = registerMenuType("item_conveyor_belt_loader",
            ItemConveyorBeltLoaderMenu::new);

    public static final RegistryObject<MenuType<ItemConveyorBeltSorterMenu>> ITEM_CONVEYOR_BELT_SORTER_MENU = registerMenuType("item_conveyor_sorter_loader",
            ItemConveyorBeltSorterMenu::new);

    public static final RegistryObject<MenuType<AutoCrafterMenu>> AUTO_CRAFTER_MENU = registerMenuType("auto_crafter",
            AutoCrafterMenu::new);

    public static final RegistryObject<MenuType<AdvancedAutoCrafterMenu>> ADVANCED_AUTO_CRAFTER_MENU = registerMenuType("advanced_auto_crafter",
            AdvancedAutoCrafterMenu::new);

    public static final RegistryObject<MenuType<CrusherMenu>> CRUSHER_MENU = registerMenuType("crusher",
            CrusherMenu::new);

    public static final RegistryObject<MenuType<AdvancedCrusherMenu>> ADVANCED_CRUSHER_MENU = registerMenuType("advanced_crusher",
            AdvancedCrusherMenu::new);

    public static final RegistryObject<MenuType<PulverizerMenu>> PULVERIZER_MENU = registerMenuType("pulverizer",
            PulverizerMenu::new);

    public static final RegistryObject<MenuType<AdvancedPulverizerMenu>> ADVANCED_PULVERIZER_MENU = registerMenuType("advanced_pulverizer",
            AdvancedPulverizerMenu::new);

    public static final RegistryObject<MenuType<SawmillMenu>> SAWMILL_MENU = registerMenuType("sawmill",
            SawmillMenu::new);

    public static final RegistryObject<MenuType<CompressorMenu>> COMPRESSOR_MENU = registerMenuType("compressor",
            CompressorMenu::new);

    public static final RegistryObject<MenuType<PlantGrowthChamberMenu>> PLANT_GROWTH_CHAMBER_MENU = registerMenuType("plant_growth_chamber",
            PlantGrowthChamberMenu::new);

    public static final RegistryObject<MenuType<StoneSolidifierMenu>> STONE_SOLIDIFIER_MENU = registerMenuType("stone_solidifier",
            StoneSolidifierMenu::new);

    public static final RegistryObject<MenuType<FiltrationPlantMenu>> FILTRATION_PLANT_MENU = registerMenuType("filtration_plant",
            FiltrationPlantMenu::new);

    public static final RegistryObject<MenuType<BlockPlacerMenu>> BLOCK_PLACER_MENU = registerMenuType("block_placer",
            BlockPlacerMenu::new);

    public static final RegistryObject<MenuType<FluidFillerMenu>> FLUID_FILLER_MENU = registerMenuType("fluid_filler",
            FluidFillerMenu::new);

    public static final RegistryObject<MenuType<FluidDrainerMenu>> FLUID_DRAINER_MENU = registerMenuType("fluid_drainer",
            FluidDrainerMenu::new);

    public static final RegistryObject<MenuType<DrainMenu>> DRAIN_MENU = registerMenuType("drain",
            DrainMenu::new);

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

    public static final RegistryObject<MenuType<AdvancedPoweredFurnaceMenu>> ADVANCED_POWERED_FURNACE_MENU = registerMenuType("advanced_powered_furnace",
            AdvancedPoweredFurnaceMenu::new);

    public static final RegistryObject<MenuType<WeatherControllerMenu>> WEATHER_CONTROLLER_MENU = registerMenuType("weather_controller",
            WeatherControllerMenu::new);

    public static final RegistryObject<MenuType<TimeControllerMenu>> TIME_CONTROLLER_MENU = registerMenuType("time_controller",
            TimeControllerMenu::new);

    public static final RegistryObject<MenuType<TeleporterMenu>> TELEPORTER_MENU = registerMenuType("teleporter",
            TeleporterMenu::new);

    public static final RegistryObject<MenuType<LightningGeneratorMenu>> LIGHTNING_GENERATOR_MENU = registerMenuType("lightning_generator",
            LightningGeneratorMenu::new);

    public static final RegistryObject<MenuType<ChargingStationMenu>> CHARGING_STATION_MENU = registerMenuType("charging_station",
            ChargingStationMenu::new);

    public static final RegistryObject<MenuType<CrystalGrowthChamberMenu>> CRYSTAL_GROWTH_CHAMBER_MENU = registerMenuType("crystal_growth_chamber",
            CrystalGrowthChamberMenu::new);

    public static final RegistryObject<MenuType<HeatGeneratorMenu>> HEAT_GENERATOR_MENU = registerMenuType("heat_generator",
            HeatGeneratorMenu::new);

    public static final RegistryObject<MenuType<ThermalGeneratorMenu>> THERMAL_GENERATOR_MENU = registerMenuType("thermal_generator",
            ThermalGeneratorMenu::new);

    public static final RegistryObject<MenuType<BatteryBoxMenu>> BATTERY_BOX_MENU = registerMenuType("battery_box",
            BatteryBoxMenu::new);
    public static final RegistryObject<MenuType<AdvancedBatteryBoxMenu>> ADVANCED_BATTERY_BOX_MENU = registerMenuType("advanced_battery_box",
            AdvancedBatteryBoxMenu::new);
    public static final RegistryObject<MenuType<CreativeBatteryBoxMenu>> CREATIVE_BATTERY_BOX_MENU = registerMenuType("creative_battery_box",
            CreativeBatteryBoxMenu::new);

    public static final RegistryObject<MenuType<MinecartChargerMenu>> MINECART_CHARGER_MENU = registerMenuType("minecart_charger",
            MinecartChargerMenu::new);

    public static final RegistryObject<MenuType<AdvancedMinecartChargerMenu>> ADVANCED_MINECART_CHARGER_MENU = registerMenuType("advanced_minecart_charger",
            AdvancedMinecartChargerMenu::new);

    public static final RegistryObject<MenuType<MinecartUnchargerMenu>> MINECART_UNCHARGER_MENU = registerMenuType("minecart_uncharger",
            MinecartUnchargerMenu::new);

    public static final RegistryObject<MenuType<AdvancedMinecartUnchargerMenu>> ADVANCED_MINECART_UNCHARGER_MENU = registerMenuType("advanced_minecart_uncharger",
            AdvancedMinecartUnchargerMenu::new);

    public static final RegistryObject<MenuType<InventoryChargerMenu>> INVENTORY_CHARGER_MENU = registerMenuType("inventory_charger",
            InventoryChargerMenu::new);

    public static final RegistryObject<MenuType<InventoryTeleporterMenu>> INVENTORY_TELEPORTER_MENU = registerMenuType("inventory_teleporter",
            InventoryTeleporterMenu::new);

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
    public static final RegistryObject<MenuType<SolarPanelMenu>> SOLAR_PANEL_MENU_6 = registerMenuType("solar_panel_6",
            SolarPanelMenu::new);

    public static final RegistryObject<MenuType<PressMoldMakerMenu>> PRESS_MOLD_MAKER_MENU = registerMenuType("press_mold_maker",
            PressMoldMakerMenu::new);

    public static final RegistryObject<MenuType<MetalPressMenu>> METAL_PRESS_MENU = registerMenuType("metal_press",
            MetalPressMenu::new);

    public static final RegistryObject<MenuType<AutoPressMoldMakerMenu>> AUTO_PRESS_MOLD_MAKER_MENU = registerMenuType("auto_press_mold_maker",
            AutoPressMoldMakerMenu::new);

    public static final RegistryObject<MenuType<AutoStonecutterMenu>> AUTO_STONECUTTER_MENU = registerMenuType("auto_stonecutter",
            AutoStonecutterMenu::new);

    public static final RegistryObject<MenuType<AssemblingMachineMenu>> ASSEMBLING_MACHINE_MENU = registerMenuType("assembling_machine",
            AssemblingMachineMenu::new);

    public static final RegistryObject<MenuType<FluidTankMenu>> FLUID_TANK_SMALL = registerMenuType("fluid_tank_small",
            FluidTankMenu::new);
    public static final RegistryObject<MenuType<FluidTankMenu>> FLUID_TANK_MEDIUM = registerMenuType("fluid_tank_medium",
            FluidTankMenu::new);
    public static final RegistryObject<MenuType<FluidTankMenu>> FLUID_TANK_LARGE = registerMenuType("fluid_tank_large",
            FluidTankMenu::new);

    public static void register(IEventBus modEventBus) {
        MENUS.register(modEventBus);
    }
}