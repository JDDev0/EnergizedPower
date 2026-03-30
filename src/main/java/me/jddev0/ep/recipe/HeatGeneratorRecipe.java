package me.jddev0.ep.recipe;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.codec.ArrayCodec;
import net.minecraft.core.registries.BuiltInRegistries;
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

public class HeatGeneratorRecipe implements EnergizedPowerBaseRecipe<RecipeInput> {
    private final Fluid[] input;
    private final long energyProduction;

    public HeatGeneratorRecipe(Fluid[] input, long energyProduction) {
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
        return EPRecipes.HEAT_GENERATOR_CATEGORY;
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

    public static final class Type implements RecipeType<HeatGeneratorRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "heat_generator";
    }

    public static final class Serializer {
        private Serializer() {}

        private static final MapCodec<HeatGeneratorRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(Codec.either(new ArrayCodec<>(BuiltInRegistries.FLUID.byNameCodec(), Fluid[]::new),
                    BuiltInRegistries.FLUID.byNameCodec()).fieldOf("ingredient").forGetter((recipe) -> {
                return recipe.input.length == 1?Either.right(recipe.input[0]):Either.left(recipe.input);
            }), Codec.LONG.fieldOf("energy").forGetter((recipe) -> {
                return recipe.energyProduction;
            })).apply(instance, (input, energy) -> {
                return input.map(
                        f -> new HeatGeneratorRecipe(f, energy),
                        f -> new HeatGeneratorRecipe(new Fluid[] {f}, energy)
                );
            });
        });

        private static final StreamCodec<RegistryFriendlyByteBuf, HeatGeneratorRecipe> STREAM_CODEC = StreamCodec.of(
                Serializer::write, Serializer::read);

        public static final RecipeSerializer<HeatGeneratorRecipe> INSTANCE = new RecipeSerializer<>(CODEC, STREAM_CODEC);
        public static final Identifier ID = EPAPI.id("heat_generator");

        private static HeatGeneratorRecipe read(RegistryFriendlyByteBuf buffer) {
            int fluidCount = buffer.readInt();
            Fluid[] input = new Fluid[fluidCount];
            for(int i = 0;i < fluidCount;i++)
                input[i] = BuiltInRegistries.FLUID.getValue(buffer.readIdentifier());

            long energyProduction = buffer.readLong();

            return new HeatGeneratorRecipe(input, energyProduction);
        }

        private static void write(RegistryFriendlyByteBuf buffer, HeatGeneratorRecipe recipe) {
            buffer.writeInt(recipe.getInput().length);
            for(Fluid fluid:recipe.input) {
                Identifier fluidId = BuiltInRegistries.FLUID.getKey(fluid);
                if(fluidId == null || fluidId.equals(Identifier.parse("empty")))
                    throw new IllegalArgumentException("Unregistered fluid '" + fluid + "'");

                buffer.writeIdentifier(fluidId);
            }

            buffer.writeLong(recipe.energyProduction);
        }
    }
}
