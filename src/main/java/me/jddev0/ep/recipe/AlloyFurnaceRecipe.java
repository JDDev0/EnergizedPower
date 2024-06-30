package me.jddev0.ep.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.util.ItemStackUtils;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class AlloyFurnaceRecipe implements Recipe<Inventory> {
    private final Identifier id;
    private final ItemStack output;
    private final OutputItemStackWithPercentages secondaryOutput;
    private final IngredientWithCount[] inputs;
    private final int ticks;

    public AlloyFurnaceRecipe(Identifier id, ItemStack output, OutputItemStackWithPercentages secondaryOutput,
                              IngredientWithCount[] inputs, int ticks) {
        this.id = id;
        this.output = output;
        this.secondaryOutput = secondaryOutput;
        this.inputs = inputs;
        this.ticks = ticks;
    }

    public ItemStack getOutput() {
        return output;
    }

    public OutputItemStackWithPercentages getSecondaryOutput() {
        return secondaryOutput;
    }

    public IngredientWithCount[] getInputs() {
        return inputs;
    }

    public int getTicks() {
        return ticks;
    }

    public ItemStack[] getMaxOutputCounts() {
        ItemStack[] generatedOutputs = new ItemStack[2];

        generatedOutputs[0] = output.copyWithCount(output.getCount());
        generatedOutputs[1] = secondaryOutput.output.copyWithCount(secondaryOutput.percentages.length);

        return generatedOutputs;
    }

    public ItemStack[] generateOutputs(Random randomSource) {
        ItemStack[] generatedOutputs = new ItemStack[2];

        generatedOutputs[0] = output.copyWithCount(output.getCount());

        int count = 0;
        for(double percentage:secondaryOutput.percentages)
            if(randomSource.nextDouble() <= percentage)
                count++;

        generatedOutputs[1] = secondaryOutput.output.copyWithCount(count);

        return generatedOutputs;
    }

    @Override
    public boolean matches(Inventory container, World level) {
        if(level.isClient)
            return false;

        boolean[] usedIndices = new boolean[3];
        for(int i = 0;i < 3;i++)
            usedIndices[i] = container.getStack(i).isEmpty();

        int len = Math.min(inputs.length, 3);
        for(int i = 0;i < len;i++) {
            IngredientWithCount input = inputs[i];

            int indexMinCount = -1;
            int minCount = Integer.MAX_VALUE;

            for(int j = 0;j < 3;j++) {
                if(usedIndices[j])
                    continue;

                ItemStack item = container.getStack(j);

                if((indexMinCount == -1 || item.getCount() < minCount) && input.input.test(item) &&
                        item.getCount() >= input.count) {
                    indexMinCount = j;
                    minCount = item.getCount();
                }
            }

            if(indexMinCount == -1)
                return false; //Ingredient did not match any item

            usedIndices[indexMinCount] = true;
        }

        for(boolean usedIndex:usedIndices)
            if(!usedIndex) //Unused items present
                return false;

        return true;
    }

    @Override
    public ItemStack craft(Inventory container, DynamicRegistryManager registries) {
        return output;
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(ModBlocks.ALLOY_FURNACE_ITEM);
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

    public static final class Type implements RecipeType<AlloyFurnaceRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "alloy_furnace";
    }

    public static final class Serializer implements RecipeSerializer<AlloyFurnaceRecipe> {
        private Serializer() {}

        public static final Serializer INSTANCE = new Serializer();
        public static final Identifier ID = Identifier.of(EnergizedPowerMod.MODID, "alloy_furnace");

        @Override
        public AlloyFurnaceRecipe read(Identifier recipeID, JsonObject json) {
            JsonArray inputsJson = JsonHelper.getArray(json, "inputs");
            IngredientWithCount[] inputs = new IngredientWithCount[inputsJson.size()];
            for(int i = 0;i < inputsJson.size();i++) {
                JsonObject inputJson = inputsJson.get(i).getAsJsonObject();

                Ingredient input = Ingredient.fromJson(inputJson.get("input"));
                int count = inputJson.has("count")?JsonHelper.getInt(inputJson, "count"):1;

                inputs[i] = new IngredientWithCount(input, count);
            }
            int ticks = JsonHelper.getInt(json, "ticks");

            ItemStack output = ItemStackUtils.fromJson(JsonHelper.getObject(json, "output"));

            OutputItemStackWithPercentages secondaryOutputItemStackWithPercentages;
            if(json.has("secondaryOutput")) {
                JsonObject secondaryOutputJson = json.getAsJsonObject("secondaryOutput");

                ItemStack secondaryOutput = ItemStackUtils.fromJson(JsonHelper.getObject(secondaryOutputJson, "output"));

                JsonArray percentagesJson = JsonHelper.getArray(secondaryOutputJson, "percentages");
                double[] percentages = new double[percentagesJson.size()];
                for(int j = 0;j < percentagesJson.size();j++) {
                    double value = percentagesJson.get(j).getAsDouble();

                    percentages[j] = value;
                }

                secondaryOutputItemStackWithPercentages = new OutputItemStackWithPercentages(secondaryOutput, percentages);
            }else {
                secondaryOutputItemStackWithPercentages = new OutputItemStackWithPercentages(ItemStack.EMPTY, new double[0]);
            }

            return new AlloyFurnaceRecipe(recipeID, output, secondaryOutputItemStackWithPercentages, inputs, ticks);
        }

        @Override
        public AlloyFurnaceRecipe read(Identifier recipeID, PacketByteBuf buffer) {
            int len = buffer.readInt();
            IngredientWithCount[] inputs = new IngredientWithCount[len];
            for(int i = 0;i < len;i++) {
                Ingredient input = Ingredient.fromPacket(buffer);
                int count = buffer.readInt();

                inputs[i] = new IngredientWithCount(input, count);
            }

            int ticks = buffer.readInt();

            ItemStack output = buffer.readItemStack();

            ItemStack secondaryOutput = buffer.readItemStack();

            int percentageCount = buffer.readInt();
            double[] percentages = new double[percentageCount];
            for(int j = 0;j < percentageCount;j++)
                percentages[j] = buffer.readDouble();

            OutputItemStackWithPercentages secondaryOutputItemStackWithPercentages =
                    new OutputItemStackWithPercentages(secondaryOutput, percentages);

            return new AlloyFurnaceRecipe(recipeID, output, secondaryOutputItemStackWithPercentages, inputs, ticks);
        }

        @Override
        public void write(PacketByteBuf buffer, AlloyFurnaceRecipe recipe) {
            buffer.writeInt(recipe.inputs.length);
            for(int i = 0; i < recipe.inputs.length; i++) {
                recipe.inputs[i].input.write(buffer);
                buffer.writeInt(recipe.inputs[i].count);
            }

            buffer.writeInt(recipe.ticks);

            buffer.writeItemStack(recipe.output);

            buffer.writeItemStack(recipe.secondaryOutput.output);

            buffer.writeInt(recipe.secondaryOutput.percentages.length);
            for(double percentage:recipe.secondaryOutput.percentages)
                buffer.writeDouble(percentage);
        }
    }

    public record IngredientWithCount(Ingredient input, int count) {}

    public record OutputItemStackWithPercentages(ItemStack output, double[] percentages) {}
}
