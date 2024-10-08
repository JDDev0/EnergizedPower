package me.jddev0.ep.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.codec.CodecFix;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.World;

public class PressMoldMakerRecipe implements Recipe<Inventory> {
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
    public boolean matches(Inventory container, World level) {
        if(level.isClient())
            return false;

        ItemStack item = container.getStack(0);
        return item.isOf(Items.CLAY_BALL) && item.getCount() >= clayCount;
    }

    @Override
    public ItemStack craft(Inventory container, DynamicRegistryManager registryAccess) {
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
        return new ItemStack(ModBlocks.PRESS_MOLD_MAKER_ITEM);
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

    public static final class Type implements RecipeType<PressMoldMakerRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "press_mold_maker";
    }

    public static final class Serializer implements RecipeSerializer<PressMoldMakerRecipe> {
        private Serializer() {}

        public static final Serializer INSTANCE = new Serializer();
        public static final Identifier ID = EPAPI.id("press_mold_maker");

        private final Codec<PressMoldMakerRecipe> CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(CodecFix.ITEM_STACK_CODEC.fieldOf("output").forGetter((recipe) -> {
                return recipe.output;
            }), Codecs.POSITIVE_INT.fieldOf("clayCount").forGetter((recipe) -> {
                return recipe.clayCount;
            })).apply(instance, PressMoldMakerRecipe::new);
        });

        @Override
        public Codec<PressMoldMakerRecipe> codec() {
            return CODEC;
        }

        @Override
        public PressMoldMakerRecipe read(PacketByteBuf buffer) {
            int clayCount = buffer.readInt();
            ItemStack output = buffer.readItemStack();

            return new PressMoldMakerRecipe(output, clayCount);
        }

        @Override
        public void write(PacketByteBuf buffer, PressMoldMakerRecipe recipe) {
            buffer.writeInt(recipe.clayCount);
            buffer.writeItemStack(recipe.output);
        }
    }
}
