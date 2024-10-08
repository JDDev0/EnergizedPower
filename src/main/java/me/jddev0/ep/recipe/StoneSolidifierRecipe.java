package me.jddev0.ep.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.codec.CodecFix;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class StoneSolidifierRecipe implements Recipe<RecipeInput> {
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
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registries) {
        return output.copy();
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(EPBlocks.STONE_SOLIDIFIER);
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
            return instance.group(CodecFix.ITEM_STACK_CODEC.fieldOf("output").forGetter((recipe) -> {
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
