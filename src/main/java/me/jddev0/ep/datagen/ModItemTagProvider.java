package me.jddev0.ep.datagen;

import me.jddev0.ep.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public ModItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup lookupProvider) {
        getOrCreateTagBuilder(ItemTags.BOOKSHELF_BOOKS).
                add(ModItems.ENERGIZED_POWER_BOOK);

        getOrCreateTagBuilder(ItemTags.LECTERN_BOOKS).
                add(ModItems.ENERGIZED_POWER_BOOK);
    }
}
