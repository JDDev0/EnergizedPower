package me.jddev0.ep.recipe;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.codec.ArrayCodec;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.List;

public class ThermalGeneratorRecipe implements EnergizedPowerBaseRecipe<RecipeInput> {
    private final Fluid[] input;
    private final long energyProduction;

    public ThermalGeneratorRecipe(Fluid[] input, long energyProduction) {
        this.input = input;
        this.energyProduction = energyProduction;
    }

    public Fluid[] getInput() {
        return input;
    }

    public long getEnergyProduction() {
        return energyProduction;
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
        return EPRecipes.THERMAL_GENERATOR_CATEGORY;
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

    public static final class Serializer implements RecipeSerializer<ThermalGeneratorRecipe> {
        private Serializer() {}

        public static final Serializer INSTANCE = new Serializer();
        public static final Identifier ID = EPAPI.id("thermal_generator");

        private final MapCodec<ThermalGeneratorRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(Codec.either(new ArrayCodec<>(Registries.FLUID.getCodec(), Fluid[]::new),
                    Registries.FLUID.getCodec()).fieldOf("ingredient").forGetter((recipe) -> {
                return recipe.input.length == 1?Either.right(recipe.input[0]):Either.left(recipe.input);
            }), Codec.LONG.fieldOf("energy").forGetter((recipe) -> {
                return recipe.energyProduction;
            })).apply(instance, (input, energy) -> {
                return input.map(
                        f -> new ThermalGeneratorRecipe(f, energy),
                        f -> new ThermalGeneratorRecipe(new Fluid[] {f}, energy)
                );
            });
        });

        private final PacketCodec<RegistryByteBuf, ThermalGeneratorRecipe> PACKET_CODEC = PacketCodec.ofStatic(
                Serializer::write, Serializer::read);

        @Override
        public MapCodec<ThermalGeneratorRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, ThermalGeneratorRecipe> packetCodec() {
            return PACKET_CODEC;
        }

        private static ThermalGeneratorRecipe read(RegistryByteBuf buffer) {
            int fluidCount = buffer.readInt();
            Fluid[] input = new Fluid[fluidCount];
            for(int i = 0;i < fluidCount;i++)
                input[i] = Registries.FLUID.get(buffer.readIdentifier());

            long energyProduction = buffer.readLong();

            return new ThermalGeneratorRecipe(input, energyProduction);
        }

        private static void write(RegistryByteBuf buffer, ThermalGeneratorRecipe recipe) {
            buffer.writeInt(recipe.getInput().length);
            for(Fluid fluid:recipe.input) {
                Identifier fluidId = Registries.FLUID.getId(fluid);
                if(fluidId == null || fluidId.equals(Identifier.of("empty")))
                    throw new IllegalArgumentException("Unregistered fluid '" + fluid + "'");

                buffer.writeIdentifier(fluidId);
            }

            buffer.writeLong(recipe.energyProduction);
        }
    }
}
