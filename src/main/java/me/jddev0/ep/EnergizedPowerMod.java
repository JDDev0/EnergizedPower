package me.jddev0.ep;

import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.behavior.ModBlockBehaviors;
import me.jddev0.ep.block.entity.EPBlockEntities;
import me.jddev0.ep.component.EPDataComponentTypes;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.entity.EPEntityTypes;
import me.jddev0.ep.event.PlayerInteractHandler;
import me.jddev0.ep.event.ServerStartingHandler;
import me.jddev0.ep.fluid.EPFluids;
import me.jddev0.ep.integration.cctweaked.EnergizedPowerCCTweakedIntegration;
import me.jddev0.ep.integration.cctweaked.EnergizedPowerCCTweakedUtils;
import me.jddev0.ep.item.*;
import me.jddev0.ep.machine.tier.BatteryTier;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.recipe.EPRecipes;
import me.jddev0.ep.screen.EPMenuTypes;
import me.jddev0.ep.villager.EPVillager;
import me.jddev0.ep.world.gen.ModWorldGeneration;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;

import java.util.function.Consumer;

public class EnergizedPowerMod implements ModInitializer {
    @Override
    public void onInitialize() {
        ModConfigs.registerConfigs(true);

        EPDataComponentTypes.register();

        EPItems.register();
        EPBlocks.register();
        EPBlockEntities.register();
        EPRecipes.register();
        EPMenuTypes.register();
        EPVillager.register();
        EPEntityTypes.register();

        EPFluids.register();

        ModBlockBehaviors.register();

        EPCreativeModeTab.register();
        addCreativeTab();

        ModWorldGeneration.register();

        ModMessages.registerTypedPayloads();
        ModMessages.registerPacketsC2S();

        PlayerInteractHandler.EVENT.register(new PlayerInteractHandler());
        ServerLifecycleEvents.SERVER_STARTING.register(new ServerStartingHandler());

        FlammableBlockRegistry.getDefaultInstance().add(EPBlocks.SAWDUST_BLOCK, 5, 20);

        if(EnergizedPowerCCTweakedUtils.isCCTweakedAvailable())
            EnergizedPowerCCTweakedIntegration.register();
    }

    private ItemStack getChargedItemStack(Item item, long energy) {
        ItemStack itemStack = new ItemStack(item);
        itemStack.set(EPDataComponentTypes.ENERGY, energy);

        return itemStack;
    }

    private void addEmptyAndFullyChargedItem(CreativeTabEntriesHelper event, Item item, long capacity) {
        event.accept(item);
        event.accept(getChargedItemStack(item, capacity));
    }

