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

    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.CONTAINERS, EnergizedPowerMod.MODID);

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

    public static final RegistryObject<MenuType<ChargerMenu>> CHARGER_MENU = registerMenuType("charger",
            ChargerMenu::new);

    public static final RegistryObject<MenuType<UnchargerMenu>> UNCHARGER_MENU = registerMenuType("uncharger",
            UnchargerMenu::new);
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

    public static void register(IEventBus modEventBus) {
        MENUS.register(modEventBus);
    }
}