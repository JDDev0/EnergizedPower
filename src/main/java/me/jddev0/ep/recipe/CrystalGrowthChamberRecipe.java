package me.jddev0.ep.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class CrystalGrowthChamberRecipe implements Recipe<Container> {
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

    public ItemStack generateOutput(RandomSource randomSource) {
        int count = 0;

        for(double percentage:output.percentages())
            if(randomSource.nextDouble() <= percentage)
                count++;

        return output.output().copyWithCount(count);
    }

    @Override
    public boolean matches(Container container, Level level) {
        if(level.isClientSide)
            return false;

        return input.test(container.getItem(0)) && container.getItem(0).getCount() >= inputCount;
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(EPBlocks.CRYSTAL_GROWTH_CHAMBER_ITEM.get());
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

    public static final class Type implements RecipeType<CrystalGrowthChamberRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "crystal_growth_chamber";
    }

    public static final class Serializer implements RecipeSerializer<CrystalGrowthChamberRecipe> {
        private Serializer() {}

        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = EPAPI.id("crystal_growth_chamber");

        private final Codec<CrystalGrowthChamberRecipe> CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(OutputItemStackWithPercentages.CODEC_NONEMPTY.fieldOf("output").forGetter((recipe) -> {
                return recipe.output;
            }), Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter((recipe) -> {
                return recipe.input;
            }), ExtraCodecs.POSITIVE_INT.optionalFieldOf("inputCount", 1).forGetter((recipe) -> {
                return recipe.inputCount;
            }), ExtraCodecs.POSITIVE_INT.fieldOf("ticks").forGetter((recipe) -> {
                return recipe.ticks;
            })).apply(instance, CrystalGrowthChamberRecipe::new);
        });

        @Override
        public Codec<CrystalGrowthChamberRecipe> codec() {
            return CODEC;
        }

        @Override
        public CrystalGrowthChamberRecipe fromNetwork(FriendlyByteBuf buffer) {
            Ingredient input = Ingredient.fromNetwork(buffer);
            int inputCount = buffer.readInt();
            int ticks = buffer.readInt();

            OutputItemStackWithPercentages output = OutputItemStackWithPercentages.fromNetwork(buffer);

            return new CrystalGrowthChamberRecipe(output, input, inputCount, ticks);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, CrystalGrowthChamberRecipe recipe) {
            recipe.input.toNetwork(buffer);
            buffer.writeInt(recipe.inputCount);
            buffer.writeInt(recipe.ticks);

            recipe.output.toNetwork(buffer);
        }
    }
}
