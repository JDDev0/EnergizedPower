package me.jddev0.ep.datagen.advancement;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.item.BatteryItem;
import me.jddev0.ep.item.ModItems;
import me.jddev0.ep.registry.tags.CommonItemTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import team.reborn.energy.api.base.SimpleEnergyItem;

import java.util.function.Consumer;

public class ModAdvancedAdvancements extends FabricAdvancementProvider {
    public ModAdvancedAdvancements(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void generateAdvancement(Consumer<AdvancementEntry> advancementOutput) {
        AdvancementEntry energizedPowerAdvanced = Advancement.Builder.create().
                display(
                        ModItems.ENERGIZED_COPPER_INGOT,
                        Text.translatable("advancements.energizedpower.energizedpower_advanced.title"),
                        Text.translatable("advancements.energizedpower.energizedpower_advanced.description"),
                        Identifier.of(EnergizedPowerMod.MODID, "textures/block/advanced_machine_frame_top.png"),
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                ).
                criterion("has_the_item",
                        InventoryChangedCriterion.Conditions.items(ItemPredicate.Builder.create().tag(
                                CommonItemTags.ENERGIZED_COPPER_INGOTS
                        ))).
                build(advancementOutput, EnergizedPowerMod.MODID + ":main/advanced/energizedpower_advanced");

        AdvancementEntry advancedAlloyIngot = addAdvancement(
                advancementOutput, energizedPowerAdvanced,
                ModItems.ADVANCED_ALLOY_INGOT, "advanced_alloy_ingot", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(ItemPredicate.Builder.create().tag(
                        CommonItemTags.ADVANCED_ALLOY_INGOTS
                ))
        );

        AdvancementEntry advancedAlloyPlate = addAdvancement(
                advancementOutput, advancedAlloyIngot,
                ModItems.ADVANCED_ALLOY_PLATE, "advanced_alloy_plate", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(ItemPredicate.Builder.create().tag(
                        CommonItemTags.ADVANCED_ALLOY_PLATES
                ))
        );

        AdvancementEntry energizedCopperCable = addAdvancement(
                advancementOutput, energizedPowerAdvanced,
                ModBlocks.ENERGIZED_COPPER_CABLE_ITEM, "energized_copper_cable", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(ModBlocks.ENERGIZED_COPPER_CABLE_ITEM)
        );

        AdvancementEntry advancedSolarCell = addAdvancement(
                advancementOutput, energizedPowerAdvanced,
                ModItems.ADVANCED_SOLAR_CELL, "advanced_solar_cell", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(ModItems.ADVANCED_SOLAR_CELL)
        );

        AdvancementEntry solarPanel4 = addAdvancement(
                advancementOutput, advancedSolarCell,
                ModBlocks.SOLAR_PANEL_ITEM_4, "solar_panel_4", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(ModBlocks.SOLAR_PANEL_ITEM_4)
        );

        AdvancementEntry solarPanel5 = addAdvancement(
                advancementOutput, solarPanel4,
                ModBlocks.SOLAR_PANEL_ITEM_5, "solar_panel_5", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(ModBlocks.SOLAR_PANEL_ITEM_5)
        );

        AdvancementEntry battery6 = addAdvancement(
                advancementOutput, energizedPowerAdvanced,
                ModItems.BATTERY_6, "battery_6", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(ModItems.BATTERY_6)
        );

        AdvancementEntry battery7 = addAdvancement(
                advancementOutput, battery6,
                ModItems.BATTERY_7, "battery_7", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(ModItems.BATTERY_7)
        );

        AdvancementEntry battery8 = addAdvancement(
                advancementOutput, battery7,
                ModItems.BATTERY_8, "battery_8", AdvancementFrame.GOAL,
                InventoryChangedCriterion.Conditions.items(ModItems.BATTERY_8)
        );

        ItemStack battery8FullyChargedIcon = new ItemStack(ModItems.BATTERY_8);
        NbtCompound nbt = battery8FullyChargedIcon.getOrCreateNbt();
        nbt.putLong(SimpleEnergyItem.ENERGY_KEY, BatteryItem.Tier.BATTERY_8.getCapacity());
        AdvancementEntry battery8FullyCharged = addAdvancement(
                advancementOutput, battery8,
                battery8FullyChargedIcon, "battery_8_fully_charged", AdvancementFrame.CHALLENGE,
                InventoryChangedCriterion.Conditions.items(ItemPredicate.Builder.create().
                        items(ModItems.BATTERY_8).
                        nbt(nbt.copy()).
                        build())
        );

        AdvancementEntry advancedBatteryBox = addAdvancement(
                advancementOutput, battery8,
                ModBlocks.ADVANCED_BATTERY_BOX_ITEM, "advanced_battery_box", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(ModBlocks.ADVANCED_BATTERY_BOX_ITEM)
        );

        AdvancementEntry advancedBatteryBoxMinecart = addAdvancement(
                advancementOutput, advancedBatteryBox,
                ModItems.ADVANCED_BATTERY_BOX_MINECART, "advanced_battery_box_minecart", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(ModItems.ADVANCED_BATTERY_BOX_MINECART)
        );

        AdvancementEntry energizedCopperPlate = addAdvancement(
                advancementOutput, energizedPowerAdvanced,
                ModItems.ENERGIZED_COPPER_PLATE, "energized_copper_plate", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(ItemPredicate.Builder.create().tag(
                        CommonItemTags.ENERGIZED_COPPER_PLATES
                ))
        );

        AdvancementEntry energizedCopperWire = addAdvancement(
                advancementOutput, energizedCopperPlate,
                ModItems.ENERGIZED_COPPER_WIRE, "energized_copper_wire", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(ItemPredicate.Builder.create().tag(
                        CommonItemTags.ENERGIZED_COPPER_WIRES
                ))
        );

        AdvancementEntry advancedCircuit = addAdvancement(
                advancementOutput, energizedCopperWire,
                ModItems.ADVANCED_CIRCUIT, "advanced_circuit", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(ModItems.ADVANCED_CIRCUIT)
        );

        AdvancementEntry advancedUpgradeModule = addAdvancement(
                advancementOutput, advancedCircuit,
                ModItems.ADVANCED_UPGRADE_MODULE, "advanced_upgrade_module", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(ModItems.ADVANCED_UPGRADE_MODULE)
        );

        AdvancementEntry speedUpgradeUpgradeModule3 = addAdvancement(
                advancementOutput, advancedUpgradeModule,
                ModItems.SPEED_UPGRADE_MODULE_3, "speed_upgrade_module_3", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(ModItems.SPEED_UPGRADE_MODULE_3)
        );

        AdvancementEntry speedUpgradeUpgradeModule4 = addAdvancement(
                advancementOutput, speedUpgradeUpgradeModule3,
                ModItems.SPEED_UPGRADE_MODULE_4, "speed_upgrade_module_4", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(ModItems.SPEED_UPGRADE_MODULE_4)
        );

        AdvancementEntry energyEfficiencyUpgradeModule3 = addAdvancement(
                advancementOutput, advancedUpgradeModule,
                ModItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_3, "energy_efficiency_upgrade_module_3", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(ModItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_3)
        );

        AdvancementEntry energyEfficiencyUpgradeModule4 = addAdvancement(
                advancementOutput, energyEfficiencyUpgradeModule3,
                ModItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_4, "energy_efficiency_upgrade_module_4", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(ModItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_4)
        );

        AdvancementEntry energyCapacityUpgradeModule3 = addAdvancement(
                advancementOutput, advancedUpgradeModule,
                ModItems.ENERGY_CAPACITY_UPGRADE_MODULE_3, "energy_capacity_upgrade_module_3", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(ModItems.ENERGY_CAPACITY_UPGRADE_MODULE_3)
        );

        AdvancementEntry energyCapacityUpgradeModule4 = addAdvancement(
                advancementOutput, energyCapacityUpgradeModule3,
                ModItems.ENERGY_CAPACITY_UPGRADE_MODULE_4, "energy_capacity_upgrade_module_4", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(ModItems.ENERGY_CAPACITY_UPGRADE_MODULE_4)
        );

        AdvancementEntry rangeUpgradeModule1 = addAdvancement(
                advancementOutput, advancedUpgradeModule,
                ModItems.RANGE_UPGRADE_MODULE_1, "range_upgrade_module_1", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(ModItems.RANGE_UPGRADE_MODULE_1)
        );

        AdvancementEntry rangeUpgradeModule2 = addAdvancement(
                advancementOutput, rangeUpgradeModule1,
                ModItems.RANGE_UPGRADE_MODULE_2, "range_upgrade_module_2", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(ModItems.RANGE_UPGRADE_MODULE_2)
        );

        AdvancementEntry rangeUpgradeModule3 = addAdvancement(
                advancementOutput, rangeUpgradeModule2,
                ModItems.RANGE_UPGRADE_MODULE_3, "range_upgrade_module_3", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(ModItems.RANGE_UPGRADE_MODULE_3)
        );

        AdvancementEntry extractionDepthUpgradeModule3 = addAdvancement(
                advancementOutput, advancedUpgradeModule,
                ModItems.EXTRACTION_DEPTH_UPGRADE_MODULE_3, "extraction_depth_upgrade_module_3", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(ModItems.EXTRACTION_DEPTH_UPGRADE_MODULE_3)
        );

        AdvancementEntry extractionDepthUpgradeModule4 = addAdvancement(
                advancementOutput, extractionDepthUpgradeModule3,
                ModItems.EXTRACTION_DEPTH_UPGRADE_MODULE_4, "extraction_depth_upgrade_module_4", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(ModItems.EXTRACTION_DEPTH_UPGRADE_MODULE_4)
        );

        AdvancementEntry moonLightUpgradeModule2 = addAdvancement(
                advancementOutput, advancedUpgradeModule,
                ModItems.MOON_LIGHT_UPGRADE_MODULE_2, "moon_light_upgrade_module_2", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(ModItems.MOON_LIGHT_UPGRADE_MODULE_2)
        );

        AdvancementEntry advancedMachineFrame = addAdvancement(
                advancementOutput, energizedPowerAdvanced,
                ModBlocks.ADVANCED_MACHINE_FRAME_ITEM, "advanced_machine_frame", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(ModBlocks.ADVANCED_MACHINE_FRAME_ITEM)
        );

        AdvancementEntry hvTransformers = addAdvancement(
                advancementOutput, advancedMachineFrame,
                ModBlocks.HV_TRANSFORMER_1_TO_N_ITEM, "hv_transformers", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(
                        ModBlocks.HV_TRANSFORMER_1_TO_N_ITEM,
                        ModBlocks.HV_TRANSFORMER_3_TO_3_ITEM,
                        ModBlocks.HV_TRANSFORMER_N_TO_1_ITEM
                )
        );

        AdvancementEntry advancedCrusher = addAdvancement(
                advancementOutput, advancedMachineFrame,
                ModBlocks.ADVANCED_CRUSHER_ITEM, "advanced_crusher", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(ModBlocks.ADVANCED_CRUSHER_ITEM)
        );

        AdvancementEntry advancedPulverizer = addAdvancement(
                advancementOutput, advancedMachineFrame,
                ModBlocks.ADVANCED_PULVERIZER_ITEM, "advanced_pulverizer", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(ModBlocks.ADVANCED_PULVERIZER_ITEM)
        );

        AdvancementEntry lightningGenerator = addAdvancement(
                advancementOutput, advancedMachineFrame,
                ModBlocks.LIGHTNING_GENERATOR_ITEM, "lightning_generator", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(ModBlocks.LIGHTNING_GENERATOR_ITEM)
        );

        AdvancementEntry crystalGrowthChamber = addAdvancement(
                advancementOutput, advancedMachineFrame,
                ModBlocks.CRYSTAL_GROWTH_CHAMBER_ITEM, "crystal_growth_chamber", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(ModBlocks.CRYSTAL_GROWTH_CHAMBER_ITEM)
        );

        AdvancementEntry energizer = addAdvancement(
                advancementOutput, advancedMachineFrame,
                ModBlocks.ENERGIZER_ITEM, "energizer", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(ModBlocks.ENERGIZER_ITEM)
        );

        AdvancementEntry energizedGoldIngot = addAdvancement(
                advancementOutput, energizer,
                ModItems.ENERGIZED_GOLD_INGOT, "energized_gold_ingot", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(ItemPredicate.Builder.create().tag(
                        CommonItemTags.ENERGIZED_GOLD_INGOTS
                ))
        );

        AdvancementEntry energizedGoldCable = addAdvancement(
                advancementOutput, energizedGoldIngot,
                ModBlocks.ENERGIZED_GOLD_CABLE_ITEM, "energized_gold_cable", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(ModBlocks.ENERGIZED_GOLD_CABLE_ITEM)
        );

        AdvancementEntry energizedGoldPlate = addAdvancement(
                advancementOutput, energizedGoldIngot,
                ModItems.ENERGIZED_GOLD_PLATE, "energized_gold_plate", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(ItemPredicate.Builder.create().tag(
                        CommonItemTags.ENERGIZED_GOLD_PLATES
                ))
        );

        AdvancementEntry energizedGoldWire = addAdvancement(
                advancementOutput, energizedGoldPlate,
                ModItems.ENERGIZED_GOLD_WIRE, "energized_gold_wire", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(ItemPredicate.Builder.create().tag(
                        CommonItemTags.ENERGIZED_GOLD_WIRES
                ))
        );

        AdvancementEntry processingUnit = addAdvancement(
                advancementOutput, energizedGoldWire,
                ModItems.PROCESSING_UNIT, "processing_unit", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(ModItems.PROCESSING_UNIT)
        );

        AdvancementEntry reinforcedAdvancedUpgradeModule = addAdvancement(
                advancementOutput, processingUnit,
                ModItems.REINFORCED_ADVANCED_UPGRADE_MODULE, "reinforced_advanced_upgrade_module", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(ModItems.REINFORCED_ADVANCED_UPGRADE_MODULE)
        );

        AdvancementEntry speedUpgradeUpgradeModule5 = addAdvancement(
                advancementOutput, reinforcedAdvancedUpgradeModule,
                ModItems.SPEED_UPGRADE_MODULE_5, "speed_upgrade_module_5", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(ModItems.SPEED_UPGRADE_MODULE_5)
        );

        AdvancementEntry energyEfficiencyUpgradeModule5 = addAdvancement(
                advancementOutput, reinforcedAdvancedUpgradeModule,
                ModItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_5, "energy_efficiency_upgrade_module_5", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(ModItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_5)
        );

        AdvancementEntry energyCapacityUpgradeModule5 = addAdvancement(
                advancementOutput, reinforcedAdvancedUpgradeModule,
                ModItems.ENERGY_CAPACITY_UPGRADE_MODULE_5, "energy_capacity_upgrade_module_5", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(ModItems.ENERGY_CAPACITY_UPGRADE_MODULE_5)
        );

        AdvancementEntry durationUpgradeModule1 = addAdvancement(
                advancementOutput, reinforcedAdvancedUpgradeModule,
                ModItems.DURATION_UPGRADE_MODULE_1, "duration_upgrade_module_1", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(ModItems.DURATION_UPGRADE_MODULE_1)
        );

        AdvancementEntry durationUpgradeModule2 = addAdvancement(
                advancementOutput, durationUpgradeModule1,
                ModItems.DURATION_UPGRADE_MODULE_2, "duration_upgrade_module_2", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(ModItems.DURATION_UPGRADE_MODULE_2)
        );

        AdvancementEntry durationUpgradeModule3 = addAdvancement(
                advancementOutput, durationUpgradeModule2,
                ModItems.DURATION_UPGRADE_MODULE_3, "duration_upgrade_module_3", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(ModItems.DURATION_UPGRADE_MODULE_3)
        );

        AdvancementEntry durationUpgradeModule4 = addAdvancement(
                advancementOutput, durationUpgradeModule3,
                ModItems.DURATION_UPGRADE_MODULE_4, "duration_upgrade_module_4", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(ModItems.DURATION_UPGRADE_MODULE_4)
        );

        AdvancementEntry durationUpgradeModule5 = addAdvancement(
                advancementOutput, durationUpgradeModule4,
                ModItems.DURATION_UPGRADE_MODULE_5, "duration_upgrade_module_5", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(ModItems.DURATION_UPGRADE_MODULE_5)
        );

        AdvancementEntry durationUpgradeModule6 = addAdvancement(
                advancementOutput, durationUpgradeModule5,
                ModItems.DURATION_UPGRADE_MODULE_6, "duration_upgrade_module_6", AdvancementFrame.CHALLENGE,
                InventoryChangedCriterion.Conditions.items(ModItems.DURATION_UPGRADE_MODULE_6)
        );

        AdvancementEntry extractionDepthUpgradeModule5 = addAdvancement(
                advancementOutput, reinforcedAdvancedUpgradeModule,
                ModItems.EXTRACTION_DEPTH_UPGRADE_MODULE_5, "extraction_depth_upgrade_module_5", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(ModItems.EXTRACTION_DEPTH_UPGRADE_MODULE_5)
        );

        AdvancementEntry moonLightUpgradeModule3 = addAdvancement(
                advancementOutput, reinforcedAdvancedUpgradeModule,
                ModItems.MOON_LIGHT_UPGRADE_MODULE_3, "moon_light_upgrade_module_3", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(ModItems.MOON_LIGHT_UPGRADE_MODULE_3)
        );

        AdvancementEntry advancedAutoCrafter = addAdvancement(
                advancementOutput, energizedGoldIngot,
                ModBlocks.ADVANCED_AUTO_CRAFTER_ITEM, "advanced_auto_crafter", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(ModBlocks.ADVANCED_AUTO_CRAFTER_ITEM)
        );

        AdvancementEntry advancedPoweredFurnace = addAdvancement(
                advancementOutput, energizedGoldIngot,
                ModBlocks.ADVANCED_POWERED_FURNACE_ITEM, "advanced_powered_furnace", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(ModBlocks.ADVANCED_POWERED_FURNACE_ITEM)
        );

        AdvancementEntry chargingStation = addAdvancement(
                advancementOutput, energizedGoldIngot,
                ModBlocks.CHARGING_STATION_ITEM, "charging_station", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(ModBlocks.CHARGING_STATION_ITEM)
        );

        AdvancementEntry advancedCharger = addAdvancement(
                advancementOutput, energizedGoldIngot,
                ModBlocks.ADVANCED_CHARGER_ITEM, "advanced_charger", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(ModBlocks.ADVANCED_CHARGER_ITEM)
        );

        AdvancementEntry advancedMinecartCharger = addAdvancement(
                advancementOutput, advancedCharger,
                ModBlocks.ADVANCED_MINECART_CHARGER_ITEM, "advanced_minecart_charger", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(ModBlocks.ADVANCED_MINECART_CHARGER_ITEM)
        );

        AdvancementEntry advancedUncharger = addAdvancement(
                advancementOutput, energizedGoldIngot,
                ModBlocks.ADVANCED_UNCHARGER_ITEM, "advanced_uncharger", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(ModBlocks.ADVANCED_UNCHARGER_ITEM)
        );

        AdvancementEntry advancedMinecartUncharger = addAdvancement(
                advancementOutput, advancedUncharger,
                ModBlocks.ADVANCED_MINECART_UNCHARGER_ITEM, "advanced_minecart_uncharger", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(ModBlocks.ADVANCED_MINECART_UNCHARGER_ITEM)
        );

        AdvancementEntry energizedCrystalMatrix = addAdvancement(
                advancementOutput, energizedGoldIngot,
                ModItems.ENERGIZED_CRYSTAL_MATRIX, "energized_crystal_matrix", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(ModItems.ENERGIZED_CRYSTAL_MATRIX)
        );

        AdvancementEntry teleporterMatrix = addAdvancement(
                advancementOutput, energizedCrystalMatrix,
                ModItems.TELEPORTER_MATRIX, "teleporter_matrix", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(ModItems.TELEPORTER_MATRIX)
        );

        AdvancementEntry teleporterProcessingUnit = addAdvancement(
                advancementOutput, teleporterMatrix,
                ModItems.TELEPORTER_PROCESSING_UNIT, "teleporter_processing_unit", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(ModItems.TELEPORTER_PROCESSING_UNIT)
        );

        AdvancementEntry energizedCrystalMatrixCable = addAdvancement(
                advancementOutput, energizedCrystalMatrix,
                ModBlocks.ENERGIZED_CRYSTAL_MATRIX_CABLE_ITEM, "energized_crystal_matrix_cable", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(ModBlocks.ENERGIZED_CRYSTAL_MATRIX_CABLE_ITEM)
        );

        AdvancementEntry reinforcedAdvancedSolarCell = addAdvancement(
                advancementOutput, energizedCrystalMatrix,
                ModItems.REINFORCED_ADVANCED_SOLAR_CELL, "reinforced_advanced_solar_cell", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(ModItems.REINFORCED_ADVANCED_SOLAR_CELL)
        );

        AdvancementEntry solarPanel6 = addAdvancement(
                advancementOutput, reinforcedAdvancedSolarCell,
                ModBlocks.SOLAR_PANEL_ITEM_6, "solar_panel_6", AdvancementFrame.CHALLENGE,
                InventoryChangedCriterion.Conditions.items(ModBlocks.SOLAR_PANEL_ITEM_6)
        );

        AdvancementEntry reinforcedAdvancedMachineFrame = addAdvancement(
                advancementOutput, energizedCrystalMatrix,
                ModBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM, "reinforced_advanced_machine_frame", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(ModBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM)
        );

        AdvancementEntry ehvTransformers = addAdvancement(
                advancementOutput, reinforcedAdvancedMachineFrame,
                ModBlocks.EHV_TRANSFORMER_1_TO_N_ITEM, "ehv_transformers", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(
                        ModBlocks.EHV_TRANSFORMER_1_TO_N_ITEM,
                        ModBlocks.EHV_TRANSFORMER_3_TO_3_ITEM,
                        ModBlocks.EHV_TRANSFORMER_N_TO_1_ITEM
                )
        );

        AdvancementEntry weatherController = addAdvancement(
                advancementOutput, reinforcedAdvancedMachineFrame,
                ModBlocks.WEATHER_CONTROLLER_ITEM, "weather_controller", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(ModBlocks.WEATHER_CONTROLLER_ITEM)
        );

        AdvancementEntry timeController = addAdvancement(
                advancementOutput, reinforcedAdvancedMachineFrame,
                ModBlocks.TIME_CONTROLLER_ITEM, "time_controller", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(ModBlocks.TIME_CONTROLLER_ITEM)
        );

        AdvancementEntry teleporter = addAdvancement(
                advancementOutput, reinforcedAdvancedMachineFrame,
                ModBlocks.TELEPORTER_ITEM, "teleporter", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(ModBlocks.TELEPORTER_ITEM)
        );

        AdvancementEntry inventoryTeleporter = addAdvancement(
                advancementOutput, teleporter,
                ModItems.INVENTORY_TELEPORTER, "inventory_teleporter", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(ModItems.INVENTORY_TELEPORTER)
        );
    }

    private AdvancementEntry addAdvancement(Consumer<AdvancementEntry> advancementOutput, AdvancementEntry parent,
                                            ItemConvertible icon, String advancementId, AdvancementFrame type,
                                            AdvancementCriterion<?> trigger) {
        return addAdvancement(advancementOutput, parent, new ItemStack(icon), advancementId, type, trigger);
    }
    private AdvancementEntry addAdvancement(Consumer<AdvancementEntry> advancementOutput, AdvancementEntry parent,
                                            ItemStack icon, String advancementId, AdvancementFrame type,
                                            AdvancementCriterion<?> trigger) {
        return Advancement.Builder.create().parent(parent).
                display(
                        icon,
                        Text.translatable("advancements.energizedpower." + advancementId + ".title"),
                        Text.translatable("advancements.energizedpower." + advancementId + ".description"),
                        null,
                        type,
                        true,
                        true,
                        false
                ).
                criterion("has_the_item", trigger).
                build(advancementOutput, EnergizedPowerMod.MODID + ":main/advanced/" + advancementId);
    }

    @Override
    public String getName() {
        return "Advancements (Advanced)";
    }
}
