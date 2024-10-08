package me.jddev0.ep.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.util.ItemStackUtils;
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

        generatedOutputs[0] = ItemStackUtils.copyWithCount(output, output.getCount());
        generatedOutputs[1] = ItemStackUtils.copyWithCount(secondaryOutput.output(), secondaryOutput.percentages().length);

        return generatedOutputs;
    }

    public ItemStack[] generateOutputs(RandomSource randomSource) {
        ItemStack[] generatedOutputs = new ItemStack[2];

        generatedOutputs[0] = ItemStackUtils.copyWithCount(output, output.getCount());

        int count = 0;
        for(double percentage:secondaryOutput.percentages())
            if(randomSource.nextDouble() <= percentage)
                count++;

        generatedOutputs[1] = ItemStackUtils.copyWithCount(secondaryOutput.output(), count);

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

                if((indexMinCount == -1 || item.getCount() < minCount) && input.input().test(item) &&
                        item.getCount() >= input.count()) {
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
    public ItemStack assemble(Container container) {
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
        public static final ResourceLocation ID = EPAPI.id("alloy_furnace");

        @Override
        public AlloyFurnaceRecipe fromJson(ResourceLocation recipeID, JsonObject json) {
            JsonArray inputsJson = GsonHelper.getAsJsonArray(json, "inputs");
            IngredientWithCount[] inputs = new IngredientWithCount[inputsJson.size()];
            for(int i = 0;i < inputsJson.size();i++)
                inputs[i] = IngredientWithCount.fromJson(inputsJson.get(i).getAsJsonObject());

            int ticks = GsonHelper.getAsInt(json, "ticks");

            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "output"));

            OutputItemStackWithPercentages secondaryOutput = json.has("secondaryOutput")?
                    OutputItemStackWithPercentages.fromJson(json.get("secondaryOutput").getAsJsonObject()):
                    OutputItemStackWithPercentages.EMPTY;

            return new AlloyFurnaceRecipe(recipeID, output, secondaryOutput, inputs, ticks);
        }

        @Override
        public AlloyFurnaceRecipe fromNetwork(ResourceLocation recipeID, FriendlyByteBuf buffer) {
            int len = buffer.readInt();
            IngredientWithCount[] inputs = new IngredientWithCount[len];
            for(int i = 0;i < len;i++)
                inputs[i] = IngredientWithCount.fromNetwork(buffer);

            int ticks = buffer.readInt();

            ItemStack output = buffer.readItem();

            OutputItemStackWithPercentages secondaryOutput = OutputItemStackWithPercentages.fromNetwork(buffer);

            return new AlloyFurnaceRecipe(recipeID, output, secondaryOutput, inputs, ticks);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, AlloyFurnaceRecipe recipe) {
            buffer.writeInt(recipe.inputs.length);
            for(int i = 0; i < recipe.inputs.length; i++)
                recipe.inputs[i].toNetwork(buffer);

            buffer.writeInt(recipe.ticks);

            buffer.writeItemStack(recipe.output, false);

            recipe.secondaryOutput.toNetwork(buffer);
        }
    }
}
