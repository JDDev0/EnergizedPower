package me.jddev0.ep.recipe;

import me.jddev0.ep.EnergizedPowerMod;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

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

    public static final RecipeSerializer<PulverizerRecipe> PULVERIZER_SERIALIZER = createSerializer("pulverizer",
            PulverizerRecipe.Serializer.INSTANCE);
    public static final RecipeType<PulverizerRecipe> PULVERIZER_TYPE = createRecipeType("pulverizer",
            PulverizerRecipe.Type.INSTANCE);

    public static final RecipeSerializer<PlantGrowthChamberRecipe> PLANT_GROWTH_CHAMBER_SERIALIZER = createSerializer("plant_growth_chamber",
            PlantGrowthChamberRecipe.Serializer.INSTANCE);
    public static final RecipeType<PlantGrowthChamberRecipe> PLANT_GROWTH_CHAMBER_TYPE = createRecipeType("plant_growth_chamber",
            PlantGrowthChamberRecipe.Type.INSTANCE);

    public static final RecipeSerializer<PlantGrowthChamberFertilizerRecipe> PLANT_GROWTH_CHAMBER_FERTILIZER_SERIALIZER = createSerializer("plant_growth_chamber_fertilizer",
            PlantGrowthChamberFertilizerRecipe.Serializer.INSTANCE);
    public static final RecipeType<PlantGrowthChamberFertilizerRecipe> PLANT_GROWTH_CHAMBER_FERTILIZER_TYPE = createRecipeType("plant_growth_chamber_fertilizer",
            PlantGrowthChamberFertilizerRecipe.Type.INSTANCE);

    public static final RecipeSerializer<HeatGeneratorRecipe> HEAT_GENERATOR_SERIALIZER = createSerializer("heat_generator",
            HeatGeneratorRecipe.Serializer.INSTANCE);
    public static final RecipeType<HeatGeneratorRecipe> HEAT_GENERATOR_TYPE = createRecipeType("heat_generator",
            HeatGeneratorRecipe.Type.INSTANCE);

    public static final RecipeSerializer<ThermalGeneratorRecipe> THERMAL_GENERATOR_SERIALIZER = createSerializer("thermal_generator",
            ThermalGeneratorRecipe.Serializer.INSTANCE);
    public static final RecipeType<ThermalGeneratorRecipe> THERMAL_GENERATOR_TYPE = createRecipeType("thermal_generator",
            ThermalGeneratorRecipe.Type.INSTANCE);

    private static <T extends Recipe<?>> RecipeSerializer<T> createSerializer(String name, RecipeSerializer<T> instance) {
        return Registry.register(Registries.RECIPE_SERIALIZER, new Identifier(EnergizedPowerMod.MODID, name), instance);
    }
    private static <T extends Recipe<?>> RecipeType<T> createRecipeType(String name, RecipeType<T> instance) {
        return Registry.register(Registries.RECIPE_TYPE, new Identifier(EnergizedPowerMod.MODID, name), instance);
    }

    public static void register() {

    }
}
