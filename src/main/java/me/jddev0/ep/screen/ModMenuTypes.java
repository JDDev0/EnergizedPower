package me.jddev0.ep.screen;

import me.jddev0.ep.EnergizedPowerMod;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public final class ModMenuTypes {
    private ModMenuTypes() {}

    public static final ScreenHandlerType<AutoCrafterMenu> AUTO_CRAFTER_MENU = createScreenHandlerType("auto_crafter",
            AutoCrafterMenu::new);

    public static final ScreenHandlerType<CrusherMenu> CRUSHER_MENU = createScreenHandlerType("crusher",
            CrusherMenu::new);

    public static final ScreenHandlerType<SawmillMenu> SAWMILL_MENU = createScreenHandlerType("sawmill",
            SawmillMenu::new);

    public static final ScreenHandlerType<BlockPlacerMenu> BLOCK_PLACER_MENU = createScreenHandlerType("block_placer",
            BlockPlacerMenu::new);

    public static final ScreenHandlerType<ChargerMenu> CHARGER_MENU = createScreenHandlerType("charger",
            ChargerMenu::new);

    public static final ScreenHandlerType<UnchargerMenu> UNCHARGER_MENU = createScreenHandlerType("uncharger",
            UnchargerMenu::new);

    public static final ScreenHandlerType<EnergizerMenu> ENERGIZER_MENU = createScreenHandlerType("energizer",
            EnergizerMenu::new);

    public static final ScreenHandlerType<CoalEngineMenu> COAL_ENGINE_MENU = createScreenHandlerType("coal_engine",
            CoalEngineMenu::new);

    private static <T extends ScreenHandler> ScreenHandlerType<T> createScreenHandlerType(String name, ScreenHandlerRegistry.ExtendedClientHandlerFactory<T> screenHandlerType) {
        return ScreenHandlerRegistry.registerExtended(new Identifier(EnergizedPowerMod.MODID, name), screenHandlerType);
    }

    public static void register() {

    }
}