package me.jddev0.ep.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.codec.ArrayCodec;
import me.jddev0.ep.codec.CodecFix;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;

public class AssemblingMachineRecipe implements EnergizedPowerBaseRecipe<RecipeInput> {
    private final ItemStack output;
    private final IngredientWithCount[] inputs;

    public AssemblingMachineRecipe(ItemStack output, IngredientWithCount[] inputs) {
        this.output = output;
        this.inputs = inputs;
    }

    public ItemStack getOutput() {
        return output;
    }

    public IngredientWithCount[] getInputs() {
        return inputs;
    }

    @Override
    public boolean matches(RecipeInput container, World level) {
        if(level.isClient())
            return false;

        boolean[] usedIndices = new boolean[4];
        for(int i = 0;i < 4;i++)
            usedIndices[i] = container.getStackInSlot(i).isEmpty();

        int len = Math.min(inputs.length, 4);
        for(int i = 0;i < len;i++) {
            IngredientWithCount input = inputs[i];

            int indexMinCount = -1;
            int minCount = Integer.MAX_VALUE;

            for(int j = 0;j < 4;j++) {
                if(usedIndices[j])
                    continue;

                ItemStack item = container.getStackInSlot(j);

                if((indexMinCount == -1 || item.getCount() < minCount) && input.input().test(item) &&
                        item.getCount() >= input.count()) {
                    indexMinCount = j;
                    minCount = item.getCount();
                }
            }

            if(indexMinCount == -1)
                return false; //Ingredient did not match any item

            usedIndices[indexMinCount] = true;
        }

        for(boolean usedIndex:usedIndices)
            if(!usedIndex) //Unused items present
                return false;

        return true;
    }


    @Override
    public ItemStack craft(RecipeInput container, RegistryWrapper.WrapperLookup registries) {
        return output;
    }

    @Override
    public IngredientPlacement getIngredientPlacement() {
        return IngredientPlacement.NONE;
    }

    @Override
    public boolean isIgnoredInRecipeBook() {
        return true;
    }

    @Override
    public RecipeBookCategory getRecipeBookCategory() {
        return EPRecipes.ASSEMBLING_MACHINE_CATEGORY;
    }

    @Override
    public RecipeSerializer<? extends Recipe<RecipeInput>> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<? extends Recipe<RecipeInput>> getType() {
        return Type.INSTANCE;
    }

    @Override
    public List<Ingredient> getIngredients() {
        return Arrays.stream(inputs).map(IngredientWithCount::input).toList();
    }

    @Override
    public boolean isIngredient(ItemStack itemStack) {
        return Arrays.stream(inputs).map(IngredientWithCount::input).anyMatch(ingredient -> ingredient.test(itemStack));
    }

    @Override
    public boolean isResult(ItemStack itemStack) {
        return ItemStack.areItemsAndComponentsEqual(output, itemStack);
    }

    public static final class Type implements RecipeType<AssemblingMachineRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "assembling_machine";
    }

    public static final class Serializer implements RecipeSerializer<AssemblingMachineRecipe> {
        private Serializer() {}

        public static final Serializer INSTANCE = new Serializer();
        public static final Identifier ID = EPAPI.id("assembling_machine");

        private final MapCodec<AssemblingMachineRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(CodecFix.ITEM_STACK_CODEC.fieldOf("result").forGetter((recipe) -> {
                return recipe.output;
            }), new ArrayCodec<>(IngredientWithCount.CODEC, IngredientWithCount[]::new).fieldOf("ingredients").forGetter((recipe) -> {
                return recipe.inputs;
            })).apply(instance, AssemblingMachineRecipe::new);
        });

        private final PacketCodec<RegistryByteBuf, AssemblingMachineRecipe> PACKET_CODEC = PacketCodec.ofStatic(
                Serializer::write, Serializer::read);

        @Override
        public MapCodec<AssemblingMachineRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, AssemblingMachineRecipe> packetCodec() {
            return PACKET_CODEC;
        }

        private static AssemblingMachineRecipe read(RegistryByteBuf buffer) {
            int len = buffer.readInt();
            IngredientWithCount[] inputs = new IngredientWithCount[len];
            for(int i = 0;i < len;i++)
                inputs[i] = IngredientWithCount.STREAM_CODEC.decode(buffer);

            ItemStack output = ItemStack.OPTIONAL_PACKET_CODEC.decode(buffer);

            return new AssemblingMachineRecipe(output, inputs);
        }

        private static void write(RegistryByteBuf buffer, AssemblingMachineRecipe recipe) {
            buffer.writeInt(recipe.inputs.length);
            for(int i = 0; i < recipe.inputs.length; i++)
                IngredientWithCount.STREAM_CODEC.encode(buffer, recipe.inputs[i]);

            ItemStack.OPTIONAL_PACKET_CODEC.encode(buffer, recipe.output);
        }
    }
}
