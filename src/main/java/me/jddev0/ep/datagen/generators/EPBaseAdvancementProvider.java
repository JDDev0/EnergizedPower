package me.jddev0.ep.datagen.generators;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public abstract class EPBaseAdvancementProvider extends FabricAdvancementProvider {
    public EPBaseAdvancementProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(dataOutput, lookupProvider);
    }

    /**
     * Method redirected to "generateAdvancements()" to better match NeoForge
     */
    @Override
    public final void generateAdvancement(HolderLookup.Provider lookupProvider, Consumer<AdvancementHolder> advancementOutput) {
        generateAdvancements(lookupProvider, advancementOutput);
    }

    protected abstract void generateAdvancements(HolderLookup.Provider lookupProvider, Consumer<AdvancementHolder> advancementOutput);
}
