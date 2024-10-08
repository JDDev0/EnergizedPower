package me.jddev0.ep.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class CrystalGrowthChamberRecipe implements Recipe<RecipeInput> {
    private final OutputItemStackWithPercentages output;
    private final Ingredient input;
    private final int inputCount;
    private final int ticks;

    public CrystalGrowthChamberRecipe(OutputItemStackWithPercentages output, Ingredient input, int inputCount, int ticks) {
        this.output = output;
        this.input = input;
        this.inputCount = inputCount;
        this.ticks = ticks;
    }

    public OutputItemStackWithPercentages getOutput() {
        return output;
    }

    public Ingredient getInput() {
        return input;
    }

    public int getInputCount() {
        return inputCount;
    }

    public int getTicks() {
        return ticks;
    }

    public ItemStack getMaxOutputCount() {
        return output.output().copyWithCount(output.percentages().length);
    }

    public ItemStack generateOutput(Random randomSource) {
        int count = 0;

        for(double percentage:output.percentages())
            if(randomSource.nextDouble() <= percentage)
                count++;

        return output.output().copyWithCount(count);
    }

    @Override
    public boolean matches(RecipeInput container, World level) {
        if(level.isClient())
            return false;

        return input.test(container.getStackInSlot(0)) && container.getStackInSlot(0).getCount() >= inputCount;
    }

    @Override
    public ItemStack craft(RecipeInput container, RegistryWrapper.WrapperLookup registries) {
        return ItemStack.EMPTY;
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
        return new ItemStack(EPBlocks.CRYSTAL_GROWTH_CHAMBER_ITEM);
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

    public static final class Type implements RecipeType<CrystalGrowthChamberRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "crystal_growth_chamber";
    }

    public static final class Serializer implements RecipeSerializer<CrystalGrowthChamberRecipe> {
        private Serializer() {}

        public static final Serializer INSTANCE = new Serializer();
        public static final Identifier ID = EPAPI.id("crystal_growth_chamber");

        private final MapCodec<CrystalGrowthChamberRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(OutputItemStackWithPercentages.CODEC_NONEMPTY.fieldOf("output").forGetter((recipe) -> {
                return recipe.output;
            }), Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("ingredient").forGetter((recipe) -> {
                return recipe.input;
            }), Codecs.POSITIVE_INT.optionalFieldOf("inputCount", 1).forGetter((recipe) -> {
                return recipe.inputCount;
            }), Codecs.POSITIVE_INT.fieldOf("ticks").forGetter((recipe) -> {
                return recipe.ticks;
            })).apply(instance, CrystalGrowthChamberRecipe::new);
        });

        private final PacketCodec<RegistryByteBuf, CrystalGrowthChamberRecipe> PACKET_CODEC = PacketCodec.ofStatic(
                Serializer::write, Serializer::read);

        @Override
        public MapCodec<CrystalGrowthChamberRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, CrystalGrowthChamberRecipe> packetCodec() {
            return PACKET_CODEC;
        }

        private static CrystalGrowthChamberRecipe read(RegistryByteBuf buffer) {
            Ingredient input = Ingredient.PACKET_CODEC.decode(buffer);
            int inputCount = buffer.readInt();
            int ticks = buffer.readInt();

            OutputItemStackWithPercentages output = OutputItemStackWithPercentages.OPTIONAL_STREAM_CODEC.decode(buffer);

            return new CrystalGrowthChamberRecipe(output, input, inputCount, ticks);
        }

        private static void write(RegistryByteBuf buffer, CrystalGrowthChamberRecipe recipe) {
            Ingredient.PACKET_CODEC.encode(buffer, recipe.input);
            buffer.writeInt(recipe.inputCount);
            buffer.writeInt(recipe.ticks);

            OutputItemStackWithPercentages.OPTIONAL_STREAM_CODEC.encode(buffer, recipe.output);
        }
    }
}
