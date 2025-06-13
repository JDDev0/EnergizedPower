package me.jddev0.ep.datagen.advancement;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.item.BatteryItem;
import me.jddev0.ep.item.EPItems;
import me.jddev0.ep.registry.tags.CommonItemTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
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
    public void generateAdvancement(Consumer<Advancement> advancementOutput) {
        Advancement energizedPowerAdvanced = Advancement.Builder.create().
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
                        ).build())).
                build(advancementOutput, EPAPI.MOD_ID + ":main/advanced/energizedpower_advanced");

        Advancement advancedAlloyIngot = addAdvancement(
                advancementOutput, energizedPowerAdvanced,
                EPItems.ADVANCED_ALLOY_INGOT, "advanced_alloy_ingot", AdvancementFrame.TASK,
                CommonItemTags.ADVANCED_ALLOY_INGOTS
        );

        Advancement advancedAlloyPlate = addAdvancement(
                advancementOutput, advancedAlloyIngot,
                EPItems.ADVANCED_ALLOY_PLATE, "advanced_alloy_plate", AdvancementFrame.TASK,
                CommonItemTags.ADVANCED_ALLOY_PLATES
        );

        Advancement energizedCopperCable = addAdvancement(
                advancementOutput, energizedPowerAdvanced,
                EPBlocks.ENERGIZED_COPPER_CABLE_ITEM, "energized_copper_cable", AdvancementFrame.TASK
        );

        Advancement advancedSolarCell = addAdvancement(
                advancementOutput, energizedPowerAdvanced,
                EPItems.ADVANCED_SOLAR_CELL, "advanced_solar_cell", AdvancementFrame.TASK
        );

        Advancement solarPanel4 = addAdvancement(
                advancementOutput, advancedSolarCell,
                EPBlocks.SOLAR_PANEL_ITEM_4, "solar_panel_4", AdvancementFrame.TASK
        );

        Advancement solarPanel5 = addAdvancement(
                advancementOutput, solarPanel4,
                EPBlocks.SOLAR_PANEL_ITEM_5, "solar_panel_5", AdvancementFrame.TASK
        );

        Advancement battery6 = addAdvancement(
                advancementOutput, energizedPowerAdvanced,
                EPItems.BATTERY_6, "battery_6", AdvancementFrame.TASK
        );

        Advancement battery7 = addAdvancement(
                advancementOutput, battery6,
                EPItems.BATTERY_7, "battery_7", AdvancementFrame.TASK
        );

        Advancement battery8 = addAdvancement(
                advancementOutput, battery7,
                EPItems.BATTERY_8, "battery_8", AdvancementFrame.GOAL
        );

        ItemStack battery8FullyChargedIcon = new ItemStack(EPItems.BATTERY_8);
        NbtCompound nbt = battery8FullyChargedIcon.getOrCreateNbt();
        nbt.putLong(SimpleEnergyItem.ENERGY_KEY, BatteryItem.Tier.BATTERY_8.getCapacity());
        Advancement battery8FullyCharged = addAdvancement(
                advancementOutput, battery8,
                battery8FullyChargedIcon, "battery_8_fully_charged", AdvancementFrame.CHALLENGE,
                InventoryChangedCriterion.Conditions.items(ItemPredicate.Builder.create().
                        items(EPItems.BATTERY_8).
                        nbt(nbt.copy()).
                        build())
        );

        Advancement advancedBatteryBox = addAdvancement(
                advancementOutput, battery8,
                EPBlocks.ADVANCED_BATTERY_BOX_ITEM, "advanced_battery_box", AdvancementFrame.TASK
        );

        Advancement advancedBatteryBoxMinecart = addAdvancement(
                advancementOutput, advancedBatteryBox,
                EPItems.ADVANCED_BATTERY_BOX_MINECART, "advanced_battery_box_minecart", AdvancementFrame.TASK
        );

        Advancement energizedCopperPlate = addAdvancement(
                advancementOutput, energizedPowerAdvanced,
                EPItems.ENERGIZED_COPPER_PLATE, "energized_copper_plate", AdvancementFrame.TASK,
                CommonItemTags.ENERGIZED_COPPER_PLATES
        );

        Advancement energizedCopperWire = addAdvancement(
                advancementOutput, energizedCopperPlate,
                EPItems.ENERGIZED_COPPER_WIRE, "energized_copper_wire", AdvancementFrame.TASK,
                CommonItemTags.ENERGIZED_COPPER_WIRES
        );

        Advancement advancedCircuit = addAdvancement(
                advancementOutput, energizedCopperWire,
                EPItems.ADVANCED_CIRCUIT, "advanced_circuit", AdvancementFrame.TASK
        );

        Advancement advancedUpgradeModule = addAdvancement(
                advancementOutput, advancedCircuit,
                EPItems.ADVANCED_UPGRADE_MODULE, "advanced_upgrade_module", AdvancementFrame.TASK
        );

        Advancement speedUpgradeUpgradeModule3 = addAdvancement(
                advancementOutput, advancedUpgradeModule,
                EPItems.SPEED_UPGRADE_MODULE_3, "speed_upgrade_module_3", AdvancementFrame.TASK
        );

        Advancement speedUpgradeUpgradeModule4 = addAdvancement(
                advancementOutput, speedUpgradeUpgradeModule3,
                EPItems.SPEED_UPGRADE_MODULE_4, "speed_upgrade_module_4", AdvancementFrame.TASK
        );

        Advancement energyEfficiencyUpgradeModule3 = addAdvancement(
                advancementOutput, advancedUpgradeModule,
                EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_3, "energy_efficiency_upgrade_module_3", AdvancementFrame.TASK
        );

        Advancement energyEfficiencyUpgradeModule4 = addAdvancement(
                advancementOutput, energyEfficiencyUpgradeModule3,
                EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_4, "energy_efficiency_upgrade_module_4", AdvancementFrame.TASK
        );

        Advancement energyCapacityUpgradeModule3 = addAdvancement(
                advancementOutput, advancedUpgradeModule,
                EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_3, "energy_capacity_upgrade_module_3", AdvancementFrame.TASK
        );

        Advancement energyCapacityUpgradeModule4 = addAdvancement(
                advancementOutput, energyCapacityUpgradeModule3,
                EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_4, "energy_capacity_upgrade_module_4", AdvancementFrame.TASK
        );

        Advancement rangeUpgradeModule1 = addAdvancement(
                advancementOutput, advancedUpgradeModule,
                EPItems.RANGE_UPGRADE_MODULE_1, "range_upgrade_module_1", AdvancementFrame.TASK
        );

        Advancement rangeUpgradeModule2 = addAdvancement(
                advancementOutput, rangeUpgradeModule1,
                EPItems.RANGE_UPGRADE_MODULE_2, "range_upgrade_module_2", AdvancementFrame.TASK
        );

        Advancement rangeUpgradeModule3 = addAdvancement(
                advancementOutput, rangeUpgradeModule2,
                EPItems.RANGE_UPGRADE_MODULE_3, "range_upgrade_module_3", AdvancementFrame.TASK
        );

        Advancement extractionDepthUpgradeModule3 = addAdvancement(
                advancementOutput, advancedUpgradeModule,
                EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_3, "extraction_depth_upgrade_module_3", AdvancementFrame.TASK
        );

        Advancement extractionDepthUpgradeModule4 = addAdvancement(
                advancementOutput, extractionDepthUpgradeModule3,
                EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_4, "extraction_depth_upgrade_module_4", AdvancementFrame.TASK
        );

        Advancement extractionRangeUpgradeModule3 = addAdvancement(
                advancementOutput, advancedUpgradeModule,
                EPItems.EXTRACTION_RANGE_UPGRADE_MODULE_3, "extraction_range_upgrade_module_3", AdvancementFrame.TASK
        );

        Advancement extractionRangeUpgradeModule4 = addAdvancement(
                advancementOutput, extractionRangeUpgradeModule3,
                EPItems.EXTRACTION_RANGE_UPGRADE_MODULE_4, "extraction_range_upgrade_module_4", AdvancementFrame.TASK
        );

        Advancement moonLightUpgradeModule2 = addAdvancement(
                advancementOutput, advancedUpgradeModule,
                EPItems.MOON_LIGHT_UPGRADE_MODULE_2, "moon_light_upgrade_module_2", AdvancementFrame.TASK
        );

        Advancement advancedMachineFrame = addAdvancement(
                advancementOutput, energizedPowerAdvanced,
                EPBlocks.ADVANCED_MACHINE_FRAME_ITEM, "advanced_machine_frame", AdvancementFrame.TASK
        );

        Advancement hvTransformers = addAdvancement(
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

        Advancement advancedCrusher = addAdvancement(
                advancementOutput, advancedMachineFrame,
                EPBlocks.ADVANCED_CRUSHER_ITEM, "advanced_crusher", AdvancementFrame.TASK
        );

        Advancement advancedPulverizer = addAdvancement(
                advancementOutput, advancedMachineFrame,
                EPBlocks.ADVANCED_PULVERIZER_ITEM, "advanced_pulverizer", AdvancementFrame.TASK
        );

        Advancement lightningGenerator = addAdvancement(
                advancementOutput, advancedMachineFrame,
                EPBlocks.LIGHTNING_GENERATOR_ITEM, "lightning_generator", AdvancementFrame.TASK
        );

        Advancement crystalGrowthChamber = addAdvancement(
                advancementOutput, advancedMachineFrame,
                EPBlocks.CRYSTAL_GROWTH_CHAMBER_ITEM, "crystal_growth_chamber", AdvancementFrame.TASK
        );

        Advancement energizer = addAdvancement(
                advancementOutput, advancedMachineFrame,
                EPBlocks.ENERGIZER_ITEM, "energizer", AdvancementFrame.TASK
        );

        Advancement energizedGoldIngot = addAdvancement(
                advancementOutput, energizer,
                EPItems.ENERGIZED_GOLD_INGOT, "energized_gold_ingot", AdvancementFrame.TASK,
                CommonItemTags.ENERGIZED_GOLD_INGOTS
        );

        Advancement energizedGoldCable = addAdvancement(
                advancementOutput, energizedGoldIngot,
                EPBlocks.ENERGIZED_GOLD_CABLE_ITEM, "energized_gold_cable", AdvancementFrame.TASK
        );

        Advancement energizedGoldPlate = addAdvancement(
                advancementOutput, energizedGoldIngot,
                EPItems.ENERGIZED_GOLD_PLATE, "energized_gold_plate", AdvancementFrame.TASK,
                CommonItemTags.ENERGIZED_GOLD_PLATES
        );

        Advancement energizedGoldWire = addAdvancement(
                advancementOutput, energizedGoldPlate,
                EPItems.ENERGIZED_GOLD_WIRE, "energized_gold_wire", AdvancementFrame.TASK,
                CommonItemTags.ENERGIZED_GOLD_WIRES
        );

        Advancement processingUnit = addAdvancement(
                advancementOutput, energizedGoldWire,
                EPItems.PROCESSING_UNIT, "processing_unit", AdvancementFrame.TASK
        );

        Advancement reinforcedAdvancedUpgradeModule = addAdvancement(
                advancementOutput, processingUnit,
                EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE, "reinforced_advanced_upgrade_module", AdvancementFrame.TASK
        );

        Advancement speedUpgradeUpgradeModule5 = addAdvancement(
                advancementOutput, reinforcedAdvancedUpgradeModule,
                EPItems.SPEED_UPGRADE_MODULE_5, "speed_upgrade_module_5", AdvancementFrame.TASK
        );

        Advancement energyEfficiencyUpgradeModule5 = addAdvancement(
                advancementOutput, reinforcedAdvancedUpgradeModule,
                EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_5, "energy_efficiency_upgrade_module_5", AdvancementFrame.TASK
        );

        Advancement energyCapacityUpgradeModule5 = addAdvancement(
                advancementOutput, reinforcedAdvancedUpgradeModule,
                EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_5, "energy_capacity_upgrade_module_5", AdvancementFrame.TASK
        );

        Advancement durationUpgradeModule1 = addAdvancement(
                advancementOutput, reinforcedAdvancedUpgradeModule,
                EPItems.DURATION_UPGRADE_MODULE_1, "duration_upgrade_module_1", AdvancementFrame.TASK
        );

        Advancement durationUpgradeModule2 = addAdvancement(
                advancementOutput, durationUpgradeModule1,
                EPItems.DURATION_UPGRADE_MODULE_2, "duration_upgrade_module_2", AdvancementFrame.TASK
        );

        Advancement durationUpgradeModule3 = addAdvancement(
                advancementOutput, durationUpgradeModule2,
                EPItems.DURATION_UPGRADE_MODULE_3, "duration_upgrade_module_3", AdvancementFrame.TASK
        );

        Advancement durationUpgradeModule4 = addAdvancement(
                advancementOutput, durationUpgradeModule3,
                EPItems.DURATION_UPGRADE_MODULE_4, "duration_upgrade_module_4", AdvancementFrame.TASK
        );

        Advancement durationUpgradeModule5 = addAdvancement(
                advancementOutput, durationUpgradeModule4,
                EPItems.DURATION_UPGRADE_MODULE_5, "duration_upgrade_module_5", AdvancementFrame.TASK
        );

        Advancement durationUpgradeModule6 = addAdvancement(
                advancementOutput, durationUpgradeModule5,
                EPItems.DURATION_UPGRADE_MODULE_6, "duration_upgrade_module_6", AdvancementFrame.CHALLENGE
        );

        Advancement extractionDepthUpgradeModule5 = addAdvancement(
                advancementOutput, reinforcedAdvancedUpgradeModule,
                EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_5, "extraction_depth_upgrade_module_5", AdvancementFrame.TASK
        );

        Advancement extractionRangeUpgradeModule5 = addAdvancement(
                advancementOutput, reinforcedAdvancedUpgradeModule,
                EPItems.EXTRACTION_RANGE_UPGRADE_MODULE_5, "extraction_range_upgrade_module_5", AdvancementFrame.TASK
        );

        Advancement moonLightUpgradeModule3 = addAdvancement(
                advancementOutput, reinforcedAdvancedUpgradeModule,
                EPItems.MOON_LIGHT_UPGRADE_MODULE_3, "moon_light_upgrade_module_3", AdvancementFrame.TASK
        );

        Advancement advancedAutoCrafter = addAdvancement(
                advancementOutput, energizedGoldIngot,
                EPBlocks.ADVANCED_AUTO_CRAFTER_ITEM, "advanced_auto_crafter", AdvancementFrame.TASK
        );

        Advancement advancedFluidPump = addAdvancement(
                advancementOutput, energizedGoldIngot,
                EPBlocks.ADVANCED_FLUID_PUMP_ITEM, "advanced_fluid_pump", AdvancementFrame.TASK
        );

        Advancement advancedPoweredFurnace = addAdvancement(
                advancementOutput, energizedGoldIngot,
                EPBlocks.ADVANCED_POWERED_FURNACE_ITEM, "advanced_powered_furnace", AdvancementFrame.TASK
        );

        Advancement chargingStation = addAdvancement(
                advancementOutput, energizedGoldIngot,
                EPBlocks.CHARGING_STATION_ITEM, "charging_station", AdvancementFrame.TASK
        );

        Advancement advancedCharger = addAdvancement(
                advancementOutput, energizedGoldIngot,
                EPBlocks.ADVANCED_CHARGER_ITEM, "advanced_charger", AdvancementFrame.TASK
        );

        Advancement advancedMinecartCharger = addAdvancement(
                advancementOutput, advancedCharger,
                EPBlocks.ADVANCED_MINECART_CHARGER_ITEM, "advanced_minecart_charger", AdvancementFrame.TASK
        );

        Advancement advancedUncharger = addAdvancement(
                advancementOutput, energizedGoldIngot,
                EPBlocks.ADVANCED_UNCHARGER_ITEM, "advanced_uncharger", AdvancementFrame.TASK
        );

        Advancement advancedMinecartUncharger = addAdvancement(
                advancementOutput, advancedUncharger,
                EPBlocks.ADVANCED_MINECART_UNCHARGER_ITEM, "advanced_minecart_uncharger", AdvancementFrame.TASK
        );

        Advancement energizedCrystalMatrix = addAdvancement(
                advancementOutput, energizedGoldIngot,
                EPItems.ENERGIZED_CRYSTAL_MATRIX, "energized_crystal_matrix", AdvancementFrame.TASK
        );

        Advancement teleporterMatrix = addAdvancement(
                advancementOutput, energizedCrystalMatrix,
                EPItems.TELEPORTER_MATRIX, "teleporter_matrix", AdvancementFrame.TASK
        );

        Advancement teleporterProcessingUnit = addAdvancement(
                advancementOutput, teleporterMatrix,
                EPItems.TELEPORTER_PROCESSING_UNIT, "teleporter_processing_unit", AdvancementFrame.TASK
        );

        Advancement energizedCrystalMatrixCable = addAdvancement(
                advancementOutput, energizedCrystalMatrix,
                EPBlocks.ENERGIZED_CRYSTAL_MATRIX_CABLE_ITEM, "energized_crystal_matrix_cable", AdvancementFrame.CHALLENGE
        );

        Advancement reinforcedAdvancedSolarCell = addAdvancement(
                advancementOutput, energizedCrystalMatrix,
                EPItems.REINFORCED_ADVANCED_SOLAR_CELL, "reinforced_advanced_solar_cell", AdvancementFrame.TASK
        );

        Advancement solarPanel6 = addAdvancement(
                advancementOutput, reinforcedAdvancedSolarCell,
                EPBlocks.SOLAR_PANEL_ITEM_6, "solar_panel_6", AdvancementFrame.CHALLENGE
        );

        Advancement reinforcedAdvancedMachineFrame = addAdvancement(
                advancementOutput, energizedCrystalMatrix,
                EPBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM, "reinforced_advanced_machine_frame", AdvancementFrame.TASK
        );

        Advancement ehvTransformers = addAdvancement(
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

        Advancement weatherController = addAdvancement(
                advancementOutput, reinforcedAdvancedMachineFrame,
                EPBlocks.WEATHER_CONTROLLER_ITEM, "weather_controller", AdvancementFrame.TASK
        );

        Advancement timeController = addAdvancement(
                advancementOutput, reinforcedAdvancedMachineFrame,
                EPBlocks.TIME_CONTROLLER_ITEM, "time_controller", AdvancementFrame.TASK
        );

        Advancement teleporter = addAdvancement(
                advancementOutput, reinforcedAdvancedMachineFrame,
                EPBlocks.TELEPORTER_ITEM, "teleporter", AdvancementFrame.TASK
        );

        Advancement inventoryTeleporter = addAdvancement(
                advancementOutput, teleporter,
                EPItems.INVENTORY_TELEPORTER, "inventory_teleporter", AdvancementFrame.TASK
        );
    }

    private Advancement addAdvancement(Consumer<Advancement> advancementOutput, Advancement parent,
                                       ItemConvertible icon, String advancementId, AdvancementFrame type) {
        return addAdvancement(advancementOutput, parent, icon, advancementId, type, icon);
    }
    private Advancement addAdvancement(Consumer<Advancement> advancementOutput, Advancement parent,
                                       ItemConvertible icon, String advancementId, AdvancementFrame type,
                                       ItemConvertible trigger) {
        return addAdvancement(advancementOutput, parent, new ItemStack(icon), advancementId, type,
                InventoryChangedCriterion.Conditions.items(trigger));
    }
    private Advancement addAdvancement(Consumer<Advancement> advancementOutput, Advancement parent,
                                       ItemConvertible icon, String advancementId, AdvancementFrame type,
                                       TagKey<Item> trigger) {
        return addAdvancement(advancementOutput, parent, new ItemStack(icon), advancementId, type,
                InventoryChangedCriterion.Conditions.items(ItemPredicate.Builder.create().tag(
                        trigger
                ).build()));
    }
    private Advancement addAdvancement(Consumer<Advancement> advancementOutput, Advancement parent,
                                       ItemConvertible icon, String advancementId, AdvancementFrame type,
                                       AbstractCriterionConditions trigger) {
        return addAdvancement(advancementOutput, parent, new ItemStack(icon), advancementId, type, trigger);
    }
    private Advancement addAdvancement(Consumer<Advancement> advancementOutput, Advancement parent,
                                            ItemStack icon, String advancementId, AdvancementFrame type,
                                            AbstractCriterionConditions trigger) {
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
