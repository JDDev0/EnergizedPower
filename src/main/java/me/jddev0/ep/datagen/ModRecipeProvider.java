package me.jddev0.ep.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;

import java.util.function.Consumer;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricDataGenerator output) {
        super(output);
    }

    @Override
    public void generateRecipes(Consumer<RecipeJsonProvider> exporter) {

    }
}
