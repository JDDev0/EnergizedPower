package me.jddev0.ep.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.api.EPAPI;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

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
    public boolean matches(RecipeInput container, World level) {
        if(level.isClient())
            return false;

        return input.test(container.getStackInSlot(1));
    }

    @Override
    public ItemStack craft(RecipeInput container, RegistryWrapper.WrapperLookup registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public IngredientPlacement getIngredientPlacement() {
        return IngredientPlacement.NONE;
    }

    @Override
    public boolean isIgnoredInRecipeBook() {
        return true;
    }

    @Override
    public RecipeBookCategory getRecipeBookCategory() {
        return EPRecipes.PLANT_GROWTH_CHAMBER_FERTILIZER_CATEGORY;
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

    public static final class Serializer implements RecipeSerializer<PlantGrowthChamberFertilizerRecipe> {
        private Serializer() {}

        public static final Serializer INSTANCE = new Serializer();
        public static final Identifier ID = EPAPI.id("plant_growth_chamber_fertilizer");

        private final MapCodec<PlantGrowthChamberFertilizerRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(Ingredient.CODEC.fieldOf("ingredient").forGetter((recipe) -> {
                return recipe.input;
            }), Codec.DOUBLE.fieldOf("speedMultiplier").forGetter((recipe) -> {
                return recipe.speedMultiplier;
            }), Codec.DOUBLE.fieldOf("energyConsumptionMultiplier").forGetter((recipe) -> {
                return recipe.energyConsumptionMultiplier;
            })).apply(instance, PlantGrowthChamberFertilizerRecipe::new);
        });

        private final PacketCodec<RegistryByteBuf, PlantGrowthChamberFertilizerRecipe> PACKET_CODEC = PacketCodec.ofStatic(
                Serializer::write, Serializer::read);

        @Override
        public MapCodec<PlantGrowthChamberFertilizerRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, PlantGrowthChamberFertilizerRecipe> packetCodec() {
            return PACKET_CODEC;
        }

        private static PlantGrowthChamberFertilizerRecipe read(RegistryByteBuf buffer) {
            Ingredient input = Ingredient.PACKET_CODEC.decode(buffer);
            double speedMultiplier = buffer.readDouble();
            double energyConsumptionMultiplier = buffer.readDouble();

            return new PlantGrowthChamberFertilizerRecipe(input, speedMultiplier, energyConsumptionMultiplier);
        }

        private static void write(RegistryByteBuf buffer, PlantGrowthChamberFertilizerRecipe recipe) {
            Ingredient.PACKET_CODEC.encode(buffer, recipe.input);
            buffer.writeDouble(recipe.speedMultiplier);
            buffer.writeDouble(recipe.energyConsumptionMultiplier);
        }
    }
}
