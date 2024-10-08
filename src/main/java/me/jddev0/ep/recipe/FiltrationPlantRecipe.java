package me.jddev0.ep.recipe;

import com.google.gson.JsonObject;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
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

    public OutputItemStackWithPercentages getOutput() {
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

        generatedOutputs[0] = output.output().copyWithCount(output.percentages().length);
        generatedOutputs[1] = secondaryOutput.output().copyWithCount(secondaryOutput.percentages().length);

        return generatedOutputs;
    }

    public ItemStack[] generateOutputs(Random randomSource) {
        ItemStack[] generatedOutputs = new ItemStack[2];
        for(int i = 0;i < 2;i++) {
            int count = 0;
            OutputItemStackWithPercentages output = i == 0?this.output:this.secondaryOutput;

            for(double percentage:output.percentages())
                if(randomSource.nextDouble() <= percentage)
                    count++;

            generatedOutputs[i] = output.output().copyWithCount(count);
        }

        return generatedOutputs;
    }

    @Override
    public boolean matches(Inventory container, World level) {
        return false;
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
    public ItemStack getOutput(DynamicRegistryManager registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(EPBlocks.FILTRATION_PLANT_ITEM);
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
        public static final Identifier ID = EPAPI.id("filtration_plant");

        @Override
        public FiltrationPlantRecipe read(Identifier recipeID, JsonObject json) {
            OutputItemStackWithPercentages[] outputs = new OutputItemStackWithPercentages[2];

            outputs[0] = OutputItemStackWithPercentages.fromJson(json.get("output").getAsJsonObject());

            outputs[1] = json.has("secondaryOutput")?
                    OutputItemStackWithPercentages.fromJson(json.get("secondaryOutput").getAsJsonObject()):
                    OutputItemStackWithPercentages.EMPTY;

            Identifier icon = Identifier.tryParse(JsonHelper.getString(json, "icon"));

            return new FiltrationPlantRecipe(recipeID, outputs[0], outputs[1], icon);
        }

        @Override
        public FiltrationPlantRecipe read(Identifier recipeID, PacketByteBuf buffer) {
            OutputItemStackWithPercentages[] outputs = new OutputItemStackWithPercentages[2];
            for(int i = 0;i < 2;i++)
                outputs[i] = OutputItemStackWithPercentages.fromNetwork(buffer);

            Identifier icon = buffer.readIdentifier();

            return new FiltrationPlantRecipe(recipeID, outputs[0], outputs[1], icon);
        }

        @Override
        public void write(PacketByteBuf buffer, FiltrationPlantRecipe recipe) {
            for(int i = 0;i < 2;i++) {
                OutputItemStackWithPercentages output = i == 0?recipe.output:recipe.secondaryOutput;

                output.toNetwork(buffer);
            }

            buffer.writeIdentifier(recipe.icon);
        }
    }
}