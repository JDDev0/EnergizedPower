package me.jddev0.ep.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.entity.FluidTransposerBlockEntity;
import me.jddev0.ep.codec.CodecFix;
import me.jddev0.ep.fluid.FluidStack;
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

public class FluidTransposerRecipe implements EnergizedPowerBaseRecipe<RecipeInput> {
    private final FluidTransposerBlockEntity.Mode mode;
    private final ItemStack output;
    private final Ingredient input;
    private final FluidStack fluid;

    public FluidTransposerRecipe(FluidTransposerBlockEntity.Mode mode, ItemStack output, Ingredient input, FluidStack fluid) {
        this.mode = mode;
        this.output = output;
        this.input = input;
        this.fluid = fluid;
    }

    public FluidTransposerBlockEntity.Mode getMode() {
        return mode;
    }

    public ItemStack getOutput() {
        return output;
    }

    public Ingredient getInput() {
        return input;
    }

    public FluidStack getFluid() {
        return fluid;
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
        return EPRecipes.FLUID_TRANSPOSER_CATEGORY;
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
        return ItemStack.areItemsAndComponentsEqual(output, itemStack);
    }

    public static final class Type implements RecipeType<FluidTransposerRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "fluid_transposer";
    }

    public static final class Serializer implements RecipeSerializer<FluidTransposerRecipe> {
        private Serializer() {}

        public static final Serializer INSTANCE = new Serializer();
        public static final Identifier ID = EPAPI.id("fluid_transposer");

        private final MapCodec<FluidTransposerRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(FluidTransposerBlockEntity.Mode.CODEC.fieldOf("mode").forGetter((recipe) -> {
                return recipe.mode;
            }), CodecFix.ITEM_STACK_CODEC.fieldOf("result").forGetter((recipe) -> {
                return recipe.output;
            }), Ingredient.CODEC.fieldOf("ingredient").forGetter((recipe) -> {
                return recipe.input;
            }), FluidStack.CODEC_MILLIBUCKETS.fieldOf("fluid").forGetter((recipe) -> {
                return recipe.fluid;
            })).apply(instance, FluidTransposerRecipe::new);
        });

        private final PacketCodec<RegistryByteBuf, FluidTransposerRecipe> PACKET_CODEC = PacketCodec.ofStatic(
                Serializer::write, Serializer::read);

        @Override
        public MapCodec<FluidTransposerRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, FluidTransposerRecipe> packetCodec() {
            return PACKET_CODEC;
        }

        private static FluidTransposerRecipe read(RegistryByteBuf buffer) {
            FluidTransposerBlockEntity.Mode mode = buffer.readEnumConstant(FluidTransposerBlockEntity.Mode.class);
            Ingredient input = Ingredient.PACKET_CODEC.decode(buffer);
            ItemStack output = ItemStack.OPTIONAL_PACKET_CODEC.decode(buffer);
            FluidStack fluid = FluidStack.PACKET_CODEC.decode(buffer);

            return new FluidTransposerRecipe(mode, output, input, fluid);
        }

        private static void write(RegistryByteBuf buffer, FluidTransposerRecipe recipe) {
            buffer.writeEnumConstant(recipe.mode);
            Ingredient.PACKET_CODEC.encode(buffer, recipe.input);
            ItemStack.OPTIONAL_PACKET_CODEC.encode(buffer, recipe.output);
            FluidStack.PACKET_CODEC.encode(buffer, recipe.fluid);
        }
    }
}
