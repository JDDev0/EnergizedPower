package me.jddev0.ep.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class PlantGrowthChamberFertilizerRecipe implements Recipe<Container> {
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
    public boolean matches(Container container, Level level) {
        if(level.isClientSide)
            return false;

        return input.test(container.getItem(1));
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.createWithCapacity(1);
        ingredients.add(0, input);
        return ingredients;
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(ModBlocks.PLANT_GROWTH_CHAMBER_ITEM.get());
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static final class Type implements RecipeType<PlantGrowthChamberFertilizerRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "plant_growth_chamber_fertilizer";
    }

    public static final class Serializer implements RecipeSerializer<PlantGrowthChamberFertilizerRecipe> {
        private Serializer() {}

        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation(EnergizedPowerMod.MODID, "plant_growth_chamber_fertilizer");

        private final Codec<PlantGrowthChamberFertilizerRecipe> CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter((recipe) -> {
                return recipe.input;
            }), Codec.DOUBLE.fieldOf("speedMultiplier").forGetter((recipe) -> {
                return recipe.speedMultiplier;
            }), Codec.DOUBLE.fieldOf("energyConsumptionMultiplier").forGetter((recipe) -> {
                return recipe.energyConsumptionMultiplier;
            })).apply(instance, PlantGrowthChamberFertilizerRecipe::new);
        });

        @Override
        public Codec<PlantGrowthChamberFertilizerRecipe> codec() {
            return CODEC;
        }

        @Override
        public PlantGrowthChamberFertilizerRecipe fromNetwork(FriendlyByteBuf buffer) {
            Ingredient input = Ingredient.fromNetwork(buffer);
            double speedMultiplier = buffer.readDouble();
            double energyConsumptionMultiplier = buffer.readDouble();

            return new PlantGrowthChamberFertilizerRecipe(input, speedMultiplier, energyConsumptionMultiplier);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, PlantGrowthChamberFertilizerRecipe recipe) {
            recipe.input.toNetwork(buffer);
            buffer.writeDouble(recipe.speedMultiplier);
            buffer.writeDouble(recipe.energyConsumptionMultiplier);
        }
    }
}
