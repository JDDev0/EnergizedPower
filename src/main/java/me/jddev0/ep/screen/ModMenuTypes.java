package me.jddev0.ep.screen;

import me.jddev0.ep.EnergizedPowerMod;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public final class ModMenuTypes {
    private ModMenuTypes() {}

    public static final ScreenHandlerType<AutoCrafterMenu> AUTO_CRAFTER_MENU = createScreenHandlerType("auto_crafter",
            new ExtendedScreenHandlerType<>(AutoCrafterMenu::new));

    public static final ScreenHandlerType<CrusherMenu> CRUSHER_MENU = createScreenHandlerType("crusher",
            new ExtendedScreenHandlerType<>(CrusherMenu::new));

    public static final ScreenHandlerType<SawmillMenu> SAWMILL_MENU = createScreenHandlerType("sawmill",
            new ExtendedScreenHandlerType<>(SawmillMenu::new));

    public static final ScreenHandlerType<BlockPlacerMenu> BLOCK_PLACER_MENU = createScreenHandlerType("block_placer",
            new ExtendedScreenHandlerType<>(BlockPlacerMenu::new));

    public static final ScreenHandlerType<ChargerMenu> CHARGER_MENU = createScreenHandlerType("charger",
            new ExtendedScreenHandlerType<>(ChargerMenu::new));

    public static final ScreenHandlerType<UnchargerMenu> UNCHARGER_MENU = createScreenHandlerType("uncharger",
            new ExtendedScreenHandlerType<>(UnchargerMenu::new));

    public static final ScreenHandlerType<EnergizerMenu> ENERGIZER_MENU = createScreenHandlerType("energizer",
            new ExtendedScreenHandlerType<>(EnergizerMenu::new));

    public static final ScreenHandlerType<CompressorMenu> COMPRESSOR_MENU = createScreenHandlerType("compressor_menu",
            new ExtendedScreenHandlerType<>(CompressorMenu::new));

    public static final ScreenHandlerType<PlantGrowthChamberMenu> PLANT_GROWTH_CHAMBER_MENU = createScreenHandlerType("plant_growth_chamber_menu",
            new ExtendedScreenHandlerType<>(PlantGrowthChamberMenu::new));

    public static final ScreenHandlerType<CoalEngineMenu> COAL_ENGINE_MENU = createScreenHandlerType("coal_engine",
            new ExtendedScreenHandlerType<>(CoalEngineMenu::new));

    public static final ScreenHandlerType<PoweredFurnaceMenu> POWERED_FURNACE_MENU = createScreenHandlerType("powered_furnace_menu",
            new ExtendedScreenHandlerType<>(PoweredFurnaceMenu::new));

    public static final ScreenHandlerType<WeatherControllerMenu> WEATHER_CONTROLLER_MENU = createScreenHandlerType("weather_controller_menu",
            new ExtendedScreenHandlerType<>(WeatherControllerMenu::new));

    public static final ScreenHandlerType<TimeControllerMenu> TIME_CONTROLLER_MENU = createScreenHandlerType("time_controller_menu",
            new ExtendedScreenHandlerType<>(TimeControllerMenu::new));

    private static <T extends ScreenHandler> ScreenHandlerType<T> createScreenHandlerType(String name, ScreenHandlerType<T> screenHandlerType) {
        return Registry.register(Registries.SCREEN_HANDLER, new Identifier(EnergizedPowerMod.MODID, name), screenHandlerType);
    }

    public static void register() {

    }
}