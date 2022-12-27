package me.jddev0.ep;

import com.mojang.logging.LogUtils;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.block.entity.ModBlockEntities;
import me.jddev0.ep.item.ModCreativeModeTab;
import me.jddev0.ep.item.ModItems;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.recipe.ModRecipes;
import me.jddev0.ep.screen.*;
import me.jddev0.ep.villager.ModVillager;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CreativeModeTabEvent;
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

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);

        modEventBus.addListener(this::registerCreativeTab);
        modEventBus.addListener(this::addCreativeTab);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        ModMessages.register();
    }

    private void registerCreativeTab(CreativeModeTabEvent.Register event) {
        ModCreativeModeTab.registerCreative(event);
    }

    private void addCreativeTab(CreativeModeTabEvent.BuildContents event) {
        if(event.getTab() == ModCreativeModeTab.ENERGIZED_POWER_TAB) {
            event.accept(ModItems.ENERGIZED_POWER_BOOK);

            event.accept(ModBlocks.COAL_ENGINE_ITEM);
            event.accept(ModBlocks.LIGHTNING_GENERATOR_ITEM);
            event.accept(ModBlocks.SOLAR_PANEL_ITEM_1);
            event.accept(ModBlocks.SOLAR_PANEL_ITEM_2);
            event.accept(ModBlocks.SOLAR_PANEL_ITEM_3);
            event.accept(ModBlocks.SOLAR_PANEL_ITEM_4);
            event.accept(ModBlocks.SOLAR_PANEL_ITEM_5);

            event.accept(ModBlocks.AUTO_CRAFTER_ITEM);
            event.accept(ModBlocks.CHARGER_ITEM);
            event.accept(ModBlocks.ENERGIZER_ITEM);

            event.accept(ModItems.BASIC_SOLAR_CELL);
            event.accept(ModItems.ADVANCED_SOLAR_CELL);

            event.accept(ModBlocks.BASIC_MACHINE_FRAME_ITEM);
            event.accept(ModBlocks.ADVANCED_MACHINE_FRAME_ITEM);

            event.accept(ModBlocks.SILICON_BLOCK_ITEM);
            event.accept(ModItems.SILICON);
            event.accept(ModItems.ENERGIZED_COPPER_INGOT);
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            MenuScreens.register(ModMenuTypes.AUTO_CRAFTER_MENU.get(), AutoCrafterScreen::new);
            MenuScreens.register(ModMenuTypes.CHARGER_MENU.get(), ChargerScreen::new);
            MenuScreens.register(ModMenuTypes.ENERGIZER_MENU.get(), EnergizerScreen::new);
            MenuScreens.register(ModMenuTypes.COAL_ENGINE_MENU.get(), CoalEngineScreen::new);
        }
    }
}
