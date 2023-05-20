package me.jddev0.ep;

import com.mojang.logging.LogUtils;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.block.behavior.ModBlockBehaviors;
import me.jddev0.ep.block.entity.ModBlockEntities;
import me.jddev0.ep.entity.ModEntityTypes;
import me.jddev0.ep.item.*;
import me.jddev0.ep.loading.EnergizedPowerBookReloadListener;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.recipe.ModRecipes;
import me.jddev0.ep.screen.*;
import me.jddev0.ep.villager.ModVillager;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(EnergizedPowerMod.MODID)
public class EnergizedPowerMod {
    public static final String MODID = "energizedpower";
    private static final Logger LOGGER = LogUtils.getLogger();

    public EnergizedPowerMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModRecipes.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModVillager.register(modEventBus);
        ModEntityTypes.register(modEventBus);

        ModBlockBehaviors.register();

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        ModMessages.register();
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            MenuScreens.register(ModMenuTypes.AUTO_CRAFTER_MENU.get(), AutoCrafterScreen::new);
            MenuScreens.register(ModMenuTypes.CRUSHER_MENU.get(), CrusherScreen::new);
            MenuScreens.register(ModMenuTypes.SAWMILL_MENU.get(), SawmillScreen::new);
            MenuScreens.register(ModMenuTypes.COMPRESSOR_MENU.get(), CompressorScreen::new);
            MenuScreens.register(ModMenuTypes.PLANT_GROWTH_CHAMBER_MENU.get(), PlantGrowthChamberScreen::new);
            MenuScreens.register(ModMenuTypes.BLOCK_PLACER_MENU.get(), BlockPlacerScreen::new);
            MenuScreens.register(ModMenuTypes.CHARGER_MENU.get(), ChargerScreen::new);
            MenuScreens.register(ModMenuTypes.UNCHARGER_MENU.get(), UnchargerScreen::new);
            MenuScreens.register(ModMenuTypes.ENERGIZER_MENU.get(), EnergizerScreen::new);
            MenuScreens.register(ModMenuTypes.COAL_ENGINE_MENU.get(), CoalEngineScreen::new);
            MenuScreens.register(ModMenuTypes.POWERED_FURNACE_MENU.get(), PoweredFurnaceScreen::new);
            MenuScreens.register(ModMenuTypes.WEATHER_CONTROLLER_MENU.get(), WeatherControllerScreen::new);
            MenuScreens.register(ModMenuTypes.TIME_CONTROLLER_MENU.get(), TimeControllerScreen::new);
            MenuScreens.register(ModMenuTypes.LIGHTNING_GENERATOR_MENU.get(), LightningGeneratorScreen::new);
            MenuScreens.register(ModMenuTypes.CHARGING_STATION_MENU.get(), ChargingStationScreen::new);
            MenuScreens.register(ModMenuTypes.HEAT_GENERATOR_MENU.get(), HeatGeneratorScreen::new);
            MenuScreens.register(ModMenuTypes.BATTERY_BOX_MENU.get(), BatteryBoxScreen::new);
            MenuScreens.register(ModMenuTypes.MINECART_CHARGER_MENU.get(), MinecartChargerScreen::new);
            MenuScreens.register(ModMenuTypes.MINECART_UNCHARGER_MENU.get(), MinecartUnchargerScreen::new);

            MenuScreens.register(ModMenuTypes.MINECART_BATTERY_BOX_MENU.get(), MinecartBatteryBoxScreen::new);

            event.enqueueWork(() -> {
                ItemProperties.registerGeneric(new ResourceLocation(MODID, "active"), (itemStack, level, entity, seed) -> {
                    Item item = itemStack.getItem();
                    return (item instanceof ActivatableItem && ((ActivatableItem)item).isActive(itemStack))?1.f:0.f;
                });
                ItemProperties.registerGeneric(new ResourceLocation(MODID, "working"), (itemStack, level, entity, seed) -> {
                    Item item = itemStack.getItem();
                    return (item instanceof WorkingItem && ((WorkingItem)item).isWorking(itemStack))?1.f:0.f;
                });
            });

            EntityRenderers.register(ModEntityTypes.BATTERY_BOX_MINECART.get(),
                    entity -> new MinecartRenderer<>(entity, new ModelLayerLocation(
                            new ResourceLocation("minecraft", "chest_minecart"), "main")));
        }

        @SubscribeEvent
        public static void loadBookPages(RegisterClientReloadListenersEvent event) {
            event.registerReloadListener(new EnergizedPowerBookReloadListener());
        }
    }
}
