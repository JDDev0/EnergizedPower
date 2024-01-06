package me.jddev0.ep.screen;

import me.jddev0.ep.EnergizedPowerMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class ModMenuTypes {
    private ModMenuTypes() {}

    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(BuiltInRegistries.MENU, EnergizedPowerMod.MODID);

    private static <T extends AbstractContainerMenu> DeferredHolder<MenuType<?>, MenuType<T>> registerMenuType(String name, IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IMenuTypeExtension.create(factory));
    }

    public static final Supplier<MenuType<ItemConveyorBeltLoaderMenu>> ITEM_CONVEYOR_BELT_LOADER_MENU = registerMenuType("item_conveyor_belt_loader",
            ItemConveyorBeltLoaderMenu::new);

    public static final Supplier<MenuType<ItemConveyorBeltSorterMenu>> ITEM_CONVEYOR_BELT_SORTER_MENU = registerMenuType("item_conveyor_sorter_loader",
            ItemConveyorBeltSorterMenu::new);

    public static final Supplier<MenuType<AutoCrafterMenu>> AUTO_CRAFTER_MENU = registerMenuType("auto_crafter",
            AutoCrafterMenu::new);

    public static final Supplier<MenuType<AdvancedAutoCrafterMenu>> ADVANCED_AUTO_CRAFTER_MENU = registerMenuType("advanced_auto_crafter",
            AdvancedAutoCrafterMenu::new);

    public static final Supplier<MenuType<CrusherMenu>> CRUSHER_MENU = registerMenuType("crusher",
            CrusherMenu::new);

    public static final Supplier<MenuType<PulverizerMenu>> PULVERIZER_MENU = registerMenuType("pulverizer",
            PulverizerMenu::new);

    public static final Supplier<MenuType<SawmillMenu>> SAWMILL_MENU = registerMenuType("sawmill",
            SawmillMenu::new);

    public static final Supplier<MenuType<CompressorMenu>> COMPRESSOR_MENU = registerMenuType("compressor",
            CompressorMenu::new);

    public static final Supplier<MenuType<PlantGrowthChamberMenu>> PLANT_GROWTH_CHAMBER_MENU = registerMenuType("plant_growth_chamber",
            PlantGrowthChamberMenu::new);

    public static final Supplier<MenuType<StoneSolidifierMenu>> STONE_SOLIDIFIER_MENU = registerMenuType("stone_solidifier",
            StoneSolidifierMenu::new);

    public static final Supplier<MenuType<BlockPlacerMenu>> BLOCK_PLACER_MENU = registerMenuType("block_placer",
            BlockPlacerMenu::new);

    public static final Supplier<MenuType<FluidFillerMenu>> FLUID_FILLER_MENU = registerMenuType("fluid_filler",
            FluidFillerMenu::new);

    public static final Supplier<MenuType<FluidDrainerMenu>> FLUID_DRAINER_MENU = registerMenuType("fluid_drainer",
            FluidDrainerMenu::new);

    public static final Supplier<MenuType<DrainMenu>> DRAIN_MENU = registerMenuType("drain",
            DrainMenu::new);

    public static final Supplier<MenuType<ChargerMenu>> CHARGER_MENU = registerMenuType("charger",
            ChargerMenu::new);

    public static final Supplier<MenuType<AdvancedChargerMenu>> ADVANCED_CHARGER_MENU = registerMenuType("advanced_charger",
            AdvancedChargerMenu::new);

    public static final Supplier<MenuType<UnchargerMenu>> UNCHARGER_MENU = registerMenuType("uncharger",
            UnchargerMenu::new);

    public static final Supplier<MenuType<AdvancedUnchargerMenu>> ADVANCED_UNCHARGER_MENU = registerMenuType("advanced_uncharger",
            AdvancedUnchargerMenu::new);
    public static final Supplier<MenuType<EnergizerMenu>> ENERGIZER_MENU = registerMenuType("energizer",
            EnergizerMenu::new);

    public static final Supplier<MenuType<CoalEngineMenu>> COAL_ENGINE_MENU = registerMenuType("coal_engine",
            CoalEngineMenu::new);

    public static final Supplier<MenuType<PoweredFurnaceMenu>> POWERED_FURNACE_MENU = registerMenuType("powered_furnace",
            PoweredFurnaceMenu::new);

    public static final Supplier<MenuType<AdvancedPoweredFurnaceMenu>> ADVANCED_POWERED_FURNACE_MENU = registerMenuType("advanced_powered_furnace",
            AdvancedPoweredFurnaceMenu::new);

    public static final Supplier<MenuType<WeatherControllerMenu>> WEATHER_CONTROLLER_MENU = registerMenuType("weather_controller",
            WeatherControllerMenu::new);

    public static final Supplier<MenuType<TimeControllerMenu>> TIME_CONTROLLER_MENU = registerMenuType("time_controller",
            TimeControllerMenu::new);

    public static final Supplier<MenuType<TeleporterMenu>> TELEPORTER_MENU = registerMenuType("teleporter",
            TeleporterMenu::new);

    public static final Supplier<MenuType<LightningGeneratorMenu>> LIGHTNING_GENERATOR_MENU = registerMenuType("lightning_generator",
            LightningGeneratorMenu::new);

    public static final Supplier<MenuType<ChargingStationMenu>> CHARGING_STATION_MENU = registerMenuType("charging_station",
            ChargingStationMenu::new);

    public static final Supplier<MenuType<CrystalGrowthChamberMenu>> CRYSTAL_GROWTH_CHAMBER_MENU = registerMenuType("crystal_growth_chamber",
            CrystalGrowthChamberMenu::new);

    public static final Supplier<MenuType<HeatGeneratorMenu>> HEAT_GENERATOR_MENU = registerMenuType("heat_generator",
            HeatGeneratorMenu::new);

    public static final Supplier<MenuType<ThermalGeneratorMenu>> THERMAL_GENERATOR_MENU = registerMenuType("thermal_generator",
            ThermalGeneratorMenu::new);

    public static final Supplier<MenuType<BatteryBoxMenu>> BATTERY_BOX_MENU = registerMenuType("battery_box",
            BatteryBoxMenu::new);
    public static final Supplier<MenuType<AdvancedBatteryBoxMenu>> ADVANCED_BATTERY_BOX_MENU = registerMenuType("advanced_battery_box",
            AdvancedBatteryBoxMenu::new);

    public static final Supplier<MenuType<MinecartChargerMenu>> MINECART_CHARGER_MENU = registerMenuType("minecart_charger",
            MinecartChargerMenu::new);

    public static final Supplier<MenuType<AdvancedMinecartChargerMenu>> ADVANCED_MINECART_CHARGER_MENU = registerMenuType("advanced_minecart_charger",
            AdvancedMinecartChargerMenu::new);

    public static final Supplier<MenuType<MinecartUnchargerMenu>> MINECART_UNCHARGER_MENU = registerMenuType("minecart_uncharger",
            MinecartUnchargerMenu::new);

    public static final Supplier<MenuType<AdvancedMinecartUnchargerMenu>> ADVANCED_MINECART_UNCHARGER_MENU = registerMenuType("advanced_minecart_uncharger",
            AdvancedMinecartUnchargerMenu::new);

    public static final Supplier<MenuType<InventoryChargerMenu>> INVENTORY_CHARGER_MENU = registerMenuType("inventory_charger",
            InventoryChargerMenu::new);

    public static final Supplier<MenuType<MinecartBatteryBoxMenu>> MINECART_BATTERY_BOX_MENU = registerMenuType("minecart_battery_box",
            MinecartBatteryBoxMenu::new);
    public static final Supplier<MenuType<MinecartAdvancedBatteryBoxMenu>> MINECART_ADVANCED_BATTERY_BOX_MENU = registerMenuType("minecart_advanced_battery_box",
            MinecartAdvancedBatteryBoxMenu::new);

    public static final Supplier<MenuType<SolarPanelMenu>> SOLAR_PANEL_MENU_1 = registerMenuType("solar_panel_1",
            SolarPanelMenu::new);
    public static final Supplier<MenuType<SolarPanelMenu>> SOLAR_PANEL_MENU_2 = registerMenuType("solar_panel_2",
            SolarPanelMenu::new);
    public static final Supplier<MenuType<SolarPanelMenu>> SOLAR_PANEL_MENU_3 = registerMenuType("solar_panel_3",
            SolarPanelMenu::new);
    public static final Supplier<MenuType<SolarPanelMenu>> SOLAR_PANEL_MENU_4 = registerMenuType("solar_panel_4",
            SolarPanelMenu::new);
    public static final Supplier<MenuType<SolarPanelMenu>> SOLAR_PANEL_MENU_5 = registerMenuType("solar_panel_5",
            SolarPanelMenu::new);
    public static final Supplier<MenuType<SolarPanelMenu>> SOLAR_PANEL_MENU_6 = registerMenuType("solar_panel_6",
            SolarPanelMenu::new);

    public static final Supplier<MenuType<PressMoldMakerMenu>> PRESS_MOLD_MAKER_MENU = registerMenuType("press_mold_maker",
            PressMoldMakerMenu::new);

    public static final Supplier<MenuType<MetalPressMenu>> METAL_PRESS_MENU = registerMenuType("metal_press",
            MetalPressMenu::new);

    public static final Supplier<MenuType<AssemblingMachineMenu>> ASSEMBLING_MACHINE_MENU = registerMenuType("assembling_machine",
            AssemblingMachineMenu::new);

    public static final Supplier<MenuType<FluidTankMenu>> FLUID_TANK_SMALL = registerMenuType("fluid_tank_small",
            FluidTankMenu::new);
    public static final Supplier<MenuType<FluidTankMenu>> FLUID_TANK_MEDIUM = registerMenuType("fluid_tank_medium",
            FluidTankMenu::new);
    public static final Supplier<MenuType<FluidTankMenu>> FLUID_TANK_LARGE = registerMenuType("fluid_tank_large",
            FluidTankMenu::new);

    public static void register(IEventBus modEventBus) {
        MENUS.register(modEventBus);
    }
}