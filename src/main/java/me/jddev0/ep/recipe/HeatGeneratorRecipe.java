package me.jddev0.ep.recipe;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.codec.ArrayCodec;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class HeatGeneratorRecipe implements Recipe<SimpleInventory> {
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
    public boolean matches(SimpleInventory container, World level) {
        return false;
    }

    @Override
    public ItemStack craft(SimpleInventory container, DynamicRegistryManager registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResult(DynamicRegistryManager registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(ModBlocks.HEAT_GENERATOR_ITEM);
    }

    @Override
    public boolean isIgnoredInRecipeBook() {
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
        public static final Identifier ID = new Identifier(EnergizedPowerMod.MODID, "heat_generator");


        private final Codec<HeatGeneratorRecipe> CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(Codec.either(new ArrayCodec<>(Registries.FLUID.getCodec(), Fluid[]::new),
                    Registries.FLUID.getCodec()).fieldOf("input").forGetter((recipe) -> {
                return Either.left(recipe.input);
            }), Codec.LONG.fieldOf("energy").forGetter((recipe) -> {
                return recipe.energyProduction;
            })).apply(instance, (input, energy) -> {
                return input.map(
                        f -> new HeatGeneratorRecipe(f, energy),
                        f -> new HeatGeneratorRecipe(new Fluid[] {f}, energy)
                );
            });
        });

        @Override
        public Codec<HeatGeneratorRecipe> codec() {
            return CODEC;
        }

        @Override
        public HeatGeneratorRecipe read(PacketByteBuf buffer) {
            int fluidCount = buffer.readInt();
            Fluid[] input = new Fluid[fluidCount];
            for(int i = 0;i < fluidCount;i++)
                input[i] = Registries.FLUID.get(buffer.readIdentifier());

            long energyProduction = buffer.readLong();

            return new HeatGeneratorRecipe(input, energyProduction);
        }

        @Override
        public void write(PacketByteBuf buffer, HeatGeneratorRecipe recipe) {
            buffer.writeInt(recipe.getInput().length);
            for(Fluid fluid:recipe.input) {
                Identifier fluidId = Registries.FLUID.getId(fluid);
                if(fluidId == null || fluidId.equals(new Identifier("empty")))
                    throw new IllegalArgumentException("Unregistered fluid '" + fluid + "'");

                buffer.writeIdentifier(fluidId);
            }

            buffer.writeLong(recipe.energyProduction);
        }
    }
}
