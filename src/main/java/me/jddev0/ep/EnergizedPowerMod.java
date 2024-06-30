package me.jddev0.ep;

import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.block.behavior.ModBlockBehaviors;
import me.jddev0.ep.block.entity.ModBlockEntities;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.entity.ModEntityTypes;
import me.jddev0.ep.event.PlayerInteractHandler;
import me.jddev0.ep.event.ServerStartingHandler;
import me.jddev0.ep.fluid.ModFluids;
import me.jddev0.ep.item.*;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.painting.ModPaintings;
import me.jddev0.ep.recipe.ModRecipes;
import me.jddev0.ep.screen.ModMenuTypes;
import me.jddev0.ep.villager.ModVillager;
import me.jddev0.ep.worldgen.ModOreGeneration;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtLong;
import net.minecraft.registry.RegistryKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.reborn.energy.api.base.SimpleEnergyItem;

import java.util.function.Consumer;

public class EnergizedPowerMod implements ModInitializer {
    public static final String MODID = "energizedpower";
    public static final Logger LOGGER = LoggerFactory.getLogger("energizedpower");

    @Override
    public void onInitialize() {
        ModConfigs.registerConfigs(true);

        ModItems.register();
        ModBlocks.register();
        ModBlockEntities.register();
        ModRecipes.register();
        ModMenuTypes.register();
        ModVillager.register();
        ModEntityTypes.register();
        ModPaintings.register();

        ModFluids.register();

        ModBlockBehaviors.register();

        ModCreativeModeTab.register();
        addCreativeTab();

        ModOreGeneration.register();

        ModMessages.registerPacketsC2S();

        PlayerInteractHandler.EVENT.register(new PlayerInteractHandler());
        ServerLifecycleEvents.SERVER_STARTING.register(new ServerStartingHandler());

        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.SAWDUST_BLOCK, 5, 20);
    }

    private ItemStack getChargedItemStack(Item item, long energy) {
        ItemStack itemStack = new ItemStack(item);
        itemStack.getOrCreateNbt().put(SimpleEnergyItem.ENERGY_KEY, NbtLong.of(energy));

        return itemStack;
    }

    private void addEmptyAndFullyChargedItem(CreativeTabEntriesHelper event, Item item, long capacity) {
        event.accept(item);
        event.accept(getChargedItemStack(item, capacity));
    }

    private void addCreativeTab() {
        addCreativeTabFor(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, event -> {
            event.accept(ModItems.ENERGIZED_POWER_BOOK);
            addEmptyAndFullyChargedItem(event, ModItems.ENERGY_ANALYZER, EnergyAnalyzerItem.ENERGY_CAPACITY);
            addEmptyAndFullyChargedItem(event, ModItems.FLUID_ANALYZER, FluidAnalyzerItem.ENERGY_CAPACITY);

            event.accept(ModItems.WOODEN_HAMMER);
            event.accept(ModItems.STONE_HAMMER);
            event.accept(ModItems.IRON_HAMMER);
            event.accept(ModItems.GOLDEN_HAMMER);
            event.accept(ModItems.DIAMOND_HAMMER);
            event.accept(ModItems.NETHERITE_HAMMER);

            event.accept(ModItems.CUTTER);

            event.accept(ModItems.WRENCH);

            event.accept(ModBlocks.ITEM_CONVEYOR_BELT_ITEM);
            event.accept(ModBlocks.ITEM_CONVEYOR_BELT_LOADER_ITEM);
            event.accept(ModBlocks.ITEM_CONVEYOR_BELT_SORTER_ITEM);
            event.accept(ModBlocks.ITEM_CONVEYOR_BELT_SWITCH_ITEM);
            event.accept(ModBlocks.ITEM_CONVEYOR_BELT_SPLITTER_ITEM);
            event.accept(ModBlocks.ITEM_CONVEYOR_BELT_MERGER_ITEM);

            event.accept(ModBlocks.IRON_FLUID_PIPE_ITEM);
            event.accept(ModBlocks.GOLDEN_FLUID_PIPE_ITEM);

            event.accept(ModBlocks.TIN_CABLE_ITEM);
            event.accept(ModBlocks.COPPER_CABLE_ITEM);
            event.accept(ModBlocks.GOLD_CABLE_ITEM);
            event.accept(ModBlocks.ENERGIZED_COPPER_CABLE_ITEM);
            event.accept(ModBlocks.ENERGIZED_GOLD_CABLE_ITEM);
            event.accept(ModBlocks.ENERGIZED_CRYSTAL_MATRIX_CABLE_ITEM);

            event.accept(ModBlocks.LV_TRANSFORMER_1_TO_N_ITEM);
            event.accept(ModBlocks.LV_TRANSFORMER_3_TO_3_ITEM);
            event.accept(ModBlocks.LV_TRANSFORMER_N_TO_1_ITEM);
            event.accept(ModBlocks.MV_TRANSFORMER_1_TO_N_ITEM);
            event.accept(ModBlocks.MV_TRANSFORMER_3_TO_3_ITEM);
            event.accept(ModBlocks.MV_TRANSFORMER_N_TO_1_ITEM);
            event.accept(ModBlocks.HV_TRANSFORMER_1_TO_N_ITEM);
            event.accept(ModBlocks.HV_TRANSFORMER_3_TO_3_ITEM);
            event.accept(ModBlocks.HV_TRANSFORMER_N_TO_1_ITEM);
            event.accept(ModBlocks.EHV_TRANSFORMER_1_TO_N_ITEM);
            event.accept(ModBlocks.EHV_TRANSFORMER_3_TO_3_ITEM);
            event.accept(ModBlocks.EHV_TRANSFORMER_N_TO_1_ITEM);

            event.accept(ModBlocks.PRESS_MOLD_MAKER_ITEM);
            event.accept(ModBlocks.ALLOY_FURNACE_ITEM);

            event.accept(ModBlocks.COAL_ENGINE_ITEM);
            event.accept(ModBlocks.HEAT_GENERATOR_ITEM);
            event.accept(ModBlocks.THERMAL_GENERATOR_ITEM);
            event.accept(ModBlocks.LIGHTNING_GENERATOR_ITEM);
            event.accept(ModBlocks.SOLAR_PANEL_ITEM_1);
            event.accept(ModBlocks.SOLAR_PANEL_ITEM_2);
            event.accept(ModBlocks.SOLAR_PANEL_ITEM_3);
            event.accept(ModBlocks.SOLAR_PANEL_ITEM_4);
            event.accept(ModBlocks.SOLAR_PANEL_ITEM_5);
            event.accept(ModBlocks.SOLAR_PANEL_ITEM_6);

            event.accept(ModBlocks.BATTERY_BOX_ITEM);
            event.accept(ModBlocks.ADVANCED_BATTERY_BOX_ITEM);
            event.accept(ModBlocks.CREATIVE_BATTERY_BOX_ITEM);

            event.accept(ModBlocks.POWERED_LAMP_ITEM);
            event.accept(ModBlocks.POWERED_FURNACE_ITEM);
            event.accept(ModBlocks.ADVANCED_POWERED_FURNACE_ITEM);
            event.accept(ModBlocks.AUTO_CRAFTER_ITEM);
            event.accept(ModBlocks.ADVANCED_AUTO_CRAFTER_ITEM);
            event.accept(ModBlocks.CRUSHER_ITEM);
            event.accept(ModBlocks.ADVANCED_CRUSHER_ITEM);
            event.accept(ModBlocks.PULVERIZER_ITEM);
            event.accept(ModBlocks.ADVANCED_PULVERIZER_ITEM);
            event.accept(ModBlocks.SAWMILL_ITEM);
            event.accept(ModBlocks.COMPRESSOR_ITEM);
            event.accept(ModBlocks.METAL_PRESS_ITEM);
            event.accept(ModBlocks.AUTO_PRESS_MOLD_MAKER_ITEM);
            event.accept(ModBlocks.AUTO_STONECUTTER_ITEM);
            event.accept(ModBlocks.ASSEMBLING_MACHINE_ITEM);
            event.accept(ModBlocks.INDUCTION_SMELTER_ITEM);
            event.accept(ModBlocks.PLANT_GROWTH_CHAMBER_ITEM);
            event.accept(ModBlocks.STONE_SOLIDIFIER_ITEM);
            event.accept(ModBlocks.FILTRATION_PLANT_ITEM);
            event.accept(ModBlocks.FLUID_TRANSPOSER_ITEM);
            event.accept(ModBlocks.BLOCK_PLACER_ITEM);
            event.accept(ModBlocks.FLUID_TANK_SMALL_ITEM);
            event.accept(ModBlocks.FLUID_TANK_MEDIUM_ITEM);
            event.accept(ModBlocks.FLUID_TANK_LARGE_ITEM);
            event.accept(ModBlocks.CREATIVE_FLUID_TANK_ITEM);
            event.accept(ModBlocks.FLUID_FILLER_ITEM);
            event.accept(ModBlocks.FLUID_DRAINER_ITEM);
            event.accept(ModBlocks.FLUID_PUMP_ITEM);
            event.accept(ModBlocks.DRAIN_ITEM);
            event.accept(ModBlocks.CHARGER_ITEM);
            event.accept(ModBlocks.ADVANCED_CHARGER_ITEM);
            event.accept(ModBlocks.UNCHARGER_ITEM);
            event.accept(ModBlocks.ADVANCED_UNCHARGER_ITEM);
            event.accept(ModBlocks.MINECART_CHARGER_ITEM);
            event.accept(ModBlocks.ADVANCED_MINECART_CHARGER_ITEM);
            event.accept(ModBlocks.MINECART_UNCHARGER_ITEM);
            event.accept(ModBlocks.ADVANCED_MINECART_UNCHARGER_ITEM);

            event.accept(ModBlocks.ENERGIZER_ITEM);
            event.accept(ModBlocks.CHARGING_STATION_ITEM);
            event.accept(ModBlocks.CRYSTAL_GROWTH_CHAMBER);

            event.accept(ModBlocks.WEATHER_CONTROLLER_ITEM);
            event.accept(ModBlocks.TIME_CONTROLLER_ITEM);
            event.accept(ModBlocks.TELEPORTER_ITEM);

            addEmptyAndFullyChargedItem(event, ModItems.INVENTORY_COAL_ENGINE, InventoryCoalEngineItem.CAPACITY);
            event.accept(ModItems.INVENTORY_CHARGER);

            addEmptyAndFullyChargedItem(event, ModItems.INVENTORY_TELEPORTER, InventoryTeleporterItem.CAPACITY);

            addEmptyAndFullyChargedItem(event, ModItems.BATTERY_1, BatteryItem.Tier.BATTERY_1.getCapacity());
            addEmptyAndFullyChargedItem(event, ModItems.BATTERY_2, BatteryItem.Tier.BATTERY_2.getCapacity());
            addEmptyAndFullyChargedItem(event, ModItems.BATTERY_3, BatteryItem.Tier.BATTERY_3.getCapacity());
            addEmptyAndFullyChargedItem(event, ModItems.BATTERY_4, BatteryItem.Tier.BATTERY_4.getCapacity());
            addEmptyAndFullyChargedItem(event, ModItems.BATTERY_5, BatteryItem.Tier.BATTERY_5.getCapacity());
            addEmptyAndFullyChargedItem(event, ModItems.BATTERY_6, BatteryItem.Tier.BATTERY_6.getCapacity());
            addEmptyAndFullyChargedItem(event, ModItems.BATTERY_7, BatteryItem.Tier.BATTERY_7.getCapacity());
            addEmptyAndFullyChargedItem(event, ModItems.BATTERY_8, BatteryItem.Tier.BATTERY_8.getCapacity());
            event.accept(ModItems.CREATIVE_BATTERY);

            event.accept(ModItems.BATTERY_BOX_MINECART);
            event.accept(ModItems.ADVANCED_BATTERY_BOX_MINECART);

            event.accept(ModBlocks.BASIC_MACHINE_FRAME_ITEM);
            event.accept(ModBlocks.HARDENED_MACHINE_FRAME_ITEM);
            event.accept(ModBlocks.ADVANCED_MACHINE_FRAME_ITEM);
            event.accept(ModBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM);

            event.accept(ModItems.BASIC_SOLAR_CELL);
            event.accept(ModItems.ADVANCED_SOLAR_CELL);
            event.accept(ModItems.REINFORCED_ADVANCED_SOLAR_CELL);

            event.accept(ModItems.BASIC_CIRCUIT);
            event.accept(ModItems.ADVANCED_CIRCUIT);
            event.accept(ModItems.PROCESSING_UNIT);

            event.accept(ModItems.TELEPORTER_PROCESSING_UNIT);
            event.accept(ModItems.TELEPORTER_MATRIX);

            event.accept(ModItems.BASIC_UPGRADE_MODULE);
            event.accept(ModItems.ADVANCED_UPGRADE_MODULE);
            event.accept(ModItems.REINFORCED_ADVANCED_UPGRADE_MODULE);

            event.accept(ModItems.SPEED_UPGRADE_MODULE_1);
            event.accept(ModItems.SPEED_UPGRADE_MODULE_2);
            event.accept(ModItems.SPEED_UPGRADE_MODULE_3);
            event.accept(ModItems.SPEED_UPGRADE_MODULE_4);
            event.accept(ModItems.SPEED_UPGRADE_MODULE_5);

            event.accept(ModItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_1);
            event.accept(ModItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_2);
            event.accept(ModItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_3);
            event.accept(ModItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_4);
            event.accept(ModItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_5);

            event.accept(ModItems.ENERGY_CAPACITY_UPGRADE_MODULE_1);
            event.accept(ModItems.ENERGY_CAPACITY_UPGRADE_MODULE_2);
            event.accept(ModItems.ENERGY_CAPACITY_UPGRADE_MODULE_3);
            event.accept(ModItems.ENERGY_CAPACITY_UPGRADE_MODULE_4);
            event.accept(ModItems.ENERGY_CAPACITY_UPGRADE_MODULE_5);

            event.accept(ModItems.DURATION_UPGRADE_MODULE_1);
            event.accept(ModItems.DURATION_UPGRADE_MODULE_2);
            event.accept(ModItems.DURATION_UPGRADE_MODULE_3);
            event.accept(ModItems.DURATION_UPGRADE_MODULE_4);
            event.accept(ModItems.DURATION_UPGRADE_MODULE_5);
            event.accept(ModItems.DURATION_UPGRADE_MODULE_6);

            event.accept(ModItems.RANGE_UPGRADE_MODULE_1);
            event.accept(ModItems.RANGE_UPGRADE_MODULE_2);
            event.accept(ModItems.RANGE_UPGRADE_MODULE_3);

            event.accept(ModItems.EXTRACTION_DEPTH_UPGRADE_MODULE_1);
            event.accept(ModItems.EXTRACTION_DEPTH_UPGRADE_MODULE_2);
            event.accept(ModItems.EXTRACTION_DEPTH_UPGRADE_MODULE_3);
            event.accept(ModItems.EXTRACTION_DEPTH_UPGRADE_MODULE_4);
            event.accept(ModItems.EXTRACTION_DEPTH_UPGRADE_MODULE_5);

            event.accept(ModItems.BLAST_FURNACE_UPGRADE_MODULE);
            event.accept(ModItems.SMOKER_UPGRADE_MODULE);

            event.accept(ModItems.MOON_LIGHT_UPGRADE_MODULE_1);
            event.accept(ModItems.MOON_LIGHT_UPGRADE_MODULE_2);
            event.accept(ModItems.MOON_LIGHT_UPGRADE_MODULE_3);

            event.accept(ModBlocks.SILICON_BLOCK_ITEM);
            event.accept(ModBlocks.TIN_BLOCK_ITEM);
            event.accept(ModBlocks.SAWDUST_BLOCK_ITEM);
            event.accept(ModItems.CABLE_INSULATOR);
            event.accept(ModItems.CHARCOAL_FILTER);
            event.accept(ModItems.SAW_BLADE);
            event.accept(ModItems.CRYSTAL_MATRIX);
            event.accept(ModItems.SAWDUST);
            event.accept(ModItems.CHARCOAL_DUST);
            event.accept(ModItems.BASIC_FERTILIZER);
            event.accept(ModItems.GOOD_FERTILIZER);
            event.accept(ModItems.ADVANCED_FERTILIZER);
            event.accept(ModItems.RAW_GEAR_PRESS_MOLD);
            event.accept(ModItems.RAW_ROD_PRESS_MOLD);
            event.accept(ModItems.RAW_WIRE_PRESS_MOLD);
            event.accept(ModItems.GEAR_PRESS_MOLD);
            event.accept(ModItems.ROD_PRESS_MOLD);
            event.accept(ModItems.WIRE_PRESS_MOLD);
            event.accept(ModItems.SILICON);
            event.accept(ModItems.TIN_DUST);
            event.accept(ModItems.COPPER_DUST);
            event.accept(ModItems.IRON_DUST);
            event.accept(ModItems.GOLD_DUST);
            event.accept(ModItems.TIN_NUGGET);
            event.accept(ModItems.TIN_INGOT);
            event.accept(ModItems.TIN_PLATE);
            event.accept(ModItems.COPPER_PLATE);
            event.accept(ModItems.IRON_PLATE);
            event.accept(ModItems.GOLD_PLATE);
            event.accept(ModItems.STEEL_INGOT);
            event.accept(ModItems.IRON_GEAR);
            event.accept(ModItems.IRON_ROD);
            event.accept(ModItems.TIN_WIRE);
            event.accept(ModItems.COPPER_WIRE);
            event.accept(ModItems.GOLD_WIRE);
            event.accept(ModItems.ENERGIZED_COPPER_INGOT);
            event.accept(ModItems.ENERGIZED_GOLD_INGOT);
            event.accept(ModItems.ENERGIZED_COPPER_PLATE);
            event.accept(ModItems.ENERGIZED_GOLD_PLATE);
            event.accept(ModItems.ENERGIZED_COPPER_WIRE);
            event.accept(ModItems.ENERGIZED_GOLD_WIRE);
            event.accept(ModItems.ENERGIZED_CRYSTAL_MATRIX);

            event.accept(ModFluids.DIRTY_WATER_BUCKET_ITEM);

            event.accept(ModItems.STONE_PEBBLE);

            event.accept(ModItems.RAW_TIN);
            event.accept(ModBlocks.TIN_ORE_ITEM);
            event.accept(ModBlocks.DEEPSLATE_TIN_ORE_ITEM);
            event.accept(ModBlocks.RAW_TIN_BLOCK_ITEM);
        });
    }

    private void addCreativeTabFor(RegistryKey<ItemGroup> groupKey,
                                   Consumer<CreativeTabEntriesHelper> consumer) {
        ItemGroupEvents.modifyEntriesEvent(groupKey).
                register(entries -> consumer.accept(new CreativeTabEntriesHelper(entries)));
    }
}
