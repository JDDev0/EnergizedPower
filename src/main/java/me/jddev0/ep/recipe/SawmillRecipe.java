package me.jddev0.ep.recipe;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.codec.CodecFix;
import me.jddev0.ep.item.ModItems;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class SawmillRecipe implements Recipe<SimpleContainer> {
    private final ItemStack output;
    private final ItemStack secondaryOutput;
    private final Ingredient input;

    public SawmillRecipe(ItemStack output, Ingredient input, int sawdustAmount) {
        this(output, new ItemStack(ModItems.SAWDUST.get(), sawdustAmount), input);
    }

    public SawmillRecipe(ItemStack output, ItemStack secondaryOutput, Ingredient input) {
        this.output = output;
        this.input = input;
        this.secondaryOutput = secondaryOutput;
    }

    public ItemStack getOutput() {
        return output;
    }

    public ItemStack getSecondaryOutput() {
        return secondaryOutput;
    }

    public Ingredient getInput() {
        return input;
    }

    @Override
    public boolean matches(SimpleContainer container, Level level) {
        if(level.isClientSide)
            return false;

        return input.test(container.getItem(0));
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
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.createWithCapacity(1);
        ingredients.add(0, input);
        return ingredients;
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(ModBlocks.SAWMILL_ITEM.get());
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

    public static final class Type implements RecipeType<SawmillRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "sawmill";
    }

    public static final class Serializer implements RecipeSerializer<SawmillRecipe> {
        private Serializer() {}

        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation(EnergizedPowerMod.MODID, "sawmill");

        private final Codec<SawmillRecipe> CODEC_ITEM_STACK = RecordCodecBuilder.create((instance) -> {
            return instance.group(CodecFix.ITEM_STACK_CODEC.fieldOf("output").forGetter((recipe) -> {
                return recipe.output;
            }), CodecFix.ITEM_STACK_CODEC.fieldOf("secondaryOutput").forGetter((recipe) -> {
                return recipe.secondaryOutput;
            }), Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter((recipe) -> {
                return recipe.input;
            })).apply(instance, SawmillRecipe::new);
        });

        private final Codec<SawmillRecipe> CODEC_SAWDUST_AMOUNT = RecordCodecBuilder.create((instance) -> {
            return instance.group(CodecFix.ITEM_STACK_CODEC.fieldOf("output").forGetter((recipe) -> {
                return recipe.output;
            }), Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter((recipe) -> {
                return recipe.input;
            }), Codec.INT.fieldOf("sawdustAmount").forGetter((recipe) -> {
                return recipe.secondaryOutput.getCount();
            })).apply(instance, SawmillRecipe::new);
        });

        @Override
        public Codec<SawmillRecipe> codec() {
            return Codec.either(CODEC_ITEM_STACK, CODEC_SAWDUST_AMOUNT).
                    xmap(e -> e.left().orElseGet(() -> e.right().orElseThrow()), Either::left);
        }

        @Override
        public SawmillRecipe fromNetwork(FriendlyByteBuf buffer) {
            Ingredient input = Ingredient.fromNetwork(buffer);
            ItemStack output = buffer.readItem();
            ItemStack secondaryOutput = buffer.readItem();

            return new SawmillRecipe(output, secondaryOutput, input);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, SawmillRecipe recipe) {
            recipe.input.toNetwork(buffer);
            buffer.writeItemStack(recipe.output, false);
            buffer.writeItemStack(recipe.secondaryOutput, false);
        }
    }
}
