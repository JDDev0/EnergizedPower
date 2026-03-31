package me.jddev0.ep.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.api.EPAPI;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import java.util.List;

public class PlantGrowthChamberFertilizerRecipe implements EnergizedPowerBaseRecipe<RecipeInput> {
    private final Ingredient input;
    private final double speedMultiplier;
    private final double energyConsumptionMultiplier;

    public PlantGrowthChamberFertilizerRecipe(Ingredient input, double speedMultiplier, double energyConsumptionMultiplier) {
        this.input = input;
        this.speedMultiplier = speedMultiplier;
        this.energyConsumptionMultiplier = energyConsumptionMultiplier;
    }

    public Ingredient getInput() {
        return input;
    }

    public double getSpeedMultiplier() {
        return speedMultiplier;
    }

    public double getEnergyConsumptionMultiplier() {
        return energyConsumptionMultiplier;
    }

    @Override
    public boolean matches(RecipeInput container, Level level) {
        if(level.isClientSide())
            return false;

        return input.test(container.getItem(1));
    }

    @Override
    public ItemStack assemble(RecipeInput container) {
        return ItemStack.EMPTY;
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return EPRecipes.PLANT_GROWTH_CHAMBER_FERTILIZER_CATEGORY.get();
    }

    @Override
    public RecipeSerializer<? extends Recipe<RecipeInput>> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<? extends Recipe<RecipeInput>> getType() {
        return Type.INSTANCE;
    }

    @Override
    public List<Ingredient> getIngredients() {
        return List.of(input);
    }

    @Override
    public boolean isIngredient(ItemStack itemStack) {
        return input.test(itemStack);
    }

    @Override
    public boolean isResult(ItemStack itemStack) {
        return false;
    }

    public static final class Type implements RecipeType<PlantGrowthChamberFertilizerRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "plant_growth_chamber_fertilizer";
    }

    public static final class Serializer {
        private Serializer() {}

        private static final MapCodec<PlantGrowthChamberFertilizerRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(Ingredient.CODEC.fieldOf("ingredient").forGetter((recipe) -> {
                return recipe.input;
            }), Codec.DOUBLE.fieldOf("speedMultiplier").forGetter((recipe) -> {
                return recipe.speedMultiplier;
            }), Codec.DOUBLE.fieldOf("energyConsumptionMultiplier").forGetter((recipe) -> {
                return recipe.energyConsumptionMultiplier;
            })).apply(instance, PlantGrowthChamberFertilizerRecipe::new);
        });

        private static final StreamCodec<RegistryFriendlyByteBuf, PlantGrowthChamberFertilizerRecipe> STREAM_CODEC = StreamCodec.of(
                Serializer::write, Serializer::read);

        public static final RecipeSerializer<PlantGrowthChamberFertilizerRecipe> INSTANCE = new RecipeSerializer<>(CODEC, STREAM_CODEC);
        public static final Identifier ID = EPAPI.id("plant_growth_chamber_fertilizer");

        private static PlantGrowthChamberFertilizerRecipe read(RegistryFriendlyByteBuf buffer) {
            Ingredient input = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            double speedMultiplier = buffer.readDouble();
            double energyConsumptionMultiplier = buffer.readDouble();

            return new PlantGrowthChamberFertilizerRecipe(input, speedMultiplier, energyConsumptionMultiplier);
        }

        private static void write(RegistryFriendlyByteBuf buffer, PlantGrowthChamberFertilizerRecipe recipe) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.input);
            buffer.writeDouble(recipe.speedMultiplier);
            buffer.writeDouble(recipe.energyConsumptionMultiplier);
        }
    }
}
