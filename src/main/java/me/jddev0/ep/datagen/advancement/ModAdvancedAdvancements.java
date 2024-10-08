package me.jddev0.ep.datagen.advancement;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.component.EPDataComponentTypes;
import me.jddev0.ep.item.BatteryItem;
import me.jddev0.ep.item.EPItems;
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
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
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
                        EPItems.ENERGIZED_COPPER_INGOT,
                        Component.translatable("advancements.energizedpower.energizedpower_advanced.title"),
                        Component.translatable("advancements.energizedpower.energizedpower_advanced.description"),
                        EPAPI.id("textures/block/advanced_machine_frame_top.png"),
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                ).
                addCriterion("has_the_item",
                        InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(
                                CommonItemTags.INGOTS_ENERGIZED_COPPER
                        ))).
                save(advancementOutput, EPAPI.id("main/advanced/energizedpower_advanced"), existingFileHelper);

        AdvancementHolder advancedAlloyIngot = addAdvancement(
                advancementOutput, existingFileHelper, energizedPowerAdvanced,
                EPItems.ADVANCED_ALLOY_INGOT, "advanced_alloy_ingot", AdvancementType.TASK,
                CommonItemTags.INGOTS_ADVANCED_ALLOY
        );

        AdvancementHolder advancedAlloyPlate = addAdvancement(
                advancementOutput, existingFileHelper, advancedAlloyIngot,
                EPItems.ADVANCED_ALLOY_PLATE, "advanced_alloy_plate", AdvancementType.TASK,
                CommonItemTags.PLATES_ADVANCED_ALLOY
        );

        AdvancementHolder energizedCopperCable = addAdvancement(
                advancementOutput, existingFileHelper, energizedPowerAdvanced,
                EPBlocks.ENERGIZED_COPPER_CABLE_ITEM, "energized_copper_cable", AdvancementType.TASK
        );

        AdvancementHolder advancedSolarCell = addAdvancement(
                advancementOutput, existingFileHelper, energizedPowerAdvanced,
                EPItems.ADVANCED_SOLAR_CELL, "advanced_solar_cell", AdvancementType.TASK
        );

        AdvancementHolder solarPanel4 = addAdvancement(
                advancementOutput, existingFileHelper, advancedSolarCell,
                EPBlocks.SOLAR_PANEL_ITEM_4, "solar_panel_4", AdvancementType.TASK
        );

        AdvancementHolder solarPanel5 = addAdvancement(
                advancementOutput, existingFileHelper, solarPanel4,
                EPBlocks.SOLAR_PANEL_ITEM_5, "solar_panel_5", AdvancementType.TASK
        );

        AdvancementHolder battery6 = addAdvancement(
                advancementOutput, existingFileHelper, energizedPowerAdvanced,
                EPItems.BATTERY_6, "battery_6", AdvancementType.TASK
        );

        AdvancementHolder battery7 = addAdvancement(
                advancementOutput, existingFileHelper, battery6,
                EPItems.BATTERY_7, "battery_7", AdvancementType.TASK
        );

        AdvancementHolder battery8 = addAdvancement(
                advancementOutput, existingFileHelper, battery7,
                EPItems.BATTERY_8, "battery_8", AdvancementType.GOAL
        );

        ItemStack battery8FullyChargedIcon = new ItemStack(EPItems.BATTERY_8.get());
        battery8FullyChargedIcon.applyComponentsAndValidate(DataComponentPatch.builder().
                set(EPDataComponentTypes.ENERGY.get(), BatteryItem.Tier.BATTERY_8.getCapacity()).
                build());
        AdvancementHolder battery8FullyCharged = addAdvancement(
                advancementOutput, existingFileHelper, battery8,
                battery8FullyChargedIcon, "battery_8_fully_charged", AdvancementType.CHALLENGE,
                InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().
                        of(EPItems.BATTERY_8).
                        hasComponents(DataComponentPredicate.builder().
                                expect(EPDataComponentTypes.ENERGY.get(), BatteryItem.Tier.BATTERY_8.getCapacity()).
                                build()).
                        build())
        );

        AdvancementHolder advancedBatteryBox = addAdvancement(
                advancementOutput, existingFileHelper, battery8,
                EPBlocks.ADVANCED_BATTERY_BOX_ITEM, "advanced_battery_box", AdvancementType.TASK
        );

        AdvancementHolder advancedBatteryBoxMinecart = addAdvancement(
                advancementOutput, existingFileHelper, advancedBatteryBox,
                EPItems.ADVANCED_BATTERY_BOX_MINECART, "advanced_battery_box_minecart", AdvancementType.TASK
        );

        AdvancementHolder energizedCopperPlate = addAdvancement(
                advancementOutput, existingFileHelper, energizedPowerAdvanced,
                EPItems.ENERGIZED_COPPER_PLATE, "energized_copper_plate", AdvancementType.TASK,
                CommonItemTags.PLATES_ENERGIZED_COPPER
        );

        AdvancementHolder energizedCopperWire = addAdvancement(
                advancementOutput, existingFileHelper, energizedCopperPlate,
                EPItems.ENERGIZED_COPPER_WIRE, "energized_copper_wire", AdvancementType.TASK,
                CommonItemTags.WIRES_ENERGIZED_COPPER
        );

        AdvancementHolder advancedCircuit = addAdvancement(
                advancementOutput, existingFileHelper, energizedCopperWire,
                EPItems.ADVANCED_CIRCUIT, "advanced_circuit", AdvancementType.TASK
        );

        AdvancementHolder advancedUpgradeModule = addAdvancement(
                advancementOutput, existingFileHelper, advancedCircuit,
                EPItems.ADVANCED_UPGRADE_MODULE, "advanced_upgrade_module", AdvancementType.TASK
        );

        AdvancementHolder speedUpgradeUpgradeModule3 = addAdvancement(
                advancementOutput, existingFileHelper, advancedUpgradeModule,
                EPItems.SPEED_UPGRADE_MODULE_3, "speed_upgrade_module_3", AdvancementType.TASK
        );

        AdvancementHolder speedUpgradeUpgradeModule4 = addAdvancement(
                advancementOutput, existingFileHelper, speedUpgradeUpgradeModule3,
                EPItems.SPEED_UPGRADE_MODULE_4, "speed_upgrade_module_4", AdvancementType.TASK
        );

        AdvancementHolder energyEfficiencyUpgradeModule3 = addAdvancement(
                advancementOutput, existingFileHelper, advancedUpgradeModule,
                EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_3, "energy_efficiency_upgrade_module_3", AdvancementType.TASK
        );

        AdvancementHolder energyEfficiencyUpgradeModule4 = addAdvancement(
                advancementOutput, existingFileHelper, energyEfficiencyUpgradeModule3,
                EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_4, "energy_efficiency_upgrade_module_4", AdvancementType.TASK
        );

        AdvancementHolder energyCapacityUpgradeModule3 = addAdvancement(
                advancementOutput, existingFileHelper, advancedUpgradeModule,
                EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_3, "energy_capacity_upgrade_module_3", AdvancementType.TASK
        );

        AdvancementHolder energyCapacityUpgradeModule4 = addAdvancement(
                advancementOutput, existingFileHelper, energyCapacityUpgradeModule3,
                EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_4, "energy_capacity_upgrade_module_4", AdvancementType.TASK
        );

        AdvancementHolder rangeUpgradeModule1 = addAdvancement(
                advancementOutput, existingFileHelper, advancedUpgradeModule,
                EPItems.RANGE_UPGRADE_MODULE_1, "range_upgrade_module_1", AdvancementType.TASK
        );

        AdvancementHolder rangeUpgradeModule2 = addAdvancement(
                advancementOutput, existingFileHelper, rangeUpgradeModule1,
                EPItems.RANGE_UPGRADE_MODULE_2, "range_upgrade_module_2", AdvancementType.TASK
        );

        AdvancementHolder rangeUpgradeModule3 = addAdvancement(
                advancementOutput, existingFileHelper, rangeUpgradeModule2,
                EPItems.RANGE_UPGRADE_MODULE_3, "range_upgrade_module_3", AdvancementType.TASK
        );

        AdvancementHolder extractionDepthUpgradeModule3 = addAdvancement(
                advancementOutput, existingFileHelper, advancedUpgradeModule,
                EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_3, "extraction_depth_upgrade_module_3", AdvancementType.TASK
        );

        AdvancementHolder extractionDepthUpgradeModule4 = addAdvancement(
                advancementOutput, existingFileHelper, extractionDepthUpgradeModule3,
                EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_4, "extraction_depth_upgrade_module_4", AdvancementType.TASK
        );

        AdvancementHolder moonLightUpgradeModule2 = addAdvancement(
                advancementOutput, existingFileHelper, advancedUpgradeModule,
                EPItems.MOON_LIGHT_UPGRADE_MODULE_2, "moon_light_upgrade_module_2", AdvancementType.TASK
        );

        AdvancementHolder advancedMachineFrame = addAdvancement(
                advancementOutput, existingFileHelper, energizedPowerAdvanced,
                EPBlocks.ADVANCED_MACHINE_FRAME_ITEM, "advanced_machine_frame", AdvancementType.TASK
        );

        AdvancementHolder hvTransformers = addAdvancement(
                advancementOutput, existingFileHelper, advancedMachineFrame,
                EPBlocks.HV_TRANSFORMER_1_TO_N_ITEM, "hv_transformers", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(
                        ItemPredicate.Builder.item().of(
                                EPBlocks.HV_TRANSFORMER_1_TO_N_ITEM,
                                EPBlocks.HV_TRANSFORMER_3_TO_3_ITEM,
                                EPBlocks.HV_TRANSFORMER_N_TO_1_ITEM
                        ).build()
                )
        );

        AdvancementHolder advancedCrusher = addAdvancement(
                advancementOutput, existingFileHelper, advancedMachineFrame,
                EPBlocks.ADVANCED_CRUSHER_ITEM, "advanced_crusher", AdvancementType.TASK
        );

        AdvancementHolder advancedPulverizer = addAdvancement(
                advancementOutput, existingFileHelper, advancedMachineFrame,
                EPBlocks.ADVANCED_PULVERIZER_ITEM, "advanced_pulverizer", AdvancementType.TASK
        );

        AdvancementHolder lightningGenerator = addAdvancement(
                advancementOutput, existingFileHelper, advancedMachineFrame,
                EPBlocks.LIGHTNING_GENERATOR_ITEM, "lightning_generator", AdvancementType.TASK
        );

        AdvancementHolder crystalGrowthChamber = addAdvancement(
                advancementOutput, existingFileHelper, advancedMachineFrame,
                EPBlocks.CRYSTAL_GROWTH_CHAMBER_ITEM, "crystal_growth_chamber", AdvancementType.TASK
        );

        AdvancementHolder energizer = addAdvancement(
                advancementOutput, existingFileHelper, advancedMachineFrame,
                EPBlocks.ENERGIZER_ITEM, "energizer", AdvancementType.TASK
        );

        AdvancementHolder energizedGoldIngot = addAdvancement(
                advancementOutput, existingFileHelper, energizer,
                EPItems.ENERGIZED_GOLD_INGOT, "energized_gold_ingot", AdvancementType.TASK,
                CommonItemTags.INGOTS_ENERGIZED_GOLD
        );

        AdvancementHolder energizedGoldCable = addAdvancement(
                advancementOutput, existingFileHelper, energizedGoldIngot,
                EPBlocks.ENERGIZED_GOLD_CABLE_ITEM, "energized_gold_cable", AdvancementType.TASK
        );

        AdvancementHolder energizedGoldPlate = addAdvancement(
                advancementOutput, existingFileHelper, energizedGoldIngot,
                EPItems.ENERGIZED_GOLD_PLATE, "energized_gold_plate", AdvancementType.TASK,
                CommonItemTags.PLATES_ENERGIZED_GOLD
        );

        AdvancementHolder energizedGoldWire = addAdvancement(
                advancementOutput, existingFileHelper, energizedGoldPlate,
                EPItems.ENERGIZED_GOLD_WIRE, "energized_gold_wire", AdvancementType.TASK,
                CommonItemTags.WIRES_ENERGIZED_GOLD
        );

        AdvancementHolder processingUnit = addAdvancement(
                advancementOutput, existingFileHelper, energizedGoldWire,
                EPItems.PROCESSING_UNIT, "processing_unit", AdvancementType.TASK
        );

        AdvancementHolder reinforcedAdvancedUpgradeModule = addAdvancement(
                advancementOutput, existingFileHelper, processingUnit,
                EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE, "reinforced_advanced_upgrade_module", AdvancementType.TASK
        );

        AdvancementHolder speedUpgradeUpgradeModule5 = addAdvancement(
                advancementOutput, existingFileHelper, reinforcedAdvancedUpgradeModule,
                EPItems.SPEED_UPGRADE_MODULE_5, "speed_upgrade_module_5", AdvancementType.TASK
        );

        AdvancementHolder energyEfficiencyUpgradeModule5 = addAdvancement(
                advancementOutput, existingFileHelper, reinforcedAdvancedUpgradeModule,
                EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_5, "energy_efficiency_upgrade_module_5", AdvancementType.TASK
        );

        AdvancementHolder energyCapacityUpgradeModule5 = addAdvancement(
                advancementOutput, existingFileHelper, reinforcedAdvancedUpgradeModule,
                EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_5, "energy_capacity_upgrade_module_5", AdvancementType.TASK
        );

        AdvancementHolder durationUpgradeModule1 = addAdvancement(
                advancementOutput, existingFileHelper, reinforcedAdvancedUpgradeModule,
                EPItems.DURATION_UPGRADE_MODULE_1, "duration_upgrade_module_1", AdvancementType.TASK
        );

        AdvancementHolder durationUpgradeModule2 = addAdvancement(
                advancementOutput, existingFileHelper, durationUpgradeModule1,
                EPItems.DURATION_UPGRADE_MODULE_2, "duration_upgrade_module_2", AdvancementType.TASK
        );

        AdvancementHolder durationUpgradeModule3 = addAdvancement(
                advancementOutput, existingFileHelper, durationUpgradeModule2,
                EPItems.DURATION_UPGRADE_MODULE_3, "duration_upgrade_module_3", AdvancementType.TASK
        );

        AdvancementHolder durationUpgradeModule4 = addAdvancement(
                advancementOutput, existingFileHelper, durationUpgradeModule3,
                EPItems.DURATION_UPGRADE_MODULE_4, "duration_upgrade_module_4", AdvancementType.TASK
        );

        AdvancementHolder durationUpgradeModule5 = addAdvancement(
                advancementOutput, existingFileHelper, durationUpgradeModule4,
                EPItems.DURATION_UPGRADE_MODULE_5, "duration_upgrade_module_5", AdvancementType.TASK
        );

        AdvancementHolder durationUpgradeModule6 = addAdvancement(
                advancementOutput, existingFileHelper, durationUpgradeModule5,
                EPItems.DURATION_UPGRADE_MODULE_6, "duration_upgrade_module_6", AdvancementType.CHALLENGE
        );

        AdvancementHolder extractionDepthUpgradeModule5 = addAdvancement(
                advancementOutput, existingFileHelper, reinforcedAdvancedUpgradeModule,
                EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_5, "extraction_depth_upgrade_module_5", AdvancementType.TASK
        );

        AdvancementHolder moonLightUpgradeModule3 = addAdvancement(
                advancementOutput, existingFileHelper, reinforcedAdvancedUpgradeModule,
                EPItems.MOON_LIGHT_UPGRADE_MODULE_3, "moon_light_upgrade_module_3", AdvancementType.TASK
        );

        AdvancementHolder advancedAutoCrafter = addAdvancement(
                advancementOutput, existingFileHelper, energizedGoldIngot,
                EPBlocks.ADVANCED_AUTO_CRAFTER_ITEM, "advanced_auto_crafter", AdvancementType.TASK
        );

        AdvancementHolder advancedPoweredFurnace = addAdvancement(
                advancementOutput, existingFileHelper, energizedGoldIngot,
                EPBlocks.ADVANCED_POWERED_FURNACE_ITEM, "advanced_powered_furnace", AdvancementType.TASK
        );

        AdvancementHolder chargingStation = addAdvancement(
                advancementOutput, existingFileHelper, energizedGoldIngot,
                EPBlocks.CHARGING_STATION_ITEM, "charging_station", AdvancementType.TASK
        );

        AdvancementHolder advancedCharger = addAdvancement(
                advancementOutput, existingFileHelper, energizedGoldIngot,
                EPBlocks.ADVANCED_CHARGER_ITEM, "advanced_charger", AdvancementType.TASK
        );

        AdvancementHolder advancedMinecartCharger = addAdvancement(
                advancementOutput, existingFileHelper, advancedCharger,
                EPBlocks.ADVANCED_MINECART_CHARGER_ITEM, "advanced_minecart_charger", AdvancementType.TASK
        );

        AdvancementHolder advancedUncharger = addAdvancement(
                advancementOutput, existingFileHelper, energizedGoldIngot,
                EPBlocks.ADVANCED_UNCHARGER_ITEM, "advanced_uncharger", AdvancementType.TASK
        );

        AdvancementHolder advancedMinecartUncharger = addAdvancement(
                advancementOutput, existingFileHelper, advancedUncharger,
                EPBlocks.ADVANCED_MINECART_UNCHARGER_ITEM, "advanced_minecart_uncharger", AdvancementType.TASK
        );

        AdvancementHolder energizedCrystalMatrix = addAdvancement(
                advancementOutput, existingFileHelper, energizedGoldIngot,
                EPItems.ENERGIZED_CRYSTAL_MATRIX, "energized_crystal_matrix", AdvancementType.TASK
        );

        AdvancementHolder teleporterMatrix = addAdvancement(
                advancementOutput, existingFileHelper, energizedCrystalMatrix,
                EPItems.TELEPORTER_MATRIX, "teleporter_matrix", AdvancementType.TASK
        );

        AdvancementHolder teleporterProcessingUnit = addAdvancement(
                advancementOutput, existingFileHelper, teleporterMatrix,
                EPItems.TELEPORTER_PROCESSING_UNIT, "teleporter_processing_unit", AdvancementType.TASK
        );

        AdvancementHolder energizedCrystalMatrixCable = addAdvancement(
                advancementOutput, existingFileHelper, energizedCrystalMatrix,
                EPBlocks.ENERGIZED_CRYSTAL_MATRIX_CABLE_ITEM, "energized_crystal_matrix_cable", AdvancementType.CHALLENGE
        );

        AdvancementHolder reinforcedAdvancedSolarCell = addAdvancement(
                advancementOutput, existingFileHelper, energizedCrystalMatrix,
                EPItems.REINFORCED_ADVANCED_SOLAR_CELL, "reinforced_advanced_solar_cell", AdvancementType.TASK
        );

        AdvancementHolder solarPanel6 = addAdvancement(
                advancementOutput, existingFileHelper, reinforcedAdvancedSolarCell,
                EPBlocks.SOLAR_PANEL_ITEM_6, "solar_panel_6", AdvancementType.CHALLENGE
        );

        AdvancementHolder reinforcedAdvancedMachineFrame = addAdvancement(
                advancementOutput, existingFileHelper, energizedCrystalMatrix,
                EPBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM, "reinforced_advanced_machine_frame", AdvancementType.TASK
        );

        AdvancementHolder ehvTransformers = addAdvancement(
                advancementOutput, existingFileHelper, reinforcedAdvancedMachineFrame,
                EPBlocks.EHV_TRANSFORMER_1_TO_N_ITEM, "ehv_transformers", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(
                        ItemPredicate.Builder.item().of(
                                EPBlocks.EHV_TRANSFORMER_1_TO_N_ITEM,
                                EPBlocks.EHV_TRANSFORMER_3_TO_3_ITEM,
                                EPBlocks.EHV_TRANSFORMER_N_TO_1_ITEM
                        ).build()
                )
        );

        AdvancementHolder weatherController = addAdvancement(
                advancementOutput, existingFileHelper, reinforcedAdvancedMachineFrame,
                EPBlocks.WEATHER_CONTROLLER_ITEM, "weather_controller", AdvancementType.TASK
        );

        AdvancementHolder timeController = addAdvancement(
                advancementOutput, existingFileHelper, reinforcedAdvancedMachineFrame,
                EPBlocks.TIME_CONTROLLER_ITEM, "time_controller", AdvancementType.TASK
        );

        AdvancementHolder teleporter = addAdvancement(
                advancementOutput, existingFileHelper, reinforcedAdvancedMachineFrame,
                EPBlocks.TELEPORTER_ITEM, "teleporter", AdvancementType.TASK
        );

        AdvancementHolder inventoryTeleporter = addAdvancement(
                advancementOutput, existingFileHelper, teleporter,
                EPItems.INVENTORY_TELEPORTER, "inventory_teleporter", AdvancementType.TASK
        );
    }

    private AdvancementHolder addAdvancement(Consumer<AdvancementHolder> advancementOutput, ExistingFileHelper existingFileHelper,
                                             AdvancementHolder parent, ItemLike icon, String advancementId, AdvancementType type) {
        return addAdvancement(advancementOutput, existingFileHelper, parent, icon, advancementId, type, icon);
    }
    private AdvancementHolder addAdvancement(Consumer<AdvancementHolder> advancementOutput, ExistingFileHelper existingFileHelper,
                                             AdvancementHolder parent, ItemLike icon, String advancementId, AdvancementType type,
                                             ItemLike trigger) {
        return addAdvancement(advancementOutput, existingFileHelper, parent, new ItemStack(icon), advancementId, type,
                InventoryChangeTrigger.TriggerInstance.hasItems(trigger));
    }
    private AdvancementHolder addAdvancement(Consumer<AdvancementHolder> advancementOutput, ExistingFileHelper existingFileHelper,
                                             AdvancementHolder parent, ItemLike icon, String advancementId, AdvancementType type,
                                             TagKey<Item> trigger) {
        return addAdvancement(advancementOutput, existingFileHelper, parent, new ItemStack(icon), advancementId, type,
                InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(
                        trigger
                )));
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
                save(advancementOutput, EPAPI.id("main/advanced/" + advancementId), existingFileHelper);
    }
}
