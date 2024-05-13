package me.jddev0.ep.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.codec.CodecFix;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.*;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class CompressorRecipe implements Recipe<Inventory> {
    private final ItemStack output;
    private final Ingredient input;

    public CompressorRecipe(ItemStack output, Ingredient input) {
        this.output = output;
        this.input = input;
    }

    public ItemStack getOutputItem() {
        return output;
    }

    public Ingredient getInputItem() {
        return input;
    }

    @Override
    public boolean matches(Inventory container, World level) {
        if(level.isClient())
            return false;

        return input.test(container.getStack(0));
    }

    @Override
    public ItemStack craft(Inventory container, RegistryWrapper.WrapperLookup registries) {
        return output;
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registries) {
        return output.copy();
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        DefaultedList<Ingredient> ingredients = DefaultedList.ofSize(1);
        ingredients.add(0, input);
        return ingredients;
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(ModBlocks.COMPRESSOR_ITEM);
    }

    @Override
    public boolean isIgnoredInRecipeBook() {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static final class Type implements RecipeType<CompressorRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "compressor";
    }

    public static final class Serializer implements RecipeSerializer<CompressorRecipe> {
        private Serializer() {}

        public static final Serializer INSTANCE = new Serializer();
        public static final Identifier ID = new Identifier(EnergizedPowerMod.MODID, "compressor");

        private final MapCodec<CompressorRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(CodecFix.ITEM_STACK_CODEC.fieldOf("output").forGetter((recipe) -> {
                return recipe.output;
            }), Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("ingredient").forGetter((recipe) -> {
                return recipe.input;
            })).apply(instance, CompressorRecipe::new);
        });

        private final PacketCodec<RegistryByteBuf, CompressorRecipe> PACKET_CODEC = PacketCodec.ofStatic(
                Serializer::write, Serializer::read);

        @Override
        public MapCodec<CompressorRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, CompressorRecipe> packetCodec() {
            return PACKET_CODEC;
        }

        private static CompressorRecipe read(RegistryByteBuf buffer) {
            Ingredient input = Ingredient.PACKET_CODEC.decode(buffer);
            ItemStack output = ItemStack.OPTIONAL_PACKET_CODEC.decode(buffer);

            return new CompressorRecipe(output, input);
        }

        private static void write(RegistryByteBuf buffer, CompressorRecipe recipe) {
            Ingredient.PACKET_CODEC.encode(buffer, recipe.input);
            ItemStack.OPTIONAL_PACKET_CODEC.encode(buffer, recipe.output);
        }
    }
}
