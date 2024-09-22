package me.jddev0.ep.recipe;

import com.google.gson.JsonObject;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.item.ModItems;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class SawmillRecipe implements Recipe<Container> {
    private final ResourceLocation id;
    private final ItemStack output;
    private final ItemStack secondaryOutput;
    private final Ingredient input;

    public SawmillRecipe(ResourceLocation id, ItemStack output, Ingredient input, int sawdustAmount) {
        this(id, output, new ItemStack(ModItems.SAWDUST.get(), sawdustAmount), input);
    }

    public SawmillRecipe(ResourceLocation id, ItemStack output, ItemStack secondaryOutput, Ingredient input) {
        this.id = id;
        this.output = output;
        this.secondaryOutput = secondaryOutput;
        this.input = input;
    }

    public ItemStack getOutput() {
        return output;
    }

    public ItemStack getSecondaryOutput() {
        return secondaryOutput;
    }

    public Ingredient getInput() {
        return input;
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
        return new ItemStack(ModBlocks.SAWMILL_ITEM.get());
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

    public static final class Type implements RecipeType<SawmillRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "sawmill";
    }

    public static final class Serializer implements RecipeSerializer<SawmillRecipe> {
        private Serializer() {}

        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation(EnergizedPowerMod.MODID, "sawmill");

        @Override
        public SawmillRecipe fromJson(ResourceLocation recipeID, JsonObject json) {
            Ingredient input = Ingredient.fromJson(json.get("ingredient"));
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "output"));

            if(json.has("secondaryOutput")) {
                ItemStack secondaryOutput = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "secondaryOutput"));
                return new SawmillRecipe(recipeID, output, secondaryOutput, input);
            }

            int sawdustAmount = json.get("sawdustAmount").getAsInt();
            return new SawmillRecipe(recipeID, output, input, sawdustAmount);
        }

        @Override
        public SawmillRecipe fromNetwork(ResourceLocation recipeID, FriendlyByteBuf buffer) {
            Ingredient input = Ingredient.fromNetwork(buffer);
            ItemStack output = buffer.readItem();
            ItemStack secondaryOutput = buffer.readItem();

            return new SawmillRecipe(recipeID, output, secondaryOutput, input);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, SawmillRecipe recipe) {
            recipe.input.toNetwork(buffer);
            buffer.writeItemStack(recipe.output, false);
            buffer.writeItemStack(recipe.secondaryOutput, false);
        }
    }
}
