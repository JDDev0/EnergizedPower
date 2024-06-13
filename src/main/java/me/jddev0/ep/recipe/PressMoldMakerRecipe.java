package me.jddev0.ep.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.codec.CodecFix;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class PressMoldMakerRecipe implements Recipe<RecipeInput> {
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
    public boolean matches(RecipeInput container, Level level) {
        if(level.isClientSide)
            return false;

        ItemStack item = container.getItem(0);
        return item.is(Items.CLAY_BALL) && item.getCount() >= clayCount;
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
        public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(EnergizedPowerMod.MODID, "press_mold_maker");

        private final MapCodec<PressMoldMakerRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(CodecFix.ITEM_STACK_CODEC.fieldOf("output").forGetter((recipe) -> {
                return recipe.output;
            }), ExtraCodecs.POSITIVE_INT.fieldOf("clayCount").forGetter((recipe) -> {
                return recipe.clayCount;
            })).apply(instance, PressMoldMakerRecipe::new);
        });

        private final StreamCodec<RegistryFriendlyByteBuf, PressMoldMakerRecipe> STREAM_CODEC = StreamCodec.of(
                Serializer::write, Serializer::read);

        @Override
        public MapCodec<PressMoldMakerRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, PressMoldMakerRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static PressMoldMakerRecipe read(RegistryFriendlyByteBuf buffer) {
            int clayCount = buffer.readInt();
            ItemStack output = ItemStack.OPTIONAL_STREAM_CODEC.decode(buffer);

            return new PressMoldMakerRecipe(output, clayCount);
        }

        private static void write(RegistryFriendlyByteBuf buffer, PressMoldMakerRecipe recipe) {
            buffer.writeInt(recipe.clayCount);
            ItemStack.OPTIONAL_STREAM_CODEC.encode(buffer, recipe.output);
        }
    }
}
