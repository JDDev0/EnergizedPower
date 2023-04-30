package me.jddev0.ep.recipe;

import me.jddev0.ep.EnergizedPowerMod;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModRecipes {
    private ModRecipes() {}

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, EnergizedPowerMod.MODID);

    public static final RegistryObject<RecipeSerializer<EnergizerRecipe>> ENERGIZER_SERIALIZER = SERIALIZERS.register("energizer",
            () -> EnergizerRecipe.Serializer.INSTANCE);

    public static final RegistryObject<RecipeSerializer<ChargerRecipe>> CHARGER_SERIALIZER = SERIALIZERS.register("charger",
            () -> ChargerRecipe.Serializer.INSTANCE);

    public static final RegistryObject<RecipeSerializer<CrusherRecipe>> CRUSHER_SERIALIZER = SERIALIZERS.register("crusher",
            () -> CrusherRecipe.Serializer.INSTANCE);

    public static final RegistryObject<RecipeSerializer<SawmillRecipe>> SAWMILL_SERIALIZER = SERIALIZERS.register("sawmill",
            () -> SawmillRecipe.Serializer.INSTANCE);

    public static final RegistryObject<RecipeSerializer<CompressorRecipe>> COMPRESSOR_SERIALIZER = SERIALIZERS.register("compressor",
            () -> CompressorRecipe.Serializer.INSTANCE);

    public static final RegistryObject<RecipeSerializer<PlantGrowthChamberRecipe>> PLANT_GROWTH_CHAMBER_SERIALIZER = SERIALIZERS.
            register("plant_growth_chamber", () -> PlantGrowthChamberRecipe.Serializer.INSTANCE);

    public static final RegistryObject<RecipeSerializer<PlantGrowthChamberFertilizerRecipe>> PLANT_GROWTH_CHAMBER_FERTILIZER_SERIALIZER = SERIALIZERS.
            register("plant_growth_chamber_fertilizer", () -> PlantGrowthChamberFertilizerRecipe.Serializer.INSTANCE);

    public static void register(IEventBus modEventBus) {
        SERIALIZERS.register(modEventBus);
    }
}
