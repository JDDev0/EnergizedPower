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

        private final Codec<HeatGeneratorRecipe> CODEC_SINGLE_FLUID = RecordCodecBuilder.create((instance) -> {
            return instance.group(BuiltInRegistries.FLUID.byNameCodec().fieldOf("input").forGetter((recipe) -> {
                return recipe.input[0];
            }), Codec.INT.fieldOf("energy").forGetter((recipe) -> {
                return recipe.energyProduction;
            })).apply(instance, (f, e) -> new HeatGeneratorRecipe(new Fluid[] {f}, e));
        });

        private final Codec<HeatGeneratorRecipe> CODEC_FLUID_ARRAY = RecordCodecBuilder.create((instance) -> {
            return instance.group(new ArrayCodec<>(BuiltInRegistries.FLUID.byNameCodec(), Fluid[]::new).fieldOf("input").forGetter((recipe) -> {
                return recipe.input;
            }), Codec.INT.fieldOf("energy").forGetter((recipe) -> {
                return recipe.energyProduction;
            })).apply(instance, HeatGeneratorRecipe::new);
        });

        @Override
        public Codec<HeatGeneratorRecipe> codec() {
            return Codec.either(CODEC_FLUID_ARRAY, CODEC_SINGLE_FLUID).
                    xmap(e -> e.left().orElseGet(() -> e.right().orElseThrow()), Either::left);
        }

        @Override
        public HeatGeneratorRecipe fromNetwork(FriendlyByteBuf buffer) {
            int fluidCount = buffer.readInt();
            Fluid[] input = new Fluid[fluidCount];
            for(int i = 0;i < fluidCount;i++)
                input[i] = BuiltInRegistries.FLUID.get(buffer.readResourceLocation());

            int energyProduction = buffer.readInt();

            return new HeatGeneratorRecipe(input, energyProduction);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, HeatGeneratorRecipe recipe) {
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
