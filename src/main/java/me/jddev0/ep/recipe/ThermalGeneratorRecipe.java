package me.jddev0.ep.recipe;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.codec.ArrayCodec;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.LinkedList;
import java.util.List;

public class ThermalGeneratorRecipe implements Recipe<SimpleContainer> {
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
    public boolean matches(SimpleContainer container, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(SimpleContainer container, RegistryAccess registryAccess) {
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

        private final Codec<ThermalGeneratorRecipe> CODEC_SINGLE_FLUID = RecordCodecBuilder.create((instance) -> {
            return instance.group(ForgeRegistries.FLUIDS.getCodec().fieldOf("input").forGetter((recipe) -> {
                return recipe.input[0];
            }), Codec.INT.fieldOf("energy").forGetter((recipe) -> {
                return recipe.energyProduction;
            })).apply(instance, (f, e) -> new ThermalGeneratorRecipe(new Fluid[] {f}, e));
        });

        private final Codec<ThermalGeneratorRecipe> CODEC_FLUID_ARRAY = RecordCodecBuilder.create((instance) -> {
            return instance.group(new ArrayCodec<>(ForgeRegistries.FLUIDS.getCodec(), Fluid[]::new).fieldOf("input").forGetter((recipe) -> {
                return recipe.input;
            }), Codec.INT.fieldOf("energy").forGetter((recipe) -> {
                return recipe.energyProduction;
            })).apply(instance, ThermalGeneratorRecipe::new);
        });

        @Override
        public Codec<ThermalGeneratorRecipe> codec() {
            return Codec.either(CODEC_FLUID_ARRAY, CODEC_SINGLE_FLUID).
                    xmap(e -> e.left().orElseGet(() -> e.right().orElseThrow()), Either::left);
        }

        @Override
        public ThermalGeneratorRecipe fromNetwork(FriendlyByteBuf buffer) {
            int fluidCount = buffer.readInt();
            Fluid[] input = new Fluid[fluidCount];
            for(int i = 0;i < fluidCount;i++)
                input[i] = ForgeRegistries.FLUIDS.getValue(buffer.readResourceLocation());

            int energyProduction = buffer.readInt();

            return new ThermalGeneratorRecipe(input, energyProduction);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, ThermalGeneratorRecipe recipe) {
            buffer.writeInt(recipe.getInput().length);
            for(Fluid fluid:recipe.input) {
                ResourceLocation fluidId = ForgeRegistries.FLUIDS.getKey(fluid);
                if(fluidId == null)
                    throw new IllegalArgumentException("Unregistered fluid '" + fluid + "'");

                buffer.writeResourceLocation(fluidId);
            }

            buffer.writeInt(recipe.energyProduction);
        }
    }
}
