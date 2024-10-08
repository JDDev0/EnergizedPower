package me.jddev0.ep.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.codec.ArrayCodec;
import me.jddev0.ep.codec.CodecFix;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.Optional;

public class AlloyFurnaceRecipe implements Recipe<RecipeInput> {
    private final ItemStack output;
    private final OutputItemStackWithPercentages secondaryOutput;
    private final IngredientWithCount[] inputs;
    private final int ticks;

    public AlloyFurnaceRecipe(ItemStack output, OutputItemStackWithPercentages secondaryOutput,
                              IngredientWithCount[] inputs, int ticks) {
        this.output = output;
        this.secondaryOutput = secondaryOutput;
        this.inputs = inputs;
        this.ticks = ticks;
    }

    public ItemStack getOutput() {
        return output;
    }

    public OutputItemStackWithPercentages getSecondaryOutput() {
        return secondaryOutput;
    }

    public IngredientWithCount[] getInputs() {
        return inputs;
    }

    public int getTicks() {
        return ticks;
    }

    public ItemStack[] getMaxOutputCounts() {
        ItemStack[] generatedOutputs = new ItemStack[2];

        generatedOutputs[0] = output.copyWithCount(output.getCount());
        generatedOutputs[1] = secondaryOutput.output().copyWithCount(secondaryOutput.percentages().length);

        return generatedOutputs;
    }

    public ItemStack[] generateOutputs(Random randomSource) {
        ItemStack[] generatedOutputs = new ItemStack[2];

        generatedOutputs[0] = output.copyWithCount(output.getCount());

        int count = 0;
        for(double percentage:secondaryOutput.percentages())
            if(randomSource.nextDouble() <= percentage)
                count++;

        generatedOutputs[1] = secondaryOutput.output().copyWithCount(count);

        return generatedOutputs;
    }

    @Override
    public boolean matches(RecipeInput container, World level) {
        if(level.isClient)
            return false;

        boolean[] usedIndices = new boolean[3];
        for(int i = 0;i < 3;i++)
            usedIndices[i] = container.getStackInSlot(i).isEmpty();

        int len = Math.min(inputs.length, 3);
        for(int i = 0;i < len;i++) {
            IngredientWithCount input = inputs[i];

            int indexMinCount = -1;
            int minCount = Integer.MAX_VALUE;

            for(int j = 0;j < 3;j++) {
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
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(EPBlocks.ALLOY_FURNACE_ITEM);
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

    public static final class Type implements RecipeType<AlloyFurnaceRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "alloy_furnace";
    }

    public static final class Serializer implements RecipeSerializer<AlloyFurnaceRecipe> {
        private Serializer() {}

        public static final Serializer INSTANCE = new Serializer();
        public static final Identifier ID = EPAPI.id("alloy_furnace");

        private final MapCodec<AlloyFurnaceRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(CodecFix.ITEM_STACK_CODEC.fieldOf("output").forGetter((recipe) -> {
                return recipe.output;
            }), OutputItemStackWithPercentages.CODEC_NONEMPTY.optionalFieldOf("secondaryOutput").forGetter((recipe) -> {
                return Optional.ofNullable(recipe.secondaryOutput.isEmpty()?null:recipe.secondaryOutput);
            }), new ArrayCodec<>(IngredientWithCount.CODEC_NONEMPTY, IngredientWithCount[]::new).fieldOf("inputs").forGetter((recipe) -> {
                return recipe.inputs;
            }), Codecs.POSITIVE_INT.fieldOf("ticks").forGetter((recipe) -> {
                return recipe.ticks;
            })).apply(instance, (output, secondaryOutput, inputs, ticks) -> new AlloyFurnaceRecipe(output,
                    secondaryOutput.orElse(OutputItemStackWithPercentages.EMPTY), inputs, ticks));
        });

        private final PacketCodec<RegistryByteBuf, AlloyFurnaceRecipe> PACKET_CODEC = PacketCodec.ofStatic(
                Serializer::write, Serializer::read);

        @Override
        public MapCodec<AlloyFurnaceRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, AlloyFurnaceRecipe> packetCodec() {
            return PACKET_CODEC;
        }

        private static AlloyFurnaceRecipe read(RegistryByteBuf buffer) {
            int len = buffer.readInt();
            IngredientWithCount[] inputs = new IngredientWithCount[len];
            for(int i = 0;i < len;i++)
                inputs[i] = IngredientWithCount.OPTIONAL_STREAM_CODEC.decode(buffer);

            int ticks = buffer.readInt();

            ItemStack output = ItemStack.OPTIONAL_PACKET_CODEC.decode(buffer);

            OutputItemStackWithPercentages secondaryOutput = OutputItemStackWithPercentages.OPTIONAL_STREAM_CODEC.decode(buffer);

            return new AlloyFurnaceRecipe(output, secondaryOutput, inputs, ticks);
        }

        private static void write(RegistryByteBuf buffer, AlloyFurnaceRecipe recipe) {
            buffer.writeInt(recipe.inputs.length);
            for(int i = 0; i < recipe.inputs.length; i++)
                IngredientWithCount.OPTIONAL_STREAM_CODEC.encode(buffer, recipe.inputs[i]);

            buffer.writeInt(recipe.ticks);

            ItemStack.OPTIONAL_PACKET_CODEC.encode(buffer, recipe.output);

            OutputItemStackWithPercentages.OPTIONAL_STREAM_CODEC.encode(buffer, recipe.secondaryOutput);
        }
    }
}
