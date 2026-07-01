package me.jddev0.ep.recipe;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.entity.FluidTransposerBlockEntity;
import me.jddev0.ep.util.ItemStackUtils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
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
import net.neoforged.neoforge.fluids.FluidStackTemplate;

import java.util.List;

public class FluidTransposerRecipe implements EnergizedPowerBaseRecipe<RecipeInput> {
    private final FluidTransposerBlockEntity.Mode mode;
    private final ItemStackTemplate output;
    private final Ingredient input;
    //FluidIngredientWithAmount second argument so that EitherCodec prefers FluidStack (FluidIngredientWithAmount is only required for fluid id list and #-prefixed fluid tags)
    private final Either<FluidStackTemplate, FluidIngredientWithAmount> fluid;

    public FluidTransposerRecipe(FluidTransposerBlockEntity.Mode mode, ItemStackTemplate output, Ingredient input, FluidStackTemplate fluid) {
        this(mode, output, input, Either.left(fluid));
    }

    /**
     * FluidIngredientWithAmount is only valid with mode FILLING -> no mode parameter necessary
     */
    public FluidTransposerRecipe(ItemStackTemplate output, Ingredient input, FluidIngredientWithAmount fluid) {
        this(FluidTransposerBlockEntity.Mode.FILLING, output, input, Either.right(fluid));
    }

    public FluidTransposerRecipe(FluidTransposerBlockEntity.Mode mode, ItemStackTemplate output, Ingredient input, Either<FluidStackTemplate, FluidIngredientWithAmount> fluid) {
        this.mode = mode;
        this.output = output;
        this.input = input;
        this.fluid = fluid;

        if(mode == FluidTransposerBlockEntity.Mode.EMPTYING && fluid.right().isPresent()) {
            throw new IllegalArgumentException("FluidIngredientWithAmount is only allowed with FILLING mode");
        }
    }

    public FluidTransposerBlockEntity.Mode getMode() {
        return mode;
    }

    public ItemStackTemplate getOutput() {
        return output;
    }

    public Ingredient getInput() {
        return input;
    }

    public Either<FluidStackTemplate, FluidIngredientWithAmount> getFluid() {
        return fluid;
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
        return EPRecipes.FLUID_TRANSPOSER_CATEGORY.get();
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

        return ItemStack.isSameItemSameComponents(output, itemStack);
    }

    public static final class Type implements RecipeType<FluidTransposerRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "fluid_transposer";
    }

    public static final class Serializer {
        private Serializer() {}

        private static final MapCodec<FluidTransposerRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(FluidTransposerBlockEntity.Mode.CODEC.fieldOf("mode").forGetter((recipe) -> {
                return recipe.mode;
            }), ItemStackTemplate.CODEC.fieldOf("result").forGetter((recipe) -> {
                return recipe.output;
            }), Ingredient.CODEC.fieldOf("ingredient").forGetter((recipe) -> {
                return recipe.input;
            }), Codec.either(FluidStackTemplate.CODEC, FluidIngredientWithAmount.CODEC).fieldOf("fluid").forGetter((recipe) -> {
                return recipe.fluid;
            })).apply(instance, FluidTransposerRecipe::new);
        });

        private static final StreamCodec<RegistryFriendlyByteBuf, FluidTransposerRecipe> STREAM_CODEC = StreamCodec.of(
                Serializer::write, Serializer::read);

        public static final RecipeSerializer<FluidTransposerRecipe> INSTANCE = new RecipeSerializer<>(CODEC, STREAM_CODEC);
        public static final Identifier ID = EPAPI.id("fluid_transposer");

        private static FluidTransposerRecipe read(RegistryFriendlyByteBuf buffer) {
            FluidTransposerBlockEntity.Mode mode = buffer.readEnum(FluidTransposerBlockEntity.Mode.class);
            Ingredient input = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            ItemStackTemplate output = ItemStackTemplate.STREAM_CODEC.decode(buffer);

            Either<FluidStackTemplate, FluidIngredientWithAmount> fluid;
            if(buffer.readBoolean()) {
                fluid = Either.left(FluidStackTemplate.STREAM_CODEC.decode(buffer));
            }else {
                fluid = Either.right(FluidIngredientWithAmount.STREAM_CODEC.decode(buffer));
            }

            return new FluidTransposerRecipe(mode, output, input, fluid);
        }

        private static void write(RegistryFriendlyByteBuf buffer, FluidTransposerRecipe recipe) {
            buffer.writeEnum(recipe.mode);
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.input);
            ItemStackTemplate.STREAM_CODEC.encode(buffer, recipe.output);

            buffer.writeBoolean(recipe.fluid.left().isPresent());
            recipe.fluid.map(
                    fluid -> {
                        FluidStackTemplate.STREAM_CODEC.encode(buffer, fluid);

                        return null;
                    },
                    fluid -> {
                        FluidIngredientWithAmount.STREAM_CODEC.encode(buffer, fluid);

                        return null;
                    }
            );
        }
    }
}
