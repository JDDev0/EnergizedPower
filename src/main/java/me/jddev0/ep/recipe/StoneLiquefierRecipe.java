package me.jddev0.ep.recipe;

import com.google.gson.JsonObject;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.fluid.FluidStack;
import me.jddev0.ep.util.FluidStackUtils;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class StoneLiquefierRecipe implements Recipe<Inventory> {
    private final Identifier id;
    private final Ingredient input;
    private final FluidStack output;

    public StoneLiquefierRecipe(Identifier id, Ingredient input, FluidStack output) {
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
    public boolean matches(Inventory container, World level) {
        if(level.isClient())
            return false;

        return input.test(container.getStack(0));
    }

    @Override
    public ItemStack craft(Inventory container, DynamicRegistryManager registryManager) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return ItemStack.EMPTY;
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        DefaultedList<Ingredient> ingredients = DefaultedList.ofSize(1);
        ingredients.add(0, input);
        return ingredients;
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(EPBlocks.STONE_LIQUEFIER);
    }

    @Override
    public Identifier getId() {
        return id;
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

    public static final class Type implements RecipeType<StoneLiquefierRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "stone_liquefier";
    }

    public static final class Serializer implements RecipeSerializer<StoneLiquefierRecipe> {
        private Serializer() {}

        public static final Serializer INSTANCE = new Serializer();
        public static final Identifier ID = EPAPI.id("stone_liquefier");

        @Override
        public StoneLiquefierRecipe read(Identifier recipeID, JsonObject json) {
            Ingredient input = Ingredient.fromJson(json.get("ingredient"));
            FluidStack output = FluidStackUtils.fromJson(JsonHelper.getObject(json, "result"));

            return new StoneLiquefierRecipe(recipeID, input, output);
        }

        @Override
        public StoneLiquefierRecipe read(Identifier recipeID, PacketByteBuf buffer) {
            Ingredient input = Ingredient.fromPacket(buffer);
            FluidStack output = FluidStack.fromPacket(buffer);

            return new StoneLiquefierRecipe(recipeID, input, output);
        }

        public void write(PacketByteBuf buffer, StoneLiquefierRecipe recipe) {
            recipe.input.write(buffer);
            recipe.output.toPacket(buffer);
        }
    }
}
