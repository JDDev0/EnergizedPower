package me.jddev0.ep.datagen.adavancement;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.component.ModDataComponentTypes;
import me.jddev0.ep.item.BatteryItem;
import me.jddev0.ep.item.ModItems;
import me.jddev0.ep.registry.tags.CommonItemTags;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.function.Consumer;

public class ModAdvancedAdvancements implements AdvancementProvider.AdvancementGenerator {
    @Override
    public void generate(HolderLookup.Provider lookupProvider, Consumer<AdvancementHolder> advancementOutput,
                         ExistingFileHelper existingFileHelper) {
        AdvancementHolder energizedPowerAdvanced = Advancement.Builder.advancement().
                display(
                        ModItems.ENERGIZED_COPPER_INGOT,
                        Component.translatable("advancements.energizedpower.energizedpower_advanced.title"),
                        Component.translatable("advancements.energizedpower.energizedpower_advanced.description"),
                        ResourceLocation.fromNamespaceAndPath(EnergizedPowerMod.MODID, "textures/block/advanced_machine_frame_top.png"),
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                ).
                addCriterion("has_the_item",
                        InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(
                                CommonItemTags.INGOTS_ENERGIZED_COPPER
                        ))).
                save(advancementOutput, ResourceLocation.fromNamespaceAndPath(EnergizedPowerMod.MODID,
                        "main/advanced/energizedpower_advanced"), existingFileHelper);

