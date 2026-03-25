package me.jddev0.ep.screen;

import me.jddev0.ep.api.EPAPI;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import java.util.function.Supplier;

public final class EPMenuTypes {
    private EPMenuTypes() {}

    public static final MenuType<ItemConveyorBeltLoaderMenu> BASIC_ITEM_CONVEYOR_BELT_LOADER_MENU = createScreenHandlerType("item_conveyor_belt_loader",
            new ExtendedScreenHandlerType<>(ItemConveyorBeltLoaderMenu::new, BlockPos.STREAM_CODEC.cast()));
    public static final MenuType<ItemConveyorBeltLoaderMenu> FAST_ITEM_CONVEYOR_BELT_LOADER_MENU = createScreenHandlerType("fast_item_conveyor_belt_loader",
            new ExtendedScreenHandlerType<>(ItemConveyorBeltLoaderMenu::new, BlockPos.STREAM_CODEC.cast()));
    public static final MenuType<ItemConveyorBeltLoaderMenu> EXPRESS_ITEM_CONVEYOR_BELT_LOADER_MENU = createScreenHandlerType("express_item_conveyor_belt_loader",
            new ExtendedScreenHandlerType<>(ItemConveyorBeltLoaderMenu::new, BlockPos.STREAM_CODEC.cast()));

    public static final MenuType<ItemConveyorBeltSorterMenu> BASIC_ITEM_CONVEYOR_BELT_SORTER_MENU = createScreenHandlerType("item_conveyor_sorter_loader",
            new ExtendedScreenHandlerType<>(ItemConveyorBeltSorterMenu::new, BlockPos.STREAM_CODEC.cast()));
    public static final MenuType<ItemConveyorBeltSorterMenu> FAST_ITEM_CONVEYOR_BELT_SORTER_MENU = createScreenHandlerType("fast_conveyor_sorter_loader",
            new ExtendedScreenHandlerType<>(ItemConveyorBeltSorterMenu::new, BlockPos.STREAM_CODEC.cast()));
    public static final MenuType<ItemConveyorBeltSorterMenu> EXPRESS_ITEM_CONVEYOR_BELT_SORTER_MENU = createScreenHandlerType("express_conveyor_sorter_loader",
            new ExtendedScreenHandlerType<>(ItemConveyorBeltSorterMenu::new, BlockPos.STREAM_CODEC.cast()));

    public static final MenuType<AutoCrafterMenu> AUTO_CRAFTER_MENU = createScreenHandlerType("auto_crafter",
            new ExtendedScreenHandlerType<>(AutoCrafterMenu::new, BlockPos.STREAM_CODEC.cast()));

    public static final MenuType<AdvancedAutoCrafterMenu> ADVANCED_AUTO_CRAFTER_MENU = createScreenHandlerType("advanced_auto_crafter",
            new ExtendedScreenHandlerType<>(AdvancedAutoCrafterMenu::new, BlockPos.STREAM_CODEC.cast()));

    public static final MenuType<CrusherMenu> CRUSHER_MENU = createScreenHandlerType("crusher",
            new ExtendedScreenHandlerType<>(CrusherMenu::new, BlockPos.STREAM_CODEC.cast()));

    public static final MenuType<AdvancedCrusherMenu> ADVANCED_CRUSHER_MENU = createScreenHandlerType("advanced_crusher",
            new ExtendedScreenHandlerType<>(AdvancedCrusherMenu::new, BlockPos.STREAM_CODEC.cast()));

    public static final MenuType<PulverizerMenu> PULVERIZER_MENU = createScreenHandlerType("pulverizer",
            new ExtendedScreenHandlerType<>(PulverizerMenu::new, BlockPos.STREAM_CODEC.cast()));

    public static final MenuType<SawmillMenu> SAWMILL_MENU = createScreenHandlerType("sawmill",
            new ExtendedScreenHandlerType<>(SawmillMenu::new, BlockPos.STREAM_CODEC.cast()));

    public static final MenuType<BlockPlacerMenu> BLOCK_PLACER_MENU = createScreenHandlerType("block_placer",
            new ExtendedScreenHandlerType<>(BlockPlacerMenu::new, BlockPos.STREAM_CODEC.cast()));

