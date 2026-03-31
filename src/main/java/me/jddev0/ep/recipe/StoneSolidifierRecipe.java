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
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import java.util.List;

public class StoneSolidifierRecipe implements EnergizedPowerBaseRecipe<RecipeInput> {
    private final ItemStackTemplate output;
    private final int waterAmount;
    private final int lavaAmount;

    public StoneSolidifierRecipe(ItemStackTemplate output, int waterAmount, int lavaAmount) {
        this.output = output;
        this.waterAmount = waterAmount;
        this.lavaAmount = lavaAmount;
    }

    public ItemStackTemplate getOutput() {
        return output;
    }

    public int getWaterAmount() {
        return waterAmount;
    }

    public int getLavaAmount() {
        return lavaAmount;
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
        return EPRecipes.STONE_SOLIDIFIER_CATEGORY.get();
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
        ItemStack output = ItemStackUtils.fromNullableItemStackTemplate(this.output);

        return ItemStack.isSameItemSameComponents(output, itemStack);
    }

    public static final class Type implements RecipeType<StoneSolidifierRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "stone_solidifier";
    }

    public static final class Serializer {
        private Serializer() {}

        private static final MapCodec<StoneSolidifierRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(ItemStackTemplate.CODEC.fieldOf("result").forGetter((recipe) -> {
                return recipe.output;
            }), ExtraCodecs.POSITIVE_INT.fieldOf("waterAmount").forGetter((recipe) -> {
                return recipe.waterAmount;
            }), ExtraCodecs.POSITIVE_INT.fieldOf("lavaAmount").forGetter((recipe) -> {
                return recipe.lavaAmount;
            })).apply(instance, StoneSolidifierRecipe::new);
        });

        private static final StreamCodec<RegistryFriendlyByteBuf, StoneSolidifierRecipe> STREAM_CODEC = StreamCodec.of(
                Serializer::write, Serializer::read);

        public static final RecipeSerializer<StoneSolidifierRecipe> INSTANCE = new RecipeSerializer<>(CODEC, STREAM_CODEC);
        public static final Identifier ID = EPAPI.id("stone_solidifier");

        private static StoneSolidifierRecipe read(RegistryFriendlyByteBuf buffer) {
            int waterAmount = buffer.readInt();
            int lavaAmount = buffer.readInt();
            ItemStackTemplate output = ItemStackTemplate.STREAM_CODEC.decode(buffer);

            return new StoneSolidifierRecipe(output, waterAmount, lavaAmount);
        }

        private static void write(RegistryFriendlyByteBuf buffer, StoneSolidifierRecipe recipe) {
            buffer.writeInt(recipe.waterAmount);
            buffer.writeInt(recipe.lavaAmount);
            ItemStackTemplate.STREAM_CODEC.encode(buffer, recipe.output);
        }
    }
}
