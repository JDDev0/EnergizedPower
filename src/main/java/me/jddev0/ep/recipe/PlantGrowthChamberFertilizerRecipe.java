package me.jddev0.ep.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class PlantGrowthChamberFertilizerRecipe implements Recipe<Inventory> {
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
    public boolean matches(Inventory container, World level) {
        if(level.isClient())
            return false;

        return input.test(container.getStack(1));
    }

    @Override
    public ItemStack craft(Inventory container, DynamicRegistryManager registryManager) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResult(DynamicRegistryManager registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        DefaultedList<Ingredient> ingredients = DefaultedList.ofSize(1);
        ingredients.add(0, input);
        return ingredients;
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(EPBlocks.PLANT_GROWTH_CHAMBER_ITEM);
    }

    @Override
    public boolean isIgnoredInRecipeBook() {
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
        public static final Identifier ID = EPAPI.id("plant_growth_chamber_fertilizer");

        private final Codec<PlantGrowthChamberFertilizerRecipe> CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("ingredient").forGetter((recipe) -> {
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
        public PlantGrowthChamberFertilizerRecipe read(PacketByteBuf buffer) {
            Ingredient input = Ingredient.fromPacket(buffer);
            double speedMultiplier = buffer.readDouble();
            double energyConsumptionMultiplier = buffer.readDouble();

            return new PlantGrowthChamberFertilizerRecipe(input, speedMultiplier, energyConsumptionMultiplier);
        }

        @Override
        public void write(PacketByteBuf buffer, PlantGrowthChamberFertilizerRecipe recipe) {
            recipe.input.write(buffer);
            buffer.writeDouble(recipe.speedMultiplier);
            buffer.writeDouble(recipe.energyConsumptionMultiplier);
        }
    }
}
