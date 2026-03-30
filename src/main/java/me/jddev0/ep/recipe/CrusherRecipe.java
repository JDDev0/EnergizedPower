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

public class CrusherRecipe implements EnergizedPowerBaseRecipe<RecipeInput> {
    private final ItemStackTemplate output;
    private final Ingredient input;

    public CrusherRecipe(ItemStackTemplate output, Ingredient input) {
        this.output = output;
        this.input = input;
    }

    public ItemStackTemplate getOutput() {
        return output;
    }

    public Ingredient getInput() {
        return input;
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
        return EPRecipes.CRUSHER_CATEGORY;
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

    public static final class Type implements RecipeType<CrusherRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "crusher";
    }

    public static final class Serializer {
        private Serializer() {}

        private static final MapCodec<CrusherRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(ItemStackTemplate.CODEC.fieldOf("result").forGetter((recipe) -> {
                return recipe.output;
            }), Ingredient.CODEC.fieldOf("ingredient").forGetter((recipe) -> {
                return recipe.input;
            })).apply(instance, CrusherRecipe::new);
        });

        private static final StreamCodec<RegistryFriendlyByteBuf, CrusherRecipe> STREAM_CODEC = StreamCodec.of(
                Serializer::write, Serializer::read);

        public static final RecipeSerializer<CrusherRecipe> INSTANCE = new RecipeSerializer<>(CODEC, STREAM_CODEC);
        public static final Identifier ID = EPAPI.id("crusher");

        private static CrusherRecipe read(RegistryFriendlyByteBuf buffer) {
            Ingredient input = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            ItemStackTemplate output = ItemStackTemplate.STREAM_CODEC.decode(buffer);

            return new CrusherRecipe(output, input);
        }

        private static void write(RegistryFriendlyByteBuf buffer, CrusherRecipe recipe) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.input);
            ItemStackTemplate.STREAM_CODEC.encode(buffer, recipe.output);
        }
    }
}
