package me.jddev0.ep.recipe;

import com.google.gson.JsonObject;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class PlantGrowthChamberFertilizerRecipe implements Recipe<SimpleInventory> {
    private final Identifier id;
    private final Ingredient input;
    private final double speedMultiplier;
    private final double energyConsumptionMultiplier;

    public PlantGrowthChamberFertilizerRecipe(Identifier id, Ingredient input, double speedMultiplier, double energyConsumptionMultiplier) {
        this.id = id;
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
    public boolean matches(SimpleInventory container, World level) {
        if(level.isClient())
            return false;

        return input.test(container.getStack(1));
    }

    @Override
    public ItemStack craft(SimpleInventory container, DynamicRegistryManager registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryAccess) {
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
        return new ItemStack(ModBlocks.PLANT_GROWTH_CHAMBER_ITEM);
    }

    @Override
    public Identifier getId() {
        return id;
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
        public static final Identifier ID = new Identifier(EnergizedPowerMod.MODID, "plant_growth_chamber_fertilizer");

        @Override
        public PlantGrowthChamberFertilizerRecipe read(Identifier recipeID, JsonObject json) {
            Ingredient input = Ingredient.fromJson(json.get("ingredient"));
            double speedMultiplier = JsonHelper.getDouble(json, "speedMultiplier");
            double energyConsumptionMultiplier = JsonHelper.getDouble(json, "energyConsumptionMultiplier");

            return new PlantGrowthChamberFertilizerRecipe(recipeID, input, speedMultiplier, energyConsumptionMultiplier);
        }

        @Override
        public PlantGrowthChamberFertilizerRecipe read(Identifier recipeID, PacketByteBuf buffer) {
            Ingredient input = Ingredient.fromPacket(buffer);
            double speedMultiplier = buffer.readDouble();
            double energyConsumptionMultiplier = buffer.readDouble();

            return new PlantGrowthChamberFertilizerRecipe(recipeID, input, speedMultiplier, energyConsumptionMultiplier);
        }

        @Override
        public void write(PacketByteBuf buffer, PlantGrowthChamberFertilizerRecipe recipe) {
            recipe.input.write(buffer);
            buffer.writeDouble(recipe.speedMultiplier);
            buffer.writeDouble(recipe.energyConsumptionMultiplier);
        }
    }
}
