package me.jddev0.ep.recipe;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.codec.ArrayCodec;
import me.jddev0.ep.codec.CodecFix;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;

public class AlloyFurnaceRecipe implements Recipe<RecipeInput> {
    private final ItemStack output;
    private final OutputItemStackWithPercentages secondaryOutput;
    private final IngredientWithCount[] inputs;
    private final int ticks;

    public AlloyFurnaceRecipe(ItemStack output, OutputItemStackWithPercentages secondaryOutput,
                              IngredientWithCount[] inputs, int ticks) {
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
    public boolean matches(RecipeInput container, World level) {
        if(level.isClient)
            return false;

        boolean[] usedIndices = new boolean[3];
        for(int i = 0;i < 3;i++)
            usedIndices[i] = container.getStackInSlot(i).isEmpty();

        int len = Math.min(inputs.length, 3);
        for(int i = 0;i < len;i++) {
            IngredientWithCount input = inputs[i];

            int indexMinCount = -1;
            int minCount = Integer.MAX_VALUE;

            for(int j = 0;j < 3;j++) {
                if(usedIndices[j])
                    continue;

                ItemStack item = container.getStackInSlot(j);

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
    public ItemStack craft(RecipeInput container, RegistryWrapper.WrapperLookup registries) {
        return output;
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(ModBlocks.ALLOY_FURNACE_ITEM);
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

        private final MapCodec<AlloyFurnaceRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(CodecFix.ITEM_STACK_CODEC.fieldOf("output").forGetter((recipe) -> {
                return recipe.output;
            }), OutputItemStackWithPercentages.CODEC.optionalFieldOf("secondaryOutput",
                    new OutputItemStackWithPercentages(ItemStack.EMPTY, new double[0])).forGetter((recipe) -> {
                return recipe.secondaryOutput;
            }), new ArrayCodec<>(IngredientWithCount.CODEC, IngredientWithCount[]::new).fieldOf("inputs").forGetter((recipe) -> {
                return recipe.inputs;
            }), Codecs.POSITIVE_INT.fieldOf("ticks").forGetter((recipe) -> {
                return recipe.ticks;
            })).apply(instance, AlloyFurnaceRecipe::new);
        });

        private final PacketCodec<RegistryByteBuf, AlloyFurnaceRecipe> PACKET_CODEC = PacketCodec.ofStatic(
                Serializer::write, Serializer::read);

        @Override
        public MapCodec<AlloyFurnaceRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, AlloyFurnaceRecipe> packetCodec() {
            return PACKET_CODEC;
        }

        private static AlloyFurnaceRecipe read(RegistryByteBuf buffer) {
            int len = buffer.readInt();
            IngredientWithCount[] inputs = new IngredientWithCount[len];
            for(int i = 0;i < len;i++) {
                Ingredient input = Ingredient.PACKET_CODEC.decode(buffer);
                int count = buffer.readInt();

                inputs[i] = new IngredientWithCount(input, count);
            }

            int ticks = buffer.readInt();

            ItemStack output = ItemStack.OPTIONAL_PACKET_CODEC.decode(buffer);

            ItemStack secondaryOutput = ItemStack.OPTIONAL_PACKET_CODEC.decode(buffer);

            int percentageCount = buffer.readInt();
            double[] percentages = new double[percentageCount];
            for(int j = 0;j < percentageCount;j++)
                percentages[j] = buffer.readDouble();

            OutputItemStackWithPercentages secondaryOutputItemStackWithPercentages =
                    new OutputItemStackWithPercentages(secondaryOutput, percentages);

            return new AlloyFurnaceRecipe(output, secondaryOutputItemStackWithPercentages, inputs, ticks);
        }

        private static void write(RegistryByteBuf buffer, AlloyFurnaceRecipe recipe) {
            buffer.writeInt(recipe.inputs.length);
            for(int i = 0; i < recipe.inputs.length; i++) {
                Ingredient.PACKET_CODEC.encode(buffer, recipe.inputs[i].input);
                buffer.writeInt(recipe.inputs[i].count);
            }

            buffer.writeInt(recipe.ticks);

            ItemStack.OPTIONAL_PACKET_CODEC.encode(buffer, recipe.output);

            ItemStack.OPTIONAL_PACKET_CODEC.encode(buffer, recipe.secondaryOutput.output);

            buffer.writeInt(recipe.secondaryOutput.percentages.length);
            for(double percentage:recipe.secondaryOutput.percentages)
                buffer.writeDouble(percentage);
        }
    }

    public record IngredientWithCount(Ingredient input, int count) {
        public static final Codec<IngredientWithCount> CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("input").forGetter((input) -> {
                return input.input;
            }), Codecs.POSITIVE_INT.optionalFieldOf("count", 1).forGetter((input) -> {
                return input.count;
            })).apply(instance, IngredientWithCount::new);
        });
    }

    public record OutputItemStackWithPercentages(ItemStack output, double[] percentages) {
        private static final Codec<double[]> DOUBLE_ARRAY_CODEC = new Codec<>() {
            private static final Codec<List<Double>> DOUBLE_LIST_CODEC = Codec.doubleRange(0, 1).listOf();

            @Override
            public <T> DataResult<Pair<double[], T>> decode(DynamicOps<T> ops, T input) {
                return DOUBLE_LIST_CODEC.decode(ops, input).map(res -> {
                    return Pair.of(res.getFirst().stream().mapToDouble(Double::doubleValue).toArray(), res.getSecond());
                });
            }

            @Override
            public <T> DataResult<T> encode(double[] input, DynamicOps<T> ops, T prefix) {
                return DOUBLE_LIST_CODEC.encode(Arrays.stream(input).boxed().toList(), ops, prefix);
            }
        };

        public static final Codec<OutputItemStackWithPercentages> CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(CodecFix.ITEM_STACK_CODEC.fieldOf("output").forGetter((output) -> {
                return output.output;
            }), DOUBLE_ARRAY_CODEC.fieldOf("percentages").forGetter((output) -> {
                return output.percentages;
            })).apply(instance, OutputItemStackWithPercentages::new);
        });
    }
}