    private void addCreativeTab() {
        addCreativeTabFor(EPCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, event -> {
            event.accept(EPItems.ENERGIZED_POWER_BOOK);
            addEmptyAndFullyChargedItem(event, EPItems.ENERGY_ANALYZER, EnergyAnalyzerItem.ENERGY_CAPACITY);
            addEmptyAndFullyChargedItem(event, EPItems.FLUID_ANALYZER, FluidAnalyzerItem.ENERGY_CAPACITY);

            event.accept(EPItems.WOODEN_HAMMER);
            event.accept(EPItems.STONE_HAMMER);
            event.accept(EPItems.COPPER_HAMMER);
            event.accept(EPItems.IRON_HAMMER);
            event.accept(EPItems.GOLDEN_HAMMER);
            event.accept(EPItems.DIAMOND_HAMMER);
            event.accept(EPItems.NETHERITE_HAMMER);

            event.accept(EPItems.CUTTER);

            event.accept(EPItems.WRENCH);

            event.accept(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_ITEM);
            event.accept(EPBlocks.FAST_ITEM_CONVEYOR_BELT_ITEM);
            event.accept(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_ITEM);
            event.accept(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_LOADER_ITEM);
            event.accept(EPBlocks.FAST_ITEM_CONVEYOR_BELT_LOADER_ITEM);
            event.accept(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_LOADER_ITEM);
            event.accept(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_SORTER_ITEM);
            event.accept(EPBlocks.FAST_ITEM_CONVEYOR_BELT_SORTER_ITEM);
            event.accept(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_SORTER_ITEM);
            event.accept(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_SWITCH_ITEM);
            event.accept(EPBlocks.FAST_ITEM_CONVEYOR_BELT_SWITCH_ITEM);
            event.accept(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_SWITCH_ITEM);
            event.accept(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_SPLITTER_ITEM);
            event.accept(EPBlocks.FAST_ITEM_CONVEYOR_BELT_SPLITTER_ITEM);
            event.accept(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_SPLITTER_ITEM);
            event.accept(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_MERGER_ITEM);
            event.accept(EPBlocks.FAST_ITEM_CONVEYOR_BELT_MERGER_ITEM);
            event.accept(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_MERGER_ITEM);

            event.accept(EPBlocks.ITEM_SILO_TINY_ITEM);
            event.accept(EPBlocks.ITEM_SILO_SMALL_ITEM);
            event.accept(EPBlocks.ITEM_SILO_MEDIUM_ITEM);
            event.accept(EPBlocks.ITEM_SILO_LARGE_ITEM);
            event.accept(EPBlocks.ITEM_SILO_GIANT_ITEM);
            event.accept(EPBlocks.CREATIVE_ITEM_SILO_ITEM);

            event.accept(EPBlocks.IRON_FLUID_PIPE_ITEM);
            event.accept(EPBlocks.GOLDEN_FLUID_PIPE_ITEM);

            event.accept(EPBlocks.TIN_CABLE_ITEM);
            event.accept(EPBlocks.COPPER_CABLE_ITEM);
            event.accept(EPBlocks.GOLD_CABLE_ITEM);
            event.accept(EPBlocks.ENERGIZED_COPPER_CABLE_ITEM);
            event.accept(EPBlocks.ENERGIZED_GOLD_CABLE_ITEM);
            event.accept(EPBlocks.ENERGIZED_CRYSTAL_MATRIX_CABLE_ITEM);

            event.accept(EPBlocks.LV_TRANSFORMER_1_TO_N_ITEM);
            event.accept(EPBlocks.LV_TRANSFORMER_3_TO_3_ITEM);
            event.accept(EPBlocks.LV_TRANSFORMER_N_TO_1_ITEM);
            event.accept(EPBlocks.CONFIGURABLE_LV_TRANSFORMER_ITEM);
            event.accept(EPBlocks.MV_TRANSFORMER_1_TO_N_ITEM);
            event.accept(EPBlocks.MV_TRANSFORMER_3_TO_3_ITEM);
            event.accept(EPBlocks.MV_TRANSFORMER_N_TO_1_ITEM);
            event.accept(EPBlocks.CONFIGURABLE_MV_TRANSFORMER_ITEM);
            event.accept(EPBlocks.HV_TRANSFORMER_1_TO_N_ITEM);
            event.accept(EPBlocks.HV_TRANSFORMER_3_TO_3_ITEM);
            event.accept(EPBlocks.HV_TRANSFORMER_N_TO_1_ITEM);
            event.accept(EPBlocks.CONFIGURABLE_HV_TRANSFORMER_ITEM);
            event.accept(EPBlocks.EHV_TRANSFORMER_1_TO_N_ITEM);
            event.accept(EPBlocks.EHV_TRANSFORMER_3_TO_3_ITEM);
            event.accept(EPBlocks.EHV_TRANSFORMER_N_TO_1_ITEM);
            event.accept(EPBlocks.CONFIGURABLE_EHV_TRANSFORMER_ITEM);

            event.accept(EPBlocks.PRESS_MOLD_MAKER_ITEM);
            event.accept(EPBlocks.ALLOY_FURNACE_ITEM);

            event.accept(EPBlocks.COAL_ENGINE_ITEM);
            event.accept(EPBlocks.HEAT_GENERATOR_ITEM);
            event.accept(EPBlocks.THERMAL_GENERATOR_ITEM);
            event.accept(EPBlocks.LIGHTNING_GENERATOR_ITEM);
            event.accept(EPBlocks.SOLAR_PANEL_ITEM_1);
            event.accept(EPBlocks.SOLAR_PANEL_ITEM_2);
            event.accept(EPBlocks.SOLAR_PANEL_ITEM_3);
            event.accept(EPBlocks.SOLAR_PANEL_ITEM_4);
            event.accept(EPBlocks.SOLAR_PANEL_ITEM_5);
            event.accept(EPBlocks.SOLAR_PANEL_ITEM_6);

            event.accept(EPBlocks.BATTERY_BOX_ITEM);
            event.accept(EPBlocks.ADVANCED_BATTERY_BOX_ITEM);
            event.accept(EPBlocks.CREATIVE_BATTERY_BOX_ITEM);

            event.accept(EPBlocks.POWERED_LAMP_ITEM);
            event.accept(EPBlocks.POWERED_FURNACE_ITEM);
            event.accept(EPBlocks.ADVANCED_POWERED_FURNACE_ITEM);
            event.accept(EPBlocks.AUTO_CRAFTER_ITEM);
            event.accept(EPBlocks.ADVANCED_AUTO_CRAFTER_ITEM);
            event.accept(EPBlocks.CRUSHER_ITEM);
            event.accept(EPBlocks.ADVANCED_CRUSHER_ITEM);
            event.accept(EPBlocks.PULVERIZER_ITEM);
            event.accept(EPBlocks.ADVANCED_PULVERIZER_ITEM);
            event.accept(EPBlocks.SAWMILL_ITEM);
            event.accept(EPBlocks.COMPRESSOR_ITEM);
            event.accept(EPBlocks.METAL_PRESS_ITEM);
            event.accept(EPBlocks.AUTO_PRESS_MOLD_MAKER_ITEM);
            event.accept(EPBlocks.AUTO_STONECUTTER_ITEM);
            event.accept(EPBlocks.ASSEMBLING_MACHINE_ITEM);
            event.accept(EPBlocks.INDUCTION_SMELTER_ITEM);
            event.accept(EPBlocks.PLANT_GROWTH_CHAMBER_ITEM);
            event.accept(EPBlocks.STONE_LIQUEFIER_ITEM);
            event.accept(EPBlocks.STONE_SOLIDIFIER_ITEM);
            event.accept(EPBlocks.FILTRATION_PLANT_ITEM);
            event.accept(EPBlocks.FLUID_TRANSPOSER_ITEM);
            event.accept(EPBlocks.BLOCK_PLACER_ITEM);
            event.accept(EPBlocks.FLUID_TANK_SMALL_ITEM);
            event.accept(EPBlocks.FLUID_TANK_MEDIUM_ITEM);
            event.accept(EPBlocks.FLUID_TANK_LARGE_ITEM);
            event.accept(EPBlocks.CREATIVE_FLUID_TANK_ITEM);
            event.accept(EPBlocks.FLUID_FILLER_ITEM);
            event.accept(EPBlocks.FLUID_DRAINER_ITEM);
            event.accept(EPBlocks.FLUID_PUMP_ITEM);
            event.accept(EPBlocks.ADVANCED_FLUID_PUMP_ITEM);
            event.accept(EPBlocks.DRAIN_ITEM);
            event.accept(EPBlocks.CHARGER_ITEM);
            event.accept(EPBlocks.ADVANCED_CHARGER_ITEM);
            event.accept(EPBlocks.UNCHARGER_ITEM);
            event.accept(EPBlocks.ADVANCED_UNCHARGER_ITEM);
            event.accept(EPBlocks.MINECART_CHARGER_ITEM);
            event.accept(EPBlocks.ADVANCED_MINECART_CHARGER_ITEM);
            event.accept(EPBlocks.MINECART_UNCHARGER_ITEM);
            event.accept(EPBlocks.ADVANCED_MINECART_UNCHARGER_ITEM);

            event.accept(EPBlocks.ENERGIZER_ITEM);
            event.accept(EPBlocks.CHARGING_STATION_ITEM);
            event.accept(EPBlocks.CRYSTAL_GROWTH_CHAMBER);

            event.accept(EPBlocks.WEATHER_CONTROLLER_ITEM);
            event.accept(EPBlocks.TIME_CONTROLLER_ITEM);
            event.accept(EPBlocks.TELEPORTER_ITEM);

            addEmptyAndFullyChargedItem(event, EPItems.INVENTORY_COAL_ENGINE, InventoryCoalEngineItem.CAPACITY);
            event.accept(EPItems.INVENTORY_CHARGER);

            addEmptyAndFullyChargedItem(event, EPItems.INVENTORY_TELEPORTER, InventoryTeleporterItem.CAPACITY);

            addEmptyAndFullyChargedItem(event, EPItems.BATTERY_1, BatteryTier.BATTERY_1.getCapacity());
            addEmptyAndFullyChargedItem(event, EPItems.BATTERY_2, BatteryTier.BATTERY_2.getCapacity());
            addEmptyAndFullyChargedItem(event, EPItems.BATTERY_3, BatteryTier.BATTERY_3.getCapacity());
            addEmptyAndFullyChargedItem(event, EPItems.BATTERY_4, BatteryTier.BATTERY_4.getCapacity());
            addEmptyAndFullyChargedItem(event, EPItems.BATTERY_5, BatteryTier.BATTERY_5.getCapacity());
            addEmptyAndFullyChargedItem(event, EPItems.BATTERY_6, BatteryTier.BATTERY_6.getCapacity());
            addEmptyAndFullyChargedItem(event, EPItems.BATTERY_7, BatteryTier.BATTERY_7.getCapacity());
            addEmptyAndFullyChargedItem(event, EPItems.BATTERY_8, BatteryTier.BATTERY_8.getCapacity());
            event.accept(EPItems.CREATIVE_BATTERY);

            event.accept(EPItems.BATTERY_BOX_MINECART);
            event.accept(EPItems.ADVANCED_BATTERY_BOX_MINECART);

            event.accept(EPBlocks.BASIC_MACHINE_FRAME_ITEM);
            event.accept(EPBlocks.HARDENED_MACHINE_FRAME_ITEM);
            event.accept(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM);
            event.accept(EPBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM);

            event.accept(EPItems.BASIC_SOLAR_CELL);
            event.accept(EPItems.ADVANCED_SOLAR_CELL);
            event.accept(EPItems.REINFORCED_ADVANCED_SOLAR_CELL);

            event.accept(EPItems.BASIC_CIRCUIT);
            event.accept(EPItems.ADVANCED_CIRCUIT);
            event.accept(EPItems.PROCESSING_UNIT);

            event.accept(EPItems.TELEPORTER_PROCESSING_UNIT);
            event.accept(EPItems.TELEPORTER_MATRIX);

            event.accept(EPItems.BASIC_UPGRADE_MODULE);
            event.accept(EPItems.ADVANCED_UPGRADE_MODULE);
            event.accept(EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE);

            event.accept(EPItems.SPEED_UPGRADE_MODULE_1);
            event.accept(EPItems.SPEED_UPGRADE_MODULE_2);
            event.accept(EPItems.SPEED_UPGRADE_MODULE_3);
            event.accept(EPItems.SPEED_UPGRADE_MODULE_4);
            event.accept(EPItems.SPEED_UPGRADE_MODULE_5);

            event.accept(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_1);
            event.accept(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_2);
            event.accept(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_3);
            event.accept(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_4);
            event.accept(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_5);

            event.accept(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_1);
            event.accept(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_2);
            event.accept(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_3);
            event.accept(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_4);
            event.accept(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_5);

            event.accept(EPItems.DURATION_UPGRADE_MODULE_1);
            event.accept(EPItems.DURATION_UPGRADE_MODULE_2);
            event.accept(EPItems.DURATION_UPGRADE_MODULE_3);
            event.accept(EPItems.DURATION_UPGRADE_MODULE_4);
            event.accept(EPItems.DURATION_UPGRADE_MODULE_5);
            event.accept(EPItems.DURATION_UPGRADE_MODULE_6);

            event.accept(EPItems.RANGE_UPGRADE_MODULE_1);
            event.accept(EPItems.RANGE_UPGRADE_MODULE_2);
            event.accept(EPItems.RANGE_UPGRADE_MODULE_3);

            event.accept(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_1);
            event.accept(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_2);
            event.accept(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_3);
            event.accept(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_4);
            event.accept(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_5);

            event.accept(EPItems.EXTRACTION_RANGE_UPGRADE_MODULE_1);
            event.accept(EPItems.EXTRACTION_RANGE_UPGRADE_MODULE_2);
            event.accept(EPItems.EXTRACTION_RANGE_UPGRADE_MODULE_3);
            event.accept(EPItems.EXTRACTION_RANGE_UPGRADE_MODULE_4);
            event.accept(EPItems.EXTRACTION_RANGE_UPGRADE_MODULE_5);

            event.accept(EPItems.BLAST_FURNACE_UPGRADE_MODULE);
            event.accept(EPItems.SMOKER_UPGRADE_MODULE);

            event.accept(EPItems.MOON_LIGHT_UPGRADE_MODULE_1);
            event.accept(EPItems.MOON_LIGHT_UPGRADE_MODULE_2);
            event.accept(EPItems.MOON_LIGHT_UPGRADE_MODULE_3);

            event.accept(EPBlocks.SILICON_BLOCK_ITEM);
            event.accept(EPBlocks.TIN_BLOCK_ITEM);
            event.accept(EPBlocks.SAWDUST_BLOCK_ITEM);
            event.accept(EPItems.CABLE_INSULATOR);
            event.accept(EPItems.CHARCOAL_FILTER);
            event.accept(EPItems.SAW_BLADE);
            event.accept(EPItems.CRYSTAL_MATRIX);
            event.accept(EPItems.SAWDUST);
            event.accept(EPItems.CHARCOAL_DUST);
            event.accept(EPItems.BASIC_FERTILIZER);
            event.accept(EPItems.GOOD_FERTILIZER);
            event.accept(EPItems.ADVANCED_FERTILIZER);
            event.accept(EPItems.RAW_GEAR_PRESS_MOLD);
            event.accept(EPItems.RAW_ROD_PRESS_MOLD);
            event.accept(EPItems.RAW_WIRE_PRESS_MOLD);
            event.accept(EPItems.GEAR_PRESS_MOLD);
            event.accept(EPItems.ROD_PRESS_MOLD);
            event.accept(EPItems.WIRE_PRESS_MOLD);
            event.accept(EPItems.SILICON);
            event.accept(EPItems.TIN_DUST);
            event.accept(EPItems.COPPER_DUST);
            event.accept(EPItems.IRON_DUST);
            event.accept(EPItems.GOLD_DUST);
            event.accept(EPItems.TIN_NUGGET);
            event.accept(EPItems.TIN_INGOT);
            event.accept(EPItems.TIN_PLATE);
            event.accept(EPItems.COPPER_PLATE);
            event.accept(EPItems.IRON_PLATE);
            event.accept(EPItems.GOLD_PLATE);
            event.accept(EPItems.STEEL_INGOT);
            event.accept(EPItems.REDSTONE_ALLOY_INGOT);
            event.accept(EPItems.ADVANCED_ALLOY_INGOT);
            event.accept(EPItems.ADVANCED_ALLOY_PLATE);
            event.accept(EPItems.IRON_GEAR);
            event.accept(EPItems.IRON_ROD);
            event.accept(EPItems.TIN_WIRE);
            event.accept(EPItems.COPPER_WIRE);
            event.accept(EPItems.GOLD_WIRE);
            event.accept(EPItems.ENERGIZED_COPPER_INGOT);
            event.accept(EPItems.ENERGIZED_GOLD_INGOT);
            event.accept(EPItems.ENERGIZED_COPPER_PLATE);
            event.accept(EPItems.ENERGIZED_GOLD_PLATE);
            event.accept(EPItems.ENERGIZED_COPPER_WIRE);
            event.accept(EPItems.ENERGIZED_GOLD_WIRE);
            event.accept(EPItems.ENERGIZED_CRYSTAL_MATRIX);

            event.accept(EPFluids.DIRTY_WATER_BUCKET_ITEM);

            event.accept(EPItems.STONE_PEBBLE);

            event.accept(EPItems.RAW_TIN);
            event.accept(EPBlocks.TIN_ORE_ITEM);
            event.accept(EPBlocks.DEEPSLATE_TIN_ORE_ITEM);
            event.accept(EPBlocks.RAW_TIN_BLOCK_ITEM);
        });
    }

    private void addCreativeTabFor(RegistryKey<ItemGroup> groupKey,
                                   Consumer<CreativeTabEntriesHelper> consumer) {
        ItemGroupEvents.modifyEntriesEvent(groupKey).
                register(entries -> consumer.accept(new CreativeTabEntriesHelper(entries)));
    }
}
