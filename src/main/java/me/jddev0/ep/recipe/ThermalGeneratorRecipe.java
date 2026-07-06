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
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;

public class ThermalGeneratorRecipe implements Recipe<RecipeInput> {
    private final FluidIngredient input;
    private final long energyProduction;

    @Deprecated(forRemoval = true)
    public ThermalGeneratorRecipe(Fluid[] input, long energyProduction) {
        this(FluidIngredient.of(input), energyProduction);
    }

    public ThermalGeneratorRecipe(FluidIngredient input, long energyProduction) {
        this.input = input;
        this.energyProduction = energyProduction;
    }

    public FluidIngredient getInput() {
        return input;
    }

    public long getEnergyProduction() {
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
        return new ItemStack(EPBlocks.THERMAL_GENERATOR_ITEM);
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

    public static final class Type implements RecipeType<ThermalGeneratorRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "thermal_generator";
    }

    public static final class Serializer implements RecipeSerializer<ThermalGeneratorRecipe> {
        private Serializer() {}

        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = EPAPI.id("thermal_generator");

        private final MapCodec<ThermalGeneratorRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(FluidIngredient.CODEC.fieldOf("input").forGetter((recipe) -> {
                return recipe.input;
            }), Codec.LONG.fieldOf("energy").forGetter((recipe) -> {
                return recipe.energyProduction;
            })).apply(instance, (input, energy) -> {
                return new ThermalGeneratorRecipe(input, energy);
            });
        });

        private final StreamCodec<RegistryFriendlyByteBuf, ThermalGeneratorRecipe> STREAM_CODEC = StreamCodec.of(
                Serializer::write, Serializer::read);

        @Override
        public MapCodec<ThermalGeneratorRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ThermalGeneratorRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static ThermalGeneratorRecipe read(RegistryFriendlyByteBuf buffer) {
            FluidIngredient input = FluidIngredient.STREAM_CODEC.decode(buffer);
            long energyProduction = buffer.readLong();

            return new ThermalGeneratorRecipe(input, energyProduction);
        }

        private static void write(RegistryFriendlyByteBuf buffer, ThermalGeneratorRecipe recipe) {
            FluidIngredient.STREAM_CODEC.encode(buffer, recipe.input);
            buffer.writeLong(recipe.energyProduction);
        }
    }
}
