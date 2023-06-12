package me.jddev0.ep.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class PlantGrowthChamberRecipe implements Recipe<SimpleInventory> {
    private final Identifier id;
    private final OutputItemStackWithPercentages[] outputs;
    private final Ingredient input;
    private final int ticks;

    public PlantGrowthChamberRecipe(Identifier id, OutputItemStackWithPercentages[] outputs, Ingredient input, int ticks) {
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
    public boolean matches(SimpleInventory container, World level) {
        if(level.isClient())
            return false;

        return input.test(container.getStack(0));
    }

    @Override
    public ItemStack craft(SimpleInventory container) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getOutput() {
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

    public static final class Type implements RecipeType<PlantGrowthChamberRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "plant_growth_chamber";
    }

    public static final class Serializer implements RecipeSerializer<PlantGrowthChamberRecipe> {
        private Serializer() {}

        public static final Serializer INSTANCE = new Serializer();
        public static final Identifier ID = new Identifier(EnergizedPowerMod.MODID, "plant_growth_chamber");

        @Override
        public PlantGrowthChamberRecipe read(Identifier recipeID, JsonObject json) {
            Ingredient input = Ingredient.fromJson(json.get("ingredient"));
            int ticks = JsonHelper.getInt(json, "ticks");

            JsonArray outputsJson = JsonHelper.getArray(json, "outputs");
            OutputItemStackWithPercentages[] outputs = new OutputItemStackWithPercentages[outputsJson.size()];
            for(int i = 0;i < outputsJson.size();i++) {
                JsonObject outputJson = outputsJson.get(i).getAsJsonObject();

                ItemStack output = ShapedRecipe.outputFromJson(JsonHelper.getObject(outputJson, "output"));

                JsonArray percentagesJson = JsonHelper.getArray(outputJson, "percentages");
                double[] percentages = new double[percentagesJson.size()];
                for(int j = 0;j < percentagesJson.size();j++)
                    percentages[j] = percentagesJson.get(j).getAsDouble();

                outputs[i] = new OutputItemStackWithPercentages(output, percentages);
            }

            return new PlantGrowthChamberRecipe(recipeID, outputs, input, ticks);
        }

        @Override
        public PlantGrowthChamberRecipe read(Identifier recipeID, PacketByteBuf buffer) {
            Ingredient input = Ingredient.fromPacket(buffer);
            int ticks = buffer.readInt();

            int outputCount = buffer.readInt();
            OutputItemStackWithPercentages[] outputs = new OutputItemStackWithPercentages[outputCount];
            for(int i = 0;i < outputCount;i++) {
                ItemStack output = buffer.readItemStack();

                int percentageCount = buffer.readInt();
                double[] percentages = new double[percentageCount];
                for(int j = 0;j < percentageCount;j++)
                    percentages[j] = buffer.readDouble();

                outputs[i] = new OutputItemStackWithPercentages(output, percentages);
            }

            return new PlantGrowthChamberRecipe(recipeID, outputs, input, ticks);
        }

        @Override
        public void write(PacketByteBuf buffer, PlantGrowthChamberRecipe recipe) {
            recipe.input.write(buffer);
            buffer.writeInt(recipe.ticks);

            buffer.writeInt(recipe.outputs.length);
            for(OutputItemStackWithPercentages output:recipe.outputs) {
                buffer.writeItemStack(output.output);

                buffer.writeInt(output.percentages.length);
                for(double percentage:output.percentages)
                    buffer.writeDouble(percentage);
            }
        }
    }

    public record OutputItemStackWithPercentages(ItemStack output, double[] percentages) {}
}
