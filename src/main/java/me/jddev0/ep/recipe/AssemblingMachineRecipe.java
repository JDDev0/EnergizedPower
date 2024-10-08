package me.jddev0.ep.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.codec.ArrayCodec;
import me.jddev0.ep.codec.CodecFix;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class AssemblingMachineRecipe implements Recipe<RecipeInput> {
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
    public boolean matches(RecipeInput container, Level level) {
        if(level.isClientSide)
            return false;

        boolean[] usedIndices = new boolean[4];
        for(int i = 0;i < 4;i++)
            usedIndices[i] = container.getItem(i).isEmpty();

        int len = Math.min(inputs.length, 4);
        for(int i = 0;i < len;i++) {
            IngredientWithCount input = inputs[i];

            int indexMinCount = -1;
            int minCount = Integer.MAX_VALUE;

            for(int j = 0;j < 4;j++) {
                if(usedIndices[j])
                    continue;

                ItemStack item = container.getItem(j);

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
        return new ItemStack(EPBlocks.ASSEMBLING_MACHINE_ITEM.get());
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

    public static final class Type implements RecipeType<AssemblingMachineRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "assembling_machine";
    }

    public static final class Serializer implements RecipeSerializer<AssemblingMachineRecipe> {
        private Serializer() {}

        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = EPAPI.id("assembling_machine");

        private final MapCodec<AssemblingMachineRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(CodecFix.ITEM_STACK_CODEC.fieldOf("output").forGetter((recipe) -> {
                return recipe.output;
            }), new ArrayCodec<>(IngredientWithCount.CODEC_NONEMPTY, IngredientWithCount[]::new).fieldOf("inputs").forGetter((recipe) -> {
                return recipe.inputs;
            })).apply(instance, AssemblingMachineRecipe::new);
        });

        private final StreamCodec<RegistryFriendlyByteBuf, AssemblingMachineRecipe> STREAM_CODEC = StreamCodec.of(
                Serializer::write, Serializer::read);

        @Override
        public MapCodec<AssemblingMachineRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, AssemblingMachineRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static AssemblingMachineRecipe read(RegistryFriendlyByteBuf buffer) {
            int len = buffer.readInt();
            IngredientWithCount[] inputs = new IngredientWithCount[len];
            for(int i = 0;i < len;i++)
                inputs[i] = IngredientWithCount.OPTIONAL_STREAM_CODEC.decode(buffer);

            ItemStack output = ItemStack.OPTIONAL_STREAM_CODEC.decode(buffer);

            return new AssemblingMachineRecipe(output, inputs);
        }

        private static void write(RegistryFriendlyByteBuf buffer, AssemblingMachineRecipe recipe) {
            buffer.writeInt(recipe.inputs.length);
            for(int i = 0; i < recipe.inputs.length; i++)
                IngredientWithCount.OPTIONAL_STREAM_CODEC.encode(buffer, recipe.inputs[i]);

            ItemStack.OPTIONAL_STREAM_CODEC.encode(buffer, recipe.output);
        }
    }
}
