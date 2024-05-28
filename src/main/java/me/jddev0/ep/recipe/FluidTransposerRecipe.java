package me.jddev0.ep.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.block.entity.FluidTransposerBlockEntity;
import me.jddev0.ep.codec.CodecFix;
import me.jddev0.ep.fluid.FluidStack;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class FluidTransposerRecipe implements Recipe<Inventory> {
    private final FluidTransposerBlockEntity.Mode mode;
    private final ItemStack output;
    private final Ingredient input;
    private final FluidStack fluid;

    public FluidTransposerRecipe(FluidTransposerBlockEntity.Mode mode, ItemStack output, Ingredient input, FluidStack fluid) {
        this.mode = mode;
        this.output = output;
        this.input = input;
        this.fluid = fluid;
    }

    public FluidTransposerBlockEntity.Mode getMode() {
        return mode;
    }

    public ItemStack getOutput() {
        return output;
    }

    public Ingredient getInput() {
        return input;
    }

    public FluidStack getFluid() {
        return fluid;
    }

    @Override
    public boolean matches(Inventory container, World level) {
        if(level.isClient())
            return false;

        return input.test(container.getStack(0));
    }

    @Override
    public ItemStack craft(Inventory container, RegistryWrapper.WrapperLookup registries) {
        return output;
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registries) {
        return output.copy();
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        DefaultedList<Ingredient> ingredients = DefaultedList.ofSize(1);
        ingredients.add(0, input);
        return ingredients;
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(ModBlocks.FLUID_TRANSPOSER_ITEM);
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

    public static final class Type implements RecipeType<FluidTransposerRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "fluid_transposer";
    }

    public static final class Serializer implements RecipeSerializer<FluidTransposerRecipe> {
        private Serializer() {}

        public static final Serializer INSTANCE = new Serializer();
        public static final Identifier ID = new Identifier(EnergizedPowerMod.MODID, "fluid_transposer");

        private final MapCodec<FluidTransposerRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(FluidTransposerBlockEntity.Mode.CODEC.fieldOf("mode").forGetter((recipe) -> {
                return recipe.mode;
            }), CodecFix.ITEM_STACK_CODEC.fieldOf("output").forGetter((recipe) -> {
                return recipe.output;
            }), Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("ingredient").forGetter((recipe) -> {
                return recipe.input;
            }), FluidStack.CODEC_MILLIBUCKETS.fieldOf("fluid").forGetter((recipe) -> {
                return recipe.fluid;
            })).apply(instance, FluidTransposerRecipe::new);
        });

        private final PacketCodec<RegistryByteBuf, FluidTransposerRecipe> PACKET_CODEC = PacketCodec.ofStatic(
                Serializer::write, Serializer::read);

        @Override
        public MapCodec<FluidTransposerRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, FluidTransposerRecipe> packetCodec() {
            return PACKET_CODEC;
        }

        private static FluidTransposerRecipe read(RegistryByteBuf buffer) {
            FluidTransposerBlockEntity.Mode mode = buffer.readEnumConstant(FluidTransposerBlockEntity.Mode.class);
            Ingredient input = Ingredient.PACKET_CODEC.decode(buffer);
            ItemStack output = ItemStack.OPTIONAL_PACKET_CODEC.decode(buffer);
            FluidStack fluid = FluidStack.PACKET_CODEC.decode(buffer);

            return new FluidTransposerRecipe(mode, output, input, fluid);
        }

        private static void write(RegistryByteBuf buffer, FluidTransposerRecipe recipe) {
            buffer.writeEnumConstant(recipe.mode);
            Ingredient.PACKET_CODEC.encode(buffer, recipe.input);
            ItemStack.OPTIONAL_PACKET_CODEC.encode(buffer, recipe.output);
            FluidStack.PACKET_CODEC.encode(buffer, recipe.fluid);
        }
    }
}
