package me.jddev0.ep.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class AlloyFurnaceRecipe implements Recipe<Container> {
    private final ResourceLocation id;
    private final ItemStack output;
    private final OutputItemStackWithPercentages secondaryOutput;
    private final IngredientWithCount[] inputs;
    private final int ticks;

    public AlloyFurnaceRecipe(ResourceLocation id, ItemStack output,
                              OutputItemStackWithPercentages secondaryOutput, IngredientWithCount[] inputs, int ticks) {
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

    public ItemStack[] generateOutputs(RandomSource randomSource) {
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
    public boolean matches(Container container, Level level) {
        if(level.isClientSide)
            return false;

        boolean[] usedIndices = new boolean[3];
        for(int i = 0;i < 3;i++)
            usedIndices[i] = container.getItem(i).isEmpty();

        int len = Math.min(inputs.length, 3);
        for(int i = 0;i < len;i++) {
            IngredientWithCount input = inputs[i];

            int indexMinCount = -1;
            int minCount = Integer.MAX_VALUE;

            for(int j = 0;j < 3;j++) {
                if(usedIndices[j])
                    continue;

                ItemStack item = container.getItem(j);

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
    public ItemStack assemble(Container container, RegistryAccess registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(ModBlocks.ALLOY_FURNACE_ITEM.get());
    }

    @Override
    public ResourceLocation getId() {
        return id;
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

    public static final class Type implements RecipeType<AlloyFurnaceRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "alloy_furnace";
    }

    public static final class Serializer implements RecipeSerializer<AlloyFurnaceRecipe> {
        private Serializer() {}

        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation(EnergizedPowerMod.MODID, "alloy_furnace");

        @Override
        public AlloyFurnaceRecipe fromJson(ResourceLocation recipeID, JsonObject json) {
            JsonArray inputsJson = GsonHelper.getAsJsonArray(json, "inputs");
            IngredientWithCount[] inputs = new IngredientWithCount[inputsJson.size()];
            for(int i = 0;i < inputsJson.size();i++) {
                JsonObject inputJson = inputsJson.get(i).getAsJsonObject();

                Ingredient input = Ingredient.fromJson(inputJson.get("input"));
                int count = inputJson.has("count")?GsonHelper.getAsInt(inputJson, "count"):1;

                inputs[i] = new IngredientWithCount(input, count);
            }
            int ticks = GsonHelper.getAsInt(json, "ticks");

            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "output"));

            OutputItemStackWithPercentages secondaryOutputItemStackWithPercentages;
            if(json.has("secondaryOutput")) {
                JsonObject secondaryOutputJson = json.getAsJsonObject("secondaryOutput");

                ItemStack secondaryOutput = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(secondaryOutputJson, "output"));

                JsonArray percentagesJson = GsonHelper.getAsJsonArray(secondaryOutputJson, "percentages");
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
        public AlloyFurnaceRecipe fromNetwork(ResourceLocation recipeID, FriendlyByteBuf buffer) {
            int len = buffer.readInt();
            IngredientWithCount[] inputs = new IngredientWithCount[len];
            for(int i = 0;i < len;i++) {
                Ingredient input = Ingredient.fromNetwork(buffer);
                int count = buffer.readInt();

                inputs[i] = new IngredientWithCount(input, count);
            }

            int ticks = buffer.readInt();

            ItemStack output = buffer.readItem();

            ItemStack secondaryOutput = buffer.readItem();

            int percentageCount = buffer.readInt();
            double[] percentages = new double[percentageCount];
            for(int j = 0;j < percentageCount;j++)
                percentages[j] = buffer.readDouble();

            OutputItemStackWithPercentages secondaryOutputItemStackWithPercentages =
                    new OutputItemStackWithPercentages(secondaryOutput, percentages);

            return new AlloyFurnaceRecipe(recipeID, output, secondaryOutputItemStackWithPercentages, inputs, ticks);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, AlloyFurnaceRecipe recipe) {
            buffer.writeInt(recipe.inputs.length);
            for(int i = 0; i < recipe.inputs.length; i++) {
                recipe.inputs[i].input.toNetwork(buffer);
                buffer.writeInt(recipe.inputs[i].count);
            }

            buffer.writeInt(recipe.ticks);

            buffer.writeItemStack(recipe.output, false);

            buffer.writeItemStack(recipe.secondaryOutput.output, false);

            buffer.writeInt(recipe.secondaryOutput.percentages.length);
            for(double percentage:recipe.secondaryOutput.percentages)
                buffer.writeDouble(percentage);
        }
    }

    public record IngredientWithCount(Ingredient input, int count) {}

    public record OutputItemStackWithPercentages(ItemStack output, double[] percentages) {}
}
