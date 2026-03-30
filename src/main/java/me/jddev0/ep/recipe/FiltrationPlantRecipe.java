package me.jddev0.ep.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.util.ItemStackUtils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
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
import java.util.List;
import java.util.Optional;

public class FiltrationPlantRecipe implements EnergizedPowerBaseRecipe<RecipeInput> {
    private final OutputItemStackTemplateWithPercentages output;
    private final OutputItemStackTemplateWithPercentages secondaryOutput;
    private final Identifier icon;

    public FiltrationPlantRecipe(OutputItemStackTemplateWithPercentages output, OutputItemStackTemplateWithPercentages secondaryOutput, Identifier icon) {
        this.output = output;
        this.secondaryOutput = secondaryOutput;
        this.icon = icon;
    }

    public OutputItemStackTemplateWithPercentages getOutput() {
        return output;
    }

    public OutputItemStackTemplateWithPercentages getSecondaryOutput() {
        return secondaryOutput;
    }

    public Identifier getIcon() {
        return icon;
    }

    public ItemStack[] getMaxOutputCounts() {
        ItemStack[] generatedOutputs = new ItemStack[2];

        ItemStack output = ItemStackUtils.fromNullableItemStackTemplate(this.output.output());
        ItemStack secondaryOutput = ItemStackUtils.fromNullableItemStackTemplate(this.secondaryOutput.output());

        generatedOutputs[0] = output.copyWithCount(this.output.percentages().length);
        generatedOutputs[1] = secondaryOutput.copyWithCount(this.secondaryOutput.percentages().length);

        return generatedOutputs;
    }

    public ItemStack[] generateOutputs(RandomSource randomSource) {
        ItemStack[] generatedOutputs = new ItemStack[2];
        for(int i = 0;i < 2;i++) {
            int count = 0;
            OutputItemStackTemplateWithPercentages output = i == 0?this.output:this.secondaryOutput;

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
        return false;
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
        return EPRecipes.FILTRATION_PLANT_CATEGORY;
    }

    @Override
    public RecipeSerializer<? extends Recipe<RecipeInput>> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public List<Ingredient> getIngredients() {
        return List.of();
    }

    @Override
    public boolean isIngredient(ItemStack itemStack) {
        return false;
    }

    @Override
    public boolean isResult(ItemStack itemStack) {
        ItemStack output = ItemStackUtils.fromNullableItemStackTemplate(this.output.output());
        ItemStack secondaryOutput = ItemStackUtils.fromNullableItemStackTemplate(this.secondaryOutput.output());

        return ItemStack.isSameItemSameComponents(output, itemStack) || (!secondaryOutput.isEmpty() &&
                ItemStack.isSameItemSameComponents(secondaryOutput, itemStack));
    }

    @Override
    public RecipeType<? extends Recipe<RecipeInput>> getType() {
        return Type.INSTANCE;
    }

    public static final class Type implements RecipeType<FiltrationPlantRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "filtration_plant";
    }

    public static final class Serializer {
        private Serializer() {}

        private static final MapCodec<FiltrationPlantRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(OutputItemStackTemplateWithPercentages.CODEC_NONEMPTY.fieldOf("result").forGetter((recipe) -> {
                return recipe.output;
            }), OutputItemStackTemplateWithPercentages.CODEC_NONEMPTY.optionalFieldOf("secondaryResult").forGetter((recipe) -> {
                return Optional.ofNullable(recipe.secondaryOutput.isEmpty()?null:recipe.secondaryOutput);
            }), Identifier.CODEC.fieldOf("icon").forGetter((recipe) -> {
                return recipe.icon;
            })).apply(instance, (output, secondaryOutput, icon) -> new FiltrationPlantRecipe(output,
                    secondaryOutput.orElse(OutputItemStackTemplateWithPercentages.EMPTY), icon));
        });

        private static final StreamCodec<RegistryFriendlyByteBuf, FiltrationPlantRecipe> STREAM_CODEC = StreamCodec.of(
                Serializer::write, Serializer::read);

        public static final RecipeSerializer<FiltrationPlantRecipe> INSTANCE = new RecipeSerializer<>(CODEC, STREAM_CODEC);
        public static final Identifier ID = EPAPI.id("filtration_plant");

        private static FiltrationPlantRecipe read(RegistryFriendlyByteBuf buffer) {
            OutputItemStackTemplateWithPercentages[] outputs = new OutputItemStackTemplateWithPercentages[2];
            for(int i = 0;i < 2;i++)
                outputs[i] = OutputItemStackTemplateWithPercentages.OPTIONAL_STREAM_CODEC.decode(buffer);

            Identifier icon = buffer.readIdentifier();

            return new FiltrationPlantRecipe(outputs[0], outputs[1], icon);
        }

        private static void write(RegistryFriendlyByteBuf buffer, FiltrationPlantRecipe recipe) {
            for(int i = 0;i < 2;i++) {
                OutputItemStackTemplateWithPercentages output = i == 0?recipe.output:recipe.secondaryOutput;
                OutputItemStackTemplateWithPercentages.OPTIONAL_STREAM_CODEC.encode(buffer, output);
            }

            buffer.writeIdentifier(recipe.icon);
        }
    }
}
