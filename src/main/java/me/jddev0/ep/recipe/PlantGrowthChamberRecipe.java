package me.jddev0.ep.recipe;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.codec.ArrayCodec;
import me.jddev0.ep.codec.CodecFix;
import me.jddev0.ep.registry.EPRegistries;
import me.jddev0.ep.soil.SoilType;
import me.jddev0.ep.util.ItemStackUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;

import java.util.*;

public class PlantGrowthChamberRecipe implements EnergizedPowerBaseRecipe<RecipeInput> {
    private final OutputItemStackTemplateWithPercentages[] outputs;
    private final Ingredient input;
    private final Either<List<ResourceKey<SoilType>>, TagKey<SoilType>> soilType;
    private final Fluid[] fluid;
    private final double fluidConsumption;
    private final int ticks;

    public PlantGrowthChamberRecipe(OutputItemStackTemplateWithPercentages[] outputs, Ingredient input,
                                    ResourceKey<SoilType> soilType, Fluid[] fluid, double fluidConsumption, int ticks) {
        this(outputs, input, Either.left(Collections.singletonList(soilType)), fluid, fluidConsumption, ticks);
    }

    public PlantGrowthChamberRecipe(OutputItemStackTemplateWithPercentages[] outputs, Ingredient input,
                                    ResourceKey<SoilType>[] soilType, Fluid[] fluid, double fluidConsumption, int ticks) {
        this(outputs, input, Either.left(Arrays.asList(soilType)), fluid, fluidConsumption, ticks);
    }

    public PlantGrowthChamberRecipe(OutputItemStackTemplateWithPercentages[] outputs, Ingredient input,
                                    List<ResourceKey<SoilType>> soilType, Fluid[] fluid, double fluidConsumption, int ticks) {
        this(outputs, input, Either.left(soilType), fluid, fluidConsumption, ticks);
    }

    public PlantGrowthChamberRecipe(OutputItemStackTemplateWithPercentages[] outputs, Ingredient input,
                                    TagKey<SoilType> soilType, Fluid[] fluid, double fluidConsumption, int ticks) {
        this(outputs, input, Either.right(soilType), fluid, fluidConsumption, ticks);
    }

    public PlantGrowthChamberRecipe(OutputItemStackTemplateWithPercentages[] outputs, Ingredient input,
                                    Either<List<ResourceKey<SoilType>>, TagKey<SoilType>> soilType, Fluid[] fluid, double fluidConsumption, int ticks) {
        this.outputs = outputs;
        this.input = input;
        this.soilType = soilType;
        this.fluid = fluid;
        this.fluidConsumption = fluidConsumption;
        this.ticks = ticks;
    }

    public OutputItemStackTemplateWithPercentages[] getOutputs() {
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
            OutputItemStackTemplateWithPercentages output = outputs[i];

            ItemStack outputItemStack = ItemStackUtils.fromNullableItemStackTemplate(output.output());

            generatedOutputs[i] = outputItemStack.copyWithCount(output.percentages().length);
        }

