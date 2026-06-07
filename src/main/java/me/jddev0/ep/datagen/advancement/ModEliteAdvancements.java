package me.jddev0.ep.datagen.advancement;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.component.EPDataComponentTypes;
import me.jddev0.ep.item.EPItems;
import me.jddev0.ep.machine.tier.BatteryTier;
import me.jddev0.ep.registry.tags.CommonItemTags;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
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

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ModEliteAdvancements extends FabricAdvancementProvider {
    public ModEliteAdvancements(FabricPackOutput dataOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(dataOutput, lookupProvider);
    }

    @Override
    public void generateAdvancement(HolderLookup.Provider lookupProvider, Consumer<AdvancementHolder> advancementOutput) {
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
                save(advancementOutput, EPAPI.MOD_ID + ":main/elite/energizedpower_elite");

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

        AdvancementHolder quantumProcessingUnit = addAdvancement(
                advancementOutput, superconductor,
                EPItems.QUANTUM_PROCESSING_UNIT, "quantum_prcessing_unit", AdvancementType.TASK
        );

        AdvancementHolder eliteUpgradeModule = addAdvancement(
                advancementOutput, quantumProcessingUnit,
                EPItems.ELITE_UPGRADE_MODULE, "elite_upgrade_module", AdvancementType.TASK
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
                save(advancementOutput, EPAPI.MOD_ID + ":main/elite/" + advancementId);
    }

    @Override
    public String getName() {
        return "Advancements (Elite)";
    }
}
