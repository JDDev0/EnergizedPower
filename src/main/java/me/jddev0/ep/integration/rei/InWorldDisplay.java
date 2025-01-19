package me.jddev0.ep.integration.rei;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.item.EPItems;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class InWorldDisplay implements Display {
    public static final CategoryIdentifier<InWorldDisplay> CATEGORY = CategoryIdentifier.of(EPAPI.MOD_ID, "in_world");

    @Override
    public List<EntryIngredient> getInputEntries() {
        return List.of(
                EntryIngredients.ofItemTag(ConventionalItemTags.SHEAR_TOOLS),
                EntryIngredients.ofItemTag(ItemTags.WOOL)
        );
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return List.of(
                EntryIngredients.of(EPItems.CABLE_INSULATOR, 18)
        );
    }

    @Override
    public CategoryIdentifier<InWorldDisplay> getCategoryIdentifier() {
        return CATEGORY;
    }

    @Override
    public Optional<Identifier> getDisplayLocation() {
        return Optional.empty();
    }

    @Override
    public @Nullable DisplaySerializer<? extends Display> getSerializer() {
        return null;
    }
}
