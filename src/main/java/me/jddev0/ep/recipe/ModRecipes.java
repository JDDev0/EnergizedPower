package me.jddev0.ep.recipe;

import me.jddev0.ep.EnergizedPowerMod;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

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

    public static void register(IEventBus modEventBus) {
        SERIALIZERS.register(modEventBus);
    }
}
