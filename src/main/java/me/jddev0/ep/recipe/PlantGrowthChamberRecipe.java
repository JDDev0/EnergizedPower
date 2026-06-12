package me.jddev0.ep.recipe;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.codec.ArrayCodec;
import me.jddev0.ep.codec.CodecFix;
import me.jddev0.ep.codec.StreamCodecFix;
import me.jddev0.ep.registry.EPRegistries;
import me.jddev0.ep.soil.SoilType;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PlantGrowthChamberRecipe implements Recipe<RecipeInput> {
    private final OutputItemStackWithPercentages[] outputs;
    private final Ingredient input;
    private final Either<List<ResourceKey<SoilType>>, TagKey<SoilType>> soilType;
    private final Fluid[] fluid;
    private final double fluidConsumption;
    private final int ticks;

    public PlantGrowthChamberRecipe(OutputItemStackWithPercentages[] outputs, Ingredient input,
                                    ResourceKey<SoilType> soilType, Fluid[] fluid, double fluidConsumption, int ticks) {
        this(outputs, input, Either.left(Collections.singletonList(soilType)), fluid, fluidConsumption, ticks);
    }

    public PlantGrowthChamberRecipe(OutputItemStackWithPercentages[] outputs, Ingredient input,
                                    ResourceKey<SoilType>[] soilType, Fluid[] fluid, double fluidConsumption, int ticks) {
        this(outputs, input, Either.left(Arrays.asList(soilType)), fluid, fluidConsumption, ticks);
    }

    public PlantGrowthChamberRecipe(OutputItemStackWithPercentages[] outputs, Ingredient input,
                                    List<ResourceKey<SoilType>> soilType, Fluid[] fluid, double fluidConsumption, int ticks) {
        this(outputs, input, Either.left(soilType), fluid, fluidConsumption, ticks);
    }

    public PlantGrowthChamberRecipe(OutputItemStackWithPercentages[] outputs, Ingredient input,
                                    TagKey<SoilType> soilType, Fluid[] fluid, double fluidConsumption, int ticks) {
        this(outputs, input, Either.right(soilType), fluid, fluidConsumption, ticks);
    }

    public PlantGrowthChamberRecipe(OutputItemStackWithPercentages[] outputs, Ingredient input,
                                    Either<List<ResourceKey<SoilType>>, TagKey<SoilType>> soilType, Fluid[] fluid, double fluidConsumption, int ticks) {
        this.outputs = outputs;
        this.input = input;
        this.soilType = soilType;
        this.fluid = fluid;
        this.fluidConsumption = fluidConsumption;
        this.ticks = ticks;
    }

    public OutputItemStackWithPercentages[] getOutputs() {
        return outputs;
    }

    public Ingredient getInput() {
        return input;
    }

    public Either<List<ResourceKey<SoilType>>, TagKey<SoilType>> getSoilType() {
        return soilType;
    }

    public Fluid[] getFluid() {
        return fluid;
    }

    public double getFluidConsumption() {
        return fluidConsumption;
    }

    public int getTicks() {
        return ticks;
    }

    public ItemStack[] getMaxOutputCounts() {
        ItemStack[] generatedOutputs = new ItemStack[outputs.length];
        for(int i = 0;i < outputs.length;i++) {
            OutputItemStackWithPercentages output = outputs[i];
            generatedOutputs[i] = output.output().copyWithCount(output.percentages().length);
        }

        return generatedOutputs;
    }

    public ItemStack[] generateOutputs(RandomSource randomSource) {
        ItemStack[] generatedOutputs = new ItemStack[outputs.length];
        for(int i = 0;i < outputs.length;i++) {
            int count = 0;
            OutputItemStackWithPercentages output = outputs[i];

            for(double percentage:output.percentages())
                if(randomSource.nextDouble() <= percentage)
                    count++;

            generatedOutputs[i] = output.output().copyWithCount(count);
        }

        return generatedOutputs;
    }

    @Override
    public boolean matches(RecipeInput container, Level level) {
        if(level.isClientSide())
            return false;

        return input.test(container.getItem(0));
    }

    @Override
    public ItemStack assemble(RecipeInput container, HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.createWithCapacity(1);
        ingredients.add(0, input);
        return ingredients;
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(EPBlocks.PLANT_GROWTH_CHAMBER_ITEM);
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

    public static final class Type implements RecipeType<PlantGrowthChamberRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "plant_growth_chamber";
    }

    public static final class Serializer implements RecipeSerializer<PlantGrowthChamberRecipe> {
        private Serializer() {}

        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = EPAPI.id("plant_growth_chamber");

        @SuppressWarnings("unchecked")
        private static final MapCodec<PlantGrowthChamberRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(new ArrayCodec<>(OutputItemStackWithPercentages.CODEC_NONEMPTY, OutputItemStackWithPercentages[]::new).
                    fieldOf("outputs").forGetter((recipe) -> {
                        return recipe.outputs;
            }), Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter((recipe) -> {
                return recipe.input;
            }), CodecFix.listOrSingleResourceKeyOrSingleTagKeyCodec(EPRegistries.SOIL_TYPE).
                    fieldOf("soilType").forGetter((recipe) -> {
                return recipe.soilType.mapBoth(
                        soilType -> soilType.size() == 1?Either.right(soilType.getFirst()):Either.left(soilType),
                        soilType -> soilType
                );
            }), CodecFix.arrayOrSingleValueCodec(BuiltInRegistries.FLUID.byNameCodec(), Fluid[]::new).
                    fieldOf("fluid").forGetter((recipe) -> {
                return recipe.fluid.length == 1?Either.right(recipe.fluid[0]):Either.left(recipe.fluid);
            }), CodecFix.POSITIVE_DOUBLE.fieldOf("fluidConsumption").forGetter((recipe) -> {
                return recipe.fluidConsumption;
            }), ExtraCodecs.POSITIVE_INT.fieldOf("ticks").forGetter((recipe) -> {
                return recipe.ticks;
            })).apply(instance, ((result, ingredient,
                                  soilType, fluid,
                                  fluidConsumption, ticks) -> {
                Fluid[] f = fluid.map(fi -> fi, fi -> new Fluid[] {fi});
                Either<List<ResourceKey<SoilType>>, TagKey<SoilType>> st = soilType.mapBoth(
                        sti -> sti.map(stii -> stii, Collections::singletonList),
                        sti -> sti
                );

                return new PlantGrowthChamberRecipe(result, ingredient, st, f, fluidConsumption, ticks);
            }));
        });

        private final StreamCodec<RegistryFriendlyByteBuf, PlantGrowthChamberRecipe> STREAM_CODEC = StreamCodec.of(
                Serializer::write, Serializer::read);

        @Override
        public MapCodec<PlantGrowthChamberRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, PlantGrowthChamberRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static PlantGrowthChamberRecipe read(RegistryFriendlyByteBuf buffer) {
            Ingredient input = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);

            Either<List<ResourceKey<SoilType>>, TagKey<SoilType>> soilType;
            if(buffer.readBoolean()) {
                int soilTypeCount = buffer.readInt();
                List<ResourceKey<SoilType>> soilTypes = new ArrayList<>(soilTypeCount);

                for(int i = 0;i < soilTypeCount;i++)
                    soilTypes.add(SOIL_TYPE_RESOURCE_KEY_STREAM_CODEC.decode(buffer));

                soilType = Either.left(soilTypes);
            }else {
                soilType = Either.right(SOIL_TYPE_TAG_KEY_STREAM_CODEC.decode(buffer));
            }

            int fluidCount = buffer.readInt();
            Fluid[] fluid = new Fluid[fluidCount];
            for(int i = 0;i < fluidCount;i++)
                fluid[i] = BuiltInRegistries.FLUID.get(buffer.readResourceLocation());

            double fluidConsumption = buffer.readDouble();

            int ticks = buffer.readInt();

            int outputCount = buffer.readInt();
            OutputItemStackWithPercentages[] outputs = new OutputItemStackWithPercentages[outputCount];
            for(int i = 0;i < outputCount;i++)
                outputs[i] = OutputItemStackWithPercentages.OPTIONAL_STREAM_CODEC.decode(buffer);

            return new PlantGrowthChamberRecipe(outputs, input, soilType, fluid, fluidConsumption, ticks);
        }

        private static final StreamCodec<ByteBuf, ResourceKey<SoilType>> SOIL_TYPE_RESOURCE_KEY_STREAM_CODEC =
                ResourceKey.streamCodec(EPRegistries.SOIL_TYPE);
        private static final StreamCodec<ByteBuf, TagKey<SoilType>> SOIL_TYPE_TAG_KEY_STREAM_CODEC =
                StreamCodecFix.tagKeyStreamCodec(EPRegistries.SOIL_TYPE);
        private static void write(RegistryFriendlyByteBuf buffer, PlantGrowthChamberRecipe recipe) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.input);

            buffer.writeBoolean(recipe.soilType.left().isPresent());
            recipe.soilType.map(
                    soilTypes -> {
                        buffer.writeInt(soilTypes.size());
                        soilTypes.forEach(soilType ->
                                SOIL_TYPE_RESOURCE_KEY_STREAM_CODEC.encode(buffer, soilType));

                        return null;
                    },
                    soilType -> {
                        SOIL_TYPE_TAG_KEY_STREAM_CODEC.encode(buffer, soilType);

                        return null;
                    }
            );

            buffer.writeInt(recipe.fluid.length);
            for(Fluid fluid:recipe.fluid) {
                ResourceLocation fluidId = BuiltInRegistries.FLUID.getKey(fluid);
                if(fluidId == null || fluidId.equals(ResourceLocation.parse("empty")))
                    throw new IllegalArgumentException("Unregistered fluid '" + fluid + "'");

                buffer.writeResourceLocation(fluidId);
            }

            buffer.writeDouble(recipe.fluidConsumption);

            buffer.writeInt(recipe.ticks);

            buffer.writeInt(recipe.outputs.length);
            for(OutputItemStackWithPercentages output:recipe.outputs)
                OutputItemStackWithPercentages.OPTIONAL_STREAM_CODEC.encode(buffer, output);
        }
    }
}
