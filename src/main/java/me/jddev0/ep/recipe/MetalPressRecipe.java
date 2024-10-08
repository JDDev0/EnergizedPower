package me.jddev0.ep.recipe;

import com.google.gson.JsonObject;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class MetalPressRecipe implements Recipe<Container> {
    private final ResourceLocation id;
    private final ItemStack output;
    private final ItemStack pressMold;
    private final Ingredient input;
    private final int inputCount;

    public MetalPressRecipe(ResourceLocation id, ItemStack output, ItemStack pressMold, Ingredient input) {
        this(id, output, pressMold, input, 1);
    }

    public MetalPressRecipe(ResourceLocation id, ItemStack output, ItemStack pressMold, Ingredient input, int inputCount) {
        this.id = id;
        this.output = output;
        this.pressMold = pressMold;
        this.input = input;
        this.inputCount = inputCount;
    }

    public ItemStack getOutput() {
        return output;
    }

    public ItemStack getPressMold() {
        return pressMold;
    }

    public Ingredient getInput() {
        return input;
    }

    public int getInputCount() {
        return inputCount;
    }

    @Override
    public boolean matches(Container container, Level level) {
        if(level.isClientSide)
            return false;

        return input.test(container.getItem(0)) && container.getItem(0).getCount() >= inputCount &&
                ItemStack.isSameItem(container.getItem(1), pressMold);
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
    public ItemStack getToastSymbol() {
        return new ItemStack(EPBlocks.METAL_PRESS_ITEM.get());
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
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static final class Type implements RecipeType<MetalPressRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "metal_press";
    }

    public static final class Serializer implements RecipeSerializer<MetalPressRecipe> {
        private Serializer() {}

        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = EPAPI.id("metal_press");

        @Override
        public MetalPressRecipe fromJson(ResourceLocation recipeID, JsonObject json) {
            Ingredient input = Ingredient.fromJson(json.get("ingredient"));
            ItemStack pressMold = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "pressMold"));
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "output"));


            if(json.has("inputCount")) {
                int inputCount = GsonHelper.getAsInt(json, "inputCount");

                return new MetalPressRecipe(recipeID, output, pressMold, input, inputCount);
            }

            return new MetalPressRecipe(recipeID, output, pressMold, input);
        }

        @Override
        public MetalPressRecipe fromNetwork(ResourceLocation recipeID, FriendlyByteBuf buffer) {
            Ingredient input = Ingredient.fromNetwork(buffer);
            int inputCount = buffer.readInt();
            ItemStack pressMold = buffer.readItem();
            ItemStack output = buffer.readItem();

            return new MetalPressRecipe(recipeID, output, pressMold, input, inputCount);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, MetalPressRecipe recipe) {
            recipe.input.toNetwork(buffer);
            buffer.writeInt(recipe.inputCount);
            buffer.writeItemStack(recipe.pressMold, false);
            buffer.writeItemStack(recipe.output, false);
        }
    }
}
