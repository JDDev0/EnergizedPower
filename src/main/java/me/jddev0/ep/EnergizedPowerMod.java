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
import net.minecraft.registry.RegistryKey;
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

	private void addEmptyAndFullyChargedItem(RegistryKey<ItemGroup> groupKey, Item item, long capacity) {
		addToCreativeTab(groupKey, item);
		addToCreativeTab(groupKey, getChargedItemStack(item, capacity));
	}

	private void addCreativeTab() {
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.ENERGIZED_POWER_BOOK);
		addEmptyAndFullyChargedItem(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.ENERGY_ANALYZER, EnergyAnalyzerItem.ENERGY_CAPACITY);
        addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.WOODEN_HAMMER);
        addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.STONE_HAMMER);
        addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.IRON_HAMMER);
        addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.GOLDEN_HAMMER);
        addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.DIAMOND_HAMMER);
        addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.NETHERITE_HAMMER);

		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.COPPER_CABLE_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.GOLD_CABLE_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.ENERGIZED_COPPER_CABLE_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.ENERGIZED_GOLD_CABLE_ITEM);

		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.TRANSFORMER_1_TO_N_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.TRANSFORMER_3_TO_3_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.TRANSFORMER_N_TO_1_ITEM);

		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.COAL_ENGINE_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.HEAT_GENERATOR_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.THERMAL_GENERATOR_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.LIGHTNING_GENERATOR_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.SOLAR_PANEL_ITEM_1);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.SOLAR_PANEL_ITEM_2);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.SOLAR_PANEL_ITEM_3);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.SOLAR_PANEL_ITEM_4);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.SOLAR_PANEL_ITEM_5);

		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.BATTERY_BOX_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.ADVANCED_BATTERY_BOX_ITEM);

		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.POWERED_FURNACE_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.AUTO_CRAFTER_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.CRUSHER_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.SAWMILL_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.COMPRESSOR_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.PLANT_GROWTH_CHAMBER_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.BLOCK_PLACER_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.CHARGER_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.UNCHARGER_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.MINECART_CHARGER_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.MINECART_UNCHARGER_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.ENERGIZER_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.CHARGING_STATION_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.WEATHER_CONTROLLER_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.TIME_CONTROLLER_ITEM);

		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.BASIC_SOLAR_CELL);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.ADVANCED_SOLAR_CELL);

		addEmptyAndFullyChargedItem(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.INVENTORY_COAL_ENGINE, InventoryCoalEngine.CAPACITY);

		addEmptyAndFullyChargedItem(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.BATTERY_1, BatteryItem.Tier.BATTERY_1.getCapacity());
		addEmptyAndFullyChargedItem(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.BATTERY_2, BatteryItem.Tier.BATTERY_2.getCapacity());
		addEmptyAndFullyChargedItem(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.BATTERY_3, BatteryItem.Tier.BATTERY_3.getCapacity());
		addEmptyAndFullyChargedItem(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.BATTERY_4, BatteryItem.Tier.BATTERY_4.getCapacity());
		addEmptyAndFullyChargedItem(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.BATTERY_5, BatteryItem.Tier.BATTERY_5.getCapacity());
		addEmptyAndFullyChargedItem(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.BATTERY_6, BatteryItem.Tier.BATTERY_6.getCapacity());
		addEmptyAndFullyChargedItem(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.BATTERY_7, BatteryItem.Tier.BATTERY_7.getCapacity());
		addEmptyAndFullyChargedItem(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.BATTERY_8, BatteryItem.Tier.BATTERY_8.getCapacity());

		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.BATTERY_BOX_MINECART);

		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.BASIC_MACHINE_FRAME_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.ADVANCED_MACHINE_FRAME_ITEM);

		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.SILICON_BLOCK_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModBlocks.SAWDUST_BLOCK_ITEM);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.CABLE_INSULATOR);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.SAW_BLADE);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.CRYSTAL_MATRIX);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.SAWDUST);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.BASIC_FERTILIZER);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.GOOD_FERTILIZER);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.ADVANCED_FERTILIZER);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.SILICON);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.COPPER_PLATE);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.IRON_PLATE);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.GOLD_PLATE);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.ENERGIZED_COPPER_INGOT);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.ENERGIZED_GOLD_INGOT);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.ENERGIZED_COPPER_PLATE);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.ENERGIZED_GOLD_PLATE);
		addToCreativeTab(ModCreativeModeTab.ENERGIZED_POWER_TAB_REG_KEY, ModItems.ENERGIZED_CRYSTAL_MATRIX);
	}

	private void addToCreativeTab(RegistryKey<ItemGroup> groupKey, Item item) {
		ItemGroupEvents.modifyEntriesEvent(groupKey).register(entries -> entries.add(item));
	}
	private void addToCreativeTab(RegistryKey<ItemGroup> groupKey, ItemStack itemStack) {
		ItemGroupEvents.modifyEntriesEvent(groupKey).register(entries -> entries.add(itemStack));
	}
}