    public static final MenuType<AdvancedPulverizerMenu> ADVANCED_PULVERIZER_MENU = createScreenHandlerType("advanced_pulverizer",
            new ExtendedScreenHandlerType<>(AdvancedPulverizerMenu::new, BlockPos.STREAM_CODEC.cast()));

    public static final MenuType<FluidFillerMenu> FLUID_FILLER_MENU = createScreenHandlerType("fluid_filler",
            new ExtendedScreenHandlerType<>(FluidFillerMenu::new, BlockPos.STREAM_CODEC.cast()));

    public static final MenuType<FluidDrainerMenu> FLUID_DRAINER_MENU = createScreenHandlerType("fluid_drainer",
            new ExtendedScreenHandlerType<>(FluidDrainerMenu::new, BlockPos.STREAM_CODEC.cast()));

    public static final MenuType<DrainMenu> DRAIN_MENU = createScreenHandlerType("drain",
            new ExtendedScreenHandlerType<>(DrainMenu::new, BlockPos.STREAM_CODEC.cast()));

    public static final MenuType<ChargerMenu> CHARGER_MENU = createScreenHandlerType("charger",
            new ExtendedScreenHandlerType<>(ChargerMenu::new, BlockPos.STREAM_CODEC.cast()));

    public static final MenuType<AdvancedChargerMenu> ADVANCED_CHARGER_MENU = createScreenHandlerType("advanced_charger",
            new ExtendedScreenHandlerType<>(AdvancedChargerMenu::new, BlockPos.STREAM_CODEC.cast()));

    public static final MenuType<UnchargerMenu> UNCHARGER_MENU = createScreenHandlerType("uncharger",
            new ExtendedScreenHandlerType<>(UnchargerMenu::new, BlockPos.STREAM_CODEC.cast()));

    public static final MenuType<AdvancedUnchargerMenu> ADVANCED_UNCHARGER_MENU = createScreenHandlerType("advanced_uncharger",
            new ExtendedScreenHandlerType<>(AdvancedUnchargerMenu::new, BlockPos.STREAM_CODEC.cast()));

    public static final MenuType<EnergizerMenu> ENERGIZER_MENU = createScreenHandlerType("energizer",
            new ExtendedScreenHandlerType<>(EnergizerMenu::new, BlockPos.STREAM_CODEC.cast()));

    public static final MenuType<CompressorMenu> COMPRESSOR_MENU = createScreenHandlerType("compressor",
            new ExtendedScreenHandlerType<>(CompressorMenu::new, BlockPos.STREAM_CODEC.cast()));

    public static final MenuType<PlantGrowthChamberMenu> PLANT_GROWTH_CHAMBER_MENU = createScreenHandlerType("plant_growth_chamber",
            new ExtendedScreenHandlerType<>(PlantGrowthChamberMenu::new, BlockPos.STREAM_CODEC.cast()));

    public static final MenuType<StoneLiquefierMenu> STONE_LIQUEFIER_MENU = createScreenHandlerType("stone_liquefier",
            new ExtendedScreenHandlerType<>(StoneLiquefierMenu::new, BlockPos.STREAM_CODEC.cast()));

    public static final MenuType<StoneSolidifierMenu> STONE_SOLIDIFIER_MENU = createScreenHandlerType("stone_solidifier",
            new ExtendedScreenHandlerType<>(StoneSolidifierMenu::new, BlockPos.STREAM_CODEC.cast()));

    public static final MenuType<FluidPumpMenu> FLUID_PUMP_MENU = createScreenHandlerType("fluid_pump",
            new ExtendedScreenHandlerType<>(FluidPumpMenu::new, BlockPos.STREAM_CODEC.cast()));

    public static final MenuType<AdvancedFluidPumpMenu> ADVANCED_FLUID_PUMP_MENU = createScreenHandlerType("advanced_fluid_pump",
            new ExtendedScreenHandlerType<>(AdvancedFluidPumpMenu::new, BlockPos.STREAM_CODEC.cast()));

    public static final MenuType<FiltrationPlantMenu> FILTRATION_PLANT_MENU = createScreenHandlerType("filtration_plant",
            new ExtendedScreenHandlerType<>(FiltrationPlantMenu::new, BlockPos.STREAM_CODEC.cast()));

    public static final MenuType<FluidTransposerMenu> FLUID_TRANSPOSER_MENU = createScreenHandlerType("fluid_transposer",
            new ExtendedScreenHandlerType<>(FluidTransposerMenu::new, BlockPos.STREAM_CODEC.cast()));

