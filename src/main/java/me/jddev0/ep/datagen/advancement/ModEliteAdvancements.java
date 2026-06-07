package me.jddev0.ep.datagen.advancement;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.component.EPDataComponentTypes;
import me.jddev0.ep.item.EPItems;
import me.jddev0.ep.machine.tier.BatteryTier;
import me.jddev0.ep.registry.tags.CommonItemTags;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.DataComponentMatchers;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentExactPredicate;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.ItemLike;

import java.util.function.Consumer;

public class ModEliteAdvancements implements AdvancementSubProvider {
    @Override
    public void generate(HolderLookup.Provider lookupProvider, Consumer<AdvancementHolder> advancementOutput) {
        AdvancementHolder energizedPowerElite = Advancement.Builder.advancement().
                display(
                        EPItems.CRYSTALLIZED_LAPIS_LAZULI,
                        Component.translatable("advancements.energizedpower.energizedpower_elite.title"),
                        Component.translatable("advancements.energizedpower.energizedpower_elite.description"),
                        EPAPI.id("block/elite_machine_frame_top"),
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                ).
                addCriterion("has_the_item",
                        InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(
                                lookupProvider.lookupOrThrow(Registries.ITEM),
                                CommonItemTags.GEMS_CRYSTALLIZED_LAPIS_LAZULI
                        ))).
                save(advancementOutput, EPAPI.id("main/elite/energizedpower_elite"));

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

        AdvancementHolder coolantCell = addAdvancement(
                advancementOutput, energizedPowerElite,
                EPItems.COOLANT_CELL, "coolant_cell", AdvancementType.TASK
        );
    }

    private AdvancementHolder addAdvancement(Consumer<AdvancementHolder> advancementOutput,
                                             AdvancementHolder parent, ItemLike icon, String advancementId, AdvancementType type) {
        return addAdvancement(advancementOutput, parent, icon, advancementId, type, icon);
    }
    private AdvancementHolder addAdvancement(Consumer<AdvancementHolder> advancementOutput,
                                             AdvancementHolder parent, ItemLike icon, String advancementId, AdvancementType type,
                                             ItemLike trigger) {
        return addAdvancement(advancementOutput, parent, new ItemStackTemplate(icon.asItem()), advancementId, type,
                InventoryChangeTrigger.TriggerInstance.hasItems(trigger));
    }
    private AdvancementHolder addAdvancement(HolderLookup.Provider lookupProvider, Consumer<AdvancementHolder> advancementOutput,
                                             AdvancementHolder parent, ItemLike icon, String advancementId, AdvancementType type,
                                             TagKey<Item> trigger) {
        return addAdvancement(advancementOutput, parent, new ItemStackTemplate(icon.asItem()), advancementId, type,
                InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(
                        lookupProvider.lookupOrThrow(Registries.ITEM),
                        trigger
                )));
    }
    private AdvancementHolder addAdvancement(Consumer<AdvancementHolder> advancementOutput,
                                             AdvancementHolder parent, ItemLike icon, String advancementId, AdvancementType type,
                                             Criterion<?> trigger) {
        return addAdvancement(advancementOutput, parent, new ItemStackTemplate(icon.asItem()), advancementId, type, trigger);
    }
    private AdvancementHolder addAdvancement(Consumer<AdvancementHolder> advancementOutput,
                                             AdvancementHolder parent, ItemStackTemplate icon, String advancementId, AdvancementType type,
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
                save(advancementOutput, EPAPI.id("main/elite/" + advancementId));
    }
}
