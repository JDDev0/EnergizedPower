package me.jddev0.ep.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.codec.CodecFix;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class MetalPressRecipe implements Recipe<RecipeInput> {
    private final ItemStack output;
    private final ItemStack pressMold;
    private final Ingredient input;
    private final int inputCount;

    public MetalPressRecipe(ItemStack output, ItemStack pressMold, Ingredient input, int inputCount) {
        this.output = output;
        this.pressMold = pressMold;
        this.input = input;
        this.inputCount = inputCount;
    }

    public ItemStack getOutput() {
        return output;
    }

    public ItemStack getPressMold() {
        return pressMold;
    }

    public Ingredient getInput() {
        return input;
    }

    public int getInputCount() {
        return inputCount;
    }

    @Override
    public boolean matches(RecipeInput container, Level level) {
        if(level.isClientSide)
            return false;

        return input.test(container.getItem(0)) && container.getItem(0).getCount() >= inputCount &&
                ItemStack.isSameItem(container.getItem(1), pressMold);
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
    public ItemStack getToastSymbol() {
        return new ItemStack(ModBlocks.METAL_PRESS_ITEM.get());
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

    public static final class Type implements RecipeType<MetalPressRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "metal_press";
    }

    public static final class Serializer implements RecipeSerializer<MetalPressRecipe> {
        private Serializer() {}

        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = EPAPI.id("metal_press");

        private final MapCodec<MetalPressRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(CodecFix.ITEM_STACK_CODEC.fieldOf("output").forGetter((recipe) -> {
                return recipe.output;
            }), CodecFix.ITEM_STACK_CODEC.fieldOf("pressMold").forGetter((recipe) -> {
                return recipe.pressMold;
            }), Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter((recipe) -> {
                return recipe.input;
            }), ExtraCodecs.POSITIVE_INT.optionalFieldOf("inputCount", 1).forGetter((recipe) -> {
                return recipe.inputCount;
            })).apply(instance, MetalPressRecipe::new);
        });

        private final StreamCodec<RegistryFriendlyByteBuf, MetalPressRecipe> STREAM_CODEC = StreamCodec.of(
                Serializer::write, Serializer::read);

        @Override
        public MapCodec<MetalPressRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, MetalPressRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static MetalPressRecipe read(RegistryFriendlyByteBuf buffer) {
            Ingredient input = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            int inputCount = buffer.readInt();
            ItemStack pressMold = ItemStack.OPTIONAL_STREAM_CODEC.decode(buffer);
            ItemStack output = ItemStack.OPTIONAL_STREAM_CODEC.decode(buffer);

            return new MetalPressRecipe(output, pressMold, input, inputCount);
        }

        private static void write(RegistryFriendlyByteBuf buffer, MetalPressRecipe recipe) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.input);
            buffer.writeInt(recipe.inputCount);
            ItemStack.OPTIONAL_STREAM_CODEC.encode(buffer, recipe.pressMold);
            ItemStack.OPTIONAL_STREAM_CODEC.encode(buffer, recipe.output);
        }
    }
}
