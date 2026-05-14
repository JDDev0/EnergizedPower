package me.jddev0.ep.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.codec.CodecFix;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

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
    public boolean matches(RecipeInput container, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(RecipeInput container, HolderLookup.Provider registries) {
        return output;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return output.copy();
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(EPBlocks.STONE_SOLIDIFIER);
    }

    @Override
    public boolean isSpecial() {
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
        public static final ResourceLocation ID = EPAPI.id("stone_solidifier");

        private final MapCodec<StoneSolidifierRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(CodecFix.ITEM_STACK_CODEC.fieldOf("output").forGetter((recipe) -> {
                return recipe.output;
            }), Codec.LONG.fieldOf("waterAmount").forGetter((recipe) -> {
                return recipe.waterAmount;
            }), Codec.LONG.fieldOf("lavaAmount").forGetter((recipe) -> {
                return recipe.lavaAmount;
            })).apply(instance, StoneSolidifierRecipe::new);
        });

        private final StreamCodec<RegistryFriendlyByteBuf, StoneSolidifierRecipe> PACKET_CODEC = StreamCodec.of(
                Serializer::write, Serializer::read);

        @Override
        public MapCodec<StoneSolidifierRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, StoneSolidifierRecipe> streamCodec() {
            return PACKET_CODEC;
        }

        private static StoneSolidifierRecipe read(RegistryFriendlyByteBuf buffer) {
            long waterAmount = buffer.readLong();
            long lavaAmount = buffer.readLong();
            ItemStack output = ItemStack.OPTIONAL_STREAM_CODEC.decode(buffer);

            return new StoneSolidifierRecipe(output, waterAmount, lavaAmount);
        }

        private static void write(RegistryFriendlyByteBuf buffer, StoneSolidifierRecipe recipe) {
            buffer.writeLong(recipe.waterAmount);
            buffer.writeLong(recipe.lavaAmount);
            ItemStack.OPTIONAL_STREAM_CODEC.encode(buffer, recipe.output);
        }
    }
}
