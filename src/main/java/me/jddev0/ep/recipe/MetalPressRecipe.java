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

public class MetalPressRecipe implements EnergizedPowerBaseRecipe<RecipeInput> {
    private final ItemStack output;
    private final ItemStack pressMold;
    private final IngredientWithCount input;

    public MetalPressRecipe(ItemStack output, ItemStack pressMold, IngredientWithCount input) {
        this.output = output;
        this.pressMold = pressMold;
        this.input = input;
    }

    public ItemStack getOutput() {
        return output;
    }

    public ItemStack getPressMold() {
        return pressMold;
    }

    public IngredientWithCount getInput() {
        return input;
    }

    @Override
    public boolean matches(RecipeInput container, World level) {
        if(level.isClient())
            return false;

        return input.input().test(container.getStackInSlot(0)) && container.getStackInSlot(0).getCount() >= input.count() &&
                ItemStack.areItemsEqual(pressMold, container.getStackInSlot(1));
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
        return EPRecipes.METAL_PRESS_CATEGORY;
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

    public static final class Type implements RecipeType<MetalPressRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "metal_press";
    }

    public static final class Serializer implements RecipeSerializer<MetalPressRecipe> {
        private Serializer() {}

        public static final Serializer INSTANCE = new Serializer();
        public static final Identifier ID = EPAPI.id("metal_press");

        private final MapCodec<MetalPressRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(CodecFix.ITEM_STACK_CODEC.fieldOf("result").forGetter((recipe) -> {
                return recipe.output;
            }), CodecFix.ITEM_STACK_CODEC.fieldOf("pressMold").forGetter((recipe) -> {
                return recipe.pressMold;
            }), IngredientWithCount.CODEC.fieldOf("ingredient").forGetter((recipe) -> {
                return recipe.input;
            })).apply(instance, MetalPressRecipe::new);
        });

        private final PacketCodec<RegistryByteBuf, MetalPressRecipe> PACKET_CODEC = PacketCodec.ofStatic(
                Serializer::write, Serializer::read);

        @Override
        public MapCodec<MetalPressRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, MetalPressRecipe> packetCodec() {
            return PACKET_CODEC;
        }

        private static MetalPressRecipe read(RegistryByteBuf buffer) {
            IngredientWithCount input = IngredientWithCount.STREAM_CODEC.decode(buffer);
            ItemStack pressMold = ItemStack.OPTIONAL_PACKET_CODEC.decode(buffer);
            ItemStack output = ItemStack.OPTIONAL_PACKET_CODEC.decode(buffer);

            return new MetalPressRecipe(output, pressMold, input);
        }

        private static void write(RegistryByteBuf buffer, MetalPressRecipe recipe) {
            IngredientWithCount.STREAM_CODEC.encode(buffer, recipe.input);
            ItemStack.OPTIONAL_PACKET_CODEC.encode(buffer, recipe.pressMold);
            ItemStack.OPTIONAL_PACKET_CODEC.encode(buffer, recipe.output);
        }
    }
}
