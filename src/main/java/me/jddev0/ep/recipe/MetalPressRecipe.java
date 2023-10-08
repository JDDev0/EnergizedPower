package me.jddev0.ep.recipe;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.codec.CodecFix;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class MetalPressRecipe implements Recipe<SimpleContainer> {
    private final ItemStack output;
    private final ItemStack pressMold;
    private final Ingredient input;
    private final int inputCount;

    public MetalPressRecipe(ItemStack output, ItemStack pressMold, Ingredient input) {
        this(output, pressMold, input, 1);
    }

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
    public boolean matches(SimpleContainer container, Level level) {
        if(level.isClientSide)
            return false;

        return input.test(container.getItem(0)) && container.getItem(0).getCount() >= inputCount &&
                ItemStack.isSameItemSameTags(pressMold, container.getItem(1));
    }

    @Override
    public ItemStack assemble(SimpleContainer container, RegistryAccess registryAccess) {
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
        return new ItemStack(ModBlocks.METAL_PRESS_ITEM.get());
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

    public static final class Type implements RecipeType<MetalPressRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "metal_press";
    }

    public static final class Serializer implements RecipeSerializer<MetalPressRecipe> {
        private Serializer() {}

        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation(EnergizedPowerMod.MODID, "metal_press");

        private final Codec<MetalPressRecipe> CODEC_INPUT_COUNT = RecordCodecBuilder.create((instance) -> {
            return instance.group(CodecFix.ITEM_STACK_CODEC.fieldOf("output").forGetter((recipe) -> {
                return recipe.output;
            }), CodecFix.ITEM_STACK_CODEC.fieldOf("pressMold").forGetter((recipe) -> {
                return recipe.pressMold;
            }), Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter((recipe) -> {
                return recipe.input;
            }), ExtraCodecs.POSITIVE_INT.fieldOf("inputCount").forGetter((recipe) -> {
                return recipe.inputCount;
            })).apply(instance, MetalPressRecipe::new);
        });

        private final Codec<MetalPressRecipe> CODEC_INGREDIENT_ONLY = RecordCodecBuilder.create((instance) -> {
            return instance.group(CodecFix.ITEM_STACK_CODEC.fieldOf("output").forGetter((recipe) -> {
                return recipe.output;
            }), CodecFix.ITEM_STACK_CODEC.fieldOf("pressMold").forGetter((recipe) -> {
                return recipe.pressMold;
            }), Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter((recipe) -> {
                return recipe.input;
            })).apply(instance, MetalPressRecipe::new);
        });

        @Override
        public Codec<MetalPressRecipe> codec() {
            return Codec.either(CODEC_INPUT_COUNT, CODEC_INGREDIENT_ONLY).
                    xmap(e -> e.left().orElseGet(() -> e.right().orElseThrow()), Either::left);
        }

        @Override
        public MetalPressRecipe fromNetwork(FriendlyByteBuf buffer) {
            Ingredient input = Ingredient.fromNetwork(buffer);
            int inputCount = buffer.readInt();
            ItemStack pressMold = buffer.readItem();
            ItemStack output = buffer.readItem();

            return new MetalPressRecipe(output, pressMold, input, inputCount);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, MetalPressRecipe recipe) {
            recipe.input.toNetwork(buffer);
            buffer.writeInt(recipe.inputCount);
            buffer.writeItemStack(recipe.pressMold, false);
            buffer.writeItemStack(recipe.output, false);
        }
    }
}
