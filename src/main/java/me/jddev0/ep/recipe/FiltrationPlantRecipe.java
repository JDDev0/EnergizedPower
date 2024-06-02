package me.jddev0.ep.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.util.ItemStackUtils;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class FiltrationPlantRecipe implements Recipe<Inventory> {
    private final Identifier id;
    private final OutputItemStackWithPercentages output;
    private final OutputItemStackWithPercentages secondaryOutput;
    private final Identifier icon;

    public FiltrationPlantRecipe(Identifier id, OutputItemStackWithPercentages output, OutputItemStackWithPercentages secondaryOutput, Identifier icon) {
        this.id = id;
        this.output = output;
        this.secondaryOutput = secondaryOutput;
        this.icon = icon;
    }

    public OutputItemStackWithPercentages getOutputItem() {
        return output;
    }

    public OutputItemStackWithPercentages getSecondaryOutput() {
        return secondaryOutput;
    }

    public Identifier getIcon() {
        return icon;
    }

    public ItemStack[] getMaxOutputCounts() {
        ItemStack[] generatedOutputs = new ItemStack[2];

        generatedOutputs[0] = ItemStackUtils.copyWithCount(output.output, output.percentages.length);
        generatedOutputs[1] = ItemStackUtils.copyWithCount(secondaryOutput.output, secondaryOutput.percentages.length);

        return generatedOutputs;
    }

    public ItemStack[] generateOutputs(Random randomSource) {
        ItemStack[] generatedOutputs = new ItemStack[2];
        for(int i = 0;i < 2;i++) {
            int count = 0;
            OutputItemStackWithPercentages output = i == 0?this.output:this.secondaryOutput;

            for(double percentage:output.percentages)
                if(randomSource.nextDouble() <= percentage)
                    count++;

            generatedOutputs[i] = ItemStackUtils.copyWithCount(output.output, count);
        }

        return generatedOutputs;
    }

    @Override
    public boolean matches(Inventory container, World level) {
        return false;
    }

    @Override
    public ItemStack craft(Inventory container) {
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
    public ItemStack createIcon() {
        return new ItemStack(ModBlocks.FILTRATION_PLANT_ITEM);
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

    public static final class Type implements RecipeType<FiltrationPlantRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "filtration_plant";
    }

    public static final class Serializer implements RecipeSerializer<FiltrationPlantRecipe> {
        private Serializer() {}

        public static final Serializer INSTANCE = new Serializer();
        public static final Identifier ID = new Identifier(EnergizedPowerMod.MODID, "filtration_plant");

        @Override
        public FiltrationPlantRecipe read(Identifier recipeID, JsonObject json) {
            OutputItemStackWithPercentages[] outputs = new OutputItemStackWithPercentages[2];
            for(int i = 0;i < 2;i++) {
                JsonObject outputJson = json.getAsJsonObject(i == 0?"output":"secondaryOutput");

                ItemStack output = ItemStackUtils.fromJson(JsonHelper.getObject(outputJson, "output"));

                JsonArray percentagesJson = JsonHelper.getArray(outputJson, "percentages");
                double[] percentages = new double[percentagesJson.size()];
                for(int j = 0;j < percentagesJson.size();j++) {
                    double value = percentagesJson.get(j).getAsDouble();

                    percentages[j] = value;
                }

                outputs[i] = new OutputItemStackWithPercentages(output, percentages);

                if(!json.has("secondaryOutput")) {
                    outputs[1] = new OutputItemStackWithPercentages(ItemStack.EMPTY, new double[0]);

                    break;
                }
            }

            Identifier icon = Identifier.tryParse(JsonHelper.getString(json, "icon"));

            return new FiltrationPlantRecipe(recipeID, outputs[0], outputs[1], icon);
        }

        @Override
        public FiltrationPlantRecipe read(Identifier recipeID, PacketByteBuf buffer) {
            OutputItemStackWithPercentages[] outputs = new OutputItemStackWithPercentages[2];
            for(int i = 0;i < 2;i++) {
                ItemStack output = buffer.readItemStack();

                int percentageCount = buffer.readInt();
                double[] percentages = new double[percentageCount];
                for(int j = 0;j < percentageCount;j++)
                    percentages[j] = buffer.readDouble();

                outputs[i] = new OutputItemStackWithPercentages(output, percentages);
            }

            Identifier icon = buffer.readIdentifier();

            return new FiltrationPlantRecipe(recipeID, outputs[0], outputs[1], icon);
        }

        @Override
        public void write(PacketByteBuf buffer, FiltrationPlantRecipe recipe) {
            for(int i = 0;i < 2;i++) {
                OutputItemStackWithPercentages output = i == 0?recipe.output:recipe.secondaryOutput;
                buffer.writeItemStack(output.output);

                buffer.writeInt(output.percentages.length);
                for(double percentage:output.percentages)
                    buffer.writeDouble(percentage);
            }

            buffer.writeIdentifier(recipe.icon);
        }
    }

    public record OutputItemStackWithPercentages(ItemStack output, double[] percentages) {}
}