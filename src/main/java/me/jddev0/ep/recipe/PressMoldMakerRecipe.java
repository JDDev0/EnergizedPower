package me.jddev0.ep.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.codec.CodecFix;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.World;

import java.util.List;

public class PressMoldMakerRecipe implements EnergizedPowerBaseRecipe<RecipeInput> {
    private final ItemStack output;
    private final int clayCount;

    public PressMoldMakerRecipe(ItemStack output, int clayCount) {
        this.output = output;
        this.clayCount = clayCount;
    }

    public ItemStack getOutput() {
        return output;
    }

    public int getClayCount() {
        return clayCount;
    }

    @Override
    public boolean matches(RecipeInput container, World level) {
        if(level.isClient())
            return false;

        ItemStack item = container.getStackInSlot(0);
        return item.isOf(Items.CLAY_BALL) && item.getCount() >= clayCount;
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
        return List.of(Ingredient.ofItems(Items.CLAY_BALL));
    }

    @Override
    public boolean isIngredient(ItemStack itemStack) {
        return itemStack.isOf(Items.CLAY_BALL);
    }

    @Override
    public boolean isResult(ItemStack itemStack) {
        return ItemStack.areItemsAndComponentsEqual(output, itemStack);
    }

    public static final class Type implements RecipeType<PressMoldMakerRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "press_mold_maker";
    }

    public static final class Serializer implements RecipeSerializer<PressMoldMakerRecipe> {
        private Serializer() {}

        public static final Serializer INSTANCE = new Serializer();
        public static final Identifier ID = EPAPI.id("press_mold_maker");

        private final MapCodec<PressMoldMakerRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(CodecFix.ITEM_STACK_CODEC.fieldOf("result").forGetter((recipe) -> {
                return recipe.output;
            }), Codecs.POSITIVE_INT.fieldOf("clayCount").forGetter((recipe) -> {
                return recipe.clayCount;
            })).apply(instance, PressMoldMakerRecipe::new);
        });

        private final PacketCodec<RegistryByteBuf, PressMoldMakerRecipe> PACKET_CODEC = PacketCodec.ofStatic(
                Serializer::write, Serializer::read);

        @Override
        public MapCodec<PressMoldMakerRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, PressMoldMakerRecipe> packetCodec() {
            return PACKET_CODEC;
        }

        private static PressMoldMakerRecipe read(RegistryByteBuf buffer) {
            int clayCount = buffer.readInt();
            ItemStack output = ItemStack.OPTIONAL_PACKET_CODEC.decode(buffer);

            return new PressMoldMakerRecipe(output, clayCount);
        }

        private static void write(RegistryByteBuf buffer, PressMoldMakerRecipe recipe) {
            buffer.writeInt(recipe.clayCount);
            ItemStack.OPTIONAL_PACKET_CODEC.encode(buffer, recipe.output);
        }
    }
}
