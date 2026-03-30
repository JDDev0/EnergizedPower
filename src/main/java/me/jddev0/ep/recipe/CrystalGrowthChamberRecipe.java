package me.jddev0.ep.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.api.EPAPI;
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
import java.util.List;

public class CrystalGrowthChamberRecipe implements EnergizedPowerBaseRecipe<RecipeInput> {
    private final OutputItemStackTemplateWithPercentages output;
    private final IngredientWithCount input;
    private final int ticks;

    public CrystalGrowthChamberRecipe(OutputItemStackTemplateWithPercentages output, IngredientWithCount input, int ticks) {
        this.output = output;
        this.input = input;
        this.ticks = ticks;
    }

    public OutputItemStackTemplateWithPercentages getOutput() {
        return output;
    }

    public IngredientWithCount getInput() {
        return input;
    }

    public int getTicks() {
        return ticks;
    }

    public ItemStack getMaxOutputCount() {
        ItemStack output = ItemStackUtils.fromNullableItemStackTemplate(this.output.output());

        return output.copyWithCount(this.output.percentages().length);
    }

    public ItemStack generateOutput(RandomSource randomSource) {
        int count = 0;

        for(double percentage:output.percentages())
            if(randomSource.nextDouble() <= percentage)
                count++;

        ItemStack output = ItemStackUtils.fromNullableItemStackTemplate(this.output.output());

        return output.copyWithCount(count);
    }

    @Override
    public boolean matches(RecipeInput container, Level level) {
        if(level.isClientSide())
            return false;

        return input.input().test(container.getItem(0)) && container.getItem(0).getCount() >= input.count();
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
        return EPRecipes.CRYSTAL_GROWTH_CHAMBER_CATEGORY;
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
        return List.of(input.input());
    }

    @Override
    public boolean isIngredient(ItemStack itemStack) {
        return input.input().test(itemStack);
    }

    @Override
    public boolean isResult(ItemStack itemStack) {
        ItemStack output = ItemStackUtils.fromNullableItemStackTemplate(this.output.output());

        return ItemStack.isSameItemSameComponents(output, itemStack);
    }

    public static final class Type implements RecipeType<CrystalGrowthChamberRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "crystal_growth_chamber";
    }

    public static final class Serializer {
        private Serializer() {}

        private static final MapCodec<CrystalGrowthChamberRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(OutputItemStackTemplateWithPercentages.CODEC_NONEMPTY.fieldOf("result").forGetter((recipe) -> {
                return recipe.output;
            }), IngredientWithCount.CODEC.fieldOf("ingredient").forGetter((recipe) -> {
                return recipe.input;
            }), ExtraCodecs.POSITIVE_INT.fieldOf("ticks").forGetter((recipe) -> {
                return recipe.ticks;
            })).apply(instance, CrystalGrowthChamberRecipe::new);
        });

        private static final StreamCodec<RegistryFriendlyByteBuf, CrystalGrowthChamberRecipe> STREAM_CODEC = StreamCodec.of(
                Serializer::write, Serializer::read);

        public static final RecipeSerializer<CrystalGrowthChamberRecipe> INSTANCE = new RecipeSerializer<>(CODEC, STREAM_CODEC);
        public static final Identifier ID = EPAPI.id("crystal_growth_chamber");

        private static CrystalGrowthChamberRecipe read(RegistryFriendlyByteBuf buffer) {
            IngredientWithCount input = IngredientWithCount.STREAM_CODEC.decode(buffer);
            int ticks = buffer.readInt();

            OutputItemStackTemplateWithPercentages output = OutputItemStackTemplateWithPercentages.OPTIONAL_STREAM_CODEC.decode(buffer);

            return new CrystalGrowthChamberRecipe(output, input, ticks);
        }

        private static void write(RegistryFriendlyByteBuf buffer, CrystalGrowthChamberRecipe recipe) {
            IngredientWithCount.STREAM_CODEC.encode(buffer, recipe.input);
            buffer.writeInt(recipe.ticks);

            OutputItemStackTemplateWithPercentages.OPTIONAL_STREAM_CODEC.encode(buffer, recipe.output);
        }
    }
}
