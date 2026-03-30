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

public class AlloyFurnaceRecipe implements EnergizedPowerBaseRecipe<RecipeInput> {
    private final ItemStackTemplate output;
    private final OutputItemStackTemplateWithPercentages secondaryOutput;
    private final IngredientWithCount[] inputs;
    private final int ticks;

    public AlloyFurnaceRecipe(ItemStackTemplate output, OutputItemStackTemplateWithPercentages secondaryOutput,
                              IngredientWithCount[] inputs, int ticks) {
        this.output = output;
        this.secondaryOutput = secondaryOutput;
        this.inputs = inputs;
        this.ticks = ticks;
    }

    public ItemStackTemplate getOutput() {
        return output;
    }

    public OutputItemStackTemplateWithPercentages getSecondaryOutput() {
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

        ItemStack output = ItemStackUtils.fromNullableItemStackTemplate(this.output);
        ItemStack secondaryOutput = ItemStackUtils.fromNullableItemStackTemplate(this.secondaryOutput.output());

        generatedOutputs[0] = output.copyWithCount(output.getCount());
        generatedOutputs[1] = secondaryOutput.copyWithCount(this.secondaryOutput.percentages().length);

        return generatedOutputs;
    }

    public ItemStack[] generateOutputs(RandomSource randomSource) {
        ItemStack[] generatedOutputs = new ItemStack[2];

        ItemStack output = ItemStackUtils.fromNullableItemStackTemplate(this.output);
        ItemStack secondaryOutput = ItemStackUtils.fromNullableItemStackTemplate(this.secondaryOutput.output());

        generatedOutputs[0] = output.copyWithCount(output.getCount());

        int count = 0;
        for(double percentage:this.secondaryOutput.percentages())
            if(randomSource.nextDouble() <= percentage)
                count++;

        generatedOutputs[1] = secondaryOutput.copyWithCount(count);

        return generatedOutputs;
    }

    @Override
    public boolean matches(RecipeInput container, Level level) {
        if(level.isClientSide())
            return false;

        boolean[] usedIndices = new boolean[3];
        for(int i = 0;i < 3;i++)
            usedIndices[i] = container.getItem(i).isEmpty();

        int len = Math.min(inputs.length, 3);
        for(int i = 0;i < len;i++) {
            IngredientWithCount input = inputs[i];

            int indexMinCount = -1;
            int minCount = Integer.MAX_VALUE;

            for(int j = 0;j < 3;j++) {
                if(usedIndices[j])
                    continue;

                ItemStack item = container.getItem(j);

                if((indexMinCount == -1 || item.getCount() < minCount) && input.input().test(item) &&
                        item.getCount() >= input.count()) {
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
    public ItemStack assemble(RecipeInput container) {
        return ItemStackUtils.fromNullableItemStackTemplate(this.output);
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
        return EPRecipes.ALLOY_FURNACE_CATEGORY;
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
        return Arrays.stream(inputs).map(IngredientWithCount::input).toList();
    }

    @Override
    public boolean isIngredient(ItemStack itemStack) {
        return Arrays.stream(inputs).map(IngredientWithCount::input).anyMatch(ingredient -> ingredient.test(itemStack));
    }

    @Override
    public boolean isResult(ItemStack itemStack) {
        ItemStack output = ItemStackUtils.fromNullableItemStackTemplate(this.output);
        ItemStack secondaryOutput = ItemStackUtils.fromNullableItemStackTemplate(this.secondaryOutput.output());

        return ItemStack.isSameItemSameComponents(output, itemStack) || (!secondaryOutput.isEmpty() &&
                ItemStack.isSameItemSameComponents(secondaryOutput, itemStack));
    }

    public static final class Type implements RecipeType<AlloyFurnaceRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "alloy_furnace";
    }

    public static final class Serializer {
        private Serializer() {}

        private static final MapCodec<AlloyFurnaceRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(ItemStackTemplate.CODEC.fieldOf("result").forGetter((recipe) -> {
                return recipe.output;
            }), OutputItemStackTemplateWithPercentages.CODEC_NONEMPTY.optionalFieldOf("secondaryResult").forGetter((recipe) -> {
                return Optional.ofNullable(recipe.secondaryOutput.isEmpty()?null:recipe.secondaryOutput);
            }), new ArrayCodec<>(IngredientWithCount.CODEC, IngredientWithCount[]::new).fieldOf("ingredients").forGetter((recipe) -> {
                return recipe.inputs;
            }), ExtraCodecs.POSITIVE_INT.fieldOf("ticks").forGetter((recipe) -> {
                return recipe.ticks;
            })).apply(instance, (output, secondaryOutput, inputs, ticks) -> new AlloyFurnaceRecipe(output,
                    secondaryOutput.orElse(OutputItemStackTemplateWithPercentages.EMPTY), inputs, ticks));
        });

        private static final StreamCodec<RegistryFriendlyByteBuf, AlloyFurnaceRecipe> STREAM_CODEC = StreamCodec.of(
                Serializer::write, Serializer::read);

        public static final RecipeSerializer<AlloyFurnaceRecipe> INSTANCE = new RecipeSerializer<>(CODEC, STREAM_CODEC);
        public static final Identifier ID = EPAPI.id("alloy_furnace");

        private static AlloyFurnaceRecipe read(RegistryFriendlyByteBuf buffer) {
            int len = buffer.readInt();
            IngredientWithCount[] inputs = new IngredientWithCount[len];
            for(int i = 0;i < len;i++)
                inputs[i] = IngredientWithCount.STREAM_CODEC.decode(buffer);

            int ticks = buffer.readInt();

            ItemStackTemplate output = ItemStackTemplate.STREAM_CODEC.decode(buffer);

            OutputItemStackTemplateWithPercentages secondaryOutput = OutputItemStackTemplateWithPercentages.OPTIONAL_STREAM_CODEC.decode(buffer);

            return new AlloyFurnaceRecipe(output, secondaryOutput, inputs, ticks);
        }

        private static void write(RegistryFriendlyByteBuf buffer, AlloyFurnaceRecipe recipe) {
            buffer.writeInt(recipe.inputs.length);
            for(int i = 0; i < recipe.inputs.length; i++)
                IngredientWithCount.STREAM_CODEC.encode(buffer, recipe.inputs[i]);

            buffer.writeInt(recipe.ticks);

            ItemStackTemplate.STREAM_CODEC.encode(buffer, recipe.output);

            OutputItemStackTemplateWithPercentages.OPTIONAL_STREAM_CODEC.encode(buffer, recipe.secondaryOutput);
        }
    }
}
