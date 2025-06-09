package me.jddev0.ep.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.fluid.FluidStack;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.*;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class StoneLiquefierRecipe implements Recipe<RecipeInput> {
    private final Ingredient input;
    private final FluidStack output;

    public StoneLiquefierRecipe(Ingredient input, FluidStack output) {
        this.input = input;
        this.output = output;
    }

    public Ingredient getInput() {
        return input;
    }

    public FluidStack getOutput() {
        return output;
    }

    @Override
    public boolean matches(RecipeInput container, World level) {
        if(level.isClient())
            return false;

        return input.test(container.getStackInSlot(0));
    }

    @Override
    public ItemStack craft(RecipeInput container, RegistryWrapper.WrapperLookup registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        DefaultedList<Ingredient> ingredients = DefaultedList.ofSize(1);
        ingredients.add(0, input);
        return ingredients;
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(EPBlocks.STONE_LIQUEFIER);
    }

    @Override
    public boolean isIgnoredInRecipeBook() {
        return true;
    }

    @Override
    public RecipeSerializer<? extends Recipe<RecipeInput>> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<? extends Recipe<RecipeInput>> getType() {
        return Type.INSTANCE;
    }

    public static final class Type implements RecipeType<StoneLiquefierRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "stone_liquefier";
    }

    public static final class Serializer implements RecipeSerializer<StoneLiquefierRecipe> {
        private Serializer() {}

        public static final Serializer INSTANCE = new Serializer();
        public static final Identifier ID = EPAPI.id("stone_liquefier");

        private final MapCodec<StoneLiquefierRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("ingredient").forGetter((recipe) -> {
                return recipe.input;
            }), FluidStack.CODEC_MILLIBUCKETS.fieldOf("result").forGetter((recipe) -> {
                return recipe.output;
            })).apply(instance, StoneLiquefierRecipe::new);
        });

        private final PacketCodec<RegistryByteBuf, StoneLiquefierRecipe> PACKET_CODEC = PacketCodec.ofStatic(
                Serializer::write, Serializer::read);

        @Override
        public MapCodec<StoneLiquefierRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, StoneLiquefierRecipe> packetCodec() {
            return PACKET_CODEC;
        }

        private static StoneLiquefierRecipe read(RegistryByteBuf buffer) {
            Ingredient input = Ingredient.PACKET_CODEC.decode(buffer);
            FluidStack output = FluidStack.PACKET_CODEC.decode(buffer);

            return new StoneLiquefierRecipe(input, output);
        }

        private static void write(RegistryByteBuf buffer, StoneLiquefierRecipe recipe) {
            Ingredient.PACKET_CODEC.encode(buffer, recipe.input);
            FluidStack.PACKET_CODEC.encode(buffer, recipe.output);
        }
    }
}
