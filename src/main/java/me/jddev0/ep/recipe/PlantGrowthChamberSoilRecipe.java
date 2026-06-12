package me.jddev0.ep.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.registry.EPRegistries;
import me.jddev0.ep.soil.SoilType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.List;

public class PlantGrowthChamberSoilRecipe implements EnergizedPowerBaseRecipe<RecipeInput> {
    private final Ingredient input;
    private final ResourceKey<SoilType> soilType;
    private final double speedMultiplier;
    private final double fluidConsumptionMultiplier;
    private final double energyConsumptionMultiplier;

    public PlantGrowthChamberSoilRecipe(Ingredient input, ResourceKey<SoilType> soilType,
                                        double speedMultiplier, double fluidConsumptionMultiplier,
                                        double energyConsumptionMultiplier) {
        this.input = input;
        this.soilType = soilType;
        this.speedMultiplier = speedMultiplier;
        this.fluidConsumptionMultiplier = fluidConsumptionMultiplier;
        this.energyConsumptionMultiplier = energyConsumptionMultiplier;
    }

    public Ingredient getInput() {
        return input;
    }

    public ResourceKey<SoilType> getSoilType() {
        return soilType;
    }

    public double getSpeedMultiplier() {
        return speedMultiplier;
    }

    public double getFluidConsumptionMultiplier() {
        return fluidConsumptionMultiplier;
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
        return EPRecipes.PLANT_GROWTH_CHAMBER_SOIL_CATEGORY;
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

    public static final class Type implements RecipeType<PlantGrowthChamberSoilRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "plant_growth_chamber_soil";
    }

    public static final class Serializer {
        private Serializer() {}

        private static final MapCodec<PlantGrowthChamberSoilRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(Ingredient.CODEC.fieldOf("ingredient").forGetter((recipe) -> {
                return recipe.input;
            }), ResourceKey.codec(EPRegistries.SOIL_TYPE).fieldOf("soilType").forGetter((recipe) -> {
                return recipe.soilType;
            }), Codec.DOUBLE.fieldOf("speedMultiplier").forGetter((recipe) -> {
                return recipe.speedMultiplier;
            }), Codec.DOUBLE.fieldOf("fluidConsumptionMultiplier").forGetter((recipe) -> {
                return recipe.fluidConsumptionMultiplier;
            }), Codec.DOUBLE.fieldOf("energyConsumptionMultiplier").forGetter((recipe) -> {
                return recipe.energyConsumptionMultiplier;
            })).apply(instance, PlantGrowthChamberSoilRecipe::new);
        });

        private static final StreamCodec<RegistryFriendlyByteBuf, PlantGrowthChamberSoilRecipe> STREAM_CODEC = StreamCodec.of(
                Serializer::write, Serializer::read);

        public static final RecipeSerializer<PlantGrowthChamberSoilRecipe> INSTANCE = new RecipeSerializer<>(CODEC, STREAM_CODEC);
        public static final Identifier ID = EPAPI.id("plant_growth_chamber_soil");

        private static PlantGrowthChamberSoilRecipe read(RegistryFriendlyByteBuf buffer) {
            Ingredient input = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            ResourceKey<SoilType> soilType = ResourceKey.streamCodec(EPRegistries.SOIL_TYPE).decode(buffer);
            double speedMultiplier = buffer.readDouble();
            double fluidConsumptionMultiplier = buffer.readDouble();
            double energyConsumptionMultiplier = buffer.readDouble();

            return new PlantGrowthChamberSoilRecipe(input, soilType, speedMultiplier, fluidConsumptionMultiplier, energyConsumptionMultiplier);
        }

        private static void write(RegistryFriendlyByteBuf buffer, PlantGrowthChamberSoilRecipe recipe) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.input);
            ResourceKey.streamCodec(EPRegistries.SOIL_TYPE).encode(buffer, recipe.soilType);
            buffer.writeDouble(recipe.speedMultiplier);
            buffer.writeDouble(recipe.fluidConsumptionMultiplier);
            buffer.writeDouble(recipe.energyConsumptionMultiplier);
        }
    }
}
