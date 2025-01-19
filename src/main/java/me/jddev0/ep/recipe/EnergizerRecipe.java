package me.jddev0.ep.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.codec.CodecFix;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.World;

import java.util.List;

public class EnergizerRecipe implements EnergizedPowerBaseRecipe<RecipeInput> {
    private final ItemStack output;
    private final Ingredient input;
    private final int energyConsumption;

    public EnergizerRecipe(ItemStack output, Ingredient input, int energyConsumption) {
        this.output = output;
        this.input = input;
        this.energyConsumption = energyConsumption;
    }

    public ItemStack getOutputItem() {
        return output;
    }

    public Ingredient getInputItem() {
        return input;
    }

    public int getEnergyConsumption() {
        return energyConsumption;
    }

    @Override
    public boolean matches(RecipeInput container, World level) {
        if(level.isClient())
            return false;

        return input.test(container.getStackInSlot(0));
    }

    @Override
    public ItemStack craft(RecipeInput container, RegistryWrapper.WrapperLookup registries) {
        return output;
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
        return EPRecipes.ENERGIZER_CATEGORY;
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
        return List.of(input);
    }

    @Override
    public boolean isIngredient(ItemStack itemStack) {
        return input.test(itemStack);
    }

    @Override
    public boolean isResult(ItemStack itemStack) {
        return ItemStack.areItemsAndComponentsEqual(output, itemStack);
    }

    public static final class Type implements RecipeType<EnergizerRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "energizer";
    }

    public static final class Serializer implements RecipeSerializer<EnergizerRecipe> {
        private Serializer() {}

        public static final Serializer INSTANCE = new Serializer();
        public static final Identifier ID = EPAPI.id("energizer");

        private final MapCodec<EnergizerRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(CodecFix.ITEM_STACK_CODEC.fieldOf("result").forGetter((recipe) -> {
                return recipe.output;
            }), Ingredient.CODEC.fieldOf("ingredient").forGetter((recipe) -> {
                return recipe.input;
            }), Codecs.POSITIVE_INT.fieldOf("energy").forGetter((recipe) -> {
                return recipe.energyConsumption;
            })).apply(instance, EnergizerRecipe::new);
        });

        private final PacketCodec<RegistryByteBuf, EnergizerRecipe> PACKET_CODEC = PacketCodec.ofStatic(
                Serializer::write, Serializer::read);

        @Override
        public MapCodec<EnergizerRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, EnergizerRecipe> packetCodec() {
            return PACKET_CODEC;
        }

        private static EnergizerRecipe read(RegistryByteBuf buffer) {
            Ingredient input = Ingredient.PACKET_CODEC.decode(buffer);
            int energyConsumption = buffer.readInt();
            ItemStack output = ItemStack.OPTIONAL_PACKET_CODEC.decode(buffer);

            return new EnergizerRecipe(output, input, energyConsumption);
        }

        private static void write(RegistryByteBuf buffer, EnergizerRecipe recipe) {
            Ingredient.PACKET_CODEC.encode(buffer, recipe.input);
            buffer.writeInt(recipe.energyConsumption);
            ItemStack.OPTIONAL_PACKET_CODEC.encode(buffer, recipe.output);
        }
    }
}