        AdvancementHolder advancedAlloyIngot = addAdvancement(
                advancementOutput, existingFileHelper, energizedPowerAdvanced,
                ModItems.ADVANCED_ALLOY_INGOT, "advanced_alloy_ingot", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(
                        CommonItemTags.INGOTS_ADVANCED_ALLOY
                ))
        );

        AdvancementHolder advancedAlloyPlate = addAdvancement(
                advancementOutput, existingFileHelper, advancedAlloyIngot,
                ModItems.ADVANCED_ALLOY_PLATE, "advanced_alloy_plate", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(
                        CommonItemTags.PLATES_ADVANCED_ALLOY
                ))
        );

        AdvancementHolder energizedCopperCable = addAdvancement(
                advancementOutput, existingFileHelper, energizedPowerAdvanced,
                ModBlocks.ENERGIZED_COPPER_CABLE_ITEM, "energized_copper_cable", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.ENERGIZED_COPPER_CABLE_ITEM)
        );

        AdvancementHolder advancedSolarCell = addAdvancement(
                advancementOutput, existingFileHelper, energizedPowerAdvanced,
                ModItems.ADVANCED_SOLAR_CELL, "advanced_solar_cell", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.ADVANCED_SOLAR_CELL)
        );

        AdvancementHolder solarPanel4 = addAdvancement(
                advancementOutput, existingFileHelper, advancedSolarCell,
                ModBlocks.SOLAR_PANEL_ITEM_4, "solar_panel_4", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.SOLAR_PANEL_ITEM_4)
        );

        AdvancementHolder solarPanel5 = addAdvancement(
                advancementOutput, existingFileHelper, solarPanel4,
                ModBlocks.SOLAR_PANEL_ITEM_5, "solar_panel_5", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.SOLAR_PANEL_ITEM_5)
        );

        AdvancementHolder battery6 = addAdvancement(
                advancementOutput, existingFileHelper, energizedPowerAdvanced,
                ModItems.BATTERY_6, "battery_6", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.BATTERY_6)
        );

        AdvancementHolder battery7 = addAdvancement(
                advancementOutput, existingFileHelper, battery6,
                ModItems.BATTERY_7, "battery_7", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.BATTERY_7)
        );

        AdvancementHolder battery8 = addAdvancement(
                advancementOutput, existingFileHelper, battery7,
                ModItems.BATTERY_8, "battery_8", AdvancementType.GOAL,
                InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.BATTERY_8)
        );

        ItemStack battery8FullyChargedIcon = new ItemStack(ModItems.BATTERY_8.get());
        battery8FullyChargedIcon.applyComponentsAndValidate(DataComponentPatch.builder().
                set(ModDataComponentTypes.ENERGY.get(), BatteryItem.Tier.BATTERY_8.getCapacity()).
                build());
        AdvancementHolder battery8FullyCharged = addAdvancement(
                advancementOutput, existingFileHelper, battery8,
                battery8FullyChargedIcon, "battery_8_fully_charged", AdvancementType.CHALLENGE,
                InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().
                        of(ModItems.BATTERY_8).
                        hasComponents(DataComponentPredicate.builder().
                                expect(ModDataComponentTypes.ENERGY.get(), BatteryItem.Tier.BATTERY_8.getCapacity()).
                                build()).
                        build())
        );

        AdvancementHolder advancedBatteryBox = addAdvancement(
                advancementOutput, existingFileHelper, battery8,
                ModBlocks.ADVANCED_BATTERY_BOX_ITEM, "advanced_battery_box", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.ADVANCED_BATTERY_BOX_ITEM)
        );

        AdvancementHolder advancedBatteryBoxMinecart = addAdvancement(
                advancementOutput, existingFileHelper, advancedBatteryBox,
                ModItems.ADVANCED_BATTERY_BOX_MINECART, "advanced_battery_box_minecart", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.ADVANCED_BATTERY_BOX_MINECART)
        );

        AdvancementHolder energizedCopperPlate = addAdvancement(
                advancementOutput, existingFileHelper, energizedPowerAdvanced,
                ModItems.ENERGIZED_COPPER_PLATE, "energized_copper_plate", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(
                        CommonItemTags.PLATES_ENERGIZED_COPPER
                ))
        );

        AdvancementHolder energizedCopperWire = addAdvancement(
                advancementOutput, existingFileHelper, energizedCopperPlate,
                ModItems.ENERGIZED_COPPER_WIRE, "energized_copper_wire", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(
                        CommonItemTags.WIRES_ENERGIZED_COPPER
                ))
        );

        AdvancementHolder advancedCircuit = addAdvancement(
                advancementOutput, existingFileHelper, energizedCopperWire,
                ModItems.ADVANCED_CIRCUIT, "advanced_circuit", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.ADVANCED_CIRCUIT)
        );

        AdvancementHolder advancedUpgradeModule = addAdvancement(
                advancementOutput, existingFileHelper, advancedCircuit,
                ModItems.ADVANCED_UPGRADE_MODULE, "advanced_upgrade_module", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.ADVANCED_UPGRADE_MODULE)
        );

        AdvancementHolder speedUpgradeUpgradeModule3 = addAdvancement(
                advancementOutput, existingFileHelper, advancedUpgradeModule,
                ModItems.SPEED_UPGRADE_MODULE_3, "speed_upgrade_module_3", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.SPEED_UPGRADE_MODULE_3)
        );

        AdvancementHolder speedUpgradeUpgradeModule4 = addAdvancement(
                advancementOutput, existingFileHelper, speedUpgradeUpgradeModule3,
                ModItems.SPEED_UPGRADE_MODULE_4, "speed_upgrade_module_4", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.SPEED_UPGRADE_MODULE_4)
        );

        AdvancementHolder energyEfficiencyUpgradeModule3 = addAdvancement(
                advancementOutput, existingFileHelper, advancedUpgradeModule,
                ModItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_3, "energy_efficiency_upgrade_module_3", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_3)
        );

        AdvancementHolder energyEfficiencyUpgradeModule4 = addAdvancement(
                advancementOutput, existingFileHelper, energyEfficiencyUpgradeModule3,
                ModItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_4, "energy_efficiency_upgrade_module_4", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_4)
        );

        AdvancementHolder energyCapacityUpgradeModule3 = addAdvancement(
                advancementOutput, existingFileHelper, advancedUpgradeModule,
                ModItems.ENERGY_CAPACITY_UPGRADE_MODULE_3, "energy_capacity_upgrade_module_3", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.ENERGY_CAPACITY_UPGRADE_MODULE_3)
        );

        AdvancementHolder energyCapacityUpgradeModule4 = addAdvancement(
                advancementOutput, existingFileHelper, energyCapacityUpgradeModule3,
                ModItems.ENERGY_CAPACITY_UPGRADE_MODULE_4, "energy_capacity_upgrade_module_4", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.ENERGY_CAPACITY_UPGRADE_MODULE_4)
        );

        AdvancementHolder rangeUpgradeModule1 = addAdvancement(
                advancementOutput, existingFileHelper, advancedUpgradeModule,
                ModItems.RANGE_UPGRADE_MODULE_1, "range_upgrade_module_1", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.RANGE_UPGRADE_MODULE_1)
        );

        AdvancementHolder rangeUpgradeModule2 = addAdvancement(
                advancementOutput, existingFileHelper, rangeUpgradeModule1,
                ModItems.RANGE_UPGRADE_MODULE_2, "range_upgrade_module_2", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.RANGE_UPGRADE_MODULE_2)
        );

        AdvancementHolder rangeUpgradeModule3 = addAdvancement(
                advancementOutput, existingFileHelper, rangeUpgradeModule2,
                ModItems.RANGE_UPGRADE_MODULE_3, "range_upgrade_module_3", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.RANGE_UPGRADE_MODULE_3)
        );

        AdvancementHolder extractionDepthUpgradeModule3 = addAdvancement(
                advancementOutput, existingFileHelper, advancedUpgradeModule,
                ModItems.EXTRACTION_DEPTH_UPGRADE_MODULE_3, "extraction_depth_upgrade_module_3", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.EXTRACTION_DEPTH_UPGRADE_MODULE_3)
        );

        AdvancementHolder extractionDepthUpgradeModule4 = addAdvancement(
                advancementOutput, existingFileHelper, extractionDepthUpgradeModule3,
                ModItems.EXTRACTION_DEPTH_UPGRADE_MODULE_4, "extraction_depth_upgrade_module_4", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.EXTRACTION_DEPTH_UPGRADE_MODULE_4)
        );

        AdvancementHolder moonLightUpgradeModule2 = addAdvancement(
                advancementOutput, existingFileHelper, advancedUpgradeModule,
                ModItems.MOON_LIGHT_UPGRADE_MODULE_2, "moon_light_upgrade_module_2", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.MOON_LIGHT_UPGRADE_MODULE_2)
        );

        AdvancementHolder advancedMachineFrame = addAdvancement(
                advancementOutput, existingFileHelper, energizedPowerAdvanced,
                ModBlocks.ADVANCED_MACHINE_FRAME_ITEM, "advanced_machine_frame", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.ADVANCED_MACHINE_FRAME_ITEM)
        );

        AdvancementHolder hvTransformers = addAdvancement(
                advancementOutput, existingFileHelper, advancedMachineFrame,
                ModBlocks.HV_TRANSFORMER_1_TO_N_ITEM, "hv_transformers", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(
                        ItemPredicate.Builder.item().of(
                                ModBlocks.HV_TRANSFORMER_1_TO_N_ITEM,
                                ModBlocks.HV_TRANSFORMER_3_TO_3_ITEM,
                                ModBlocks.HV_TRANSFORMER_N_TO_1_ITEM
                        ).build()
                )
        );

        AdvancementHolder advancedCrusher = addAdvancement(
                advancementOutput, existingFileHelper, advancedMachineFrame,
                ModBlocks.ADVANCED_CRUSHER_ITEM, "advanced_crusher", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.ADVANCED_CRUSHER_ITEM)
        );

        AdvancementHolder advancedPulverizer = addAdvancement(
                advancementOutput, existingFileHelper, advancedMachineFrame,
                ModBlocks.ADVANCED_PULVERIZER_ITEM, "advanced_pulverizer", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.ADVANCED_PULVERIZER_ITEM)
        );

        AdvancementHolder lightningGenerator = addAdvancement(
                advancementOutput, existingFileHelper, advancedMachineFrame,
                ModBlocks.LIGHTNING_GENERATOR_ITEM, "lightning_generator", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.LIGHTNING_GENERATOR_ITEM)
        );

        AdvancementHolder crystalGrowthChamber = addAdvancement(
                advancementOutput, existingFileHelper, advancedMachineFrame,
                ModBlocks.CRYSTAL_GROWTH_CHAMBER_ITEM, "crystal_growth_chamber", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.CRYSTAL_GROWTH_CHAMBER_ITEM)
        );

        AdvancementHolder energizer = addAdvancement(
                advancementOutput, existingFileHelper, advancedMachineFrame,
                ModBlocks.ENERGIZER_ITEM, "energizer", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.ENERGIZER_ITEM)
        );

        AdvancementHolder energizedGoldIngot = addAdvancement(
                advancementOutput, existingFileHelper, energizer,
                ModItems.ENERGIZED_GOLD_INGOT, "energized_gold_ingot", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(
                        CommonItemTags.INGOTS_ENERGIZED_GOLD
                ))
        );

        AdvancementHolder energizedGoldCable = addAdvancement(
                advancementOutput, existingFileHelper, energizedGoldIngot,
                ModBlocks.ENERGIZED_GOLD_CABLE_ITEM, "energized_gold_cable", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.ENERGIZED_GOLD_CABLE_ITEM)
        );

        AdvancementHolder energizedGoldPlate = addAdvancement(
                advancementOutput, existingFileHelper, energizedGoldIngot,
                ModItems.ENERGIZED_GOLD_PLATE, "energized_gold_plate", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(
                        CommonItemTags.PLATES_ENERGIZED_GOLD
                ))
        );

        AdvancementHolder energizedGoldWire = addAdvancement(
                advancementOutput, existingFileHelper, energizedGoldPlate,
                ModItems.ENERGIZED_GOLD_WIRE, "energized_gold_wire", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(
                        CommonItemTags.WIRES_ENERGIZED_GOLD
                ))
        );

        AdvancementHolder processingUnit = addAdvancement(
                advancementOutput, existingFileHelper, energizedGoldWire,
                ModItems.PROCESSING_UNIT, "processing_unit", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.PROCESSING_UNIT)
        );

        AdvancementHolder reinforcedAdvancedUpgradeModule = addAdvancement(
                advancementOutput, existingFileHelper, processingUnit,
                ModItems.REINFORCED_ADVANCED_UPGRADE_MODULE, "reinforced_advanced_upgrade_module", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.REINFORCED_ADVANCED_UPGRADE_MODULE)
        );

        AdvancementHolder speedUpgradeUpgradeModule5 = addAdvancement(
                advancementOutput, existingFileHelper, reinforcedAdvancedUpgradeModule,
                ModItems.SPEED_UPGRADE_MODULE_5, "speed_upgrade_module_5", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.SPEED_UPGRADE_MODULE_5)
        );

        AdvancementHolder energyEfficiencyUpgradeModule5 = addAdvancement(
                advancementOutput, existingFileHelper, reinforcedAdvancedUpgradeModule,
                ModItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_5, "energy_efficiency_upgrade_module_5", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_5)
        );

        AdvancementHolder energyCapacityUpgradeModule5 = addAdvancement(
                advancementOutput, existingFileHelper, reinforcedAdvancedUpgradeModule,
                ModItems.ENERGY_CAPACITY_UPGRADE_MODULE_5, "energy_capacity_upgrade_module_5", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.ENERGY_CAPACITY_UPGRADE_MODULE_5)
        );

        AdvancementHolder durationUpgradeModule1 = addAdvancement(
                advancementOutput, existingFileHelper, reinforcedAdvancedUpgradeModule,
                ModItems.DURATION_UPGRADE_MODULE_1, "duration_upgrade_module_1", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.DURATION_UPGRADE_MODULE_1)
        );

        AdvancementHolder durationUpgradeModule2 = addAdvancement(
                advancementOutput, existingFileHelper, durationUpgradeModule1,
                ModItems.DURATION_UPGRADE_MODULE_2, "duration_upgrade_module_2", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.DURATION_UPGRADE_MODULE_2)
        );

        AdvancementHolder durationUpgradeModule3 = addAdvancement(
                advancementOutput, existingFileHelper, durationUpgradeModule2,
                ModItems.DURATION_UPGRADE_MODULE_3, "duration_upgrade_module_3", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.DURATION_UPGRADE_MODULE_3)
        );

        AdvancementHolder durationUpgradeModule4 = addAdvancement(
                advancementOutput, existingFileHelper, durationUpgradeModule3,
                ModItems.DURATION_UPGRADE_MODULE_4, "duration_upgrade_module_4", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.DURATION_UPGRADE_MODULE_4)
        );

        AdvancementHolder durationUpgradeModule5 = addAdvancement(
                advancementOutput, existingFileHelper, durationUpgradeModule4,
                ModItems.DURATION_UPGRADE_MODULE_5, "duration_upgrade_module_5", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.DURATION_UPGRADE_MODULE_5)
        );

        AdvancementHolder durationUpgradeModule6 = addAdvancement(
                advancementOutput, existingFileHelper, durationUpgradeModule5,
                ModItems.DURATION_UPGRADE_MODULE_6, "duration_upgrade_module_6", AdvancementType.CHALLENGE,
                InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.DURATION_UPGRADE_MODULE_6)
        );

        AdvancementHolder extractionDepthUpgradeModule5 = addAdvancement(
                advancementOutput, existingFileHelper, reinforcedAdvancedUpgradeModule,
                ModItems.EXTRACTION_DEPTH_UPGRADE_MODULE_5, "extraction_depth_upgrade_module_5", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.EXTRACTION_DEPTH_UPGRADE_MODULE_5)
        );

        AdvancementHolder moonLightUpgradeModule3 = addAdvancement(
                advancementOutput, existingFileHelper, reinforcedAdvancedUpgradeModule,
                ModItems.MOON_LIGHT_UPGRADE_MODULE_3, "moon_light_upgrade_module_3", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.MOON_LIGHT_UPGRADE_MODULE_3)
        );

        AdvancementHolder advancedAutoCrafter = addAdvancement(
                advancementOutput, existingFileHelper, energizedGoldIngot,
                ModBlocks.ADVANCED_AUTO_CRAFTER_ITEM, "advanced_auto_crafter", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.ADVANCED_AUTO_CRAFTER_ITEM)
        );

        AdvancementHolder advancedPoweredFurnace = addAdvancement(
                advancementOutput, existingFileHelper, energizedGoldIngot,
                ModBlocks.ADVANCED_POWERED_FURNACE_ITEM, "advanced_powered_furnace", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.ADVANCED_POWERED_FURNACE_ITEM)
        );

        AdvancementHolder chargingStation = addAdvancement(
                advancementOutput, existingFileHelper, energizedGoldIngot,
                ModBlocks.CHARGING_STATION_ITEM, "charging_station", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.CHARGING_STATION_ITEM)
        );

        AdvancementHolder advancedCharger = addAdvancement(
                advancementOutput, existingFileHelper, energizedGoldIngot,
                ModBlocks.ADVANCED_CHARGER_ITEM, "advanced_charger", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.ADVANCED_CHARGER_ITEM)
        );

        AdvancementHolder advancedMinecartCharger = addAdvancement(
                advancementOutput, existingFileHelper, advancedCharger,
                ModBlocks.ADVANCED_MINECART_CHARGER_ITEM, "advanced_minecart_charger", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.ADVANCED_MINECART_CHARGER_ITEM)
        );

        AdvancementHolder advancedUncharger = addAdvancement(
                advancementOutput, existingFileHelper, energizedGoldIngot,
                ModBlocks.ADVANCED_UNCHARGER_ITEM, "advanced_uncharger", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.ADVANCED_UNCHARGER_ITEM)
        );

        AdvancementHolder advancedMinecartUncharger = addAdvancement(
                advancementOutput, existingFileHelper, advancedUncharger,
                ModBlocks.ADVANCED_MINECART_UNCHARGER_ITEM, "advanced_minecart_uncharger", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.ADVANCED_MINECART_UNCHARGER_ITEM)
        );

        AdvancementHolder energizedCrystalMatrix = addAdvancement(
                advancementOutput, existingFileHelper, energizedGoldIngot,
                ModItems.ENERGIZED_CRYSTAL_MATRIX, "energized_crystal_matrix", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.ENERGIZED_CRYSTAL_MATRIX)
        );

        AdvancementHolder teleporterMatrix = addAdvancement(
                advancementOutput, existingFileHelper, energizedCrystalMatrix,
                ModItems.TELEPORTER_MATRIX, "teleporter_matrix", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.TELEPORTER_MATRIX)
        );

        AdvancementHolder teleporterProcessingUnit = addAdvancement(
                advancementOutput, existingFileHelper, teleporterMatrix,
                ModItems.TELEPORTER_PROCESSING_UNIT, "teleporter_processing_unit", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.TELEPORTER_PROCESSING_UNIT)
        );

        AdvancementHolder energizedCrystalMatrixCable = addAdvancement(
                advancementOutput, existingFileHelper, energizedCrystalMatrix,
                ModBlocks.ENERGIZED_CRYSTAL_MATRIX_CABLE_ITEM, "energized_crystal_matrix_cable", AdvancementType.CHALLENGE,
                InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.ENERGIZED_CRYSTAL_MATRIX_CABLE_ITEM)
        );

        AdvancementHolder reinforcedAdvancedSolarCell = addAdvancement(
                advancementOutput, existingFileHelper, energizedCrystalMatrix,
                ModItems.REINFORCED_ADVANCED_SOLAR_CELL, "reinforced_advanced_solar_cell", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.REINFORCED_ADVANCED_SOLAR_CELL)
        );

        AdvancementHolder solarPanel6 = addAdvancement(
                advancementOutput, existingFileHelper, reinforcedAdvancedSolarCell,
                ModBlocks.SOLAR_PANEL_ITEM_6, "solar_panel_6", AdvancementType.CHALLENGE,
                InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.SOLAR_PANEL_ITEM_6)
        );

        AdvancementHolder reinforcedAdvancedMachineFrame = addAdvancement(
                advancementOutput, existingFileHelper, energizedCrystalMatrix,
                ModBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM, "reinforced_advanced_machine_frame", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM)
        );

        AdvancementHolder ehvTransformers = addAdvancement(
                advancementOutput, existingFileHelper, reinforcedAdvancedMachineFrame,
                ModBlocks.EHV_TRANSFORMER_1_TO_N_ITEM, "ehv_transformers", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(
                        ItemPredicate.Builder.item().of(
                                ModBlocks.EHV_TRANSFORMER_1_TO_N_ITEM,
                                ModBlocks.EHV_TRANSFORMER_3_TO_3_ITEM,
                                ModBlocks.EHV_TRANSFORMER_N_TO_1_ITEM
                        ).build()
                )
        );

        AdvancementHolder weatherController = addAdvancement(
                advancementOutput, existingFileHelper, reinforcedAdvancedMachineFrame,
                ModBlocks.WEATHER_CONTROLLER_ITEM, "weather_controller", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.WEATHER_CONTROLLER_ITEM)
        );

        AdvancementHolder timeController = addAdvancement(
                advancementOutput, existingFileHelper, reinforcedAdvancedMachineFrame,
                ModBlocks.TIME_CONTROLLER_ITEM, "time_controller", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.TIME_CONTROLLER_ITEM)
        );

        AdvancementHolder teleporter = addAdvancement(
                advancementOutput, existingFileHelper, reinforcedAdvancedMachineFrame,
                ModBlocks.TELEPORTER_ITEM, "teleporter", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.TELEPORTER_ITEM)
        );

        AdvancementHolder inventoryTeleporter = addAdvancement(
                advancementOutput, existingFileHelper, teleporter,
                ModItems.INVENTORY_TELEPORTER, "inventory_teleporter", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.INVENTORY_TELEPORTER)
        );
    }

    private AdvancementHolder addAdvancement(Consumer<AdvancementHolder> advancementOutput, ExistingFileHelper existingFileHelper,
                                             AdvancementHolder parent, ItemLike icon, String advancementId, AdvancementType type,
                                             Criterion<?> trigger) {
        return addAdvancement(advancementOutput, existingFileHelper, parent, new ItemStack(icon), advancementId, type, trigger);
    }
    private AdvancementHolder addAdvancement(Consumer<AdvancementHolder> advancementOutput, ExistingFileHelper existingFileHelper,
                                             AdvancementHolder parent, ItemStack icon, String advancementId, AdvancementType type,
                                             Criterion<?> trigger) {
        return Advancement.Builder.advancement().parent(parent).
                display(
                        icon,
                        Component.translatable("advancements.energizedpower." + advancementId + ".title"),
                        Component.translatable("advancements.energizedpower." + advancementId + ".description"),
                        null,
                        type,
                        true,
                        true,
                        false
                ).
                addCriterion("has_the_item", trigger).
                save(advancementOutput, ResourceLocation.fromNamespaceAndPath(EnergizedPowerMod.MODID,
                        "main/advanced/" + advancementId), existingFileHelper);
    }
}