package me.jddev0.ep.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class PlantGrowthChamberRecipe implements Recipe<SimpleContainer> {
    private final ResourceLocation id;
    private final OutputItemStackWithPercentages[] outputs;
    private final Ingredient input;
    private final int ticks;

    public PlantGrowthChamberRecipe(ResourceLocation id, OutputItemStackWithPercentages[] outputs, Ingredient input, int ticks) {
        this.id = id;
        this.outputs = outputs;
        this.input = input;
        this.ticks = ticks;
    }

    public OutputItemStackWithPercentages[] getOutputs() {
        return outputs;
    }

    public Ingredient getInput() {
        return input;
    }

    public int getTicks() {
        return ticks;
    }

    public ItemStack[] getMaxOutputCounts() {
        ItemStack[] generatedOutputs = new ItemStack[outputs.length];
        for(int i = 0;i < outputs.length;i++) {
            OutputItemStackWithPercentages output = outputs[i];
            ItemStack itemStackCopy = output.output.copy();
            itemStackCopy.setCount(output.percentages.length);
            generatedOutputs[i] = itemStackCopy;
        }

        return generatedOutputs;
    }

    public ItemStack[] generateOutputs(Random randomSource) {
        ItemStack[] generatedOutputs = new ItemStack[outputs.length];
        for(int i = 0;i < outputs.length;i++) {
            int count = 0;
            OutputItemStackWithPercentages output = outputs[i];

            for(double percentage:output.percentages)
                if(randomSource.nextDouble() <= percentage)
                    count++;

            ItemStack itemStackCopy = output.output.copy();
            itemStackCopy.setCount(count);
            generatedOutputs[i] = itemStackCopy;
        }

        return generatedOutputs;
    }

    @Override
    public boolean matches(SimpleContainer container, Level level) {
        if(level.isClientSide)
            return false;

        return input.test(container.getItem(0));
    }

    @Override
    public ItemStack assemble(SimpleContainer container) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem() {
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
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static final class Type implements RecipeType<PlantGrowthChamberRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "plant_growth_chamber";
    }

    public static final class Serializer implements RecipeSerializer<PlantGrowthChamberRecipe> {
        private Serializer() {}

        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation(EnergizedPowerMod.MODID, "plant_growth_chamber");

        @Override
        public PlantGrowthChamberRecipe fromJson(ResourceLocation recipeID, JsonObject json) {
            Ingredient input = Ingredient.fromJson(json.get("ingredient"));
            int ticks = GsonHelper.getAsInt(json, "ticks");

            JsonArray outputsJson = GsonHelper.getAsJsonArray(json, "outputs");
            OutputItemStackWithPercentages[] outputs = new OutputItemStackWithPercentages[outputsJson.size()];
            for(int i = 0;i < outputsJson.size();i++) {
                JsonObject outputJson = outputsJson.get(i).getAsJsonObject();

                ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(outputJson, "output"));

                JsonArray percentagesJson = GsonHelper.getAsJsonArray(outputJson, "percentages");
                double[] percentages = new double[percentagesJson.size()];
                for(int j = 0;j < percentagesJson.size();j++)
                    percentages[j] = percentagesJson.get(j).getAsDouble();

                outputs[i] = new OutputItemStackWithPercentages(output, percentages);
            }

            return new PlantGrowthChamberRecipe(recipeID, outputs, input, ticks);
        }

        @Override
        public PlantGrowthChamberRecipe fromNetwork(ResourceLocation recipeID, FriendlyByteBuf buffer) {
            Ingredient input = Ingredient.fromNetwork(buffer);
            int ticks = buffer.readInt();

            int outputCount = buffer.readInt();
            OutputItemStackWithPercentages[] outputs = new OutputItemStackWithPercentages[outputCount];
            for(int i = 0;i < outputCount;i++) {
                ItemStack output = buffer.readItem();

                int percentageCount = buffer.readInt();
                double[] percentages = new double[percentageCount];
                for(int j = 0;j < percentageCount;j++)
                    percentages[j] = buffer.readDouble();

                outputs[i] = new OutputItemStackWithPercentages(output, percentages);
            }

            return new PlantGrowthChamberRecipe(recipeID, outputs, input, ticks);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, PlantGrowthChamberRecipe recipe) {
            recipe.input.toNetwork(buffer);
            buffer.writeInt(recipe.ticks);

            buffer.writeInt(recipe.outputs.length);
            for(OutputItemStackWithPercentages output:recipe.outputs) {
                buffer.writeItemStack(output.output, false);

                buffer.writeInt(output.percentages.length);
                for(double percentage:output.percentages)
                    buffer.writeDouble(percentage);
            }
        }

        @Override
        public RecipeSerializer<?> setRegistryName(ResourceLocation name) {
            return INSTANCE;
        }

        @Nullable
        @Override
        public ResourceLocation getRegistryName() {
            return ID;
        }

        @Override
        public Class<RecipeSerializer<?>> getRegistryType() {
            return PlantGrowthChamberRecipe.Serializer.castClass(RecipeSerializer.class);
        }

        @SuppressWarnings("unchecked")
        private static <G> Class<G> castClass(Class<?> cls) {
            return (Class<G>)cls;
        }
    }

    public record OutputItemStackWithPercentages(ItemStack output, double[] percentages) {}
}
