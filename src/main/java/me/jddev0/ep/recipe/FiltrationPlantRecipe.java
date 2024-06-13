package me.jddev0.ep.recipe;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.codec.CodecFix;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.Arrays;
import java.util.List;

public class FiltrationPlantRecipe implements Recipe<RecipeInput> {
    private final OutputItemStackWithPercentages output;
    private final OutputItemStackWithPercentages secondaryOutput;
    private final ResourceLocation icon;

    public FiltrationPlantRecipe(OutputItemStackWithPercentages output, OutputItemStackWithPercentages secondaryOutput, ResourceLocation icon) {
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

        generatedOutputs[0] = output.output.copyWithCount(output.percentages.length);
        generatedOutputs[1] = secondaryOutput.output.copyWithCount(secondaryOutput.percentages.length);

        return generatedOutputs;
    }

    public ItemStack[] generateOutputs(RandomSource randomSource) {
        ItemStack[] generatedOutputs = new ItemStack[2];
        for(int i = 0;i < 2;i++) {
            int count = 0;
            OutputItemStackWithPercentages output = i == 0?this.output:this.secondaryOutput;

            for(double percentage:output.percentages)
                if(randomSource.nextDouble() <= percentage)
                    count++;

            generatedOutputs[i] = output.output.copyWithCount(count);
        }

        return generatedOutputs;
    }

    @Override
    public boolean matches(RecipeInput container, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(RecipeInput container, HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(ModBlocks.FILTRATION_PLANT.get());
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
        public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(EnergizedPowerMod.MODID, "filtration_plant");

        private final MapCodec<FiltrationPlantRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(OutputItemStackWithPercentages.CODEC.fieldOf("output").forGetter((recipe) -> {
                return recipe.output;
            }), OutputItemStackWithPercentages.CODEC.optionalFieldOf("secondaryOutput",
                    new OutputItemStackWithPercentages(ItemStack.EMPTY, new double[0])).forGetter((recipe) -> {
                return recipe.secondaryOutput;
            }), ResourceLocation.CODEC.fieldOf("icon").forGetter((recipe) -> {
                return recipe.icon;
            })).apply(instance, FiltrationPlantRecipe::new);
        });

        private final StreamCodec<RegistryFriendlyByteBuf, FiltrationPlantRecipe> STREAM_CODEC = StreamCodec.of(
                Serializer::write, Serializer::read);

        @Override
        public MapCodec<FiltrationPlantRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, FiltrationPlantRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static FiltrationPlantRecipe read(RegistryFriendlyByteBuf buffer) {
            OutputItemStackWithPercentages[] outputs = new OutputItemStackWithPercentages[2];
            for(int i = 0;i < 2;i++) {
                ItemStack output = ItemStack.OPTIONAL_STREAM_CODEC.decode(buffer);

                int percentageCount = buffer.readInt();
                double[] percentages = new double[percentageCount];
                for(int j = 0;j < percentageCount;j++)
                    percentages[j] = buffer.readDouble();

                outputs[i] = new OutputItemStackWithPercentages(output, percentages);
            }

            ResourceLocation icon = buffer.readResourceLocation();

            return new FiltrationPlantRecipe(outputs[0], outputs[1], icon);
        }

        private static void write(RegistryFriendlyByteBuf buffer, FiltrationPlantRecipe recipe) {
            for(int i = 0;i < 2;i++) {
                OutputItemStackWithPercentages output = i == 0?recipe.output:recipe.secondaryOutput;
                ItemStack.OPTIONAL_STREAM_CODEC.encode(buffer, output.output);

                buffer.writeInt(output.percentages.length);
                for(double percentage:output.percentages)
                    buffer.writeDouble(percentage);
            }

            buffer.writeResourceLocation(recipe.icon);
        }
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
