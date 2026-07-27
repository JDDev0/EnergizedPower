package me.jddev0.ep.datagen.advancement;

import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.datagen.generators.EPBaseAdvancementProvider;
import me.jddev0.ep.item.EPItems;
import me.jddev0.ep.registry.tags.CommonItemTags;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;

import java.util.function.Consumer;

public class ModEliteAdvancements extends EPBaseAdvancementProvider {
    public ModEliteAdvancements() {
        super("main/elite");
    }

    @Override
    protected void generateAdvancements(HolderLookup.Provider lookupProvider, Consumer<AdvancementHolder> advancementOutput) {
        AdvancementHolder energizedPowerElite = addRootAdvancement(
                lookupProvider, advancementOutput,
                "elite_machine_frame_top", EPItems.CRYSTALLIZED_LAPIS_LAZULI, "energizedpower_elite",
                CommonItemTags.GEMS_CRYSTALLIZED_LAPIS_LAZULI
        );

        AdvancementHolder crystallizedAlloyIngot = addAdvancement(
                lookupProvider,
                advancementOutput, energizedPowerElite,
                EPItems.CRYSTALLIZED_ALLOY_INGOT, "crystallized_alloy_ingot", AdvancementType.TASK,
                CommonItemTags.INGOTS_CRYSTALLIZED_ALLOY
        );

        AdvancementHolder crystallizedAlloyPlate = addAdvancement(
                lookupProvider,
                advancementOutput, crystallizedAlloyIngot,
                EPItems.CRYSTALLIZED_ALLOY_PLATE, "crystallized_alloy_plate", AdvancementType.TASK,
                CommonItemTags.PLATES_CRYSTALLIZED_ALLOY
        );

        AdvancementHolder pressurizedFluidPipe = addAdvancement(
                advancementOutput, crystallizedAlloyPlate,
                EPBlocks.PRESSURIZED_FLUID_PIPE_ITEM, "pressurized_fluid_pipe", AdvancementType.TASK
        );

        AdvancementHolder energizedAlloyIngot = addAdvancement(
                lookupProvider,
                advancementOutput, crystallizedAlloyIngot,
                EPItems.ENERGIZED_ALLOY_INGOT, "energized_alloy_ingot", AdvancementType.TASK,
                CommonItemTags.INGOTS_ENERGIZED_ALLOY
        );

        AdvancementHolder battery9 = addAdvancement(
                advancementOutput, energizedAlloyIngot,
                EPItems.BATTERY_9, "battery_9", AdvancementType.TASK
        );

        AdvancementHolder battery10 = addAdvancement(
                advancementOutput, battery9,
                EPItems.BATTERY_10, "battery_10", AdvancementType.CHALLENGE
        );

        AdvancementHolder eliteBatteryBox = addAdvancement(
                advancementOutput, battery10,
                EPBlocks.ELITE_BATTERY_BOX_ITEM, "elite_battery_box", AdvancementType.TASK
        );

        AdvancementHolder eliteBatteryBoxMinecart = addAdvancement(
                advancementOutput, eliteBatteryBox,
                EPItems.ELITE_BATTERY_BOX_MINECART, "elite_battery_box_minecart", AdvancementType.TASK
        );

        AdvancementHolder eliteMachineFrame = addAdvancement(
                advancementOutput, energizedAlloyIngot,
                EPBlocks.ELITE_MACHINE_FRAME_ITEM, "elite_machine_frame", AdvancementType.TASK
        );

        AdvancementHolder uhvTransformers = addAdvancement(
                advancementOutput, eliteMachineFrame,
                EPBlocks.UHV_TRANSFORMER_1_TO_N_ITEM, "uhv_transformers", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(
                        ItemPredicate.Builder.item().of(
                                EPBlocks.UHV_TRANSFORMER_1_TO_N_ITEM,
                                EPBlocks.UHV_TRANSFORMER_3_TO_3_ITEM,
                                EPBlocks.UHV_TRANSFORMER_N_TO_1_ITEM,
                                EPBlocks.CONFIGURABLE_UHV_TRANSFORMER_ITEM
                        ).build()
                )
        );

        AdvancementHolder eliteCharger = addAdvancement(
                advancementOutput, eliteMachineFrame,
                EPBlocks.ELITE_CHARGER_ITEM, "elite_charger", AdvancementType.TASK
        );

        AdvancementHolder eliteMinecartCharger = addAdvancement(
                advancementOutput, eliteCharger,
                EPBlocks.ELITE_MINECART_CHARGER_ITEM, "elite_minecart_charger", AdvancementType.TASK
        );

        AdvancementHolder eliteUncharger = addAdvancement(
                advancementOutput, eliteMachineFrame,
                EPBlocks.ELITE_UNCHARGER_ITEM, "elite_uncharger", AdvancementType.TASK
        );

        AdvancementHolder energizedAlloyPlate = addAdvancement(
                lookupProvider,
                advancementOutput, energizedAlloyIngot,
                EPItems.ENERGIZED_ALLOY_PLATE, "energized_alloy_plate", AdvancementType.TASK,
                CommonItemTags.PLATES_ENERGIZED_ALLOY
        );

        AdvancementHolder superconductor = addAdvancement(
                lookupProvider,
                advancementOutput, energizedAlloyPlate,
                EPItems.SUPERCONDUCTOR, "superconductor", AdvancementType.TASK,
                CommonItemTags.WIRES_SUPERCONDUCTOR
        );

        AdvancementHolder superconductiveCable = addAdvancement(
                advancementOutput, superconductor,
                EPBlocks.SUPERCONDUCTIVE_CABLE_ITEM, "superconductive_cable", AdvancementType.CHALLENGE
        );

        AdvancementHolder eliteSolarCell = addAdvancement(
                advancementOutput, energizedAlloyIngot,
                EPItems.ELITE_SOLAR_CELL, "elite_solar_cell", AdvancementType.TASK
        );

        AdvancementHolder solarPanel7 = addAdvancement(
                advancementOutput, eliteSolarCell,
                EPBlocks.SOLAR_PANEL_ITEM_7, "solar_panel_7", AdvancementType.CHALLENGE
        );

        AdvancementHolder quantumProcessingUnit = addAdvancement(
                advancementOutput, superconductor,
                EPItems.QUANTUM_PROCESSING_UNIT, "quantum_processing_unit", AdvancementType.TASK
        );

        AdvancementHolder eliteUpgradeModule = addAdvancement(
                advancementOutput, quantumProcessingUnit,
                EPItems.ELITE_UPGRADE_MODULE, "elite_upgrade_module", AdvancementType.TASK
        );

        AdvancementHolder speedUpgradeModule7 = addAdvancement(
                advancementOutput, eliteUpgradeModule,
                EPItems.SPEED_UPGRADE_MODULE_7, "speed_upgrade_module_7", AdvancementType.TASK
        );

        AdvancementHolder speedUpgradeModule8 = addAdvancement(
                advancementOutput, speedUpgradeModule7,
                EPItems.SPEED_UPGRADE_MODULE_8, "speed_upgrade_module_8", AdvancementType.TASK
        );

        AdvancementHolder energizingSpeedUpgradeUpgradeModule5 = addAdvancement(
                advancementOutput, eliteUpgradeModule,
                EPItems.ENERGIZING_SPEED_UPGRADE_MODULE_5, "energizing_speed_upgrade_module_5", AdvancementType.TASK
        );

        AdvancementHolder energizingSpeedUpgradeUpgradeModule6 = addAdvancement(
                advancementOutput, energizingSpeedUpgradeUpgradeModule5,
                EPItems.ENERGIZING_SPEED_UPGRADE_MODULE_6, "energizing_speed_upgrade_module_6", AdvancementType.TASK
        );

        AdvancementHolder energizingSpeedUpgradeUpgradeModule7 = addAdvancement(
                advancementOutput, energizingSpeedUpgradeUpgradeModule6,
                EPItems.ENERGIZING_SPEED_UPGRADE_MODULE_7, "energizing_speed_upgrade_module_7", AdvancementType.TASK
        );

        AdvancementHolder energizingSpeedUpgradeUpgradeModule8 = addAdvancement(
                advancementOutput, energizingSpeedUpgradeUpgradeModule7,
                EPItems.ENERGIZING_SPEED_UPGRADE_MODULE_8, "energizing_speed_upgrade_module_8", AdvancementType.TASK
        );

        AdvancementHolder energyEfficiencyUpgradeModule7 = addAdvancement(
                advancementOutput, eliteUpgradeModule,
                EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_7, "energy_efficiency_upgrade_module_7", AdvancementType.TASK
        );

        AdvancementHolder energyEfficiencyUpgradeModule8 = addAdvancement(
                advancementOutput, energyEfficiencyUpgradeModule7,
                EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_8, "energy_efficiency_upgrade_module_8", AdvancementType.TASK
        );

        AdvancementHolder energyProductionUpgradeModule7 = addAdvancement(
                advancementOutput, eliteUpgradeModule,
                EPItems.ENERGY_PRODUCTION_UPGRADE_MODULE_7, "energy_production_upgrade_module_7", AdvancementType.TASK
        );

        AdvancementHolder energyProductionUpgradeModule8 = addAdvancement(
                advancementOutput, energyProductionUpgradeModule7,
                EPItems.ENERGY_PRODUCTION_UPGRADE_MODULE_8, "energy_production_upgrade_module_8", AdvancementType.TASK
        );

        AdvancementHolder energyCapacityUpgradeModule7 = addAdvancement(
                advancementOutput, eliteUpgradeModule,
                EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_7, "energy_capacity_upgrade_module_7", AdvancementType.TASK
        );

        AdvancementHolder energyCapacityUpgradeModule8 = addAdvancement(
                advancementOutput, energyCapacityUpgradeModule7,
                EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_8, "energy_capacity_upgrade_module_8", AdvancementType.TASK
        );

        AdvancementHolder moonLightUpgradeModule4 = addAdvancement(
                advancementOutput, eliteUpgradeModule,
                EPItems.MOON_LIGHT_UPGRADE_MODULE_4, "moon_light_upgrade_module_4", AdvancementType.TASK
        );

        AdvancementHolder moonLightUpgradeModule5 = addAdvancement(
                advancementOutput, moonLightUpgradeModule4,
                EPItems.MOON_LIGHT_UPGRADE_MODULE_5, "moon_light_upgrade_module_5", AdvancementType.TASK
        );

        AdvancementHolder xpExtractionUpgradeModule7 = addAdvancement(
                advancementOutput, eliteUpgradeModule,
                EPItems.XP_EXTRACTION_UPGRADE_MODULE_7, "xp_extraction_upgrade_module_7", AdvancementType.TASK
        );

        AdvancementHolder xpExtractionUpgradeModule8 = addAdvancement(
                advancementOutput, xpExtractionUpgradeModule7,
                EPItems.XP_EXTRACTION_UPGRADE_MODULE_8, "xp_extraction_upgrade_module_8", AdvancementType.TASK
        );

        AdvancementHolder coolantCell = addAdvancement(
                advancementOutput, energizedPowerElite,
                EPItems.COOLANT_CELL, "coolant_cell", AdvancementType.TASK
        );
    }
}
