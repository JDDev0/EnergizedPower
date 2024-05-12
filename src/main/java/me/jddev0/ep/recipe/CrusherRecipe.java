package me.jddev0.ep.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.codec.CodecFix;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class CrusherRecipe implements Recipe<Container> {
    private final ItemStack output;
    private final Ingredient input;

    public CrusherRecipe(ItemStack output, Ingredient input) {
        this.output = output;
        this.input = input;
    }

    public ItemStack getOutput() {
        return output;
    }

    public Ingredient getInput() {
        return input;
    }

    @Override
    public boolean matches(Container container, Level level) {
        if(level.isClientSide)
            return false;

        return input.test(container.getItem(0));
    }

    @Override
    public ItemStack assemble(Container container, HolderLookup.Provider registries) {
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
        return new ItemStack(ModBlocks.CRUSHER_ITEM.get());
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

    public static final class Type implements RecipeType<CrusherRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "crusher";
    }

    public static final class Serializer implements RecipeSerializer<CrusherRecipe> {
        private Serializer() {}

        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation(EnergizedPowerMod.MODID, "crusher");

        private final MapCodec<CrusherRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(CodecFix.ITEM_STACK_CODEC.fieldOf("output").forGetter((recipe) -> {
                return recipe.output;
            }), Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter((recipe) -> {
                return recipe.input;
            })).apply(instance, CrusherRecipe::new);
        });

        private final StreamCodec<RegistryFriendlyByteBuf, CrusherRecipe> STREAM_CODEC = StreamCodec.of(
                Serializer::write, Serializer::read);

        @Override
        public MapCodec<CrusherRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CrusherRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static CrusherRecipe read(RegistryFriendlyByteBuf buffer) {
            Ingredient input = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            ItemStack output = ItemStack.OPTIONAL_STREAM_CODEC.decode(buffer);

            return new CrusherRecipe(output, input);
        }

        private static void write(RegistryFriendlyByteBuf buffer, CrusherRecipe recipe) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.input);
            ItemStack.OPTIONAL_STREAM_CODEC.encode(buffer, recipe.output);
        }
    }
}
