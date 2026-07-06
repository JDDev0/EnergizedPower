package me.jddev0.ep.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;

public class HeatGeneratorRecipe implements Recipe<RecipeInput> {
    private final FluidIngredient input;
    private final int energyProduction;

    @Deprecated(forRemoval = true)
    public HeatGeneratorRecipe(Fluid[] input, int energyProduction) {
        this(FluidIngredient.of(input), energyProduction);
    }

    public HeatGeneratorRecipe(FluidIngredient input, int energyProduction) {
        this.input = input;
        this.energyProduction = energyProduction;
    }

    public FluidIngredient getInput() {
        return input;
    }

    public int getEnergyProduction() {
        return energyProduction;
    }

    @Override
    public boolean matches(RecipeInput container, Level level) {
        return false;
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
    public ItemStack getToastSymbol() {
        return new ItemStack(EPBlocks.HEAT_GENERATOR_ITEM.get());
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

    public static final class Type implements RecipeType<HeatGeneratorRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "heat_generator";
    }

    public static final class Serializer implements RecipeSerializer<HeatGeneratorRecipe> {
        private Serializer() {}

        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = EPAPI.id("heat_generator");

        private final MapCodec<HeatGeneratorRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(FluidIngredient.CODEC.fieldOf("input").forGetter((recipe) -> {
                return recipe.input;
            }), Codec.INT.fieldOf("energy").forGetter((recipe) -> {
                return recipe.energyProduction;
            })).apply(instance, (input, energy) -> {
                return new HeatGeneratorRecipe(input, energy);
            });
        });

        private final StreamCodec<RegistryFriendlyByteBuf, HeatGeneratorRecipe> STREAM_CODEC = StreamCodec.of(
                Serializer::write, Serializer::read);

        @Override
        public MapCodec<HeatGeneratorRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, HeatGeneratorRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static HeatGeneratorRecipe read(RegistryFriendlyByteBuf buffer) {
            FluidIngredient input = FluidIngredient.STREAM_CODEC.decode(buffer);
            int energyProduction = buffer.readInt();

            return new HeatGeneratorRecipe(input, energyProduction);
        }

        private static void write(RegistryFriendlyByteBuf buffer, HeatGeneratorRecipe recipe) {
            FluidIngredient.STREAM_CODEC.encode(buffer, recipe.input);
            buffer.writeInt(recipe.energyProduction);
        }
    }
}
