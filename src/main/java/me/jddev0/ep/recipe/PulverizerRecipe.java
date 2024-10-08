package me.jddev0.ep.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class PulverizerRecipe implements Recipe<Container> {
    private final ResourceLocation id;
    private final OutputItemStackWithPercentages output;
    private final OutputItemStackWithPercentages secondaryOutput;
    private final Ingredient input;

    public PulverizerRecipe(ResourceLocation id, OutputItemStackWithPercentages output, OutputItemStackWithPercentages secondaryOutput, Ingredient input) {
        this.id = id;
        this.output = output;
        this.secondaryOutput = secondaryOutput;
        this.input = input;
    }

    public OutputItemStackWithPercentages getOutput() {
        return output;
    }

    public OutputItemStackWithPercentages getSecondaryOutput() {
        return secondaryOutput;
    }

    public Ingredient getInput() {
        return input;
    }

    public ItemStack[] getMaxOutputCounts(boolean advanced) {
        ItemStack[] generatedOutputs = new ItemStack[2];

        generatedOutputs[0] = output.output.copyWithCount(advanced?output.percentagesAdvanced.length:
                output.percentages.length);
        generatedOutputs[1] = secondaryOutput.output.copyWithCount(advanced?secondaryOutput.percentagesAdvanced.length:
                secondaryOutput.percentages.length);

        return generatedOutputs;
    }

    public ItemStack[] generateOutputs(RandomSource randomSource, boolean advanced) {
        ItemStack[] generatedOutputs = new ItemStack[2];
        for(int i = 0;i < 2;i++) {
            int count = 0;
            OutputItemStackWithPercentages output = i == 0?this.output:this.secondaryOutput;

            for(double percentage:(advanced?output.percentagesAdvanced:output.percentages))
                if(randomSource.nextDouble() <= percentage)
                    count++;

            generatedOutputs[i] = output.output.copyWithCount(count);
        }

        return generatedOutputs;
    }

    @Override
    public boolean matches(Container container, Level level) {
        if(level.isClientSide)
            return false;

        return input.test(container.getItem(0));
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
        return new ItemStack(EPBlocks.PULVERIZER_ITEM.get());
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

    public static final class Type implements RecipeType<PulverizerRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "pulverizer";
    }

    public static final class Serializer implements RecipeSerializer<PulverizerRecipe> {
        private Serializer() {}

        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = EPAPI.id("pulverizer");

        @Override
        public PulverizerRecipe fromJson(ResourceLocation recipeID, JsonObject json) {
            Ingredient input = Ingredient.fromJson(json.get("ingredient"));

            OutputItemStackWithPercentages[] outputs = new OutputItemStackWithPercentages[2];
            for(int i = 0;i < 2;i++) {
                JsonObject outputJson = json.getAsJsonObject(i == 0?"output":"secondaryOutput");

                ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(outputJson, "output"));

                JsonArray percentagesJson = GsonHelper.getAsJsonArray(outputJson, "percentages");
                double[] percentages = new double[percentagesJson.size()];
                boolean minimumAtLeastOneFlag = false;
                for(int j = 0;j < percentagesJson.size();j++) {
                    double value = percentagesJson.get(j).getAsDouble();

                    minimumAtLeastOneFlag |= (int)value >= 1;
                    percentages[j] = value;
                }

                if(i == 0 && !minimumAtLeastOneFlag)
                    throw new JsonSyntaxException("The primary output must have a minimum count of at least 1 (At least one percentage value must be >= 1.0)");

                JsonArray percentagesAdvancedJson = outputJson.has("percentagesAdvanced")?
                        GsonHelper.getAsJsonArray(outputJson, "percentagesAdvanced"):percentagesJson;
                double[] percentagesAdvanced = new double[percentagesAdvancedJson.size()];
                minimumAtLeastOneFlag = false;
                for(int j = 0;j < percentagesAdvancedJson.size();j++) {
                    double value = percentagesAdvancedJson.get(j).getAsDouble();

                    minimumAtLeastOneFlag |= (int)value >= 1;
                    percentagesAdvanced[j] = value;
                }

                if(i == 0 && !minimumAtLeastOneFlag)
                    throw new JsonSyntaxException("The primary output must have a minimum count of at least 1 (At least one percentage value must be >= 1.0)");

                outputs[i] = new OutputItemStackWithPercentages(output, percentages, percentagesAdvanced);

                if(!json.has("secondaryOutput")) {
                    outputs[1] = new OutputItemStackWithPercentages(ItemStack.EMPTY, new double[0], new double[0]);

                    break;
                }
            }

            return new PulverizerRecipe(recipeID, outputs[0], outputs[1], input);
        }

        @Override
        public PulverizerRecipe fromNetwork(ResourceLocation recipeID, FriendlyByteBuf buffer) {
            Ingredient input = Ingredient.fromNetwork(buffer);

            OutputItemStackWithPercentages[] outputs = new OutputItemStackWithPercentages[2];
            for(int i = 0;i < 2;i++) {
                ItemStack output = buffer.readItem();

                int percentageCount = buffer.readInt();
                double[] percentages = new double[percentageCount];
                for(int j = 0;j < percentageCount;j++)
                    percentages[j] = buffer.readDouble();

                int percentageAdvancedCount = buffer.readInt();
                double[] percentagesAdvanced = new double[percentageAdvancedCount];
                for(int j = 0;j < percentageAdvancedCount;j++)
                    percentagesAdvanced[j] = buffer.readDouble();

                outputs[i] = new OutputItemStackWithPercentages(output, percentages, percentagesAdvanced);
            }

            return new PulverizerRecipe(recipeID, outputs[0], outputs[1], input);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, PulverizerRecipe recipe) {
            recipe.input.toNetwork(buffer);

            for(int i = 0;i < 2;i++) {
                OutputItemStackWithPercentages output = i == 0?recipe.output:recipe.secondaryOutput;
                buffer.writeItemStack(output.output, false);

                buffer.writeInt(output.percentages.length);
                for(double percentage:output.percentages)
                    buffer.writeDouble(percentage);

                buffer.writeInt(output.percentagesAdvanced.length);
                for(double percentage:output.percentagesAdvanced)
                    buffer.writeDouble(percentage);
            }
        }
    }

    public record OutputItemStackWithPercentages(ItemStack output, double[] percentages, double[] percentagesAdvanced) {
        public OutputItemStackWithPercentages(ItemStack output, double percentage, double percentageAdvanced) {
            this(output, new double[] {
                    percentage
            }, new double[] {
                    percentageAdvanced
            });
        }

        public OutputItemStackWithPercentages(ItemStack output) {
            this(output, 1., 1.);
        }
    }
}