    public static final MenuType<CoalEngineMenu> COAL_ENGINE_MENU = createScreenHandlerType("coal_engine",
            new ExtendedScreenHandlerType<>(CoalEngineMenu::new, BlockPos.STREAM_CODEC.cast()));

    public static final MenuType<PoweredFurnaceMenu> POWERED_FURNACE_MENU = createScreenHandlerType("powered_furnace",
            new ExtendedScreenHandlerType<>(PoweredFurnaceMenu::new, BlockPos.STREAM_CODEC.cast()));

    public static final MenuType<AdvancedPoweredFurnaceMenu> ADVANCED_POWERED_FURNACE_MENU = createScreenHandlerType("advanced_powered_furnace",
            new ExtendedScreenHandlerType<>(AdvancedPoweredFurnaceMenu::new, BlockPos.STREAM_CODEC.cast()));

    public static final MenuType<WeatherControllerMenu> WEATHER_CONTROLLER_MENU = createScreenHandlerType("weather_controller",
            new ExtendedScreenHandlerType<>(WeatherControllerMenu::new, BlockPos.STREAM_CODEC.cast()));

    public static final MenuType<TimeControllerMenu> TIME_CONTROLLER_MENU = createScreenHandlerType("time_controller",
            new ExtendedScreenHandlerType<>(TimeControllerMenu::new, BlockPos.STREAM_CODEC.cast()));

    public static final MenuType<LightningGeneratorMenu> LIGHTNING_GENERATOR_MENU = createScreenHandlerType("lightning_generator",
            new ExtendedScreenHandlerType<>(LightningGeneratorMenu::new, BlockPos.STREAM_CODEC.cast()));

    public static final MenuType<ChargingStationMenu> CHARGING_STATION_MENU = createScreenHandlerType("charging_station",
            new ExtendedScreenHandlerType<>(ChargingStationMenu::new, BlockPos.STREAM_CODEC.cast()));

    public static final MenuType<CrystalGrowthChamberMenu> CRYSTAL_GROWTH_CHAMBER_MENU = createScreenHandlerType("crystal_growth_chamber",
            new ExtendedScreenHandlerType<>(CrystalGrowthChamberMenu::new, BlockPos.STREAM_CODEC.cast()));

    public static final MenuType<TeleporterMenu> TELEPORTER_MENU = createScreenHandlerType("teleporter",
            new ExtendedScreenHandlerType<>(TeleporterMenu::new, BlockPos.STREAM_CODEC.cast()));

    public static final MenuType<HeatGeneratorMenu> HEAT_GENERATOR_MENU = createScreenHandlerType("heat_generator",
            new ExtendedScreenHandlerType<>(HeatGeneratorMenu::new, BlockPos.STREAM_CODEC.cast()));

    public static final MenuType<ThermalGeneratorMenu> THERMAL_GENERATOR_MENU = createScreenHandlerType("thermal_generator",
            new ExtendedScreenHandlerType<>(ThermalGeneratorMenu::new, BlockPos.STREAM_CODEC.cast()));

    public static final MenuType<BatteryBoxMenu> BATTERY_BOX_MENU = createScreenHandlerType("battery_box",
            new ExtendedScreenHandlerType<>(BatteryBoxMenu::new, BlockPos.STREAM_CODEC.cast()));
    public static final MenuType<AdvancedBatteryBoxMenu> ADVANCED_BATTERY_BOX_MENU = createScreenHandlerType("advanced_battery_box",
            new ExtendedScreenHandlerType<>(AdvancedBatteryBoxMenu::new, BlockPos.STREAM_CODEC.cast()));

    public static final MenuType<CreativeBatteryBoxMenu> CREATIVE_BATTERY_BOX_MENU = createScreenHandlerType("creative_battery_box",
            new ExtendedScreenHandlerType<>(CreativeBatteryBoxMenu::new, BlockPos.STREAM_CODEC.cast()));

    public static final MenuType<MinecartChargerMenu> MINECART_CHARGER_MENU = createScreenHandlerType("minecart_charger",
            new ExtendedScreenHandlerType<>(MinecartChargerMenu::new, BlockPos.STREAM_CODEC.cast()));

