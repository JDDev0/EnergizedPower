package me.jddev0.ep.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.codec.ArrayCodec;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;

public class PlantGrowthChamberRecipe implements EnergizedPowerBaseRecipe<RecipeInput> {
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
    public boolean matches(RecipeInput container, World level) {
        if(level.isClient())
            return false;

        return input.test(container.getStackInSlot(0));
    }

    @Override
    public ItemStack craft(RecipeInput container, RegistryWrapper.WrapperLookup registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public IngredientPlacement getIngredientPlacement() {
        return IngredientPlacement.NONE;
    }

    @Override
    public boolean isIgnoredInRecipeBook() {
        return true;
    }

    @Override
    public RecipeBookCategory getRecipeBookCategory() {
        return EPRecipes.PLANT_GROWTH_CHAMBER_CATEGORY;
    }

    @Override
    public RecipeSerializer<? extends Recipe<RecipeInput>> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<? extends Recipe<RecipeInput>> getType() {
        return Type.INSTANCE;
    }

    @Override
    public List<Ingredient> getIngredients() {
        return List.of(input);
    }

    @Override
    public boolean isIngredient(ItemStack itemStack) {
        return input.test(itemStack);
    }

    @Override
    public boolean isResult(ItemStack itemStack) {
        return Arrays.stream(outputs).map(OutputItemStackWithPercentages::output).
                anyMatch(output -> ItemStack.areItemsAndComponentsEqual(output, itemStack));
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

        private final MapCodec<PlantGrowthChamberRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(new ArrayCodec<>(OutputItemStackWithPercentages.CODEC_NONEMPTY, OutputItemStackWithPercentages[]::new).
                    fieldOf("results").forGetter((recipe) -> {
                return recipe.outputs;
            }), Ingredient.CODEC.fieldOf("ingredient").forGetter((recipe) -> {
                return recipe.input;
            }), Codecs.POSITIVE_INT.fieldOf("ticks").forGetter((recipe) -> {
                return recipe.ticks;
            })).apply(instance, PlantGrowthChamberRecipe::new);
        });

        private final PacketCodec<RegistryByteBuf, PlantGrowthChamberRecipe> PACKET_CODEC = PacketCodec.ofStatic(
                Serializer::write, Serializer::read);

        @Override
        public MapCodec<PlantGrowthChamberRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, PlantGrowthChamberRecipe> packetCodec() {
            return PACKET_CODEC;
        }

        private static PlantGrowthChamberRecipe read(RegistryByteBuf buffer) {
            Ingredient input = Ingredient.PACKET_CODEC.decode(buffer);
            int ticks = buffer.readInt();

            int outputCount = buffer.readInt();
            OutputItemStackWithPercentages[] outputs = new OutputItemStackWithPercentages[outputCount];
            for(int i = 0;i < outputCount;i++)
                outputs[i] = OutputItemStackWithPercentages.OPTIONAL_STREAM_CODEC.decode(buffer);

            return new PlantGrowthChamberRecipe(outputs, input, ticks);
        }

        private static void write(RegistryByteBuf buffer, PlantGrowthChamberRecipe recipe) {
            Ingredient.PACKET_CODEC.encode(buffer, recipe.input);
            buffer.writeInt(recipe.ticks);

            buffer.writeInt(recipe.outputs.length);
            for(OutputItemStackWithPercentages output:recipe.outputs)
                OutputItemStackWithPercentages.OPTIONAL_STREAM_CODEC.encode(buffer, output);
        }
    }
}
