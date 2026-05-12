package me.jddev0.ep.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.util.ItemStackUtils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStackTemplate;

import java.util.List;

public class FluidFreezerRecipe implements EnergizedPowerBaseRecipe<RecipeInput> {
    private final FluidStackTemplate input;
    private final ItemStackTemplate output;

    public FluidFreezerRecipe(FluidStackTemplate input, ItemStackTemplate output) {
        this.input = input;
        this.output = output;
    }

    public FluidStackTemplate getInput() {
        return input;
    }

    public ItemStackTemplate getOutput() {
        return output;
    }

    @Override
    public boolean matches(RecipeInput container, Level level) {
        return false;
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
        return EPRecipes.FLUID_FREEZER_CATEGORY.get();
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
        return List.of();
    }

    @Override
    public boolean isIngredient(ItemStack itemStack) {
        return false;
    }

    @Override
    public boolean isResult(ItemStack itemStack) {
        return false;
    }

    public static final class Type implements RecipeType<FluidFreezerRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "fluid_freezer";
    }

    public static final class Serializer {
        private Serializer() {}

        private static final MapCodec<FluidFreezerRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(FluidStackTemplate.CODEC.fieldOf("ingredient").forGetter((recipe) -> {
                return recipe.input;
            }), ItemStackTemplate.CODEC.fieldOf("result").forGetter((recipe) -> {
                return recipe.output;
            })).apply(instance, FluidFreezerRecipe::new);
        });

        private static final StreamCodec<RegistryFriendlyByteBuf, FluidFreezerRecipe> STREAM_CODEC = StreamCodec.of(
                Serializer::write, Serializer::read);

        public static final RecipeSerializer<FluidFreezerRecipe> INSTANCE = new RecipeSerializer<>(CODEC, STREAM_CODEC);
        public static final Identifier ID = EPAPI.id("fluid_freezer");

        private static FluidFreezerRecipe read(RegistryFriendlyByteBuf buffer) {
            FluidStackTemplate input = FluidStackTemplate.STREAM_CODEC.decode(buffer);
            ItemStackTemplate output = ItemStackTemplate.STREAM_CODEC.decode(buffer);

            return new FluidFreezerRecipe(input, output);
        }

        private static void write(RegistryFriendlyByteBuf buffer, FluidFreezerRecipe recipe) {
            FluidStackTemplate.STREAM_CODEC.encode(buffer, recipe.input);
            ItemStackTemplate.STREAM_CODEC.encode(buffer, recipe.output);
        }
    }
}
