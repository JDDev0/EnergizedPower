package me.jddev0.ep.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.codec.CodecFix;
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

public class StoneSolidifierRecipe implements EnergizedPowerBaseRecipe<RecipeInput> {
    private final ItemStack output;
    private final long waterAmount;
    private final long lavaAmount;

    public StoneSolidifierRecipe(ItemStack output, long waterAmount, long lavaAmount) {
        this.output = output;
        this.waterAmount = waterAmount;
        this.lavaAmount = lavaAmount;
    }

    public ItemStack getOutput() {
        return output;
    }

    public long getWaterAmount() {
        return waterAmount;
    }

    public long getLavaAmount() {
        return lavaAmount;
    }

    @Override
    public boolean matches(RecipeInput container, World level) {
        return false;
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
        return EPRecipes.STONE_SOLIDIFIER_CATEGORY;
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
        return ItemStack.areItemsAndComponentsEqual(output, itemStack);
    }

    public static final class Type implements RecipeType<StoneSolidifierRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "stone_solidifier";
    }

    public static final class Serializer implements RecipeSerializer<StoneSolidifierRecipe> {
        private Serializer() {}

        public static final Serializer INSTANCE = new Serializer();
        public static final Identifier ID = EPAPI.id("stone_solidifier");

        private final MapCodec<StoneSolidifierRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(CodecFix.ITEM_STACK_CODEC.fieldOf("result").forGetter((recipe) -> {
                return recipe.output;
            }), Codec.LONG.fieldOf("waterAmount").forGetter((recipe) -> {
                return recipe.waterAmount;
            }), Codec.LONG.fieldOf("lavaAmount").forGetter((recipe) -> {
                return recipe.lavaAmount;
            })).apply(instance, StoneSolidifierRecipe::new);
        });

        private final PacketCodec<RegistryByteBuf, StoneSolidifierRecipe> PACKET_CODEC = PacketCodec.ofStatic(
                Serializer::write, Serializer::read);

        @Override
        public MapCodec<StoneSolidifierRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, StoneSolidifierRecipe> packetCodec() {
            return PACKET_CODEC;
        }

        private static StoneSolidifierRecipe read(RegistryByteBuf buffer) {
            long waterAmount = buffer.readLong();
            long lavaAmount = buffer.readLong();
            ItemStack output = ItemStack.OPTIONAL_PACKET_CODEC.decode(buffer);

            return new StoneSolidifierRecipe(output, waterAmount, lavaAmount);
        }

        private static void write(RegistryByteBuf buffer, StoneSolidifierRecipe recipe) {
            buffer.writeLong(recipe.waterAmount);
            buffer.writeLong(recipe.lavaAmount);
            ItemStack.OPTIONAL_PACKET_CODEC.encode(buffer, recipe.output);
        }
    }
}
