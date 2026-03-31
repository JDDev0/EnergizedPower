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
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import java.util.List;

public class MetalPressRecipe implements EnergizedPowerBaseRecipe<RecipeInput> {
    private final ItemStackTemplate output;
    private final ItemStackTemplate pressMold;
    private final IngredientWithCount input;

    public MetalPressRecipe(ItemStackTemplate output, ItemStackTemplate pressMold, IngredientWithCount input) {
        this.output = output;
        this.pressMold = pressMold;
        this.input = input;
    }

    public ItemStackTemplate getOutput() {
        return output;
    }

    public ItemStackTemplate getPressMold() {
        return pressMold;
    }

    public IngredientWithCount getInput() {
        return input;
    }

    @Override
    public boolean matches(RecipeInput container, Level level) {
        if(level.isClientSide())
            return false;

        ItemStack pressMold = ItemStackUtils.fromNullableItemStackTemplate(this.pressMold);

        return input.input().test(container.getItem(0)) && container.getItem(0).getCount() >= input.count() &&
                ItemStack.isSameItem(pressMold, container.getItem(1));
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
        return EPRecipes.METAL_PRESS_CATEGORY.get();
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
        ItemStack output = ItemStackUtils.fromNullableItemStackTemplate(this.output);

        return ItemStack.isSameItemSameComponents(output, itemStack);
    }

    public static final class Type implements RecipeType<MetalPressRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "metal_press";
    }

    public static final class Serializer {
        private Serializer() {}

        private static final MapCodec<MetalPressRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(ItemStackTemplate.CODEC.fieldOf("result").forGetter((recipe) -> {
                return recipe.output;
            }), ItemStackTemplate.CODEC.fieldOf("pressMold").forGetter((recipe) -> {
                return recipe.pressMold;
            }), IngredientWithCount.CODEC.fieldOf("ingredient").forGetter((recipe) -> {
                return recipe.input;
            })).apply(instance, MetalPressRecipe::new);
        });

        private static final StreamCodec<RegistryFriendlyByteBuf, MetalPressRecipe> STREAM_CODEC = StreamCodec.of(
                Serializer::write, Serializer::read);

        public static final RecipeSerializer<MetalPressRecipe> INSTANCE = new RecipeSerializer<>(CODEC, STREAM_CODEC);
        public static final Identifier ID = EPAPI.id("metal_press");

        private static MetalPressRecipe read(RegistryFriendlyByteBuf buffer) {
            IngredientWithCount input = IngredientWithCount.STREAM_CODEC.decode(buffer);
            ItemStackTemplate pressMold = ItemStackTemplate.STREAM_CODEC.decode(buffer);
            ItemStackTemplate output = ItemStackTemplate.STREAM_CODEC.decode(buffer);

            return new MetalPressRecipe(output, pressMold, input);
        }

        private static void write(RegistryFriendlyByteBuf buffer, MetalPressRecipe recipe) {
            IngredientWithCount.STREAM_CODEC.encode(buffer, recipe.input);
            ItemStackTemplate.STREAM_CODEC.encode(buffer, recipe.pressMold);
            ItemStackTemplate.STREAM_CODEC.encode(buffer, recipe.output);
        }
    }
}
