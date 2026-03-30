package me.jddev0.ep.recipe;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.util.ItemStackUtils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class PulverizerRecipe implements EnergizedPowerBaseRecipe<RecipeInput> {
    private final OutputItemStackWithPercentages output;
    private final OutputItemStackWithPercentages secondaryOutput;
    private final Ingredient input;

    public PulverizerRecipe(OutputItemStackWithPercentages output, OutputItemStackWithPercentages secondaryOutput, Ingredient input) {
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

        ItemStack output = ItemStackUtils.fromNullableItemStackTemplate(this.output.output);
        ItemStack secondaryOutput = ItemStackUtils.fromNullableItemStackTemplate(this.secondaryOutput.output);

        generatedOutputs[0] = output.copyWithCount(advanced?this.output.percentagesAdvanced.length:
                this.output.percentages.length);
        generatedOutputs[1] = secondaryOutput.copyWithCount(advanced?this.secondaryOutput.percentagesAdvanced.length:
                this.secondaryOutput.percentages.length);

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

            ItemStack outputItemStack = ItemStackUtils.fromNullableItemStackTemplate(output.output);

            generatedOutputs[i] = outputItemStack.copyWithCount(count);
        }

        return generatedOutputs;
    }

    @Override
    public boolean matches(RecipeInput container, Level level) {
        if(level.isClientSide())
            return false;

        return input.test(container.getItem(0));
    }

    @Override
    public ItemStack assemble(RecipeInput container) {
        return ItemStack.EMPTY;
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return EPRecipes.PULVERIZER_CATEGORY;
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
        ItemStack output = ItemStackUtils.fromNullableItemStackTemplate(this.output.output);
        ItemStack secondaryOutput = ItemStackUtils.fromNullableItemStackTemplate(this.secondaryOutput.output());

        return ItemStack.isSameItemSameComponents(output, itemStack) || (!secondaryOutput.isEmpty() &&
                ItemStack.isSameItemSameComponents(secondaryOutput, itemStack));
    }

    public static final class Type implements RecipeType<PulverizerRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "pulverizer";
    }

    public static final class Serializer {
        private Serializer() {}

        private static final MapCodec<PulverizerRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(OutputItemStackWithPercentages.createCodec(true).fieldOf("result").forGetter((recipe) -> {
                return recipe.output;
            }), OutputItemStackWithPercentages.createCodec(false).optionalFieldOf("secondaryResult").forGetter((recipe) -> {
                if(recipe.secondaryOutput.output == null || recipe.secondaryOutput.percentages.length == 0)
                    return Optional.empty();

                return Optional.of(recipe.secondaryOutput);
            }), Ingredient.CODEC.fieldOf("ingredient").forGetter((recipe) -> {
                return recipe.input;
            })).apply(instance, (output, secondaryOutput, input) -> new PulverizerRecipe(output,
                    secondaryOutput.orElse(new OutputItemStackWithPercentages(null, new double[0], new double[0])),
                    input));
        });

        private static final StreamCodec<RegistryFriendlyByteBuf, PulverizerRecipe> STREAM_CODEC = StreamCodec.of(
                Serializer::write, Serializer::read);

        public static final RecipeSerializer<PulverizerRecipe> INSTANCE = new RecipeSerializer<>(CODEC, STREAM_CODEC);
        public static final Identifier ID = EPAPI.id("pulverizer");

        private static PulverizerRecipe read(RegistryFriendlyByteBuf buffer) {
            Ingredient input = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);

            OutputItemStackWithPercentages[] outputs = new OutputItemStackWithPercentages[2];
            for(int i = 0;i < 2;i++) {
                ItemStackTemplate output = ItemStackTemplate.STREAM_CODEC.decode(buffer);

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

            return new PulverizerRecipe(outputs[0], outputs[1], input);
        }

        private static void write(RegistryFriendlyByteBuf buffer, PulverizerRecipe recipe) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.input);

            for(int i = 0;i < 2;i++) {
                OutputItemStackWithPercentages output = i == 0?recipe.output:recipe.secondaryOutput;
                ItemStackTemplate.STREAM_CODEC.encode(buffer, output.output);

                buffer.writeInt(output.percentages.length);
                for(double percentage:output.percentages)
                    buffer.writeDouble(percentage);

                buffer.writeInt(output.percentagesAdvanced.length);
                for(double percentage:output.percentagesAdvanced)
                    buffer.writeDouble(percentage);
            }
        }
    }

    public record OutputItemStackWithPercentages(ItemStackTemplate output, double[] percentages, double[] percentagesAdvanced) {
        public OutputItemStackWithPercentages(ItemStackTemplate output, double percentage, double percentageAdvanced) {
            this(output, new double[] {
                    percentage
            }, new double[] {
                    percentageAdvanced
            });
        }

        public OutputItemStackWithPercentages(ItemStackTemplate output) {
            this(output, 1., 1.);
        }

        private static Codec<double[]> createDoubleArrayCodec(boolean atLeastOnePercentageValue) {
            return new Codec<>() {
                private static final Codec<List<Double>> DOUBLE_LIST_CODEC = Codec.doubleRange(0, 1).listOf();

                @Override
                public <T> DataResult<Pair<double[], T>> decode(DynamicOps<T> ops, T input) {
                    return DOUBLE_LIST_CODEC.decode(ops, input).flatMap(res -> {
                        boolean errorFlag = atLeastOnePercentageValue && res.getFirst().stream().noneMatch(d -> (int)(double)d >= 1);

                        Pair<double[], T> newRes = Pair.of(res.getFirst().stream().mapToDouble(Double::doubleValue).toArray(), res.getSecond());
                        if(errorFlag)
                            return DataResult.error(() -> "The primary output must have a minimum count of at least 1 (At least one percentage value must be >= 1.0)", newRes);

                        return DataResult.success(newRes);
                    });
                }

                @Override
                public <T> DataResult<T> encode(double[] input, DynamicOps<T> ops, T prefix) {
                    return DOUBLE_LIST_CODEC.encode(Arrays.stream(input).boxed().toList(), ops, prefix);
                }
            };
        }

        public static Codec<OutputItemStackWithPercentages> createCodec(boolean atLeastOnePercentageValue) {
            return RecordCodecBuilder.create((instance) -> {
                return instance.group(ItemStackTemplate.CODEC.fieldOf("result").forGetter((output) -> {
                    return output.output;
                }), createDoubleArrayCodec(atLeastOnePercentageValue).fieldOf("percentages").forGetter((output) -> {
                    return output.percentages;
                }), createDoubleArrayCodec(atLeastOnePercentageValue).optionalFieldOf("percentagesAdvanced").forGetter((output) -> {
                    return Optional.of(output.percentagesAdvanced);
                })).apply(instance, (output, percentages, percentagesAdvanced) -> {
                    if(percentagesAdvanced.isPresent())
                        return new OutputItemStackWithPercentages(output, percentages, percentagesAdvanced.get());

                    //Normal and advanced have the same percentages
                    return new OutputItemStackWithPercentages(output, percentages, percentages);
                });
            });
        }
    }
}
