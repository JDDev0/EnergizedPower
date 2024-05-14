package me.jddev0.ep.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class CrystalGrowthChamberRecipe implements Recipe<Inventory> {
    private final Identifier id;
    private final OutputItemStackWithPercentages output;
    private final Ingredient input;
    private final int inputCount;
    private final int ticks;

    public CrystalGrowthChamberRecipe(Identifier id, OutputItemStackWithPercentages output, Ingredient input,
                                      int inputCount, int ticks) {
        this.id = id;
        this.output = output;
        this.input = input;
        this.inputCount = inputCount;
        this.ticks = ticks;
    }

    public OutputItemStackWithPercentages getOutput() {
        return output;
    }

    public Ingredient getInput() {
        return input;
    }

    public int getInputCount() {
        return inputCount;
    }

    public int getTicks() {
        return ticks;
    }

    public ItemStack getMaxOutputCount() {
        return output.output.copyWithCount(output.percentages.length);
    }

    public ItemStack generateOutput(Random randomSource) {
        int count = 0;

        for(double percentage:output.percentages)
            if(randomSource.nextDouble() <= percentage)
                count++;

        return output.output.copyWithCount(count);
    }

    @Override
    public boolean matches(Inventory container, World level) {
        if(level.isClient())
            return false;

        return input.test(container.getStack(0)) && container.getStack(0).getCount() >= inputCount;
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
        return new ItemStack(ModBlocks.CRYSTAL_GROWTH_CHAMBER_ITEM);
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

    public static final class Type implements RecipeType<CrystalGrowthChamberRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "crystal_growth_chamber";
    }

    public static final class Serializer implements RecipeSerializer<CrystalGrowthChamberRecipe> {
        private Serializer() {}

        public static final Serializer INSTANCE = new Serializer();
        public static final Identifier ID = new Identifier(EnergizedPowerMod.MODID, "crystal_growth_chamber");

        @Override
        public CrystalGrowthChamberRecipe read(Identifier recipeID, JsonObject json) {
            Ingredient input = Ingredient.fromJson(json.get("ingredient"));
            int inputCount = json.has("inputCount")?JsonHelper.getInt(json, "inputCount"):1;
            int ticks = JsonHelper.getInt(json, "ticks");

            JsonObject outputJson = json.get("output").getAsJsonObject();

            ItemStack output = ShapedRecipe.outputFromJson(JsonHelper.getObject(outputJson, "output"));

            JsonArray percentagesJson = JsonHelper.getArray(outputJson, "percentages");
            double[] percentages = new double[percentagesJson.size()];
            for(int j = 0;j < percentagesJson.size();j++)
                percentages[j] = percentagesJson.get(j).getAsDouble();

            OutputItemStackWithPercentages outputWithPercentages = new OutputItemStackWithPercentages(output, percentages);

            return new CrystalGrowthChamberRecipe(recipeID, outputWithPercentages, input, inputCount, ticks);
        }

        @Override
        public CrystalGrowthChamberRecipe read(Identifier recipeId, PacketByteBuf buffer) {
            Ingredient input = Ingredient.fromPacket(buffer);
            int inputCount = buffer.readInt();
            int ticks = buffer.readInt();

            ItemStack output = buffer.readItemStack();

            int percentageCount = buffer.readInt();
            double[] percentages = new double[percentageCount];
            for(int j = 0;j < percentageCount;j++)
                percentages[j] = buffer.readDouble();

            OutputItemStackWithPercentages outputItemStackWithPercentages = new OutputItemStackWithPercentages(output, percentages);

            return new CrystalGrowthChamberRecipe(recipeId, outputItemStackWithPercentages, input, inputCount, ticks);
        }

        @Override
        public void write(PacketByteBuf buffer, CrystalGrowthChamberRecipe recipe) {
            recipe.input.write(buffer);
            buffer.writeInt(recipe.inputCount);
            buffer.writeInt(recipe.ticks);

            buffer.writeItemStack(recipe.output.output);

            buffer.writeInt(recipe.output.percentages.length);
            for(double percentage:recipe.output.percentages)
                buffer.writeDouble(percentage);
        }
    }

    public record OutputItemStackWithPercentages(ItemStack output, double[] percentages) {}
}
