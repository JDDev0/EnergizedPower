package me.jddev0.ep.recipe;

import me.jddev0.ep.EnergizedPowerMod;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public final class ModRecipes {
    private ModRecipes() {}

    public static final RecipeSerializer<EnergizerRecipe> ENERGIZER_SERIALIZER = createSerializer("energizer",
            EnergizerRecipe.Serializer.INSTANCE);
    public static final RecipeType<EnergizerRecipe> ENERGIZER_TYPE = createRecipeType("energizer",
            EnergizerRecipe.Type.INSTANCE);

    public static final RecipeSerializer<ChargerRecipe> CHARGER_SERIALIZER = createSerializer("charger",
            ChargerRecipe.Serializer.INSTANCE);
    public static final RecipeType<ChargerRecipe> CHARGER_TYPE = createRecipeType("charger",
            ChargerRecipe.Type.INSTANCE);

    public static final RecipeSerializer<CrusherRecipe> CRUSHER_SERIALIZER = createSerializer("crusher",
            CrusherRecipe.Serializer.INSTANCE);
    public static final RecipeType<CrusherRecipe> CRUSHER_TYPE = createRecipeType("crusher",
            CrusherRecipe.Type.INSTANCE);

    public static final RecipeSerializer<SawmillRecipe> SAWMILL_SERIALIZER = createSerializer("sawmill",
            SawmillRecipe.Serializer.INSTANCE);
    public static final RecipeType<SawmillRecipe> SAWMILL_TYPE = createRecipeType("sawmill",
            SawmillRecipe.Type.INSTANCE);

    public static final RecipeSerializer<CompressorRecipe> COMPRESSOR_SERIALIZER = createSerializer("compressor",
            CompressorRecipe.Serializer.INSTANCE);
    public static final RecipeType<CompressorRecipe> COMPRESSOR_TYPE = createRecipeType("compressor",
            CompressorRecipe.Type.INSTANCE);

    private static <T extends Recipe<?>> RecipeSerializer<T> createSerializer(String name, RecipeSerializer<T> instance) {
        return Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(EnergizedPowerMod.MODID, name), instance);
    }
    private static <T extends Recipe<?>> RecipeType<T> createRecipeType(String name, RecipeType<T> instance) {
        return Registry.register(Registry.RECIPE_TYPE, new Identifier(EnergizedPowerMod.MODID, name), instance);
    }

    public static void register() {

    }
}