        return generatedOutputs;
    }

    public ItemStack[] generateOutputs(RandomSource randomSource) {
        ItemStack[] generatedOutputs = new ItemStack[outputs.length];
        for(int i = 0;i < outputs.length;i++) {
            int count = 0;
            OutputItemStackTemplateWithPercentages output = outputs[i];

            for(double percentage:output.percentages())
                if(randomSource.nextDouble() <= percentage)
                    count++;

            ItemStack outputItemStack = ItemStackUtils.fromNullableItemStackTemplate(output.output());

            generatedOutputs[i] = outputItemStack.copyWithCount(count);
        }

        return generatedOutputs;
    }

    @Override
    public boolean matches(RecipeInput container, Level level) {
        if(level.isClientSide() || !(level instanceof ServerLevel serverLevel))
            return false;

        Optional<RecipeHolder<PlantGrowthChamberSoilRecipe>> soilRecipe = serverLevel.recipeAccess().
                getRecipeFor(EPRecipes.PLANT_GROWTH_CHAMBER_SOIL_TYPE, container, level);
        if(soilRecipe.isEmpty())
            return false;

        ResourceKey<SoilType> soilType = soilRecipe.get().value().getSoilType();

        return input.test(container.getItem(0)) && this.soilType.map(
                st -> st.stream().anyMatch(sti -> sti.identifier().equals(soilType.identifier())),
                st -> level.registryAccess().lookupOrThrow(EPRegistries.SOIL_TYPE).getOrThrow(soilType).is(st)
        );
    }

    @Override
    public ItemStack assemble(RecipeInput container) {
        return ItemStack.EMPTY;
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return EPRecipes.PLANT_GROWTH_CHAMBER_CATEGORY;
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
        return List.of(input);
    }

    @Override
    public boolean isIngredient(ItemStack itemStack) {
        return input.test(itemStack);
    }

    @Override
    public boolean isResult(ItemStack itemStack) {
        return Arrays.stream(outputs).map(OutputItemStackTemplateWithPercentages::output).
                anyMatch(output -> {
                    ItemStack outputItemStack = ItemStackUtils.fromNullableItemStackTemplate(output);

                    return ItemStack.isSameItemSameComponents(outputItemStack, itemStack);
                });
    }

    public static final class Type implements RecipeType<PlantGrowthChamberRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "plant_growth_chamber";
    }

    public static final class Serializer {
        private Serializer() {}

        @SuppressWarnings("unchecked")
        private static final MapCodec<PlantGrowthChamberRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(new ArrayCodec<>(OutputItemStackTemplateWithPercentages.CODEC_NONEMPTY, OutputItemStackTemplateWithPercentages[]::new).
                    fieldOf("results").forGetter((recipe) -> {
                return recipe.outputs;
            }), Ingredient.CODEC.fieldOf("ingredient").forGetter((recipe) -> {
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

        private static final StreamCodec<RegistryFriendlyByteBuf, PlantGrowthChamberRecipe> STREAM_CODEC = StreamCodec.of(
                Serializer::write, Serializer::read);

        public static final RecipeSerializer<PlantGrowthChamberRecipe> INSTANCE = new RecipeSerializer<>(CODEC, STREAM_CODEC);
        public static final Identifier ID = EPAPI.id("plant_growth_chamber");

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
                fluid[i] = BuiltInRegistries.FLUID.getValue(buffer.readIdentifier());

            double fluidConsumption = buffer.readDouble();

            int ticks = buffer.readInt();

            int outputCount = buffer.readInt();
            OutputItemStackTemplateWithPercentages[] outputs = new OutputItemStackTemplateWithPercentages[outputCount];
            for(int i = 0;i < outputCount;i++)
                outputs[i] = OutputItemStackTemplateWithPercentages.OPTIONAL_STREAM_CODEC.decode(buffer);

            return new PlantGrowthChamberRecipe(outputs, input, soilType, fluid, fluidConsumption, ticks);
        }

        private static final StreamCodec<ByteBuf, ResourceKey<SoilType>> SOIL_TYPE_RESOURCE_KEY_STREAM_CODEC =
                ResourceKey.streamCodec(EPRegistries.SOIL_TYPE);
        private static final StreamCodec<ByteBuf, TagKey<SoilType>> SOIL_TYPE_TAG_KEY_STREAM_CODEC =
                TagKey.streamCodec(EPRegistries.SOIL_TYPE);
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
                Identifier fluidId = BuiltInRegistries.FLUID.getKey(fluid);
                if(fluidId == null || fluidId.equals(Identifier.parse("empty")))
                    throw new IllegalArgumentException("Unregistered fluid '" + fluid + "'");

                buffer.writeIdentifier(fluidId);
            }

            buffer.writeDouble(recipe.fluidConsumption);

            buffer.writeInt(recipe.ticks);

            buffer.writeInt(recipe.outputs.length);
            for(OutputItemStackTemplateWithPercentages output:recipe.outputs)
                OutputItemStackTemplateWithPercentages.OPTIONAL_STREAM_CODEC.encode(buffer, output);
        }
    }
}
