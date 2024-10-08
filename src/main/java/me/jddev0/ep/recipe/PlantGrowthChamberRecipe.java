package me.jddev0.ep.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.codec.ArrayCodec;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class PlantGrowthChamberRecipe implements Recipe<Inventory> {
    private final OutputItemStackWithPercentages[] outputs;
    private final Ingredient input;
    private final int ticks;

    public PlantGrowthChamberRecipe(OutputItemStackWithPercentages[] outputs, Ingredient input, int ticks) {
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
            generatedOutputs[i] = output.output().copyWithCount(output.percentages().length);
        }

        return generatedOutputs;
    }

    public ItemStack[] generateOutputs(Random randomSource) {
        ItemStack[] generatedOutputs = new ItemStack[outputs.length];
        for(int i = 0;i < outputs.length;i++) {
            int count = 0;
            OutputItemStackWithPercentages output = outputs[i];

            for(double percentage:output.percentages())
                if(randomSource.nextDouble() <= percentage)
                    count++;

            generatedOutputs[i] = output.output().copyWithCount(count);
        }

        return generatedOutputs;
    }

    @Override
    public boolean matches(Inventory container, World level) {
        if(level.isClient())
            return false;

        return input.test(container.getStack(0));
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
    public ItemStack getResult(DynamicRegistryManager registryAccess) {
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
        return new ItemStack(EPBlocks.PLANT_GROWTH_CHAMBER_ITEM);
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
        public static final Identifier ID = EPAPI.id("plant_growth_chamber");

        private final Codec<PlantGrowthChamberRecipe> CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(new ArrayCodec<>(OutputItemStackWithPercentages.CODEC_NONEMPTY, OutputItemStackWithPercentages[]::new).fieldOf("outputs").forGetter((recipe) -> {
                return recipe.outputs;
            }), Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("ingredient").forGetter((recipe) -> {
                return recipe.input;
            }), Codecs.POSITIVE_INT.fieldOf("ticks").forGetter((recipe) -> {
                return recipe.ticks;
            })).apply(instance, PlantGrowthChamberRecipe::new);
        });

        @Override
        public Codec<PlantGrowthChamberRecipe> codec() {
            return CODEC;
        }

        @Override
        public PlantGrowthChamberRecipe read(PacketByteBuf buffer) {
            Ingredient input = Ingredient.fromPacket(buffer);
            int ticks = buffer.readInt();

            int outputCount = buffer.readInt();
            OutputItemStackWithPercentages[] outputs = new OutputItemStackWithPercentages[outputCount];
            for(int i = 0;i < outputCount;i++)
                outputs[i] = OutputItemStackWithPercentages.fromNetwork(buffer);

            return new PlantGrowthChamberRecipe(outputs, input, ticks);
        }

        @Override
        public void write(PacketByteBuf buffer, PlantGrowthChamberRecipe recipe) {
            recipe.input.write(buffer);
            buffer.writeInt(recipe.ticks);

            buffer.writeInt(recipe.outputs.length);
            for(OutputItemStackWithPercentages output:recipe.outputs)
                output.toNetwork(buffer);
        }
    }
}
