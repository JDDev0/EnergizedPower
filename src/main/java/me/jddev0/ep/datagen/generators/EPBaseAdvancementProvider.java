package me.jddev0.ep.datagen.generators;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.item.EPItems;
import me.jddev0.ep.registry.tags.CommonItemTags;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.ItemLike;

import java.util.function.Consumer;

public abstract class EPBaseAdvancementProvider implements AdvancementSubProvider {
    protected final String advancementPathPrefix;

    public EPBaseAdvancementProvider(String advancementPathPrefix) {
        this.advancementPathPrefix = advancementPathPrefix;
    }

    /**
     * Method redirected to "generateAdvancements()" to better match Fabric
     */
    @Override
    public final void generate(HolderLookup.Provider lookupProvider, Consumer<AdvancementHolder> advancementOutput) {
        generateAdvancements(lookupProvider, advancementOutput);
    }

    protected abstract void generateAdvancements(HolderLookup.Provider lookupProvider, Consumer<AdvancementHolder> advancementOutput);

    protected AdvancementHolder addAdvancement(Consumer<AdvancementHolder> advancementOutput,
                                             AdvancementHolder parent, ItemLike icon, String advancementId, AdvancementType type) {
        return addAdvancement(advancementOutput, parent, icon, advancementId, type, icon);
    }
    protected AdvancementHolder addAdvancement(Consumer<AdvancementHolder> advancementOutput,
                                             AdvancementHolder parent, ItemLike icon, String advancementId, AdvancementType type,
                                             ItemLike trigger) {
        return addAdvancement(advancementOutput, parent, new ItemStackTemplate(icon.asItem()), advancementId, type,
                InventoryChangeTrigger.TriggerInstance.hasItems(trigger));
    }
    protected AdvancementHolder addAdvancement(HolderLookup.Provider lookupProvider, Consumer<AdvancementHolder> advancementOutput,
                                             AdvancementHolder parent, ItemLike icon, String advancementId, AdvancementType type,
                                             TagKey<Item> trigger) {
        return addAdvancement(advancementOutput, parent, new ItemStackTemplate(icon.asItem()), advancementId, type,
                InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(
                        lookupProvider.lookupOrThrow(Registries.ITEM),
                        trigger
                )));
    }
    protected AdvancementHolder addAdvancement(Consumer<AdvancementHolder> advancementOutput,
                                             AdvancementHolder parent, ItemLike icon, String advancementId, AdvancementType type,
                                             Criterion<?> trigger) {
        return addAdvancement(advancementOutput, parent, new ItemStackTemplate(icon.asItem()), advancementId, type, trigger);
    }
    protected AdvancementHolder addAdvancement(Consumer<AdvancementHolder> advancementOutput,
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
                save(advancementOutput, EPAPI.id(advancementPathPrefix + "/" + advancementId));
    }

    protected AdvancementHolder addRootAdvancement(Consumer<AdvancementHolder> advancementOutput,
                                                   String backgroundBlockTexture, ItemLike icon, String advancementId) {
        return addRootAdvancement(advancementOutput, backgroundBlockTexture, icon, advancementId, icon);
    }
    protected AdvancementHolder addRootAdvancement(Consumer<AdvancementHolder> advancementOutput,
                                                   String backgroundBlockTexture, ItemLike icon, String advancementId,
                                                   ItemLike trigger) {
        return addRootAdvancement(advancementOutput, backgroundBlockTexture, new ItemStackTemplate(icon.asItem()), advancementId,
                InventoryChangeTrigger.TriggerInstance.hasItems(trigger));
    }
    protected AdvancementHolder addRootAdvancement(HolderLookup.Provider lookupProvider, Consumer<AdvancementHolder> advancementOutput,
                                                   String backgroundBlockTexture, ItemLike icon, String advancementId,
                                                   TagKey<Item> trigger) {
        return addRootAdvancement(advancementOutput, backgroundBlockTexture, new ItemStackTemplate(icon.asItem()), advancementId,
                InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(
                        lookupProvider.lookupOrThrow(Registries.ITEM),
                        trigger
                )));
    }
    protected AdvancementHolder addRootAdvancement(Consumer<AdvancementHolder> advancementOutput,
                                                   String backgroundBlockTexture, ItemLike icon, String advancementId,
                                                   Criterion<?> trigger) {
        return addRootAdvancement(advancementOutput, backgroundBlockTexture, new ItemStackTemplate(icon.asItem()), advancementId, trigger);
    }
    protected AdvancementHolder addRootAdvancement(Consumer<AdvancementHolder> advancementOutput,
                                                   String backgroundBlockTexture, ItemStackTemplate icon, String advancementId,
                                                   Criterion<?> trigger) {
        return Advancement.Builder.advancement().
                display(
                        icon,
                        Component.translatable("advancements.energizedpower." + advancementId + ".title"),
                        Component.translatable("advancements.energizedpower." + advancementId + ".description"),
                        EPAPI.id("block/" + backgroundBlockTexture),
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                ).
                addCriterion("has_the_item", trigger).
                save(advancementOutput, EPAPI.id(advancementPathPrefix + "/" + advancementId));
    }
}
