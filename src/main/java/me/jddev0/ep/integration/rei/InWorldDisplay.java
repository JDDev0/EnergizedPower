package me.jddev0.ep.integration.rei;

import me.jddev0.ep.item.ModItems;
import me.jddev0.ep.registry.tags.CommonItemTags;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.recipe.Ingredient;
import net.minecraft.tag.ItemTags;

import java.util.List;

public class InWorldDisplay implements Display {
    @Override
    public List<EntryIngredient> getInputEntries() {
        return List.of(
                EntryIngredients.ofIngredient(Ingredient.fromTag(CommonItemTags.SHEARS)),
                EntryIngredients.ofIngredient(Ingredient.fromTag(ItemTags.WOOL))
        );
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return List.of(
                EntryIngredients.of(ModItems.CABLE_INSULATOR, 18)
        );
    }

    @Override
    public CategoryIdentifier<InWorldDisplay> getCategoryIdentifier() {
        return InWorldCategory.CATEGORY;
    }
}
