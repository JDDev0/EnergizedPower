package me.jddev0.ep.recipe;

import me.jddev0.ep.api.EPAPI;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public final class EPRecipes {
    private EPRecipes() {}

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

    public static final RecipeSerializer<PressMoldMakerRecipe> PRESS_MOLD_MAKER_SERIALIZER = createSerializer("press_mold_maker",
            PressMoldMakerRecipe.Serializer.INSTANCE);
    public static final RecipeType<PressMoldMakerRecipe> PRESS_MOLD_MAKER_TYPE = createRecipeType("press_mold_maker",
            PressMoldMakerRecipe.Type.INSTANCE);

    public static final RecipeSerializer<MetalPressRecipe> METAL_PRESS_SERIALIZER = createSerializer("metal_press",
            MetalPressRecipe.Serializer.INSTANCE);
    public static final RecipeType<MetalPressRecipe> METAL_PRESS_TYPE = createRecipeType("metal_press",
            MetalPressRecipe.Type.INSTANCE);

    public static final RecipeSerializer<AssemblingMachineRecipe> ASSEMBLING_MACHINE_SERIALIZER =
            createSerializer("assembling_machine", AssemblingMachineRecipe.Serializer.INSTANCE);
    public static final RecipeType<AssemblingMachineRecipe> ASSEMBLING_MACHINE_TYPE =
            createRecipeType("assembling_machine", AssemblingMachineRecipe.Type.INSTANCE);

    public static final RecipeSerializer<StoneSolidifierRecipe> STONE_SOLIDIFIER_SERIALIZER = createSerializer("stone_solidifier",
            StoneSolidifierRecipe.Serializer.INSTANCE);
    public static final RecipeType<StoneSolidifierRecipe> STONE_SOLIDIFIER_TYPE = createRecipeType("stone_solidifier",
            StoneSolidifierRecipe.Type.INSTANCE);

    public static final RecipeSerializer<FiltrationPlantRecipe> FILTRATION_PLANT_SERIALIZER = createSerializer("filtration_plant",
            FiltrationPlantRecipe.Serializer.INSTANCE);
    public static final RecipeType<FiltrationPlantRecipe> FILTRATION_PLANT_TYPE = createRecipeType("filtration_plant",
            FiltrationPlantRecipe.Type.INSTANCE);

    public static final RecipeSerializer<CrystalGrowthChamberRecipe> CRYSTAL_GROWTH_CHAMBER_SERIALIZER =
            createSerializer("crystal_growth_chamber", CrystalGrowthChamberRecipe.Serializer.INSTANCE);
    public static final RecipeType<CrystalGrowthChamberRecipe> CRYSTAL_GROWTH_CHAMBER_TYPE =
            createRecipeType("crystal_growth_chamber", CrystalGrowthChamberRecipe.Type.INSTANCE);

    public static final RecipeSerializer<FluidTransposerRecipe> FLUID_TRANSPOSER_SERIALIZER =
            createSerializer("fluid_transposer", FluidTransposerRecipe.Serializer.INSTANCE);
    public static final RecipeType<FluidTransposerRecipe> FLUID_TRANSPOSER_TYPE =
            createRecipeType("fluid_transposer", FluidTransposerRecipe.Type.INSTANCE);

    public static final RecipeSerializer<AlloyFurnaceRecipe> ALLOY_FURNACE_SERIALIZER =
            createSerializer("alloy_furnace", AlloyFurnaceRecipe.Serializer.INSTANCE);
    public static final RecipeType<AlloyFurnaceRecipe> ALLOY_FURNACE_TYPE =
            createRecipeType("alloy_furnace", AlloyFurnaceRecipe.Type.INSTANCE);

    public static final RecipeSerializer<TeleporterMatrixSettingsCopyRecipe>
            TELEPORTER_MATRIX_SETTINGS_COPY_SERIALIZER = createSerializer("teleporter_matrix_settings_copy",
            new SpecialRecipeSerializer<>(TeleporterMatrixSettingsCopyRecipe::new));

    private static <T extends Recipe<?>> RecipeSerializer<T> createSerializer(String name, RecipeSerializer<T> instance) {
        return Registry.register(Registries.RECIPE_SERIALIZER, EPAPI.id(name), instance);
    }
    private static <T extends Recipe<?>> RecipeType<T> createRecipeType(String name, RecipeType<T> instance) {
        return Registry.register(Registries.RECIPE_TYPE, EPAPI.id(name), instance);
    }

    public static void register() {

    }
}
