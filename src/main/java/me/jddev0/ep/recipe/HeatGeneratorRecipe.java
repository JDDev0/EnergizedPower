package me.jddev0.ep.recipe;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.codec.ArrayCodec;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;

public class HeatGeneratorRecipe implements Recipe<Container> {
    private final Fluid[] input;
    private final int energyProduction;

    public HeatGeneratorRecipe(Fluid[] input, int energyProduction) {
        this.input = input;
        this.energyProduction = energyProduction;
    }

    public Fluid[] getInput() {
        return input;
    }

    public int getEnergyProduction() {
        return energyProduction;
    }

    @Override
    public boolean matches(Container container, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(Container container, HolderLookup.Provider registries) {
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
        return new ItemStack(ModBlocks.HEAT_GENERATOR_ITEM.get());
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
        public static final ResourceLocation ID = new ResourceLocation(EnergizedPowerMod.MODID, "heat_generator");

        private final MapCodec<HeatGeneratorRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(Codec.either(new ArrayCodec<>(BuiltInRegistries.FLUID.byNameCodec(), Fluid[]::new),
                    BuiltInRegistries.FLUID.byNameCodec()).fieldOf("input").forGetter((recipe) -> {
                return Either.left(recipe.input);
            }), Codec.INT.fieldOf("energy").forGetter((recipe) -> {
                return recipe.energyProduction;
            })).apply(instance, (input, energy) -> {
                return input.map(
                        f -> new HeatGeneratorRecipe(f, energy),
                        f -> new HeatGeneratorRecipe(new Fluid[] {f}, energy)
                );
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
            int fluidCount = buffer.readInt();
            Fluid[] input = new Fluid[fluidCount];
            for(int i = 0;i < fluidCount;i++)
                input[i] = BuiltInRegistries.FLUID.get(buffer.readResourceLocation());

            int energyProduction = buffer.readInt();

            return new HeatGeneratorRecipe(input, energyProduction);
        }

        private static void write(RegistryFriendlyByteBuf buffer, HeatGeneratorRecipe recipe) {
            buffer.writeInt(recipe.getInput().length);
            for(Fluid fluid:recipe.input) {
                ResourceLocation fluidId = BuiltInRegistries.FLUID.getKey(fluid);
                if(fluidId == null || fluidId.equals(new ResourceLocation("empty")))
                    throw new IllegalArgumentException("Unregistered fluid '" + fluid + "'");

                buffer.writeResourceLocation(fluidId);
            }

            buffer.writeInt(recipe.energyProduction);
        }
    }
}
