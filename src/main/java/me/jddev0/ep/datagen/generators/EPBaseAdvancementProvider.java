package me.jddev0.ep.datagen.generators;

import me.jddev0.ep.api.EPAPI;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
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
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public abstract class EPBaseAdvancementProvider extends FabricAdvancementProvider {
    protected final String advancementPathPrefix;

    public EPBaseAdvancementProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, String advancementPathPrefix) {
        super(dataOutput, lookupProvider);

        this.advancementPathPrefix = advancementPathPrefix;
    }

    @Override
    public final @NotNull String getName() {
        return "Advancements (" + advancementPathPrefix + ")";
    }

    /**
     * Method redirected to "generateAdvancements()" to better match NeoForge
     */
    @Override
    public final void generateAdvancement(HolderLookup.Provider lookupProvider, Consumer<AdvancementHolder> advancementOutput) {
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
                save(advancementOutput, EPAPI.MOD_ID + ":" + advancementPathPrefix + "/" + advancementId);
    }

    protected AdvancementHolder addRootAdvancement(Consumer<AdvancementHolder> advancementOutput,
                                                   String backgroundBlockTexture, ItemLike icon, String advancementId) {
        return addRootAdvancement(advancementOutput, backgroundBlockTexture, icon, advancementId, icon);
    }
    protected AdvancementHolder addRootAdvancement(Consumer<AdvancementHolder> advancementOutput,
                                                   String backgroundBlockTexture, ItemLike icon, String advancementId,
                                                   ItemLike trigger) {
        return addRootAdvancement(advancementOutput, backgroundBlockTexture, new ItemStack(icon.asItem()), advancementId,
                InventoryChangeTrigger.TriggerInstance.hasItems(trigger));
    }
    protected AdvancementHolder addRootAdvancement(HolderLookup.Provider lookupProvider, Consumer<AdvancementHolder> advancementOutput,
                                                   String backgroundBlockTexture, ItemLike icon, String advancementId,
                                                   TagKey<Item> trigger) {
        return addRootAdvancement(advancementOutput, backgroundBlockTexture, new ItemStack(icon.asItem()), advancementId,
                InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(
                        trigger
                )));
    }
    protected AdvancementHolder addRootAdvancement(Consumer<AdvancementHolder> advancementOutput,
                                                   String backgroundBlockTexture, ItemLike icon, String advancementId,
                                                   Criterion<?> trigger) {
        return addRootAdvancement(advancementOutput, backgroundBlockTexture, new ItemStack(icon.asItem()), advancementId, trigger);
    }
    protected AdvancementHolder addRootAdvancement(Consumer<AdvancementHolder> advancementOutput,
                                                   String backgroundBlockTexture, ItemStack icon, String advancementId,
                                                   Criterion<?> trigger) {
        return Advancement.Builder.advancement().
                display(
                        icon,
                        Component.translatable("advancements.energizedpower." + advancementId + ".title"),
                        Component.translatable("advancements.energizedpower." + advancementId + ".description"),
                        EPAPI.id("textures/block/" + backgroundBlockTexture + ".png"),
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                ).
                addCriterion("has_the_item", trigger).
                save(advancementOutput, EPAPI.MOD_ID + ":" + advancementPathPrefix + "/" + advancementId);
    }
}
