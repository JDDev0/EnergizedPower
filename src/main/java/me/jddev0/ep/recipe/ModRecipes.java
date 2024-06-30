package me.jddev0.ep.recipe;

import me.jddev0.ep.EnergizedPowerMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;

import java.util.function.Supplier;

public final class ModRecipes {
    private ModRecipes() {}

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, EnergizedPowerMod.MODID);
    public static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, EnergizedPowerMod.MODID);

    public static final Supplier<RecipeSerializer<EnergizerRecipe>> ENERGIZER_SERIALIZER = SERIALIZERS.register("energizer",
            () -> EnergizerRecipe.Serializer.INSTANCE);
    public static final Supplier<RecipeType<EnergizerRecipe>> ENERGIZER_TYPE = TYPES.register("type",
            () -> EnergizerRecipe.Type.INSTANCE);

    public static final Supplier<RecipeSerializer<ChargerRecipe>> CHARGER_SERIALIZER = SERIALIZERS.register("charger",
            () -> ChargerRecipe.Serializer.INSTANCE);
    public static final Supplier<RecipeType<ChargerRecipe>> CHARGER_TYPE = TYPES.register("charger",
            () -> ChargerRecipe.Type.INSTANCE);

    public static final Supplier<RecipeSerializer<CrusherRecipe>> CRUSHER_SERIALIZER = SERIALIZERS.register("crusher",
            () -> CrusherRecipe.Serializer.INSTANCE);
    public static final Supplier<RecipeType<CrusherRecipe>> CRUSHER_TYPE = TYPES.register("crusher",
            () -> CrusherRecipe.Type.INSTANCE);

    public static final Supplier<RecipeSerializer<PulverizerRecipe>> PULVERIZER_SERIALIZER = SERIALIZERS.register("pulverizer",
            () -> PulverizerRecipe.Serializer.INSTANCE);
    public static final Supplier<RecipeType<PulverizerRecipe>> PULVERIZER_TYPE = TYPES.register("pulverizer",
            () -> PulverizerRecipe.Type.INSTANCE);

    public static final Supplier<RecipeSerializer<SawmillRecipe>> SAWMILL_SERIALIZER = SERIALIZERS.register("sawmill",
            () -> SawmillRecipe.Serializer.INSTANCE);
    public static final Supplier<RecipeType<SawmillRecipe>> SAWMILL_TYPE = TYPES.register("sawmill",
            () -> SawmillRecipe.Type.INSTANCE);

    public static final Supplier<RecipeSerializer<CompressorRecipe>> COMPRESSOR_SERIALIZER = SERIALIZERS.register("compressor",
            () -> CompressorRecipe.Serializer.INSTANCE);
    public static final Supplier<RecipeType<CompressorRecipe>> COMPRESSOR_TYPE = TYPES.register("compressor",
            () -> CompressorRecipe.Type.INSTANCE);

    public static final Supplier<RecipeSerializer<PlantGrowthChamberRecipe>> PLANT_GROWTH_CHAMBER_SERIALIZER = SERIALIZERS.
            register("plant_growth_chamber", () -> PlantGrowthChamberRecipe.Serializer.INSTANCE);
    public static final Supplier<RecipeType<PlantGrowthChamberRecipe>> PLANT_GROWTH_CHAMBER_TYPE = TYPES.
            register("plant_growth_chamber", () -> PlantGrowthChamberRecipe.Type.INSTANCE);

    public static final Supplier<RecipeSerializer<PlantGrowthChamberFertilizerRecipe>> PLANT_GROWTH_CHAMBER_FERTILIZER_SERIALIZER = SERIALIZERS.
            register("plant_growth_chamber_fertilizer", () -> PlantGrowthChamberFertilizerRecipe.Serializer.INSTANCE);
    public static final Supplier<RecipeType<PlantGrowthChamberFertilizerRecipe>> PLANT_GROWTH_CHAMBER_FERTILIZER_TYPE = TYPES.
            register("plant_growth_chamber_fertilizer", () -> PlantGrowthChamberFertilizerRecipe.Type.INSTANCE);

    public static final Supplier<RecipeSerializer<HeatGeneratorRecipe>> HEAT_GENERATOR_SERIALIZER = SERIALIZERS.
            register("heat_generator", () -> HeatGeneratorRecipe.Serializer.INSTANCE);
    public static final Supplier<RecipeType<HeatGeneratorRecipe>> HEAT_GENERATOR_TYPE = TYPES.
            register("heat_generator", () -> HeatGeneratorRecipe.Type.INSTANCE);

    public static final Supplier<RecipeSerializer<ThermalGeneratorRecipe>> THERMAL_GENERATOR_SERIALIZER = SERIALIZERS.
            register("thermal_generator", () -> ThermalGeneratorRecipe.Serializer.INSTANCE);
    public static final Supplier<RecipeType<ThermalGeneratorRecipe>> THERMAL_GENERATOR_TYPE = TYPES.
            register("thermal_generator", () -> ThermalGeneratorRecipe.Type.INSTANCE);

    public static final Supplier<RecipeSerializer<PressMoldMakerRecipe>> PRESS_MOLD_MAKER_SERIALIZER = SERIALIZERS.
            register("press_mold_maker", () -> PressMoldMakerRecipe.Serializer.INSTANCE);
    public static final Supplier<RecipeType<PressMoldMakerRecipe>> PRESS_MOLD_MAKER_TYPE = TYPES.
            register("press_mold_maker", () -> PressMoldMakerRecipe.Type.INSTANCE);

    public static final Supplier<RecipeSerializer<MetalPressRecipe>> METAL_PRESS_SERIALIZER = SERIALIZERS.
            register("metal_press", () -> MetalPressRecipe.Serializer.INSTANCE);
    public static final Supplier<RecipeType<MetalPressRecipe>> METAL_PRESS_TYPE = TYPES.
            register("metal_press", () -> MetalPressRecipe.Type.INSTANCE);

    public static final Supplier<RecipeSerializer<AssemblingMachineRecipe>> ASSEMBLING_MACHINE_SERIALIZER = SERIALIZERS.
            register("assembling_machine", () -> AssemblingMachineRecipe.Serializer.INSTANCE);
    public static final Supplier<RecipeType<AssemblingMachineRecipe>> ASSEMBLING_MACHINE_TYPE = TYPES.
            register("assembling_machine", () -> AssemblingMachineRecipe.Type.INSTANCE);

    public static final Supplier<RecipeSerializer<StoneSolidifierRecipe>> STONE_SOLIDIFIER_SERIALIZER = SERIALIZERS.
            register("stone_solidifier", () -> StoneSolidifierRecipe.Serializer.INSTANCE);
    public static final Supplier<RecipeType<StoneSolidifierRecipe>> STONE_SOLIDIFIER_TYPE = TYPES.
            register("stone_solidifier", () -> StoneSolidifierRecipe.Type.INSTANCE);

    public static final Supplier<RecipeSerializer<FiltrationPlantRecipe>> FILTRATION_PLANT_SERIALIZER = SERIALIZERS.
            register("filtration_plant", () -> FiltrationPlantRecipe.Serializer.INSTANCE);
    public static final Supplier<RecipeType<FiltrationPlantRecipe>> FILTRATION_PLANT_TYPE = TYPES.
            register("filtration_plant", () -> FiltrationPlantRecipe.Type.INSTANCE);

    public static final Supplier<RecipeSerializer<CrystalGrowthChamberRecipe>> CRYSTAL_GROWTH_CHAMBER_SERIALIZER = SERIALIZERS.
            register("crystal_growth_chamber", () -> CrystalGrowthChamberRecipe.Serializer.INSTANCE);
    public static final Supplier<RecipeType<CrystalGrowthChamberRecipe>> CRYSTAL_GROWTH_CHAMBER_TYPE = TYPES.
            register("crystal_growth_chamber", () -> CrystalGrowthChamberRecipe.Type.INSTANCE);

    public static final Supplier<RecipeSerializer<FluidTransposerRecipe>> FLUID_TRANSPOSER_SERIALIZER = SERIALIZERS.
            register("fluid_transposer", () -> FluidTransposerRecipe.Serializer.INSTANCE);
    public static final Supplier<RecipeType<FluidTransposerRecipe>> FLUID_TRANSPOSER_TYPE = TYPES.
            register("fluid_transposer", () -> FluidTransposerRecipe.Type.INSTANCE);

    public static final Supplier<RecipeSerializer<AlloyFurnaceRecipe>> ALLOY_FURNACE_SERIALIZER = SERIALIZERS.
            register("alloy_furnace", () -> AlloyFurnaceRecipe.Serializer.INSTANCE);
    public static final Supplier<RecipeType<AlloyFurnaceRecipe>> ALLOY_FURNACE_TYPE = TYPES.
            register("alloy_furnace", () -> AlloyFurnaceRecipe.Type.INSTANCE);

    public static final Supplier<RecipeSerializer<TeleporterMatrixSettingsCopyRecipe>>
            TELEPORTER_MATRIX_SETTINGS_COPY_SERIALIZER = SERIALIZERS.register("teleporter_matrix_settings_copy",
            () -> new SimpleCraftingRecipeSerializer<>(TeleporterMatrixSettingsCopyRecipe::new));

    public static void register(IEventBus modEventBus) {
        SERIALIZERS.register(modEventBus);
        TYPES.register(modEventBus);
    }
}
