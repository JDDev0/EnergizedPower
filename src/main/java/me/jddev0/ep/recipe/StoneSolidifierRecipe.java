package me.jddev0.ep.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.codec.CodecFix;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class StoneSolidifierRecipe implements Recipe<Container> {
    private final ItemStack output;
    private final int waterAmount;
    private final int lavaAmount;

    public StoneSolidifierRecipe(ItemStack output, int waterAmount, int lavaAmount) {
        this.output = output;
        this.waterAmount = waterAmount;
        this.lavaAmount = lavaAmount;
    }

    public ItemStack getOutput() {
        return output;
    }

    public int getWaterAmount() {
        return waterAmount;
    }

    public int getLavaAmount() {
        return lavaAmount;
    }

    @Override
    public boolean matches(Container container, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        return output;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return output.copy();
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(ModBlocks.STONE_SOLIDIFIER.get());
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

        private final Codec<StoneSolidifierRecipe> CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(CodecFix.ITEM_STACK_CODEC.fieldOf("output").forGetter((recipe) -> {
                return recipe.output;
            }), ExtraCodecs.POSITIVE_INT.fieldOf("waterAmount").forGetter((recipe) -> {
                return recipe.waterAmount;
            }), ExtraCodecs.POSITIVE_INT.fieldOf("lavaAmount").forGetter((recipe) -> {
                return recipe.lavaAmount;
            })).apply(instance, StoneSolidifierRecipe::new);
        });

        @Override
        public Codec<StoneSolidifierRecipe> codec() {
            return CODEC;
        }

        @Override
        public StoneSolidifierRecipe fromNetwork(FriendlyByteBuf buffer) {
            int waterAmount = buffer.readInt();
            int lavaAmount = buffer.readInt();
            ItemStack output = buffer.readItem();

            return new StoneSolidifierRecipe(output, waterAmount, lavaAmount);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, StoneSolidifierRecipe recipe) {
            buffer.writeInt(recipe.waterAmount);
            buffer.writeInt(recipe.lavaAmount);
            buffer.writeItemStack(recipe.output, false);
        }
    }
}