    public static final MenuType<AdvancedMinecartChargerMenu> ADVANCED_MINECART_CHARGER_MENU = createScreenHandlerType("advanced_minecart_charger",
            new ExtendedScreenHandlerType<>(AdvancedMinecartChargerMenu::new, BlockPos.STREAM_CODEC.cast()));

    public static final MenuType<MinecartUnchargerMenu> MINECART_UNCHARGER_MENU = createScreenHandlerType("minecart_uncharger",
            new ExtendedScreenHandlerType<>(MinecartUnchargerMenu::new, BlockPos.STREAM_CODEC.cast()));

    public static final MenuType<AdvancedMinecartUnchargerMenu> ADVANCED_MINECART_UNCHARGER_MENU = createScreenHandlerType("advanced_minecart_uncharger",
            new ExtendedScreenHandlerType<>(AdvancedMinecartUnchargerMenu::new, BlockPos.STREAM_CODEC.cast()));

    public static final MenuType<SolarPanelMenu> SOLAR_PANEL_MENU_1 = createScreenHandlerType("solar_panel_1",
            new ExtendedScreenHandlerType<>(SolarPanelMenu::new, BlockPos.STREAM_CODEC.cast()));
    public static final MenuType<SolarPanelMenu> SOLAR_PANEL_MENU_2 = createScreenHandlerType("solar_panel_2",
            new ExtendedScreenHandlerType<>(SolarPanelMenu::new, BlockPos.STREAM_CODEC.cast()));
    public static final MenuType<SolarPanelMenu> SOLAR_PANEL_MENU_3 = createScreenHandlerType("solar_panel_3",
            new ExtendedScreenHandlerType<>(SolarPanelMenu::new, BlockPos.STREAM_CODEC.cast()));
    public static final MenuType<SolarPanelMenu> SOLAR_PANEL_MENU_4 = createScreenHandlerType("solar_panel_4",
            new ExtendedScreenHandlerType<>(SolarPanelMenu::new, BlockPos.STREAM_CODEC.cast()));
    public static final MenuType<SolarPanelMenu> SOLAR_PANEL_MENU_5 = createScreenHandlerType("solar_panel_5",
            new ExtendedScreenHandlerType<>(SolarPanelMenu::new, BlockPos.STREAM_CODEC.cast()));
    public static final MenuType<SolarPanelMenu> SOLAR_PANEL_MENU_6 = createScreenHandlerType("solar_panel_6",
            new ExtendedScreenHandlerType<>(SolarPanelMenu::new, BlockPos.STREAM_CODEC.cast()));

    public static final MenuType<TransformerMenu> LV_TRANSFORMER_1_TO_N_MENU = createScreenHandlerType("lv_transformer_1_to_n",
            new ExtendedScreenHandlerType<>(TransformerMenu::new, BlockPos.STREAM_CODEC.cast()));
    public static final MenuType<TransformerMenu> LV_TRANSFORMER_3_TO_3_MENU = createScreenHandlerType("lv_transformer_3_to_3",
            new ExtendedScreenHandlerType<>(TransformerMenu::new, BlockPos.STREAM_CODEC.cast()));
    public static final MenuType<TransformerMenu> LV_TRANSFORMER_N_TO_1_MENU = createScreenHandlerType("lv_transformer_n_to_1",
            new ExtendedScreenHandlerType<>(TransformerMenu::new, BlockPos.STREAM_CODEC.cast()));

    public static final MenuType<TransformerMenu> MV_TRANSFORMER_1_TO_N_MENU = createScreenHandlerType("mv_transformer_1_to_n",
            new ExtendedScreenHandlerType<>(TransformerMenu::new, BlockPos.STREAM_CODEC.cast()));
    public static final MenuType<TransformerMenu> MV_TRANSFORMER_3_TO_3_MENU = createScreenHandlerType("mv_transformer_3_to_3",
            new ExtendedScreenHandlerType<>(TransformerMenu::new, BlockPos.STREAM_CODEC.cast()));
    public static final MenuType<TransformerMenu> MV_TRANSFORMER_N_TO_1_MENU = createScreenHandlerType("mv_transformer_n_to_1",
            new ExtendedScreenHandlerType<>(TransformerMenu::new, BlockPos.STREAM_CODEC.cast()));

