package me.jddev0.ep.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.item.EPItems;
import me.jddev0.ep.util.ItemStackUtils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
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
import java.util.List;
import java.util.Optional;

public class SawmillRecipe implements EnergizedPowerBaseRecipe<RecipeInput> {
    private final ItemStackTemplate output;
    private final ItemStackTemplate secondaryOutput;
    private final Ingredient input;

    public SawmillRecipe(ItemStackTemplate output, Ingredient input, int sawdustAmount) {
        this(output, sawdustAmount == 0?null:new ItemStackTemplate(EPItems.SAWDUST, sawdustAmount), input);
    }

    public SawmillRecipe(ItemStackTemplate output, ItemStackTemplate secondaryOutput, Ingredient input) {
        this.output = output;
        this.secondaryOutput = secondaryOutput;
        this.input = input;
    }

    public ItemStackTemplate getOutput() {
        return output;
    }

    public ItemStackTemplate getSecondaryOutput() {
        return secondaryOutput;
    }

    public Ingredient getInput() {
        return input;
    }

    @Override
    public boolean matches(RecipeInput container, Level level) {
        if(level.isClientSide())
            return false;

        return input.test(container.getItem(0));
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
        return EPRecipes.SAWMILL_CATEGORY;
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
        ItemStack output = ItemStackUtils.fromNullableItemStackTemplate(this.output);
        ItemStack secondaryOutput = ItemStackUtils.fromNullableItemStackTemplate(this.secondaryOutput);

        return ItemStack.isSameItemSameComponents(output, itemStack) || (!secondaryOutput.isEmpty() &&
                ItemStack.isSameItemSameComponents(secondaryOutput, itemStack));
    }

    public static final class Type implements RecipeType<SawmillRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "sawmill";
    }

    public static final class Serializer {
        private Serializer() {}

        private static final MapCodec<SawmillRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(ItemStackTemplate.CODEC.fieldOf("result").forGetter((recipe) -> {
                return recipe.output;
            }), Ingredient.CODEC.fieldOf("ingredient").forGetter((recipe) -> {
                return recipe.input;
            }), ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("sawdustAmount").forGetter((recipe) -> {
                if(recipe.secondaryOutput == null)
                    return Optional.of(0);

                return ItemStackUtils.isSameItemSameComponents(recipe.secondaryOutput, new ItemStackTemplate(EPItems.SAWDUST))?
                        Optional.of(recipe.secondaryOutput.count()):Optional.empty();
            }), ItemStackTemplate.CODEC.optionalFieldOf("secondaryResult").forGetter((recipe) -> {
                if(recipe.secondaryOutput == null)
                    return Optional.empty();

                return ItemStackUtils.isSameItemSameComponents(recipe.secondaryOutput, new ItemStackTemplate(EPItems.SAWDUST))?
                        Optional.empty():Optional.of(recipe.secondaryOutput);
            })).apply(instance, (output, ingredient, sawdustAmount, secondaryOutput) -> {
                return secondaryOutput.map(o -> new SawmillRecipe(output, o, ingredient)).
                        orElseGet(() -> sawdustAmount.map(a -> new SawmillRecipe(output, ingredient, a)).
                                orElseThrow(() -> new IllegalArgumentException("Either \"sawdustAmount\" or \"secondaryOutput\" must be present")));
            });
        });

        private static final StreamCodec<RegistryFriendlyByteBuf, SawmillRecipe> STREAM_CODEC = StreamCodec.of(
                Serializer::write, Serializer::read);

        public static final RecipeSerializer<SawmillRecipe> INSTANCE = new RecipeSerializer<>(CODEC, STREAM_CODEC);
        public static final Identifier ID = EPAPI.id("sawmill");

        private static SawmillRecipe read(RegistryFriendlyByteBuf buffer) {
            Ingredient input = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            ItemStackTemplate output = ItemStackTemplate.STREAM_CODEC.decode(buffer);
            ItemStackTemplate secondaryOutput = buffer.readBoolean()?ItemStackTemplate.STREAM_CODEC.decode(buffer):null;

            return new SawmillRecipe(output, secondaryOutput, input);
        }

        private static void write(RegistryFriendlyByteBuf buffer, SawmillRecipe recipe) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.input);
            ItemStackTemplate.STREAM_CODEC.encode(buffer, recipe.output);
            if(recipe.secondaryOutput == null) {
                buffer.writeBoolean(false);
            }else {
                buffer.writeBoolean(true);
                ItemStackTemplate.STREAM_CODEC.encode(buffer, recipe.secondaryOutput);
            }
        }
    }
}
