package me.jddev0.ep.recipe;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.codec.ArrayCodec;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;

public class ThermalGeneratorRecipe implements Recipe<Container> {
    private final Fluid[] input;
    private final int energyProduction;

    public ThermalGeneratorRecipe(Fluid[] input, int energyProduction) {
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
        return new ItemStack(ModBlocks.THERMAL_GENERATOR_ITEM.get());
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
        public static final ResourceLocation ID = new ResourceLocation(EnergizedPowerMod.MODID, "thermal_generator");

        private final Codec<ThermalGeneratorRecipe> CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(Codec.either(new ArrayCodec<>(BuiltInRegistries.FLUID.byNameCodec(), Fluid[]::new),
                    BuiltInRegistries.FLUID.byNameCodec()).fieldOf("input").forGetter((recipe) -> {
                return recipe.input.length == 1?Either.right(recipe.input[0]):Either.left(recipe.input);
            }), Codec.INT.fieldOf("energy").forGetter((recipe) -> {
                return recipe.energyProduction;
            })).apply(instance, (input, energy) -> {
                return input.map(
                        f -> new ThermalGeneratorRecipe(f, energy),
                        f -> new ThermalGeneratorRecipe(new Fluid[] {f}, energy)
                );
            });
        });

        @Override
        public Codec<ThermalGeneratorRecipe> codec() {
            return CODEC;
        }

        @Override
        public ThermalGeneratorRecipe fromNetwork(FriendlyByteBuf buffer) {
            int fluidCount = buffer.readInt();
            Fluid[] input = new Fluid[fluidCount];
            for(int i = 0;i < fluidCount;i++)
                input[i] = BuiltInRegistries.FLUID.get(buffer.readResourceLocation());

            int energyProduction = buffer.readInt();

            return new ThermalGeneratorRecipe(input, energyProduction);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, ThermalGeneratorRecipe recipe) {
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