    public static final MenuType<TransformerMenu> HV_TRANSFORMER_1_TO_N_MENU = createScreenHandlerType("hv_transformer_1_to_n",
            new ExtendedScreenHandlerType<>(TransformerMenu::new, BlockPos.STREAM_CODEC.cast()));
    public static final MenuType<TransformerMenu> HV_TRANSFORMER_3_TO_3_MENU = createScreenHandlerType("hv_transformer_3_to_3",
            new ExtendedScreenHandlerType<>(TransformerMenu::new, BlockPos.STREAM_CODEC.cast()));
    public static final MenuType<TransformerMenu> HV_TRANSFORMER_N_TO_1_MENU = createScreenHandlerType("hv_transformer_n_to_1",
            new ExtendedScreenHandlerType<>(TransformerMenu::new, BlockPos.STREAM_CODEC.cast()));

    public static final MenuType<TransformerMenu> EHV_TRANSFORMER_1_TO_N_MENU = createScreenHandlerType("ehv_transformer_1_to_n",
            new ExtendedScreenHandlerType<>(TransformerMenu::new, BlockPos.STREAM_CODEC.cast()));
    public static final MenuType<TransformerMenu> EHV_TRANSFORMER_3_TO_3_MENU = createScreenHandlerType("ehv_transformer_3_to_3",
            new ExtendedScreenHandlerType<>(TransformerMenu::new, BlockPos.STREAM_CODEC.cast()));
    public static final MenuType<TransformerMenu> EHV_TRANSFORMER_N_TO_1_MENU = createScreenHandlerType("ehv_transformer_n_to_1",
            new ExtendedScreenHandlerType<>(TransformerMenu::new, BlockPos.STREAM_CODEC.cast()));

    public static final MenuType<TransformerMenu> CONFIGURABLE_LV_TRANSFORMER_MENU = createScreenHandlerType("configurable_lv_transformer",
            new ExtendedScreenHandlerType<>(TransformerMenu::new, BlockPos.STREAM_CODEC.cast()));
    public static final MenuType<TransformerMenu> CONFIGURABLE_MV_TRANSFORMER_MENU = createScreenHandlerType("configurable_mv_transformer",
            new ExtendedScreenHandlerType<>(TransformerMenu::new, BlockPos.STREAM_CODEC.cast()));
    public static final MenuType<TransformerMenu> CONFIGURABLE_HV_TRANSFORMER_MENU = createScreenHandlerType("configurable_hv_transformer",
            new ExtendedScreenHandlerType<>(TransformerMenu::new, BlockPos.STREAM_CODEC.cast()));
    public static final MenuType<TransformerMenu> CONFIGURABLE_EHV_TRANSFORMER_MENU = createScreenHandlerType("configurable_ehv_transformer",
            new ExtendedScreenHandlerType<>(TransformerMenu::new, BlockPos.STREAM_CODEC.cast()));

    public static final MenuType<PressMoldMakerMenu> PRESS_MOLD_MAKER_MENU = createScreenHandlerType("press_mold_maker",
            new ExtendedScreenHandlerType<>(PressMoldMakerMenu::new, BlockPos.STREAM_CODEC.cast()));

    public static final MenuType<AlloyFurnaceMenu> ALLOY_FURNACE_MENU = createScreenHandlerType("alloy_furnace",
            new ExtendedScreenHandlerType<>(AlloyFurnaceMenu::new, BlockPos.STREAM_CODEC.cast()));

    public static final MenuType<MetalPressMenu> METAL_PRESS_MENU = createScreenHandlerType("metal_press",
            new ExtendedScreenHandlerType<>(MetalPressMenu::new, BlockPos.STREAM_CODEC.cast()));

    public static final MenuType<AutoPressMoldMakerMenu> AUTO_PRESS_MOLD_MAKER_MENU = createScreenHandlerType("auto_press_mold_maker",
            new ExtendedScreenHandlerType<>(AutoPressMoldMakerMenu::new, BlockPos.STREAM_CODEC.cast()));

    public static final MenuType<AutoStonecutterMenu> AUTO_STONECUTTER_MENU = createScreenHandlerType("auto_stonecutter",
            new ExtendedScreenHandlerType<>(AutoStonecutterMenu::new, BlockPos.STREAM_CODEC.cast()));


    public static final MenuType<AssemblingMachineMenu> ASSEMBLING_MACHINE_MENU = createScreenHandlerType("assembling_machine",
            new ExtendedScreenHandlerType<>(AssemblingMachineMenu::new, BlockPos.STREAM_CODEC.cast()));

