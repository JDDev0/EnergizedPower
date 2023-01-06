package me.jddev0.ep.integration.rei;

import me.jddev0.ep.item.ModItems;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.Tags;

import java.util.List;

public class InWorldDisplay implements Display {
    @Override
    public List<EntryIngredient> getInputEntries() {
        return List.of(
                EntryIngredients.ofItemTag(Tags.Items.SHEARS),
                EntryIngredients.ofItemTag(ItemTags.WOOL)
        );
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return List.of(
                EntryIngredients.of(ModItems.CABLE_INSULATOR.get(), 18)
        );
    }

    @Override
    public CategoryIdentifier<InWorldDisplay> getCategoryIdentifier() {
        return InWorldCategory.CATEGORY;
    }
}
