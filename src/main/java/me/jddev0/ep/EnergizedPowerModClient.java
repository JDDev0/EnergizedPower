package me.jddev0.ep;

import me.jddev0.ep.item.ActivatableItem;
import me.jddev0.ep.item.WorkingItem;
import me.jddev0.ep.loading.EnergizedPowerBookReloadListener;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.screen.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
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
        HandledScreens.register(ModMenuTypes.BLOCK_PLACER_MENU, BlockPlacerScreen::new);
        HandledScreens.register(ModMenuTypes.CHARGER_MENU, ChargerScreen::new);
        HandledScreens.register(ModMenuTypes.UNCHARGER_MENU, UnchargerScreen::new);
        HandledScreens.register(ModMenuTypes.ENERGIZER_MENU, EnergizerScreen::new);
        HandledScreens.register(ModMenuTypes.COAL_ENGINE_MENU, CoalEngineScreen::new);

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
    }
}
