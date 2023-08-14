package me.jddev0.ep.recipe;

import me.jddev0.ep.EnergizedPowerMod;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModRecipes {
    private ModRecipes() {}

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, EnergizedPowerMod.MODID);
    public static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, EnergizedPowerMod.MODID);

    public static final RegistryObject<RecipeSerializer<EnergizerRecipe>> ENERGIZER_SERIALIZER = SERIALIZERS.register("energizer",
            () -> EnergizerRecipe.Serializer.INSTANCE);
    public static final RegistryObject<RecipeType<EnergizerRecipe>> ENERGIZER_TYPE = TYPES.register("type",
            () -> EnergizerRecipe.Type.INSTANCE);

    public static final RegistryObject<RecipeSerializer<ChargerRecipe>> CHARGER_SERIALIZER = SERIALIZERS.register("charger",
            () -> ChargerRecipe.Serializer.INSTANCE);
    public static final RegistryObject<RecipeType<ChargerRecipe>> CHARGER_TYPE = TYPES.register("charger",
            () -> ChargerRecipe.Type.INSTANCE);

    public static final RegistryObject<RecipeSerializer<CrusherRecipe>> CRUSHER_SERIALIZER = SERIALIZERS.register("crusher",
            () -> CrusherRecipe.Serializer.INSTANCE);
    public static final RegistryObject<RecipeType<CrusherRecipe>> CRUSHER_TYPE = TYPES.register("crusher",
            () -> CrusherRecipe.Type.INSTANCE);

    public static final RegistryObject<RecipeSerializer<PulverizerRecipe>> PULVERIZER_SERIALIZER = SERIALIZERS.register("pulverizer",
            () -> PulverizerRecipe.Serializer.INSTANCE);
    public static final RegistryObject<RecipeType<PulverizerRecipe>> PULVERIZER_TYPE = TYPES.register("pulverizer",
            () -> PulverizerRecipe.Type.INSTANCE);

    public static final RegistryObject<RecipeSerializer<SawmillRecipe>> SAWMILL_SERIALIZER = SERIALIZERS.register("sawmill",
            () -> SawmillRecipe.Serializer.INSTANCE);
    public static final RegistryObject<RecipeType<SawmillRecipe>> SAWMILL_TYPE = TYPES.register("sawmill",
            () -> SawmillRecipe.Type.INSTANCE);

    public static final RegistryObject<RecipeSerializer<CompressorRecipe>> COMPRESSOR_SERIALIZER = SERIALIZERS.register("compressor",
            () -> CompressorRecipe.Serializer.INSTANCE);
    public static final RegistryObject<RecipeType<CompressorRecipe>> COMPRESSOR_TYPE = TYPES.register("compressor",
            () -> CompressorRecipe.Type.INSTANCE);

    public static final RegistryObject<RecipeSerializer<PlantGrowthChamberRecipe>> PLANT_GROWTH_CHAMBER_SERIALIZER = SERIALIZERS.
            register("plant_growth_chamber", () -> PlantGrowthChamberRecipe.Serializer.INSTANCE);
    public static final RegistryObject<RecipeType<PlantGrowthChamberRecipe>> PLANT_GROWTH_CHAMBER_TYPE = TYPES.
            register("plant_growth_chamber", () -> PlantGrowthChamberRecipe.Type.INSTANCE);

    public static final RegistryObject<RecipeSerializer<PlantGrowthChamberFertilizerRecipe>> PLANT_GROWTH_CHAMBER_FERTILIZER_SERIALIZER = SERIALIZERS.
            register("plant_growth_chamber_fertilizer", () -> PlantGrowthChamberFertilizerRecipe.Serializer.INSTANCE);
    public static final RegistryObject<RecipeType<PlantGrowthChamberFertilizerRecipe>> PLANT_GROWTH_CHAMBER_FERTILIZER_TYPE = TYPES.
            register("plant_growth_chamber_fertilizer", () -> PlantGrowthChamberFertilizerRecipe.Type.INSTANCE);

    public static final RegistryObject<RecipeSerializer<HeatGeneratorRecipe>> HEAT_GENERATOR_SERIALIZER = SERIALIZERS.
            register("heat_generator", () -> HeatGeneratorRecipe.Serializer.INSTANCE);
    public static final RegistryObject<RecipeType<HeatGeneratorRecipe>> HEAT_GENERATOR_TYPE = TYPES.
            register("heat_generator", () -> HeatGeneratorRecipe.Type.INSTANCE);

    public static final RegistryObject<RecipeSerializer<ThermalGeneratorRecipe>> THERMAL_GENERATOR_SERIALIZER = SERIALIZERS.
            register("thermal_generator", () -> ThermalGeneratorRecipe.Serializer.INSTANCE);
    public static final RegistryObject<RecipeType<ThermalGeneratorRecipe>> THERMAL_GENERATOR_TYPE = TYPES.
            register("thermal_generator", () -> ThermalGeneratorRecipe.Type.INSTANCE);

    public static void register(IEventBus modEventBus) {
        SERIALIZERS.register(modEventBus);
        TYPES.register(modEventBus);
    }
}
