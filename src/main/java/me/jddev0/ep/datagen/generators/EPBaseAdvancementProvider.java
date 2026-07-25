package me.jddev0.ep.datagen.generators;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.advancements.AdvancementSubProvider;

import java.util.function.Consumer;

public abstract class EPBaseAdvancementProvider implements AdvancementSubProvider {
    /**
     * Method redirected to "generateAdvancements()" to better match Fabric
     */
    @Override
    public final void generate(HolderLookup.Provider lookupProvider, Consumer<AdvancementHolder> advancementOutput) {
        generateAdvancements(lookupProvider, advancementOutput);
    }

    protected abstract void generateAdvancements(HolderLookup.Provider lookupProvider, Consumer<AdvancementHolder> advancementOutput);
}
