package me.jddev0.ep.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.codec.CodecFix;
import me.jddev0.ep.item.EPItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class SawmillRecipe implements Recipe<RecipeInput> {
    private final ItemStack output;
    private final ItemStack secondaryOutput;
    private final Ingredient input;

    public SawmillRecipe(ItemStack output, Ingredient input, int sawdustAmount) {
        this(output, new ItemStack(EPItems.SAWDUST.get(), sawdustAmount), input);
    }

    public SawmillRecipe(ItemStack output, ItemStack secondaryOutput, Ingredient input) {
        this.output = output;
        this.secondaryOutput = secondaryOutput;
        this.input = input;
    }

    public ItemStack getOutput() {
        return output;
    }

    public ItemStack getSecondaryOutput() {
        return secondaryOutput;
    }

    public Ingredient getInput() {
        return input;
    }

    @Override
    public boolean matches(RecipeInput container, Level level) {
        if(level.isClientSide)
            return false;

        return input.test(container.getItem(0));
    }

    @Override
    public ItemStack assemble(RecipeInput container, HolderLookup.Provider registries) {
        return output;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return output.copy();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.createWithCapacity(1);
        ingredients.add(0, input);
        return ingredients;
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(EPBlocks.SAWMILL_ITEM.get());
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

    public static final class Type implements RecipeType<SawmillRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "sawmill";
    }

    public static final class Serializer implements RecipeSerializer<SawmillRecipe> {
        private Serializer() {}

        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = EPAPI.id("sawmill");

        private final MapCodec<SawmillRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(CodecFix.ITEM_STACK_CODEC.fieldOf("output").forGetter((recipe) -> {
                return recipe.output;
            }), Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter((recipe) -> {
                return recipe.input;
            }), ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("sawdustAmount").forGetter((recipe) -> {
                if(recipe.secondaryOutput.isEmpty())
                    return Optional.of(0);

                return ItemStack.isSameItemSameComponents(recipe.secondaryOutput, new ItemStack(EPItems.SAWDUST.get()))?
                        Optional.of(recipe.secondaryOutput.getCount()):Optional.empty();
            }), CodecFix.ITEM_STACK_CODEC.optionalFieldOf("secondaryOutput").forGetter((recipe) -> {
                if(recipe.secondaryOutput.isEmpty())
                    return Optional.empty();

                return ItemStack.isSameItemSameComponents(recipe.secondaryOutput, new ItemStack(EPItems.SAWDUST.get()))?
                        Optional.empty():Optional.of(recipe.secondaryOutput);
            })).apply(instance, (output, ingredient, sawdustAmount, secondaryOutput) -> {
                return secondaryOutput.map(o -> new SawmillRecipe(output, o, ingredient)).
                        orElseGet(() -> sawdustAmount.map(a -> new SawmillRecipe(output, ingredient, a)).
                                orElseThrow(() -> new IllegalArgumentException("Either \"sawdustAmount\" or \"secondaryOutput\" must be present")));
            });
        });

        private final StreamCodec<RegistryFriendlyByteBuf, SawmillRecipe> STREAM_CODEC = StreamCodec.of(
                Serializer::write, Serializer::read);

        @Override
        public MapCodec<SawmillRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, SawmillRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static SawmillRecipe read(RegistryFriendlyByteBuf buffer) {
            Ingredient input = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            ItemStack output = ItemStack.OPTIONAL_STREAM_CODEC.decode(buffer);
            ItemStack secondaryOutput = ItemStack.OPTIONAL_STREAM_CODEC.decode(buffer);

            return new SawmillRecipe(output, secondaryOutput, input);
        }

        private static void write(RegistryFriendlyByteBuf buffer, SawmillRecipe recipe) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.input);
            ItemStack.OPTIONAL_STREAM_CODEC.encode(buffer, recipe.output);
            ItemStack.OPTIONAL_STREAM_CODEC.encode(buffer, recipe.secondaryOutput);
        }
    }
}
