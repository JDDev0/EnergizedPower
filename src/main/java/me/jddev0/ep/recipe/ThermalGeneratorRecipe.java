package me.jddev0.ep.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.api.EPAPI;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;

import java.util.List;

public class ThermalGeneratorRecipe implements EnergizedPowerBaseRecipe<RecipeInput> {
    private final FluidIngredient input;
    private final int energyProduction;

    @Deprecated(forRemoval = true)
    public ThermalGeneratorRecipe(Fluid[] input, int energyProduction) {
        this(FluidIngredient.of(input), energyProduction);
    }

    public ThermalGeneratorRecipe(FluidIngredient input, int energyProduction) {
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
        return EPRecipes.THERMAL_GENERATOR_CATEGORY.get();
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
        return List.of();
    }

    @Override
    public boolean isIngredient(ItemStack itemStack) {
        return false;
    }

    @Override
    public boolean isResult(ItemStack itemStack) {
        return false;
    }

    public static final class Type implements RecipeType<ThermalGeneratorRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "thermal_generator";
    }

    public static final class Serializer {
        private Serializer() {}

        private static final MapCodec<ThermalGeneratorRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(FluidIngredient.CODEC.fieldOf("ingredient").forGetter((recipe) -> {
                return recipe.input;
            }), Codec.INT.fieldOf("energy").forGetter((recipe) -> {
                return recipe.energyProduction;
            })).apply(instance, (input, energy) -> {
                return new ThermalGeneratorRecipe(input, energy);
            });
        });

        private static final StreamCodec<RegistryFriendlyByteBuf, ThermalGeneratorRecipe> STREAM_CODEC = StreamCodec.of(
                Serializer::write, Serializer::read);

        public static final RecipeSerializer<ThermalGeneratorRecipe> INSTANCE = new RecipeSerializer<>(CODEC, STREAM_CODEC);
        public static final Identifier ID = EPAPI.id("thermal_generator");

        private static ThermalGeneratorRecipe read(RegistryFriendlyByteBuf buffer) {
            FluidIngredient input = FluidIngredient.STREAM_CODEC.decode(buffer);
            int energyProduction = buffer.readInt();

            return new ThermalGeneratorRecipe(input, energyProduction);
        }

        private static void write(RegistryFriendlyByteBuf buffer, ThermalGeneratorRecipe recipe) {
            FluidIngredient.STREAM_CODEC.encode(buffer, recipe.input);
            buffer.writeInt(recipe.energyProduction);
        }
    }
}
