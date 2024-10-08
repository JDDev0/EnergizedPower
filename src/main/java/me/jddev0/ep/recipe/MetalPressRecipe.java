package me.jddev0.ep.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.codec.CodecFix;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.World;

public class MetalPressRecipe implements Recipe<RecipeInput> {
    private final ItemStack output;
    private final ItemStack pressMold;
    private final Ingredient input;
    private final int inputCount;

    public MetalPressRecipe(ItemStack output, ItemStack pressMold, Ingredient input, int inputCount) {
        this.output = output;
        this.pressMold = pressMold;
        this.input = input;
        this.inputCount = inputCount;
    }

    public ItemStack getOutput() {
        return output;
    }

    public ItemStack getPressMold() {
        return pressMold;
    }

    public Ingredient getInput() {
        return input;
    }

    public int getInputCount() {
        return inputCount;
    }

    @Override
    public boolean matches(RecipeInput container, World level) {
        if(level.isClient())
            return false;

        return input.test(container.getStackInSlot(0)) && container.getStackInSlot(0).getCount() >= inputCount &&
                ItemStack.areItemsEqual(pressMold, container.getStackInSlot(1));
    }

    @Override
    public ItemStack craft(RecipeInput container, RegistryWrapper.WrapperLookup registries) {
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
    public ItemStack createIcon() {
        return new ItemStack(EPBlocks.METAL_PRESS_ITEM);
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

    public static final class Type implements RecipeType<MetalPressRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "metal_press";
    }

    public static final class Serializer implements RecipeSerializer<MetalPressRecipe> {
        private Serializer() {}

        public static final Serializer INSTANCE = new Serializer();
        public static final Identifier ID = EPAPI.id("metal_press");

        private final MapCodec<MetalPressRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(CodecFix.ITEM_STACK_CODEC.fieldOf("output").forGetter((recipe) -> {
                return recipe.output;
            }), CodecFix.ITEM_STACK_CODEC.fieldOf("pressMold").forGetter((recipe) -> {
                return recipe.pressMold;
            }), Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("ingredient").forGetter((recipe) -> {
                return recipe.input;
            }), Codecs.POSITIVE_INT.optionalFieldOf("inputCount", 1).forGetter((recipe) -> {
                return recipe.inputCount;
            })).apply(instance, MetalPressRecipe::new);
        });

        private final PacketCodec<RegistryByteBuf, MetalPressRecipe> PACKET_CODEC = PacketCodec.ofStatic(
                Serializer::write, Serializer::read);

        @Override
        public MapCodec<MetalPressRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, MetalPressRecipe> packetCodec() {
            return PACKET_CODEC;
        }

        private static MetalPressRecipe read(RegistryByteBuf buffer) {
            Ingredient input = Ingredient.PACKET_CODEC.decode(buffer);
            int inputCount = buffer.readInt();
            ItemStack pressMold = ItemStack.OPTIONAL_PACKET_CODEC.decode(buffer);
            ItemStack output = ItemStack.OPTIONAL_PACKET_CODEC.decode(buffer);

            return new MetalPressRecipe(output, pressMold, input, inputCount);
        }

        private static void write(RegistryByteBuf buffer, MetalPressRecipe recipe) {
            Ingredient.PACKET_CODEC.encode(buffer, recipe.input);
            buffer.writeInt(recipe.inputCount);
            ItemStack.OPTIONAL_PACKET_CODEC.encode(buffer, recipe.pressMold);
            ItemStack.OPTIONAL_PACKET_CODEC.encode(buffer, recipe.output);
        }
    }
}
