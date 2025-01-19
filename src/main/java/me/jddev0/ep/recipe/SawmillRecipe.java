package me.jddev0.ep.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.codec.CodecFix;
import me.jddev0.ep.item.EPItems;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;

public class SawmillRecipe implements EnergizedPowerBaseRecipe<RecipeInput> {
    private final ItemStack output;
    private final ItemStack secondaryOutput;
    private final Ingredient input;

    public SawmillRecipe(ItemStack output, Ingredient input, int sawdustAmount) {
        this(output, new ItemStack(EPItems.SAWDUST, sawdustAmount), input);
    }

    public SawmillRecipe(ItemStack output, ItemStack secondaryOutput, Ingredient input) {
        this.output = output;
        this.secondaryOutput = secondaryOutput;
        this.input = input;
    }

    public ItemStack getOutputItem() {
        return output;
    }

    public ItemStack getSecondaryOutput() {
        return secondaryOutput;
    }

    public Ingredient getInputItem() {
        return input;
    }

    @Override
    public boolean matches(RecipeInput container, World level) {
        if(level.isClient())
            return false;

        return input.test(container.getStackInSlot(0));
    }

    @Override
    public ItemStack craft(RecipeInput container, RegistryWrapper.WrapperLookup registries) {
        return output;
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
        return ItemStack.areItemsAndComponentsEqual(output, itemStack) || (secondaryOutput != null &&
                ItemStack.areItemsAndComponentsEqual(secondaryOutput, itemStack));
    }

    public static final class Type implements RecipeType<SawmillRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "sawmill";
    }

    public static final class Serializer implements RecipeSerializer<SawmillRecipe> {
        private Serializer() {}

        public static final Serializer INSTANCE = new Serializer();
        public static final Identifier ID = EPAPI.id("sawmill");

        private final MapCodec<SawmillRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(CodecFix.ITEM_STACK_CODEC.fieldOf("result").forGetter((recipe) -> {
                return recipe.output;
            }), Ingredient.CODEC.fieldOf("ingredient").forGetter((recipe) -> {
                return recipe.input;
            }), Codecs.NON_NEGATIVE_INT.optionalFieldOf("sawdustAmount").forGetter((recipe) -> {
                if(recipe.secondaryOutput.isEmpty())
                    return Optional.of(0);

                return ItemStack.areItemsAndComponentsEqual(recipe.secondaryOutput, new ItemStack(EPItems.SAWDUST))?
                        Optional.of(recipe.secondaryOutput.getCount()):Optional.empty();
            }), CodecFix.ITEM_STACK_CODEC.optionalFieldOf("secondaryResult").forGetter((recipe) -> {
                if(recipe.secondaryOutput.isEmpty())
                    return Optional.empty();

                return ItemStack.areItemsAndComponentsEqual(recipe.secondaryOutput, new ItemStack(EPItems.SAWDUST))?
                        Optional.empty():Optional.of(recipe.secondaryOutput);
            })).apply(instance, (output, ingredient, sawdustAmount, secondaryOutput) -> {
                return secondaryOutput.map(o -> new SawmillRecipe(output, o, ingredient)).
                        orElseGet(() -> sawdustAmount.map(a -> new SawmillRecipe(output, ingredient, a)).
                                orElseThrow(() -> new IllegalArgumentException("Either \"sawdustAmount\" or \"secondaryOutput\" must be present")));
            });
        });

        private final PacketCodec<RegistryByteBuf, SawmillRecipe> PACKET_CODEC = PacketCodec.ofStatic(
                Serializer::write, Serializer::read);

        @Override
        public MapCodec<SawmillRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, SawmillRecipe> packetCodec() {
            return PACKET_CODEC;
        }

        private static SawmillRecipe read(RegistryByteBuf buffer) {
            Ingredient input = Ingredient.PACKET_CODEC.decode(buffer);
            ItemStack output = ItemStack.OPTIONAL_PACKET_CODEC.decode(buffer);
            ItemStack secondaryOutput = ItemStack.OPTIONAL_PACKET_CODEC.decode(buffer);

            return new SawmillRecipe(output, secondaryOutput, input);
        }

        private static void write(RegistryByteBuf buffer, SawmillRecipe recipe) {
            Ingredient.PACKET_CODEC.encode(buffer, recipe.input);
            ItemStack.OPTIONAL_PACKET_CODEC.encode(buffer, recipe.output);
            ItemStack.OPTIONAL_PACKET_CODEC.encode(buffer, recipe.secondaryOutput);
        }
    }
}
