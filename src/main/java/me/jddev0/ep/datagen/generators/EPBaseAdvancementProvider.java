package me.jddev0.ep.datagen.generators;

import me.jddev0.ep.api.EPAPI;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.function.Consumer;

public abstract class EPBaseAdvancementProvider implements AdvancementProvider.AdvancementGenerator {
    protected final String advancementPathPrefix;

    protected ExistingFileHelper existingFileHelper;

    public EPBaseAdvancementProvider(String advancementPathPrefix) {
        this.advancementPathPrefix = advancementPathPrefix;
    }

    /**
     * Method redirected to "generateAdvancements()" to match 26.1.x
     */
    @Override
    public final void generate(HolderLookup.Provider lookupProvider, Consumer<AdvancementHolder> advancementOutput, ExistingFileHelper existingFileHelper) {
        this.existingFileHelper = existingFileHelper;

        try {
            generateAdvancements(lookupProvider, advancementOutput);
        }finally {
            this.existingFileHelper = null;
        }
    }

    protected abstract void generateAdvancements(HolderLookup.Provider lookupProvider, Consumer<AdvancementHolder> advancementOutput);

    protected AdvancementHolder addAdvancement(Consumer<AdvancementHolder> advancementOutput,
                                               AdvancementHolder parent, ItemLike icon, String advancementId, AdvancementType type) {
        return addAdvancement(advancementOutput, parent, icon, advancementId, type, icon);
    }
    protected AdvancementHolder addAdvancement(Consumer<AdvancementHolder> advancementOutput,
                                             AdvancementHolder parent, ItemLike icon, String advancementId, AdvancementType type,
                                             ItemLike trigger) {
        return addAdvancement(advancementOutput, parent, new ItemStack(icon.asItem()), advancementId, type,
                InventoryChangeTrigger.TriggerInstance.hasItems(trigger));
    }
    protected AdvancementHolder addAdvancement(HolderLookup.Provider lookupProvider, Consumer<AdvancementHolder> advancementOutput,
                                             AdvancementHolder parent, ItemLike icon, String advancementId, AdvancementType type,
                                             TagKey<Item> trigger) {
        return addAdvancement(advancementOutput, parent, new ItemStack(icon.asItem()), advancementId, type,
                InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(
                        trigger
                )));
    }
    protected AdvancementHolder addAdvancement(Consumer<AdvancementHolder> advancementOutput,
                                             AdvancementHolder parent, ItemLike icon, String advancementId, AdvancementType type,
                                             Criterion<?> trigger) {
        return addAdvancement(advancementOutput, parent, new ItemStack(icon.asItem()), advancementId, type, trigger);
    }
    protected AdvancementHolder addAdvancement(Consumer<AdvancementHolder> advancementOutput,
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
                save(advancementOutput, EPAPI.id(advancementPathPrefix + "/" + advancementId), existingFileHelper);
    }
}
