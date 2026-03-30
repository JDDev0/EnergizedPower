package me.jddev0.ep.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.util.ItemStackUtils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import java.util.List;

public class PressMoldMakerRecipe implements EnergizedPowerBaseRecipe<RecipeInput> {
    private final ItemStackTemplate output;
    private final int clayCount;

    public PressMoldMakerRecipe(ItemStackTemplate output, int clayCount) {
        this.output = output;
        this.clayCount = clayCount;
    }

    public ItemStackTemplate getOutput() {
        return output;
    }

    public int getClayCount() {
        return clayCount;
    }

    @Override
    public boolean matches(RecipeInput container, Level level) {
        if(level.isClientSide())
            return false;

        ItemStack item = container.getItem(0);
        return item.is(Items.CLAY_BALL) && item.getCount() >= clayCount;
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
        return EPRecipes.PRESS_MOLD_MAKER_CATEGORY;
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
        return List.of(Ingredient.of(Items.CLAY_BALL));
    }

    @Override
    public boolean isIngredient(ItemStack itemStack) {
        return itemStack.is(Items.CLAY_BALL);
    }

    @Override
    public boolean isResult(ItemStack itemStack) {
        ItemStack output = ItemStackUtils.fromNullableItemStackTemplate(this.output);

        return ItemStack.isSameItemSameComponents(output, itemStack);
    }

    public static final class Type implements RecipeType<PressMoldMakerRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "press_mold_maker";
    }

    public static final class Serializer {
        private Serializer() {}

        private static final MapCodec<PressMoldMakerRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(ItemStackTemplate.CODEC.fieldOf("result").forGetter((recipe) -> {
                return recipe.output;
            }), ExtraCodecs.POSITIVE_INT.fieldOf("clayCount").forGetter((recipe) -> {
                return recipe.clayCount;
            })).apply(instance, PressMoldMakerRecipe::new);
        });

        private static final StreamCodec<RegistryFriendlyByteBuf, PressMoldMakerRecipe> STREAM_CODEC = StreamCodec.of(
                Serializer::write, Serializer::read);

        public static final RecipeSerializer<PressMoldMakerRecipe> INSTANCE = new RecipeSerializer<>(CODEC, STREAM_CODEC);
        public static final Identifier ID = EPAPI.id("press_mold_maker");

        private static PressMoldMakerRecipe read(RegistryFriendlyByteBuf buffer) {
            int clayCount = buffer.readInt();
            ItemStackTemplate output = ItemStackTemplate.STREAM_CODEC.decode(buffer);

            return new PressMoldMakerRecipe(output, clayCount);
        }

        private static void write(RegistryFriendlyByteBuf buffer, PressMoldMakerRecipe recipe) {
            buffer.writeInt(recipe.clayCount);
            ItemStackTemplate.STREAM_CODEC.encode(buffer, recipe.output);
        }
    }
}