    public static final MenuType<InductionSmelterMenu> INDUCTION_SMELTER_MENU = createScreenHandlerType("induction_smelter",
            new ExtendedScreenHandlerType<>(InductionSmelterMenu::new, BlockPos.STREAM_CODEC.cast()));

    public static final MenuType<InventoryChargerMenu> INVENTORY_CHARGER_MENU = createScreenHandlerType("inventory_charger",
            new MenuType<>(InventoryChargerMenu::new, FeatureFlags.VANILLA_SET));

    public static final MenuType<InventoryTeleporterMenu> INVENTORY_TELEPORTER_MENU = createScreenHandlerType("inventory_teleporter",
            new MenuType<>(InventoryTeleporterMenu::new, FeatureFlags.VANILLA_SET));

    public static final MenuType<MinecartBatteryBoxMenu> MINECART_BATTERY_BOX_MENU = createScreenHandlerType("minecart_battery_box",
            new MenuType<>(MinecartBatteryBoxMenu::new, FeatureFlags.VANILLA_SET));
    public static final MenuType<MinecartAdvancedBatteryBoxMenu> MINECART_ADVANCED_BATTERY_BOX_MENU = createScreenHandlerType("minecart_advanced_battery_box",
            new MenuType<>(MinecartAdvancedBatteryBoxMenu::new, FeatureFlags.VANILLA_SET));

    public static final MenuType<FluidTankMenu> FLUID_TANK_SMALL = createScreenHandlerType("fluid_tank_small",
            new ExtendedScreenHandlerType<>(FluidTankMenu::new, BlockPos.STREAM_CODEC.cast()));
    public static final MenuType<FluidTankMenu> FLUID_TANK_MEDIUM = createScreenHandlerType("fluid_tank_medium",
            new ExtendedScreenHandlerType<>(FluidTankMenu::new, BlockPos.STREAM_CODEC.cast()));
    public static final MenuType<FluidTankMenu> FLUID_TANK_LARGE = createScreenHandlerType("fluid_tank_large",
            new ExtendedScreenHandlerType<>(FluidTankMenu::new, BlockPos.STREAM_CODEC.cast()));

    public static final MenuType<CreativeFluidTankMenu> CREATIVE_FLUID_TANK = createScreenHandlerType("creative_fluid_tank",
            new ExtendedScreenHandlerType<>(CreativeFluidTankMenu::new, BlockPos.STREAM_CODEC.cast()));

    public static final MenuType<ItemSiloMenu> ITEM_SILO_TINY = createScreenHandlerType("item_silo_tiny",
            new ExtendedScreenHandlerType<>(ItemSiloMenu::new, BlockPos.STREAM_CODEC.cast()));
    public static final MenuType<ItemSiloMenu> ITEM_SILO_SMALL = createScreenHandlerType("item_silo_small",
            new ExtendedScreenHandlerType<>(ItemSiloMenu::new, BlockPos.STREAM_CODEC.cast()));
    public static final MenuType<ItemSiloMenu> ITEM_SILO_MEDIUM = createScreenHandlerType("item_silo_medium",
            new ExtendedScreenHandlerType<>(ItemSiloMenu::new, BlockPos.STREAM_CODEC.cast()));
    public static final MenuType<ItemSiloMenu> ITEM_SILO_LARGE = createScreenHandlerType("item_silo_large",
            new ExtendedScreenHandlerType<>(ItemSiloMenu::new, BlockPos.STREAM_CODEC.cast()));
    public static final MenuType<ItemSiloMenu> ITEM_SILO_GIANT = createScreenHandlerType("item_silo_giant",
            new ExtendedScreenHandlerType<>(ItemSiloMenu::new, BlockPos.STREAM_CODEC.cast()));
    public static final MenuType<CreativeItemSiloMenu> CREATIVE_ITEM_SILO_MENU = createScreenHandlerType("creative_item_silo",
            new ExtendedScreenHandlerType<>(CreativeItemSiloMenu::new, BlockPos.STREAM_CODEC.cast()));

    private static <T extends AbstractContainerMenu> MenuType<T> createScreenHandlerType(String name, MenuType<T> screenHandlerType) {
        return Registry.register(BuiltInRegistries.MENU, EPAPI.id(name), screenHandlerType);
    }

    public static void register() {

    }
}