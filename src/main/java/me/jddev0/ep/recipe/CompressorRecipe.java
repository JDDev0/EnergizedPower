package me.jddev0.ep.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.codec.CodecFix;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.List;

public class CompressorRecipe implements EnergizedPowerBaseRecipe<RecipeInput> {
    private final ItemStack output;
    private final IngredientWithCount input;

    public CompressorRecipe(ItemStack output, IngredientWithCount input) {
        this.output = output;
        this.input = input;
    }

    public ItemStack getOutputItem() {
        return output;
    }

    public IngredientWithCount getInput() {
        return input;
    }

    @Override
    public boolean matches(RecipeInput container, World level) {
        if(level.isClient())
            return false;

        return input.input().test(container.getStackInSlot(0)) && container.getStackInSlot(0).getCount() >= input.count();
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
        return EPRecipes.COMPRESSOR_CATEGORY;
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
        return ItemStack.areItemsAndComponentsEqual(output, itemStack);
    }

    public static final class Type implements RecipeType<CompressorRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "compressor";
    }

    public static final class Serializer implements RecipeSerializer<CompressorRecipe> {
        private Serializer() {}

        public static final Serializer INSTANCE = new Serializer();
        public static final Identifier ID = EPAPI.id("compressor");

        private final MapCodec<CompressorRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(CodecFix.ITEM_STACK_CODEC.fieldOf("result").forGetter((recipe) -> {
                return recipe.output;
            }), IngredientWithCount.CODEC.fieldOf("ingredient").forGetter((recipe) -> {
                return recipe.input;
            })).apply(instance, CompressorRecipe::new);
        });

        private final PacketCodec<RegistryByteBuf, CompressorRecipe> PACKET_CODEC = PacketCodec.ofStatic(
                Serializer::write, Serializer::read);

        @Override
        public MapCodec<CompressorRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, CompressorRecipe> packetCodec() {
            return PACKET_CODEC;
        }

        private static CompressorRecipe read(RegistryByteBuf buffer) {
            IngredientWithCount input = IngredientWithCount.STREAM_CODEC.decode(buffer);
            ItemStack output = ItemStack.OPTIONAL_PACKET_CODEC.decode(buffer);

            return new CompressorRecipe(output, input);
        }

        private static void write(RegistryByteBuf buffer, CompressorRecipe recipe) {
            IngredientWithCount.STREAM_CODEC.encode(buffer, recipe.input);
            ItemStack.OPTIONAL_PACKET_CODEC.encode(buffer, recipe.output);
        }
    }
}
