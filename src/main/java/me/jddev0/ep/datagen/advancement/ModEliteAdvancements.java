package me.jddev0.ep.datagen.advancement;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.component.EPDataComponentTypes;
import me.jddev0.ep.item.EPItems;
import me.jddev0.ep.machine.tier.BatteryTier;
import me.jddev0.ep.registry.tags.CommonItemTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
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
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ModEliteAdvancements extends FabricAdvancementProvider {
    public ModEliteAdvancements(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(dataOutput, lookupProvider);
    }

    @Override
    public void generateAdvancement(HolderLookup.Provider lookupProvider, Consumer<AdvancementHolder> advancementOutput) {
        AdvancementHolder energizedPowerElite = Advancement.Builder.advancement().
                display(
                        EPItems.CRYSTALLIZED_LAPIS_LAZULI,
                        Component.translatable("advancements.energizedpower.energizedpower_elite.title"),
                        Component.translatable("advancements.energizedpower.energizedpower_elite.description"),
                        EPAPI.id("textures/block/elite_machine_frame_top.png"),
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                ).
                addCriterion("has_the_item",
                        InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(
                                CommonItemTags.GEMS_CRYSTALLIZED_LAPIS_LAZULI
                        ))).
                save(advancementOutput, EPAPI.MOD_ID + ":main/elite/energizedpower_elite");

        AdvancementHolder crystallizedAlloyIngot = addAdvancement(
                advancementOutput, energizedPowerElite,
                EPItems.CRYSTALLIZED_ALLOY_INGOT, "crystallized_alloy_ingot", AdvancementType.TASK,
                CommonItemTags.INGOTS_CRYSTALLIZED_ALLOY
        );

        AdvancementHolder crystallizedAlloyPlate = addAdvancement(
                advancementOutput, crystallizedAlloyIngot,
                EPItems.CRYSTALLIZED_ALLOY_PLATE, "crystallized_alloy_plate", AdvancementType.TASK,
                CommonItemTags.PLATES_CRYSTALLIZED_ALLOY
        );

        AdvancementHolder energizedAlloyIngot = addAdvancement(
                advancementOutput, crystallizedAlloyIngot,
                EPItems.ENERGIZED_ALLOY_INGOT, "energized_alloy_ingot", AdvancementType.TASK,
                CommonItemTags.INGOTS_ENERGIZED_ALLOY
        );

        AdvancementHolder energizedAlloyPlate = addAdvancement(
                advancementOutput, energizedAlloyIngot,
                EPItems.ENERGIZED_ALLOY_PLATE, "energized_alloy_plate", AdvancementType.TASK,
                CommonItemTags.PLATES_ENERGIZED_ALLOY
        );
    }

    private AdvancementHolder addAdvancement(Consumer<AdvancementHolder> advancementOutput,
                                             AdvancementHolder parent, ItemLike icon, String advancementId, AdvancementType type) {
        return addAdvancement(advancementOutput, parent, icon, advancementId, type, icon);
    }
    private AdvancementHolder addAdvancement(Consumer<AdvancementHolder> advancementOutput,
                                             AdvancementHolder parent, ItemLike icon, String advancementId, AdvancementType type,
                                             ItemLike trigger) {
        return addAdvancement(advancementOutput, parent, new ItemStack(icon.asItem()), advancementId, type,
                InventoryChangeTrigger.TriggerInstance.hasItems(trigger));
    }
    private AdvancementHolder addAdvancement(Consumer<AdvancementHolder> advancementOutput,
                                             AdvancementHolder parent, ItemLike icon, String advancementId, AdvancementType type,
                                             TagKey<Item> trigger) {
        return addAdvancement(advancementOutput, parent, new ItemStack(icon.asItem()), advancementId, type,
                InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(
                        trigger
                )));
    }
    private AdvancementHolder addAdvancement(Consumer<AdvancementHolder> advancementOutput,
                                             AdvancementHolder parent, ItemLike icon, String advancementId, AdvancementType type,
                                             Criterion<?> trigger) {
        return addAdvancement(advancementOutput, parent, new ItemStack(icon.asItem()), advancementId, type, trigger);
    }
    private AdvancementHolder addAdvancement(Consumer<AdvancementHolder> advancementOutput,
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
                save(advancementOutput, EPAPI.MOD_ID + ":main/elite/" + advancementId);
    }

    @Override
    public String getName() {
        return "Advancements (Elite)";
    }
}
