package me.jddev0.ep.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.codec.CodecFix;
import me.jddev0.ep.item.ModItems;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.World;

import java.util.Optional;

public class SawmillRecipe implements Recipe<Inventory> {
    private final ItemStack output;
    private final ItemStack secondaryOutput;
    private final Ingredient input;

    public SawmillRecipe(ItemStack output, Ingredient input, int sawdustAmount) {
        this(output, new ItemStack(ModItems.SAWDUST, sawdustAmount), input);
    }

    public SawmillRecipe(ItemStack output, ItemStack secondaryOutput, Ingredient input) {
        this.output = output;
        this.secondaryOutput = secondaryOutput;
        this.input = input;
    }

    public ItemStack getOutputItem() {
        return output;
    }

    public ItemStack getSecondaryOutput() {
        return secondaryOutput;
    }

    public Ingredient getInputItem() {
        return input;
    }

    @Override
    public boolean matches(Inventory container, World level) {
        if(level.isClient())
            return false;

        return input.test(container.getStack(0));
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
    public ItemStack getResult(DynamicRegistryManager registryManage) {
        return output.copy();
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        DefaultedList<Ingredient> ingredients = DefaultedList.ofSize(1);
        ingredients.add(0, input);
        return ingredients;
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(ModBlocks.SAWMILL_ITEM);
    }

    @Override
    public boolean isIgnoredInRecipeBook() {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SawmillRecipe.Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return SawmillRecipe.Type.INSTANCE;
    }

    public static final class Type implements RecipeType<SawmillRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "sawmill";
    }

    public static final class Serializer implements RecipeSerializer<SawmillRecipe> {
        private Serializer() {}

        public static final Serializer INSTANCE = new Serializer();
        public static final Identifier ID = EPAPI.id("sawmill");

        private final Codec<SawmillRecipe> CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(CodecFix.ITEM_STACK_CODEC.fieldOf("output").forGetter((recipe) -> {
                return recipe.output;
            }), Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("ingredient").forGetter((recipe) -> {
                return recipe.input;
            }), Codecs.NONNEGATIVE_INT.optionalFieldOf("sawdustAmount").forGetter((recipe) -> {
                if(recipe.secondaryOutput.isEmpty())
                    return Optional.of(0);

                return ItemStack.canCombine(recipe.secondaryOutput, new ItemStack(ModItems.SAWDUST))?
                        Optional.of(recipe.secondaryOutput.getCount()):Optional.empty();
            }), CodecFix.ITEM_STACK_CODEC.optionalFieldOf("secondaryOutput").forGetter((recipe) -> {
                if(recipe.secondaryOutput.isEmpty())
                    return Optional.empty();

                return ItemStack.canCombine(recipe.secondaryOutput, new ItemStack(ModItems.SAWDUST))?
                        Optional.empty():Optional.of(recipe.secondaryOutput);
            })).apply(instance, (output, ingredient, sawdustAmount, secondaryOutput) -> {
                return secondaryOutput.map(o -> new SawmillRecipe(output, o, ingredient)).
                        orElseGet(() -> sawdustAmount.map(a -> new SawmillRecipe(output, ingredient, a)).
                                orElseThrow(() -> new IllegalArgumentException("Either \"sawdustAmount\" or \"secondaryOutput\" must be present")));
            });
        });

        @Override
        public Codec<SawmillRecipe> codec() {
            return CODEC;
        }

        @Override
        public SawmillRecipe read(PacketByteBuf buffer) {
            Ingredient input = Ingredient.fromPacket(buffer);
            ItemStack output = buffer.readItemStack();
            ItemStack secondaryOutput = buffer.readItemStack();

            return new SawmillRecipe(output, secondaryOutput, input);
        }

        @Override
        public void write(PacketByteBuf buffer, SawmillRecipe recipe) {
            recipe.input.write(buffer);
            buffer.writeItemStack(recipe.output);
            buffer.writeItemStack(recipe.secondaryOutput);
        }
    }
}
