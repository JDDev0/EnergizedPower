package me.jddev0.ep.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.codec.CodecFix;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class EnergizerRecipe implements Recipe<Container> {
    private final ItemStack output;
    private final Ingredient input;
    private final int energyConsumption;

    public EnergizerRecipe(ItemStack output, Ingredient input, int energyConsumption) {
        this.output = output;
        this.input = input;
        this.energyConsumption = energyConsumption;
    }

    public ItemStack getOutput() {
        return output;
    }

    public Ingredient getInput() {
        return input;
    }

    public int getEnergyConsumption() {
        return energyConsumption;
    }

    @Override
    public boolean matches(Container container, Level level) {
        if(level.isClientSide)
            return false;

        return input.test(container.getItem(0));
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        return output;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return output.copy();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.createWithCapacity(1);
        ingredients.add(0, input);
        return ingredients;
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(ModBlocks.ENERGIZER_ITEM.get());
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

    public static final class Type implements RecipeType<EnergizerRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "energizer";
    }

    public static final class Serializer implements RecipeSerializer<EnergizerRecipe> {
        private Serializer() {}

        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation(EnergizedPowerMod.MODID, "energizer");

        private final Codec<EnergizerRecipe> CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(CodecFix.ITEM_STACK_CODEC.fieldOf("output").forGetter((recipe) -> {
                return recipe.output;
            }), Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter((recipe) -> {
                return recipe.input;
            }), ExtraCodecs.POSITIVE_INT.fieldOf("energy").forGetter((recipe) -> {
                return recipe.energyConsumption;
            })).apply(instance, EnergizerRecipe::new);
        });

        @Override
        public Codec<EnergizerRecipe> codec() {
            return CODEC;
        }

        @Override
        public EnergizerRecipe fromNetwork(FriendlyByteBuf buffer) {
            Ingredient input = Ingredient.fromNetwork(buffer);
            int energyConsumption = buffer.readInt();
            ItemStack output = buffer.readItem();

            return new EnergizerRecipe(output, input, energyConsumption);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, EnergizerRecipe recipe) {
            recipe.input.toNetwork(buffer);
            buffer.writeInt(recipe.energyConsumption);
            buffer.writeItemStack(recipe.output, false);
        }
    }
}
