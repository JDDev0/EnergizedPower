package me.jddev0.ep.datagen.advancement;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.item.BatteryItem;
import me.jddev0.ep.item.EPItems;
import me.jddev0.ep.registry.tags.CommonItemTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
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
                        EPItems.ENERGIZED_COPPER_INGOT,
                        Text.translatable("advancements.energizedpower.energizedpower_advanced.title"),
                        Text.translatable("advancements.energizedpower.energizedpower_advanced.description"),
                        EPAPI.id("textures/block/advanced_machine_frame_top.png"),
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                ).
                criterion("has_the_item",
                        InventoryChangedCriterion.Conditions.items(ItemPredicate.Builder.create().tag(
                                CommonItemTags.ENERGIZED_COPPER_INGOTS
                        ))).
                build(advancementOutput, EPAPI.MOD_ID + ":main/advanced/energizedpower_advanced");

        AdvancementEntry advancedAlloyIngot = addAdvancement(
                advancementOutput, energizedPowerAdvanced,
                EPItems.ADVANCED_ALLOY_INGOT, "advanced_alloy_ingot", AdvancementFrame.TASK,
                CommonItemTags.ADVANCED_ALLOY_INGOTS
        );

        AdvancementEntry advancedAlloyPlate = addAdvancement(
                advancementOutput, advancedAlloyIngot,
                EPItems.ADVANCED_ALLOY_PLATE, "advanced_alloy_plate", AdvancementFrame.TASK,
                CommonItemTags.ADVANCED_ALLOY_PLATES
        );

        AdvancementEntry energizedCopperCable = addAdvancement(
                advancementOutput, energizedPowerAdvanced,
                EPBlocks.ENERGIZED_COPPER_CABLE_ITEM, "energized_copper_cable", AdvancementFrame.TASK
        );

        AdvancementEntry advancedSolarCell = addAdvancement(
                advancementOutput, energizedPowerAdvanced,
                EPItems.ADVANCED_SOLAR_CELL, "advanced_solar_cell", AdvancementFrame.TASK
        );

        AdvancementEntry solarPanel4 = addAdvancement(
                advancementOutput, advancedSolarCell,
                EPBlocks.SOLAR_PANEL_ITEM_4, "solar_panel_4", AdvancementFrame.TASK
        );

        AdvancementEntry solarPanel5 = addAdvancement(
                advancementOutput, solarPanel4,
                EPBlocks.SOLAR_PANEL_ITEM_5, "solar_panel_5", AdvancementFrame.TASK
        );

        AdvancementEntry battery6 = addAdvancement(
                advancementOutput, energizedPowerAdvanced,
                EPItems.BATTERY_6, "battery_6", AdvancementFrame.TASK
        );

        AdvancementEntry battery7 = addAdvancement(
                advancementOutput, battery6,
                EPItems.BATTERY_7, "battery_7", AdvancementFrame.TASK
        );

        AdvancementEntry battery8 = addAdvancement(
                advancementOutput, battery7,
                EPItems.BATTERY_8, "battery_8", AdvancementFrame.GOAL
        );

        ItemStack battery8FullyChargedIcon = new ItemStack(EPItems.BATTERY_8);
        NbtCompound nbt = battery8FullyChargedIcon.getOrCreateNbt();
        nbt.putLong(SimpleEnergyItem.ENERGY_KEY, BatteryItem.Tier.BATTERY_8.getCapacity());
        AdvancementEntry battery8FullyCharged = addAdvancement(
                advancementOutput, battery8,
                battery8FullyChargedIcon, "battery_8_fully_charged", AdvancementFrame.CHALLENGE,
                InventoryChangedCriterion.Conditions.items(ItemPredicate.Builder.create().
                        items(EPItems.BATTERY_8).
                        nbt(nbt.copy()).
                        build())
        );

        AdvancementEntry advancedBatteryBox = addAdvancement(
                advancementOutput, battery8,
                EPBlocks.ADVANCED_BATTERY_BOX_ITEM, "advanced_battery_box", AdvancementFrame.TASK
        );

        AdvancementEntry advancedBatteryBoxMinecart = addAdvancement(
                advancementOutput, advancedBatteryBox,
                EPItems.ADVANCED_BATTERY_BOX_MINECART, "advanced_battery_box_minecart", AdvancementFrame.TASK
        );

        AdvancementEntry energizedCopperPlate = addAdvancement(
                advancementOutput, energizedPowerAdvanced,
                EPItems.ENERGIZED_COPPER_PLATE, "energized_copper_plate", AdvancementFrame.TASK,
                CommonItemTags.ENERGIZED_COPPER_PLATES
        );

        AdvancementEntry energizedCopperWire = addAdvancement(
                advancementOutput, energizedCopperPlate,
                EPItems.ENERGIZED_COPPER_WIRE, "energized_copper_wire", AdvancementFrame.TASK,
                CommonItemTags.ENERGIZED_COPPER_WIRES
        );

        AdvancementEntry advancedCircuit = addAdvancement(
                advancementOutput, energizedCopperWire,
                EPItems.ADVANCED_CIRCUIT, "advanced_circuit", AdvancementFrame.TASK
        );

        AdvancementEntry advancedUpgradeModule = addAdvancement(
                advancementOutput, advancedCircuit,
                EPItems.ADVANCED_UPGRADE_MODULE, "advanced_upgrade_module", AdvancementFrame.TASK
        );

        AdvancementEntry speedUpgradeUpgradeModule3 = addAdvancement(
                advancementOutput, advancedUpgradeModule,
                EPItems.SPEED_UPGRADE_MODULE_3, "speed_upgrade_module_3", AdvancementFrame.TASK
        );

        AdvancementEntry speedUpgradeUpgradeModule4 = addAdvancement(
                advancementOutput, speedUpgradeUpgradeModule3,
                EPItems.SPEED_UPGRADE_MODULE_4, "speed_upgrade_module_4", AdvancementFrame.TASK
        );

        AdvancementEntry energyEfficiencyUpgradeModule3 = addAdvancement(
                advancementOutput, advancedUpgradeModule,
                EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_3, "energy_efficiency_upgrade_module_3", AdvancementFrame.TASK
        );

        AdvancementEntry energyEfficiencyUpgradeModule4 = addAdvancement(
                advancementOutput, energyEfficiencyUpgradeModule3,
                EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_4, "energy_efficiency_upgrade_module_4", AdvancementFrame.TASK
        );

        AdvancementEntry energyCapacityUpgradeModule3 = addAdvancement(
                advancementOutput, advancedUpgradeModule,
                EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_3, "energy_capacity_upgrade_module_3", AdvancementFrame.TASK
        );

        AdvancementEntry energyCapacityUpgradeModule4 = addAdvancement(
                advancementOutput, energyCapacityUpgradeModule3,
                EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_4, "energy_capacity_upgrade_module_4", AdvancementFrame.TASK
        );

        AdvancementEntry rangeUpgradeModule1 = addAdvancement(
                advancementOutput, advancedUpgradeModule,
                EPItems.RANGE_UPGRADE_MODULE_1, "range_upgrade_module_1", AdvancementFrame.TASK
        );

        AdvancementEntry rangeUpgradeModule2 = addAdvancement(
                advancementOutput, rangeUpgradeModule1,
                EPItems.RANGE_UPGRADE_MODULE_2, "range_upgrade_module_2", AdvancementFrame.TASK
        );

        AdvancementEntry rangeUpgradeModule3 = addAdvancement(
                advancementOutput, rangeUpgradeModule2,
                EPItems.RANGE_UPGRADE_MODULE_3, "range_upgrade_module_3", AdvancementFrame.TASK
        );

        AdvancementEntry extractionDepthUpgradeModule3 = addAdvancement(
                advancementOutput, advancedUpgradeModule,
                EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_3, "extraction_depth_upgrade_module_3", AdvancementFrame.TASK
        );

        AdvancementEntry extractionDepthUpgradeModule4 = addAdvancement(
                advancementOutput, extractionDepthUpgradeModule3,
                EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_4, "extraction_depth_upgrade_module_4", AdvancementFrame.TASK
        );

        AdvancementEntry moonLightUpgradeModule2 = addAdvancement(
                advancementOutput, advancedUpgradeModule,
                EPItems.MOON_LIGHT_UPGRADE_MODULE_2, "moon_light_upgrade_module_2", AdvancementFrame.TASK
        );

        AdvancementEntry advancedMachineFrame = addAdvancement(
                advancementOutput, energizedPowerAdvanced,
                EPBlocks.ADVANCED_MACHINE_FRAME_ITEM, "advanced_machine_frame", AdvancementFrame.TASK
        );

        AdvancementEntry hvTransformers = addAdvancement(
                advancementOutput, advancedMachineFrame,
                EPBlocks.HV_TRANSFORMER_1_TO_N_ITEM, "hv_transformers", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(
                        ItemPredicate.Builder.create().items(
                                EPBlocks.HV_TRANSFORMER_1_TO_N_ITEM,
                                EPBlocks.HV_TRANSFORMER_3_TO_3_ITEM,
                                EPBlocks.HV_TRANSFORMER_N_TO_1_ITEM
                        ).build()
                )
        );

        AdvancementEntry advancedCrusher = addAdvancement(
                advancementOutput, advancedMachineFrame,
                EPBlocks.ADVANCED_CRUSHER_ITEM, "advanced_crusher", AdvancementFrame.TASK
        );

        AdvancementEntry advancedPulverizer = addAdvancement(
                advancementOutput, advancedMachineFrame,
                EPBlocks.ADVANCED_PULVERIZER_ITEM, "advanced_pulverizer", AdvancementFrame.TASK
        );

        AdvancementEntry lightningGenerator = addAdvancement(
                advancementOutput, advancedMachineFrame,
                EPBlocks.LIGHTNING_GENERATOR_ITEM, "lightning_generator", AdvancementFrame.TASK
        );

        AdvancementEntry crystalGrowthChamber = addAdvancement(
                advancementOutput, advancedMachineFrame,
                EPBlocks.CRYSTAL_GROWTH_CHAMBER_ITEM, "crystal_growth_chamber", AdvancementFrame.TASK
        );

        AdvancementEntry energizer = addAdvancement(
                advancementOutput, advancedMachineFrame,
                EPBlocks.ENERGIZER_ITEM, "energizer", AdvancementFrame.TASK
        );

        AdvancementEntry energizedGoldIngot = addAdvancement(
                advancementOutput, energizer,
                EPItems.ENERGIZED_GOLD_INGOT, "energized_gold_ingot", AdvancementFrame.TASK,
                CommonItemTags.ENERGIZED_GOLD_INGOTS
        );

        AdvancementEntry energizedGoldCable = addAdvancement(
                advancementOutput, energizedGoldIngot,
                EPBlocks.ENERGIZED_GOLD_CABLE_ITEM, "energized_gold_cable", AdvancementFrame.TASK
        );

        AdvancementEntry energizedGoldPlate = addAdvancement(
                advancementOutput, energizedGoldIngot,
                EPItems.ENERGIZED_GOLD_PLATE, "energized_gold_plate", AdvancementFrame.TASK,
                CommonItemTags.ENERGIZED_GOLD_PLATES
        );

        AdvancementEntry energizedGoldWire = addAdvancement(
                advancementOutput, energizedGoldPlate,
                EPItems.ENERGIZED_GOLD_WIRE, "energized_gold_wire", AdvancementFrame.TASK,
                CommonItemTags.ENERGIZED_GOLD_WIRES
        );

        AdvancementEntry processingUnit = addAdvancement(
                advancementOutput, energizedGoldWire,
                EPItems.PROCESSING_UNIT, "processing_unit", AdvancementFrame.TASK
        );

        AdvancementEntry reinforcedAdvancedUpgradeModule = addAdvancement(
                advancementOutput, processingUnit,
                EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE, "reinforced_advanced_upgrade_module", AdvancementFrame.TASK
        );

        AdvancementEntry speedUpgradeUpgradeModule5 = addAdvancement(
                advancementOutput, reinforcedAdvancedUpgradeModule,
                EPItems.SPEED_UPGRADE_MODULE_5, "speed_upgrade_module_5", AdvancementFrame.TASK
        );

        AdvancementEntry energyEfficiencyUpgradeModule5 = addAdvancement(
                advancementOutput, reinforcedAdvancedUpgradeModule,
                EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_5, "energy_efficiency_upgrade_module_5", AdvancementFrame.TASK
        );

        AdvancementEntry energyCapacityUpgradeModule5 = addAdvancement(
                advancementOutput, reinforcedAdvancedUpgradeModule,
                EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_5, "energy_capacity_upgrade_module_5", AdvancementFrame.TASK
        );

        AdvancementEntry durationUpgradeModule1 = addAdvancement(
                advancementOutput, reinforcedAdvancedUpgradeModule,
                EPItems.DURATION_UPGRADE_MODULE_1, "duration_upgrade_module_1", AdvancementFrame.TASK
        );

        AdvancementEntry durationUpgradeModule2 = addAdvancement(
                advancementOutput, durationUpgradeModule1,
                EPItems.DURATION_UPGRADE_MODULE_2, "duration_upgrade_module_2", AdvancementFrame.TASK
        );

        AdvancementEntry durationUpgradeModule3 = addAdvancement(
                advancementOutput, durationUpgradeModule2,
                EPItems.DURATION_UPGRADE_MODULE_3, "duration_upgrade_module_3", AdvancementFrame.TASK
        );

        AdvancementEntry durationUpgradeModule4 = addAdvancement(
                advancementOutput, durationUpgradeModule3,
                EPItems.DURATION_UPGRADE_MODULE_4, "duration_upgrade_module_4", AdvancementFrame.TASK
        );

        AdvancementEntry durationUpgradeModule5 = addAdvancement(
                advancementOutput, durationUpgradeModule4,
                EPItems.DURATION_UPGRADE_MODULE_5, "duration_upgrade_module_5", AdvancementFrame.TASK
        );

        AdvancementEntry durationUpgradeModule6 = addAdvancement(
                advancementOutput, durationUpgradeModule5,
                EPItems.DURATION_UPGRADE_MODULE_6, "duration_upgrade_module_6", AdvancementFrame.CHALLENGE
        );

        AdvancementEntry extractionDepthUpgradeModule5 = addAdvancement(
                advancementOutput, reinforcedAdvancedUpgradeModule,
                EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_5, "extraction_depth_upgrade_module_5", AdvancementFrame.TASK
        );

        AdvancementEntry moonLightUpgradeModule3 = addAdvancement(
                advancementOutput, reinforcedAdvancedUpgradeModule,
                EPItems.MOON_LIGHT_UPGRADE_MODULE_3, "moon_light_upgrade_module_3", AdvancementFrame.TASK
        );

        AdvancementEntry advancedAutoCrafter = addAdvancement(
                advancementOutput, energizedGoldIngot,
                EPBlocks.ADVANCED_AUTO_CRAFTER_ITEM, "advanced_auto_crafter", AdvancementFrame.TASK
        );

        AdvancementEntry advancedPoweredFurnace = addAdvancement(
                advancementOutput, energizedGoldIngot,
                EPBlocks.ADVANCED_POWERED_FURNACE_ITEM, "advanced_powered_furnace", AdvancementFrame.TASK
        );

        AdvancementEntry chargingStation = addAdvancement(
                advancementOutput, energizedGoldIngot,
                EPBlocks.CHARGING_STATION_ITEM, "charging_station", AdvancementFrame.TASK
        );

        AdvancementEntry advancedCharger = addAdvancement(
                advancementOutput, energizedGoldIngot,
                EPBlocks.ADVANCED_CHARGER_ITEM, "advanced_charger", AdvancementFrame.TASK
        );

        AdvancementEntry advancedMinecartCharger = addAdvancement(
                advancementOutput, advancedCharger,
                EPBlocks.ADVANCED_MINECART_CHARGER_ITEM, "advanced_minecart_charger", AdvancementFrame.TASK
        );

        AdvancementEntry advancedUncharger = addAdvancement(
                advancementOutput, energizedGoldIngot,
                EPBlocks.ADVANCED_UNCHARGER_ITEM, "advanced_uncharger", AdvancementFrame.TASK
        );

        AdvancementEntry advancedMinecartUncharger = addAdvancement(
                advancementOutput, advancedUncharger,
                EPBlocks.ADVANCED_MINECART_UNCHARGER_ITEM, "advanced_minecart_uncharger", AdvancementFrame.TASK
        );

        AdvancementEntry energizedCrystalMatrix = addAdvancement(
                advancementOutput, energizedGoldIngot,
                EPItems.ENERGIZED_CRYSTAL_MATRIX, "energized_crystal_matrix", AdvancementFrame.TASK
        );

        AdvancementEntry teleporterMatrix = addAdvancement(
                advancementOutput, energizedCrystalMatrix,
                EPItems.TELEPORTER_MATRIX, "teleporter_matrix", AdvancementFrame.TASK
        );

        AdvancementEntry teleporterProcessingUnit = addAdvancement(
                advancementOutput, teleporterMatrix,
                EPItems.TELEPORTER_PROCESSING_UNIT, "teleporter_processing_unit", AdvancementFrame.TASK
        );

        AdvancementEntry energizedCrystalMatrixCable = addAdvancement(
                advancementOutput, energizedCrystalMatrix,
                EPBlocks.ENERGIZED_CRYSTAL_MATRIX_CABLE_ITEM, "energized_crystal_matrix_cable", AdvancementFrame.CHALLENGE
        );

        AdvancementEntry reinforcedAdvancedSolarCell = addAdvancement(
                advancementOutput, energizedCrystalMatrix,
                EPItems.REINFORCED_ADVANCED_SOLAR_CELL, "reinforced_advanced_solar_cell", AdvancementFrame.TASK
        );

        AdvancementEntry solarPanel6 = addAdvancement(
                advancementOutput, reinforcedAdvancedSolarCell,
                EPBlocks.SOLAR_PANEL_ITEM_6, "solar_panel_6", AdvancementFrame.CHALLENGE
        );

        AdvancementEntry reinforcedAdvancedMachineFrame = addAdvancement(
                advancementOutput, energizedCrystalMatrix,
                EPBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM, "reinforced_advanced_machine_frame", AdvancementFrame.TASK
        );

        AdvancementEntry ehvTransformers = addAdvancement(
                advancementOutput, reinforcedAdvancedMachineFrame,
                EPBlocks.EHV_TRANSFORMER_1_TO_N_ITEM, "ehv_transformers", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(
                        ItemPredicate.Builder.create().items(
                                EPBlocks.EHV_TRANSFORMER_1_TO_N_ITEM,
                                EPBlocks.EHV_TRANSFORMER_3_TO_3_ITEM,
                                EPBlocks.EHV_TRANSFORMER_N_TO_1_ITEM
                        ).build()
                )
        );

        AdvancementEntry weatherController = addAdvancement(
                advancementOutput, reinforcedAdvancedMachineFrame,
                EPBlocks.WEATHER_CONTROLLER_ITEM, "weather_controller", AdvancementFrame.TASK
        );

        AdvancementEntry timeController = addAdvancement(
                advancementOutput, reinforcedAdvancedMachineFrame,
                EPBlocks.TIME_CONTROLLER_ITEM, "time_controller", AdvancementFrame.TASK
        );

        AdvancementEntry teleporter = addAdvancement(
                advancementOutput, reinforcedAdvancedMachineFrame,
                EPBlocks.TELEPORTER_ITEM, "teleporter", AdvancementFrame.TASK
        );

        AdvancementEntry inventoryTeleporter = addAdvancement(
                advancementOutput, teleporter,
                EPItems.INVENTORY_TELEPORTER, "inventory_teleporter", AdvancementFrame.TASK
        );
    }

    private AdvancementEntry addAdvancement(Consumer<AdvancementEntry> advancementOutput, AdvancementEntry parent,
                                            ItemConvertible icon, String advancementId, AdvancementFrame type) {
        return addAdvancement(advancementOutput, parent, icon, advancementId, type, icon);
    }
    private AdvancementEntry addAdvancement(Consumer<AdvancementEntry> advancementOutput, AdvancementEntry parent,
                                            ItemConvertible icon, String advancementId, AdvancementFrame type,
                                            ItemConvertible trigger) {
        return addAdvancement(advancementOutput, parent, new ItemStack(icon), advancementId, type,
                InventoryChangedCriterion.Conditions.items(trigger));
    }
    private AdvancementEntry addAdvancement(Consumer<AdvancementEntry> advancementOutput, AdvancementEntry parent,
                                            ItemConvertible icon, String advancementId, AdvancementFrame type,
                                            TagKey<Item> trigger) {
        return addAdvancement(advancementOutput, parent, new ItemStack(icon), advancementId, type,
                InventoryChangedCriterion.Conditions.items(ItemPredicate.Builder.create().tag(
                        trigger
                )));
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
                build(advancementOutput, EPAPI.MOD_ID + ":main/advanced/" + advancementId);
    }

    @Override
    public String getName() {
        return "Advancements (Advanced)";
    }
}