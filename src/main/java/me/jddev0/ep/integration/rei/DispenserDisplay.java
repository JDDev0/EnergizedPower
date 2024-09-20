package me.jddev0.ep.integration.rei;

import me.jddev0.ep.item.ModItems;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.minecraft.registry.tag.ItemTags;

import java.util.List;

public class DispenserDisplay implements Display {
    @Override
    public List<EntryIngredient> getInputEntries() {
        return List.of(
                EntryIngredients.ofItemTag(ConventionalItemTags.SHEARS),
                EntryIngredients.ofItemTag(ItemTags.WOOL)
        );
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return List.of(
                EntryIngredients.of(ModItems.CABLE_INSULATOR, 18)
        );
    }

    @Override
    public CategoryIdentifier<DispenserDisplay> getCategoryIdentifier() {
        return DispenserCategory.CATEGORY;
    }
}
