package me.jddev0.ep.datagen;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.item.ModItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class ModItemTagProvider extends ItemTagsProvider {
    public ModItemTagProvider(DataGenerator output, BlockTagsProvider blockTagsProvider,
                              @Nullable ExistingFileHelper existingFileHelper) {
        super(output, blockTagsProvider, EnergizedPowerMod.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        tag(ItemTags.LECTERN_BOOKS).
                add(ModItems.ENERGIZED_POWER_BOOK.get());
    }
}
