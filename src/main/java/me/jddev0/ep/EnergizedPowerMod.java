package me.jddev0.ep;

import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.block.behavior.ModBlockBehaviors;
import me.jddev0.ep.block.entity.ModBlockEntities;
import me.jddev0.ep.component.ModDataComponentTypes;
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
import net.minecraft.registry.RegistryKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnergizedPowerMod implements ModInitializer {
	public static final String MODID = "energizedpower";
	public static final Logger LOGGER = LoggerFactory.getLogger("energizedpower");

	@Override
	public void onInitialize() {
        ModConfigs.registerConfigs(true);

		ModDataComponentTypes.register();

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

		ModMessages.registerTypedPayloads();
		ModMessages.registerPacketsC2S();

		PlayerInteractHandler.EVENT.register(new PlayerInteractHandler());
		ServerLifecycleEvents.SERVER_STARTING.register(new ServerStartingHandler());

		FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.SAWDUST_BLOCK, 5, 20);
	}

	private ItemStack getChargedItemStack(Item item, long energy) {
		ItemStack itemStack = new ItemStack(item);
		itemStack.set(ModDataComponentTypes.ENERGY, energy);

		return itemStack;
	}

	private void addEmptyAndFullyChargedItem(RegistryKey<ItemGroup> groupKey, Item item, long capacity) {
		addToCreativeTab(groupKey, item);
		addToCreativeTab(groupKey, getChargedItemStack(item, capacity));
	}

	private void addCreativeTab() {
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.ENERGIZED_POWER_BOOK);
		addEmptyAndFullyChargedItem(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.ENERGY_ANALYZER, EnergyAnalyzerItem.ENERGY_CAPACITY);
		addEmptyAndFullyChargedItem(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.FLUID_ANALYZER, FluidAnalyzerItem.ENERGY_CAPACITY);
        addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.WOODEN_HAMMER);
        addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.STONE_HAMMER);
        addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.IRON_HAMMER);
        addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.GOLDEN_HAMMER);
        addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.DIAMOND_HAMMER);
        addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.NETHERITE_HAMMER);

        addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.CUTTER);

        addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.WRENCH);

        addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.ITEM_CONVEYOR_BELT_ITEM);
        addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.ITEM_CONVEYOR_BELT_LOADER_ITEM);
        addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.ITEM_CONVEYOR_BELT_SORTER_ITEM);
        addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.ITEM_CONVEYOR_BELT_SWITCH_ITEM);
        addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.ITEM_CONVEYOR_BELT_SPLITTER_ITEM);
        addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.ITEM_CONVEYOR_BELT_MERGER_ITEM);

        addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.IRON_FLUID_PIPE_ITEM);
        addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.GOLDEN_FLUID_PIPE_ITEM);

		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.TIN_CABLE_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.COPPER_CABLE_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.GOLD_CABLE_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.ENERGIZED_COPPER_CABLE_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.ENERGIZED_GOLD_CABLE_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.ENERGIZED_CRYSTAL_MATRIX_CABLE_ITEM);

		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.LV_TRANSFORMER_1_TO_N_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.LV_TRANSFORMER_3_TO_3_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.LV_TRANSFORMER_N_TO_1_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.MV_TRANSFORMER_1_TO_N_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.MV_TRANSFORMER_3_TO_3_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.MV_TRANSFORMER_N_TO_1_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.HV_TRANSFORMER_1_TO_N_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.HV_TRANSFORMER_3_TO_3_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.HV_TRANSFORMER_N_TO_1_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.EHV_TRANSFORMER_1_TO_N_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.EHV_TRANSFORMER_3_TO_3_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.EHV_TRANSFORMER_N_TO_1_ITEM);

		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.PRESS_MOLD_MAKER_ITEM);

		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.COAL_ENGINE_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.HEAT_GENERATOR_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.THERMAL_GENERATOR_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.LIGHTNING_GENERATOR_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.SOLAR_PANEL_ITEM_1);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.SOLAR_PANEL_ITEM_2);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.SOLAR_PANEL_ITEM_3);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.SOLAR_PANEL_ITEM_4);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.SOLAR_PANEL_ITEM_5);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.SOLAR_PANEL_ITEM_6);

		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.BATTERY_BOX_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.ADVANCED_BATTERY_BOX_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.CREATIVE_BATTERY_BOX_ITEM);

		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.POWERED_LAMP_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.POWERED_FURNACE_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.ADVANCED_POWERED_FURNACE_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.AUTO_CRAFTER_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.ADVANCED_AUTO_CRAFTER_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.CRUSHER_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.ADVANCED_CRUSHER_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.PULVERIZER_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.ADVANCED_PULVERIZER_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.SAWMILL_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.COMPRESSOR_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.METAL_PRESS_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.AUTO_PRESS_MOLD_MAKER_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.AUTO_STONECUTTER_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.ASSEMBLING_MACHINE_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.PLANT_GROWTH_CHAMBER_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.STONE_SOLIDIFIER_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.FILTRATION_PLANT_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.BLOCK_PLACER_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.FLUID_TANK_SMALL_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.FLUID_TANK_MEDIUM_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.FLUID_TANK_LARGE_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.FLUID_FILLER_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.FLUID_DRAINER_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.DRAIN_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.CHARGER_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.ADVANCED_CHARGER_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.UNCHARGER_ITEM);
        addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.ADVANCED_UNCHARGER_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.MINECART_CHARGER_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.ADVANCED_MINECART_CHARGER_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.MINECART_UNCHARGER_ITEM);
        addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.ADVANCED_MINECART_UNCHARGER_ITEM);

		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.ENERGIZER_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.CHARGING_STATION_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.CRYSTAL_GROWTH_CHAMBER_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.WEATHER_CONTROLLER_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.TIME_CONTROLLER_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.TELEPORTER_ITEM);

		addEmptyAndFullyChargedItem(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.INVENTORY_COAL_ENGINE, InventoryCoalEngineItem.CAPACITY);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.INVENTORY_CHARGER);

		addEmptyAndFullyChargedItem(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.BATTERY_1, BatteryItem.Tier.BATTERY_1.getCapacity());
		addEmptyAndFullyChargedItem(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.BATTERY_2, BatteryItem.Tier.BATTERY_2.getCapacity());
		addEmptyAndFullyChargedItem(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.BATTERY_3, BatteryItem.Tier.BATTERY_3.getCapacity());
		addEmptyAndFullyChargedItem(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.BATTERY_4, BatteryItem.Tier.BATTERY_4.getCapacity());
		addEmptyAndFullyChargedItem(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.BATTERY_5, BatteryItem.Tier.BATTERY_5.getCapacity());
		addEmptyAndFullyChargedItem(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.BATTERY_6, BatteryItem.Tier.BATTERY_6.getCapacity());
		addEmptyAndFullyChargedItem(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.BATTERY_7, BatteryItem.Tier.BATTERY_7.getCapacity());
		addEmptyAndFullyChargedItem(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.BATTERY_8, BatteryItem.Tier.BATTERY_8.getCapacity());
        addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.CREATIVE_BATTERY);

		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.BATTERY_BOX_MINECART);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.ADVANCED_BATTERY_BOX_MINECART);

		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.BASIC_MACHINE_FRAME_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.HARDENED_MACHINE_FRAME_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.ADVANCED_MACHINE_FRAME_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM);

		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.BASIC_SOLAR_CELL);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.ADVANCED_SOLAR_CELL);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.REINFORCED_ADVANCED_SOLAR_CELL);

		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.BASIC_CIRCUIT);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.ADVANCED_CIRCUIT);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.PROCESSING_UNIT);

		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.TELEPORTER_PROCESSING_UNIT);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.TELEPORTER_MATRIX);

		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.BASIC_UPGRADE_MODULE);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.ADVANCED_UPGRADE_MODULE);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.REINFORCED_ADVANCED_UPGRADE_MODULE);

		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.SPEED_UPGRADE_MODULE_1);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.SPEED_UPGRADE_MODULE_2);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.SPEED_UPGRADE_MODULE_3);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.SPEED_UPGRADE_MODULE_4);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.SPEED_UPGRADE_MODULE_5);

		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_1);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_2);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_3);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_4);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_5);

		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.ENERGY_CAPACITY_UPGRADE_MODULE_1);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.ENERGY_CAPACITY_UPGRADE_MODULE_2);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.ENERGY_CAPACITY_UPGRADE_MODULE_3);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.ENERGY_CAPACITY_UPGRADE_MODULE_4);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.ENERGY_CAPACITY_UPGRADE_MODULE_5);

		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.DURATION_UPGRADE_MODULE_1);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.DURATION_UPGRADE_MODULE_2);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.DURATION_UPGRADE_MODULE_3);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.DURATION_UPGRADE_MODULE_4);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.DURATION_UPGRADE_MODULE_5);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.DURATION_UPGRADE_MODULE_6);

		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.RANGE_UPGRADE_MODULE_1);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.RANGE_UPGRADE_MODULE_2);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.RANGE_UPGRADE_MODULE_3);

		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.BLAST_FURNACE_UPGRADE_MODULE);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.SMOKER_UPGRADE_MODULE);

		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.MOON_LIGHT_UPGRADE_MODULE_1);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.MOON_LIGHT_UPGRADE_MODULE_2);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.MOON_LIGHT_UPGRADE_MODULE_3);

		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.SILICON_BLOCK_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.TIN_BLOCK_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.SAWDUST_BLOCK_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.CABLE_INSULATOR);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.CHARCOAL_FILTER);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.SAW_BLADE);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.CRYSTAL_MATRIX);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.SAWDUST);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.CHARCOAL_DUST);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.BASIC_FERTILIZER);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.GOOD_FERTILIZER);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.ADVANCED_FERTILIZER);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.RAW_GEAR_PRESS_MOLD);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.RAW_ROD_PRESS_MOLD);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.RAW_WIRE_PRESS_MOLD);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.GEAR_PRESS_MOLD);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.ROD_PRESS_MOLD);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.WIRE_PRESS_MOLD);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.SILICON);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.TIN_DUST);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.COPPER_DUST);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.IRON_DUST);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.GOLD_DUST);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.TIN_NUGGET);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.TIN_INGOT);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.TIN_PLATE);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.COPPER_PLATE);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.IRON_PLATE);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.GOLD_PLATE);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.IRON_GEAR);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.IRON_ROD);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.TIN_WIRE);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.COPPER_WIRE);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.GOLD_WIRE);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.ENERGIZED_COPPER_INGOT);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.ENERGIZED_GOLD_INGOT);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.ENERGIZED_COPPER_PLATE);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.ENERGIZED_GOLD_PLATE);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.ENERGIZED_COPPER_WIRE);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.ENERGIZED_GOLD_WIRE);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.ENERGIZED_CRYSTAL_MATRIX);

		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModFluids.DIRTY_WATER_BUCKET_ITEM);

		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.RAW_TIN);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.TIN_ORE_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.DEEPSLATE_TIN_ORE_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.RAW_TIN_BLOCK_ITEM);
	}

	private void addToCreativeTab(RegistryKey<ItemGroup> groupKey, Item item) {
		ItemGroupEvents.modifyEntriesEvent(groupKey).register(entries -> entries.add(item));
	}
	private void addToCreativeTab(RegistryKey<ItemGroup> groupKey, ItemStack itemStack) {
		ItemGroupEvents.modifyEntriesEvent(groupKey).register(entries -> entries.add(itemStack));
	}
}