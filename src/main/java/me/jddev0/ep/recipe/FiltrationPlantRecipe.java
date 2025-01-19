package me.jddev0.ep.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.api.EPAPI;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;

public class FiltrationPlantRecipe implements EnergizedPowerBaseRecipe<RecipeInput> {
    private final OutputItemStackWithPercentages output;
    private final OutputItemStackWithPercentages secondaryOutput;
    private final Identifier icon;

    public FiltrationPlantRecipe(OutputItemStackWithPercentages output, OutputItemStackWithPercentages secondaryOutput, Identifier icon) {
        this.output = output;
        this.secondaryOutput = secondaryOutput;
        this.icon = icon;
    }

    public OutputItemStackWithPercentages getOutput() {
        return output;
    }

    public OutputItemStackWithPercentages getSecondaryOutput() {
        return secondaryOutput;
    }

    public Identifier getIcon() {
        return icon;
    }

    public ItemStack[] getMaxOutputCounts() {
        ItemStack[] generatedOutputs = new ItemStack[2];

        generatedOutputs[0] = output.output().copyWithCount(output.percentages().length);
        generatedOutputs[1] = secondaryOutput.output().copyWithCount(secondaryOutput.percentages().length);

        return generatedOutputs;
    }

    public ItemStack[] generateOutputs(Random randomSource) {
        ItemStack[] generatedOutputs = new ItemStack[2];
        for(int i = 0;i < 2;i++) {
            int count = 0;
            OutputItemStackWithPercentages output = i == 0?this.output:this.secondaryOutput;

            for(double percentage:output.percentages())
                if(randomSource.nextDouble() <= percentage)
                    count++;

            generatedOutputs[i] = output.output().copyWithCount(count);
        }

        return generatedOutputs;
    }

    @Override
    public boolean matches(RecipeInput container, World level) {
        return false;
    }

    @Override
    public ItemStack craft(RecipeInput container, RegistryWrapper.WrapperLookup registries) {
        return ItemStack.EMPTY;
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
        return EPRecipes.FILTRATION_PLANT_CATEGORY;
    }

    @Override
    public RecipeSerializer<? extends Recipe<RecipeInput>> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public List<Ingredient> getIngredients() {
        return List.of();
    }

    @Override
    public boolean isIngredient(ItemStack itemStack) {
        return false;
    }

    @Override
    public boolean isResult(ItemStack itemStack) {
        return ItemStack.areItemsAndComponentsEqual(output.output(), itemStack) || (secondaryOutput != null &&
                ItemStack.areItemsAndComponentsEqual(secondaryOutput.output(), itemStack));
    }

    @Override
    public RecipeType<? extends Recipe<RecipeInput>> getType() {
        return Type.INSTANCE;
    }

    public static final class Type implements RecipeType<FiltrationPlantRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "filtration_plant";
    }

    public static final class Serializer implements RecipeSerializer<FiltrationPlantRecipe> {
        private Serializer() {}

        public static final Serializer INSTANCE = new Serializer();
        public static final Identifier ID = EPAPI.id("filtration_plant");

        private final MapCodec<FiltrationPlantRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(OutputItemStackWithPercentages.CODEC_NONEMPTY.fieldOf("result").forGetter((recipe) -> {
                return recipe.output;
            }), OutputItemStackWithPercentages.CODEC_NONEMPTY.optionalFieldOf("secondaryResult").forGetter((recipe) -> {
                return Optional.ofNullable(recipe.secondaryOutput.isEmpty()?null:recipe.secondaryOutput);
            }), Identifier.CODEC.fieldOf("icon").forGetter((recipe) -> {
                return recipe.icon;
            })).apply(instance, (output, secondaryOutput, icon) -> new FiltrationPlantRecipe(output,
                    secondaryOutput.orElse(OutputItemStackWithPercentages.EMPTY), icon));
        });

        private final PacketCodec<RegistryByteBuf, FiltrationPlantRecipe> PACKET_CODEC = PacketCodec.ofStatic(
                Serializer::write, Serializer::read);

        @Override
        public MapCodec<FiltrationPlantRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, FiltrationPlantRecipe> packetCodec() {
            return PACKET_CODEC;
        }

        private static FiltrationPlantRecipe read(RegistryByteBuf buffer) {
            OutputItemStackWithPercentages[] outputs = new OutputItemStackWithPercentages[2];
            for(int i = 0;i < 2;i++)
                outputs[i] = OutputItemStackWithPercentages.OPTIONAL_STREAM_CODEC.decode(buffer);

            Identifier icon = buffer.readIdentifier();

            return new FiltrationPlantRecipe(outputs[0], outputs[1], icon);
        }

        private static void write(RegistryByteBuf buffer, FiltrationPlantRecipe recipe) {
            for(int i = 0;i < 2;i++) {
                OutputItemStackWithPercentages output = i == 0?recipe.output:recipe.secondaryOutput;
                OutputItemStackWithPercentages.OPTIONAL_STREAM_CODEC.encode(buffer, output);
            }

            buffer.writeIdentifier(recipe.icon);
        }
    }
}
