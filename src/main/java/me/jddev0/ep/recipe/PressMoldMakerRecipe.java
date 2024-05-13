package me.jddev0.ep.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.codec.CodecFix;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class PressMoldMakerRecipe implements Recipe<Container> {
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
    public boolean matches(Container container, Level level) {
        if(level.isClientSide)
            return false;

        ItemStack item = container.getItem(0);
        return item.is(Items.CLAY_BALL) && item.getCount() >= clayCount;
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
        return new ItemStack(ModBlocks.PRESS_MOLD_MAKER_ITEM.get());
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

    public static final class Type implements RecipeType<PressMoldMakerRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "press_mold_maker";
    }

    public static final class Serializer implements RecipeSerializer<PressMoldMakerRecipe> {
        private Serializer() {}

        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation(EnergizedPowerMod.MODID, "press_mold_maker");

        private final Codec<PressMoldMakerRecipe> CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(CodecFix.ITEM_STACK_CODEC.fieldOf("output").forGetter((recipe) -> {
                return recipe.output;
            }), ExtraCodecs.POSITIVE_INT.fieldOf("clayCount").forGetter((recipe) -> {
                return recipe.clayCount;
            })).apply(instance, PressMoldMakerRecipe::new);
        });

        @Override
        public Codec<PressMoldMakerRecipe> codec() {
            return CODEC;
        }

        @Override
        public PressMoldMakerRecipe fromNetwork(FriendlyByteBuf buffer) {
            int clayCount = buffer.readInt();
            ItemStack output = buffer.readItem();

            return new PressMoldMakerRecipe(output, clayCount);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, PressMoldMakerRecipe recipe) {
            buffer.writeInt(recipe.clayCount);
            buffer.writeItemStack(recipe.output, false);
        }
    }
}
