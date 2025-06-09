package me.jddev0.ep.recipe;

import com.google.gson.JsonObject;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.util.FluidStackUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;

public class StoneLiquefierRecipe implements Recipe<Container> {
    private final ResourceLocation id;
    private final Ingredient input;
    private final FluidStack output;

    public StoneLiquefierRecipe(ResourceLocation id, Ingredient input, FluidStack output) {
        this.id = id;
        this.input = input;
        this.output = output;
    }

    public Ingredient getInput() {
        return input;
    }

    public FluidStack getOutput() {
        return output;
    }

    @Override
    public boolean matches(Container container, Level level) {
        if(level.isClientSide)
            return false;

        return input.test(container.getItem(0));
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.createWithCapacity(1);
        ingredients.add(0, input);
        return ingredients;
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(EPBlocks.STONE_LIQUEFIER.get());
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public RecipeSerializer<? extends Recipe<Container>> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<? extends Recipe<Container>> getType() {
        return Type.INSTANCE;
    }

    public static final class Type implements RecipeType<StoneLiquefierRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "stone_liquefier";
    }

    public static final class Serializer implements RecipeSerializer<StoneLiquefierRecipe> {
        private Serializer() {}

        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = EPAPI.id("stone_liquefier");

        @Override
        public StoneLiquefierRecipe fromJson(ResourceLocation recipeID, JsonObject json) {
            Ingredient input = Ingredient.fromJson(json.get("ingredient"));
            FluidStack output = FluidStackUtils.fromJson(json.get("result"));

            return new StoneLiquefierRecipe(recipeID, input, output);
        }

        @Override
        public StoneLiquefierRecipe fromNetwork(ResourceLocation recipeID, FriendlyByteBuf buffer) {
            Ingredient input = Ingredient.fromNetwork(buffer);
            FluidStack output = buffer.readFluidStack();

            return new StoneLiquefierRecipe(recipeID, input, output);
        }

        public void toNetwork(FriendlyByteBuf buffer, StoneLiquefierRecipe recipe) {
            recipe.input.toNetwork(buffer);
            buffer.writeFluidStack(recipe.output);
        }
    }
}
