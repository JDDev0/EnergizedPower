package me.jddev0.ep.datagen.generators;

import me.jddev0.ep.api.EPAPI;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.advancements.triggers.InventoryChangeTrigger;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.ItemLike;

public abstract class EPBaseAdvancementProvider extends AdvancementSubProvider {
    protected final String advancementPathPrefix;

    public EPBaseAdvancementProvider(BootstrapContext<Advancement> output, String advancementPathPrefix) {
        super(output);
        this.advancementPathPrefix = advancementPathPrefix;
    }

    @Override
    public void generate() {
        generateAdvancements(output);
    }

    protected abstract void generateAdvancements(BootstrapContext<Advancement> advancementOutput);

    protected AdvancementHolder addAdvancement(BootstrapContext<Advancement> advancementOutput,
                                             AdvancementHolder parent, ItemLike icon, String advancementId, AdvancementType type) {
        return addAdvancement(advancementOutput, parent, icon, advancementId, type, icon);
    }
    protected AdvancementHolder addAdvancement(BootstrapContext<Advancement> advancementOutput,
                                             AdvancementHolder parent, ItemLike icon, String advancementId, AdvancementType type,
                                             ItemLike trigger) {
        return addAdvancement(advancementOutput, parent, new ItemStackTemplate(icon.asItem()), advancementId, type,
                InventoryChangeTrigger.TriggerInstance.hasItems(trigger));
    }
    protected AdvancementHolder addAdvancement(BootstrapContext<Advancement> advancementOutput,
                                             AdvancementHolder parent, ItemLike icon, String advancementId, AdvancementType type,
                                             TagKey<Item> trigger) {
        return addAdvancement(advancementOutput, parent, new ItemStackTemplate(icon.asItem()), advancementId, type,
                InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(
                        output.lookup(Registries.ITEM),
                        trigger
                )));
    }
    protected AdvancementHolder addAdvancement(BootstrapContext<Advancement> advancementOutput,
                                             AdvancementHolder parent, ItemLike icon, String advancementId, AdvancementType type,
                                             Criterion<?> trigger) {
        return addAdvancement(advancementOutput, parent, new ItemStackTemplate(icon.asItem()), advancementId, type, trigger);
    }
    protected AdvancementHolder addAdvancement(BootstrapContext<Advancement> advancementOutput,
                                             AdvancementHolder parent, ItemStackTemplate icon, String advancementId, AdvancementType type,
                                             Criterion<?> trigger) {
        return Advancement.Builder.advancement().parent(parent).
                display(
                        icon,
                        Component.translatable("advancements.energizedpower." + advancementId + ".title"),
                        Component.translatable("advancements.energizedpower." + advancementId + ".description"),
                        type,
                        true,
                        true,
                        false
                ).
                addCriterion("has_the_item", trigger).
                save(advancementOutput, EPAPI.id(advancementPathPrefix + "/" + advancementId));
    }

    protected AdvancementHolder addRootAdvancement(BootstrapContext<Advancement> advancementOutput,
                                                   String backgroundBlockTexture, ItemLike icon, String advancementId) {
        return addRootAdvancement(advancementOutput, backgroundBlockTexture, icon, advancementId, icon);
    }
    protected AdvancementHolder addRootAdvancement(BootstrapContext<Advancement> advancementOutput,
                                                   String backgroundBlockTexture, ItemLike icon, String advancementId,
                                                   ItemLike trigger) {
        return addRootAdvancement(advancementOutput, backgroundBlockTexture, new ItemStackTemplate(icon.asItem()), advancementId,
                InventoryChangeTrigger.TriggerInstance.hasItems(trigger));
    }
    protected AdvancementHolder addRootAdvancement(BootstrapContext<Advancement> advancementOutput,
                                                   String backgroundBlockTexture, ItemLike icon, String advancementId,
                                                   TagKey<Item> trigger) {
        return addRootAdvancement(advancementOutput, backgroundBlockTexture, new ItemStackTemplate(icon.asItem()), advancementId,
                InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(
                        output.lookup(Registries.ITEM),
                        trigger
                )));
    }
    protected AdvancementHolder addRootAdvancement(BootstrapContext<Advancement> advancementOutput,
                                                   String backgroundBlockTexture, ItemLike icon, String advancementId,
                                                   Criterion<?> trigger) {
        return addRootAdvancement(advancementOutput, backgroundBlockTexture, new ItemStackTemplate(icon.asItem()), advancementId, trigger);
    }
    protected AdvancementHolder addRootAdvancement(BootstrapContext<Advancement> advancementOutput,
                                                   String backgroundBlockTexture, ItemStackTemplate icon, String advancementId,
                                                   Criterion<?> trigger) {
        return Advancement.Builder.advancement().
                rootDisplay(
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
