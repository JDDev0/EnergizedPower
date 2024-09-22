package me.jddev0.ep.recipe;

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

public class FiltrationPlantRecipe implements Recipe<Container> {
    private final ResourceLocation id;
    private final OutputItemStackWithPercentages output;
    private final OutputItemStackWithPercentages secondaryOutput;
    private final ResourceLocation icon;

    public FiltrationPlantRecipe(ResourceLocation id, OutputItemStackWithPercentages output, OutputItemStackWithPercentages secondaryOutput, ResourceLocation icon) {
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

    public ResourceLocation getIcon() {
        return icon;
    }

    public ItemStack[] getMaxOutputCounts() {
        ItemStack[] generatedOutputs = new ItemStack[2];

        generatedOutputs[0] = output.output().copyWithCount(output.percentages().length);
        generatedOutputs[1] = secondaryOutput.output().copyWithCount(secondaryOutput.percentages().length);

        return generatedOutputs;
    }

    public ItemStack[] generateOutputs(RandomSource randomSource) {
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
    public boolean matches(Container container, Level level) {
        return false;
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
    public ItemStack getToastSymbol() {
        return new ItemStack(ModBlocks.FILTRATION_PLANT.get());
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

    public static final class Type implements RecipeType<FiltrationPlantRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "filtration_plant";
    }

    public static final class Serializer implements RecipeSerializer<FiltrationPlantRecipe> {
        private Serializer() {}

        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation(EnergizedPowerMod.MODID, "filtration_plant");

        @Override
        public FiltrationPlantRecipe fromJson(ResourceLocation recipeID, JsonObject json) {
            OutputItemStackWithPercentages[] outputs = new OutputItemStackWithPercentages[2];

            outputs[0] = OutputItemStackWithPercentages.fromJson(json.get("output").getAsJsonObject());

            outputs[1] = json.has("secondaryOutput")?
                    OutputItemStackWithPercentages.fromJson(json.get("secondaryOutput").getAsJsonObject()):
                    OutputItemStackWithPercentages.EMPTY;

            ResourceLocation icon = ResourceLocation.tryParse(GsonHelper.getAsString(json, "icon"));

            return new FiltrationPlantRecipe(recipeID, outputs[0], outputs[1], icon);
        }

        @Override
        public FiltrationPlantRecipe fromNetwork(ResourceLocation recipeID, FriendlyByteBuf buffer) {
            OutputItemStackWithPercentages[] outputs = new OutputItemStackWithPercentages[2];
            for(int i = 0;i < 2;i++)
                outputs[i] = OutputItemStackWithPercentages.fromNetwork(buffer);

            ResourceLocation icon = buffer.readResourceLocation();

            return new FiltrationPlantRecipe(recipeID, outputs[0], outputs[1], icon);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, FiltrationPlantRecipe recipe) {
            for(int i = 0;i < 2;i++) {
                OutputItemStackWithPercentages output = i == 0?recipe.output:recipe.secondaryOutput;
                output.toNetwork(buffer);
            }

            buffer.writeResourceLocation(recipe.icon);
        }
    }
}
