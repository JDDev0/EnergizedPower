package me.jddev0.ep.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.codec.ArrayCodec;
import me.jddev0.ep.util.ItemStackUtils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
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

public class PlantGrowthChamberRecipe implements EnergizedPowerBaseRecipe<RecipeInput> {
    private final OutputItemStackTemplateWithPercentages[] outputs;
    private final Ingredient input;
    private final int ticks;

    public PlantGrowthChamberRecipe(OutputItemStackTemplateWithPercentages[] outputs, Ingredient input, int ticks) {
        this.outputs = outputs;
        this.input = input;
        this.ticks = ticks;
    }

    public OutputItemStackTemplateWithPercentages[] getOutputs() {
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
            OutputItemStackTemplateWithPercentages output = outputs[i];

            ItemStack outputItemStack = ItemStackUtils.fromNullableItemStackTemplate(output.output());

            generatedOutputs[i] = outputItemStack.copyWithCount(output.percentages().length);
        }

        return generatedOutputs;
    }

    public ItemStack[] generateOutputs(RandomSource randomSource) {
        ItemStack[] generatedOutputs = new ItemStack[outputs.length];
        for(int i = 0;i < outputs.length;i++) {
            int count = 0;
            OutputItemStackTemplateWithPercentages output = outputs[i];

            for(double percentage:output.percentages())
                if(randomSource.nextDouble() <= percentage)
                    count++;

            ItemStack outputItemStack = ItemStackUtils.fromNullableItemStackTemplate(output.output());

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
        return EPRecipes.PLANT_GROWTH_CHAMBER_CATEGORY.get();
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
        return Arrays.stream(outputs).map(OutputItemStackTemplateWithPercentages::output).
                anyMatch(output -> {
                    ItemStack outputItemStack = ItemStackUtils.fromNullableItemStackTemplate(output);

                    return ItemStack.isSameItemSameComponents(outputItemStack, itemStack);
                });
    }

    public static final class Type implements RecipeType<PlantGrowthChamberRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "plant_growth_chamber";
    }

    public static final class Serializer {
        private Serializer() {}

        private static final MapCodec<PlantGrowthChamberRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(new ArrayCodec<>(OutputItemStackTemplateWithPercentages.CODEC_NONEMPTY, OutputItemStackTemplateWithPercentages[]::new).
                    fieldOf("results").forGetter((recipe) -> {
                        return recipe.outputs;
                    }), Ingredient.CODEC.fieldOf("ingredient").forGetter((recipe) -> {
                return recipe.input;
            }), ExtraCodecs.POSITIVE_INT.fieldOf("ticks").forGetter((recipe) -> {
                return recipe.ticks;
            })).apply(instance, PlantGrowthChamberRecipe::new);
        });

        private static final StreamCodec<RegistryFriendlyByteBuf, PlantGrowthChamberRecipe> STREAM_CODEC = StreamCodec.of(
                Serializer::write, Serializer::read);

        public static final RecipeSerializer<PlantGrowthChamberRecipe> INSTANCE = new RecipeSerializer<>(CODEC, STREAM_CODEC);
        public static final Identifier ID = EPAPI.id("plant_growth_chamber");

        private static PlantGrowthChamberRecipe read(RegistryFriendlyByteBuf buffer) {
            Ingredient input = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            int ticks = buffer.readInt();

            int outputCount = buffer.readInt();
            OutputItemStackTemplateWithPercentages[] outputs = new OutputItemStackTemplateWithPercentages[outputCount];
            for(int i = 0;i < outputCount;i++)
                outputs[i] = OutputItemStackTemplateWithPercentages.OPTIONAL_STREAM_CODEC.decode(buffer);

            return new PlantGrowthChamberRecipe(outputs, input, ticks);
        }

        private static void write(RegistryFriendlyByteBuf buffer, PlantGrowthChamberRecipe recipe) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.input);
            buffer.writeInt(recipe.ticks);

            buffer.writeInt(recipe.outputs.length);
            for(OutputItemStackTemplateWithPercentages output:recipe.outputs)
                OutputItemStackTemplateWithPercentages.OPTIONAL_STREAM_CODEC.encode(buffer, output);
        }
    }
}
