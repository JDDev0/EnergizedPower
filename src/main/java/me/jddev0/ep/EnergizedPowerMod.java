package me.jddev0.ep;

import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.block.behavior.ModBlockBehaviors;
import me.jddev0.ep.block.entity.ModBlockEntities;
import me.jddev0.ep.entity.ModEntityTypes;
import me.jddev0.ep.event.PlayerInteractHandler;
import me.jddev0.ep.event.ServerStartingHandler;
import me.jddev0.ep.item.*;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.recipe.ModRecipes;
import me.jddev0.ep.screen.ModMenuTypes;
import me.jddev0.ep.villager.ModVillager;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.reborn.energy.api.base.SimpleEnergyItem;

public class EnergizedPowerMod implements ModInitializer {
	public static final String MODID = "energizedpower";
	public static final Logger LOGGER = LoggerFactory.getLogger("energizedpower");

	@Override
	public void onInitialize() {
		ModItems.register();
		ModBlocks.register();
		ModBlockEntities.register();
		ModRecipes.register();
		ModMenuTypes.register();
		ModVillager.register();
		ModEntityTypes.register();

		ModBlockBehaviors.register();

		ModCreativeModeTab.register();
		addCreativeTab();

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

	private void addEmptyAndFullyChargedItem(ItemGroup itemGroup, Item item, long capacity) {
		addToCreativeTab(itemGroup, item);
		addToCreativeTab(itemGroup, getChargedItemStack(item, capacity));
	}

	private void addCreativeTab() {
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModItems.ENERGIZED_POWER_BOOK);
		addEmptyAndFullyChargedItem(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModItems.ENERGY_ANALYZER, EnergyAnalyzerItem.ENERGY_CAPACITY);
        addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModItems.WOODEN_HAMMER);
        addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModItems.STONE_HAMMER);
        addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModItems.IRON_HAMMER);
        addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModItems.GOLDEN_HAMMER);
        addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModItems.DIAMOND_HAMMER);
        addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModItems.NETHERITE_HAMMER);

		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModBlocks.COPPER_CABLE_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModBlocks.GOLD_CABLE_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModBlocks.ENERGIZED_COPPER_CABLE_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModBlocks.ENERGIZED_GOLD_CABLE_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModBlocks.ENERGIZED_CRYSTAL_MATRIX_CABLE_ITEM);

		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModBlocks.MV_TRANSFORMER_1_TO_N_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModBlocks.MV_TRANSFORMER_3_TO_3_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModBlocks.MV_TRANSFORMER_N_TO_1_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModBlocks.HV_TRANSFORMER_1_TO_N_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModBlocks.HV_TRANSFORMER_3_TO_3_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModBlocks.HV_TRANSFORMER_N_TO_1_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModBlocks.EHV_TRANSFORMER_1_TO_N_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModBlocks.EHV_TRANSFORMER_3_TO_3_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModBlocks.EHV_TRANSFORMER_N_TO_1_ITEM);

		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModBlocks.COAL_ENGINE_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModBlocks.HEAT_GENERATOR_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModBlocks.THERMAL_GENERATOR_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModBlocks.LIGHTNING_GENERATOR_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModBlocks.SOLAR_PANEL_ITEM_1);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModBlocks.SOLAR_PANEL_ITEM_2);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModBlocks.SOLAR_PANEL_ITEM_3);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModBlocks.SOLAR_PANEL_ITEM_4);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModBlocks.SOLAR_PANEL_ITEM_5);

		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModBlocks.BATTERY_BOX_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModBlocks.ADVANCED_BATTERY_BOX_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModBlocks.CREATIVE_BATTERY_BOX_ITEM);

		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModBlocks.POWERED_FURNACE_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModBlocks.AUTO_CRAFTER_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModBlocks.CRUSHER_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModBlocks.SAWMILL_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModBlocks.COMPRESSOR_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModBlocks.PLANT_GROWTH_CHAMBER_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModBlocks.BLOCK_PLACER_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModBlocks.FLUID_FILLER_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModBlocks.FLUID_DRAINER_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModBlocks.CHARGER_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModBlocks.ADVANCED_CHARGER_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModBlocks.UNCHARGER_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModBlocks.ADVANCED_UNCHARGER_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModBlocks.MINECART_CHARGER_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModBlocks.ADVANCED_MINECART_CHARGER_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModBlocks.MINECART_UNCHARGER_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModBlocks.ADVANCED_MINECART_UNCHARGER_ITEM);

		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModBlocks.ENERGIZER_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModBlocks.CHARGING_STATION_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModBlocks.WEATHER_CONTROLLER_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModBlocks.TIME_CONTROLLER_ITEM);

		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModItems.BASIC_SOLAR_CELL);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModItems.ADVANCED_SOLAR_CELL);

		addEmptyAndFullyChargedItem(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModItems.INVENTORY_COAL_ENGINE, InventoryCoalEngine.CAPACITY);

		addEmptyAndFullyChargedItem(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModItems.BATTERY_1, BatteryItem.Tier.BATTERY_1.getCapacity());
		addEmptyAndFullyChargedItem(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModItems.BATTERY_2, BatteryItem.Tier.BATTERY_2.getCapacity());
		addEmptyAndFullyChargedItem(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModItems.BATTERY_3, BatteryItem.Tier.BATTERY_3.getCapacity());
		addEmptyAndFullyChargedItem(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModItems.BATTERY_4, BatteryItem.Tier.BATTERY_4.getCapacity());
		addEmptyAndFullyChargedItem(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModItems.BATTERY_5, BatteryItem.Tier.BATTERY_5.getCapacity());
		addEmptyAndFullyChargedItem(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModItems.BATTERY_6, BatteryItem.Tier.BATTERY_6.getCapacity());
		addEmptyAndFullyChargedItem(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModItems.BATTERY_7, BatteryItem.Tier.BATTERY_7.getCapacity());
		addEmptyAndFullyChargedItem(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModItems.BATTERY_8, BatteryItem.Tier.BATTERY_8.getCapacity());
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModItems.CREATIVE_BATTERY);

		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModItems.BATTERY_BOX_MINECART);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModItems.ADVANCED_BATTERY_BOX_MINECART);

		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModBlocks.BASIC_MACHINE_FRAME_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModBlocks.ADVANCED_MACHINE_FRAME_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM);

		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModBlocks.SILICON_BLOCK_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModBlocks.SAWDUST_BLOCK_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModItems.CABLE_INSULATOR);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModItems.SAW_BLADE);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModItems.CRYSTAL_MATRIX);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModItems.SAWDUST);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModItems.BASIC_FERTILIZER);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModItems.GOOD_FERTILIZER);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModItems.ADVANCED_FERTILIZER);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModItems.SILICON);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModItems.COPPER_PLATE);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModItems.IRON_PLATE);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModItems.GOLD_PLATE);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModItems.ENERGIZED_COPPER_INGOT);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModItems.ENERGIZED_GOLD_INGOT);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModItems.ENERGIZED_COPPER_PLATE);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModItems.ENERGIZED_GOLD_PLATE);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB, ModItems.ENERGIZED_CRYSTAL_MATRIX);
	}

	private void addToCreativeTab(ItemGroup group, Item item) {
		ItemGroupEvents.modifyEntriesEvent(group).register(entries -> entries.add(item));
	}
	private void addToCreativeTab(ItemGroup group, ItemStack itemStack) {
		ItemGroupEvents.modifyEntriesEvent(group).register(entries -> entries.add(itemStack));
	}
}