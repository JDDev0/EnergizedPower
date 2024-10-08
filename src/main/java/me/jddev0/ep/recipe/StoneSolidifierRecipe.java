package me.jddev0.ep.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.codec.CodecFix;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class StoneSolidifierRecipe implements Recipe<Inventory> {
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
    public boolean matches(Inventory container, World level) {
        return false;
    }

    @Override
    public ItemStack craft(Inventory container, DynamicRegistryManager registryManager) {
        return output;
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResult(DynamicRegistryManager registryAccess) {
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

        private final Codec<StoneSolidifierRecipe> CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(CodecFix.ITEM_STACK_CODEC.fieldOf("output").forGetter((recipe) -> {
                return recipe.output;
            }), Codec.LONG.fieldOf("waterAmount").forGetter((recipe) -> {
                return recipe.waterAmount;
            }), Codec.LONG.fieldOf("lavaAmount").forGetter((recipe) -> {
                return recipe.lavaAmount;
            })).apply(instance, StoneSolidifierRecipe::new);
        });

        @Override
        public Codec<StoneSolidifierRecipe> codec() {
            return CODEC;
        }

        @Override
        public StoneSolidifierRecipe read(PacketByteBuf buffer) {
            long waterAmount = buffer.readLong();
            long lavaAmount = buffer.readLong();
            ItemStack output = buffer.readItemStack();

            return new StoneSolidifierRecipe(output, waterAmount, lavaAmount);
        }

        @Override
        public void write(PacketByteBuf buffer, StoneSolidifierRecipe recipe) {
            buffer.writeLong(recipe.waterAmount);
            buffer.writeLong(recipe.lavaAmount);
            buffer.writeItemStack(recipe.output);
        }
    }
}
