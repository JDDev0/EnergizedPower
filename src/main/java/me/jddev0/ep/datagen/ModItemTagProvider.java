package me.jddev0.ep.datagen;

import me.jddev0.ep.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.tag.ItemTags;

public class ModItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public ModItemTagProvider(FabricDataGenerator output) {
        super(output);
    }

    @Override
    protected void generateTags() {
        getOrCreateTagBuilder(ItemTags.LECTERN_BOOKS).
                add(ModItems.ENERGIZED_POWER_BOOK);
    }
}
