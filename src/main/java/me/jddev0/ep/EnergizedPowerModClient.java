package me.jddev0.ep;

import me.jddev0.ep.item.ActivatableItem;
import me.jddev0.ep.item.WorkingItem;
import me.jddev0.ep.loading.EnergizedPowerBookReloadListener;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.screen.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.mixin.object.builder.ModelPredicateProviderRegistryAccessor;
import net.minecraft.item.Item;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

public class EnergizedPowerModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ScreenRegistry.register(ModMenuTypes.AUTO_CRAFTER_MENU, AutoCrafterScreen::new);
        ScreenRegistry.register(ModMenuTypes.CRUSHER_MENU, CrusherScreen::new);
        ScreenRegistry.register(ModMenuTypes.SAWMILL_MENU, SawmillScreen::new);
        ScreenRegistry.register(ModMenuTypes.BLOCK_PLACER_MENU, BlockPlacerScreen::new);
        ScreenRegistry.register(ModMenuTypes.CHARGER_MENU, ChargerScreen::new);
        ScreenRegistry.register(ModMenuTypes.UNCHARGER_MENU, UnchargerScreen::new);
        ScreenRegistry.register(ModMenuTypes.ENERGIZER_MENU, EnergizerScreen::new);
        ScreenRegistry.register(ModMenuTypes.COAL_ENGINE_MENU, CoalEngineScreen::new);


        ModelPredicateProviderRegistryAccessor.callRegister(new Identifier(EnergizedPowerMod.MODID, "active"), (itemStack, level, entity, seed) -> {
            Item item = itemStack.getItem();
            return (item instanceof ActivatableItem && ((ActivatableItem)item).isActive(itemStack))?1.f:0.f;
        });
        ModelPredicateProviderRegistryAccessor.callRegister(new Identifier(EnergizedPowerMod.MODID, "working"), (itemStack, level, entity, seed) -> {
            Item item = itemStack.getItem();
            return (item instanceof WorkingItem && ((WorkingItem)item).isWorking(itemStack))?1.f:0.f;
        });

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new EnergizedPowerBookReloadListener());

        ModMessages.registerPacketsS2C();
    }
}
