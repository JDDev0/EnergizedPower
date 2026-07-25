package me.jddev0.ep.datagen.generators;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.function.Consumer;

public abstract class EPBaseAdvancementProvider implements AdvancementProvider.AdvancementGenerator {
    protected ExistingFileHelper existingFileHelper;

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
}
