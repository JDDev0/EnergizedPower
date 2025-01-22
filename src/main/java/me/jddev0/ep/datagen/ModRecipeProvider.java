package me.jddev0.ep.datagen;

import me.jddev0.ep.datagen.recipe.ModRecipeGenerator;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider.Runner {
    protected ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider wrapperLookup, RecipeOutput recipeOutput) {
        return new ModRecipeGenerator(wrapperLookup, recipeOutput);
    }

    @Override
    public String getName() {
        return "Recipes";
    }
}
