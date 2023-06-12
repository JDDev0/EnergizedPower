package me.jddev0.ep.recipe;

import com.google.gson.JsonObject;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.item.ModItems;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class SawmillRecipe implements Recipe<SimpleInventory> {
    private final Identifier id;
    private final ItemStack output;
    private final ItemStack secondaryOutput;
    private final Ingredient input;

    public SawmillRecipe(Identifier id, ItemStack output, Ingredient input, int sawdustAmount) {
        this(id, output, new ItemStack(ModItems.SAWDUST, sawdustAmount), input);
    }

    public SawmillRecipe(Identifier id, ItemStack output, ItemStack secondaryOutput, Ingredient input) {
        this.id = id;
        this.output = output;
        this.input = input;
        this.secondaryOutput = secondaryOutput;
    }

    public ItemStack getOutputItem() {
        return output;
    }

    public ItemStack getSecondaryOutput() {
        return secondaryOutput;
    }

    public Ingredient getInputItem() {
        return input;
    }

    @Override
    public boolean matches(SimpleInventory container, World level) {
        if(level.isClient())
            return false;

        return input.test(container.getStack(0));
    }

    @Override
    public ItemStack craft(SimpleInventory container, DynamicRegistryManager registryManager) {
        return output;
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManage) {
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
        return new ItemStack(ModBlocks.SAWMILL_ITEM);
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
        return SawmillRecipe.Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return SawmillRecipe.Type.INSTANCE;
    }

    public static final class Type implements RecipeType<SawmillRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "sawmill";
    }

    public static final class Serializer implements RecipeSerializer<SawmillRecipe> {
        private Serializer() {}

        public static final Serializer INSTANCE = new Serializer();
        public static final Identifier ID = new Identifier(EnergizedPowerMod.MODID, "sawmill");

        @Override
        public SawmillRecipe read(Identifier recipeID, JsonObject json) {
            Ingredient input = Ingredient.fromJson(json.get("ingredient"));
            ItemStack output = ShapedRecipe.outputFromJson(JsonHelper.getObject(json, "output"));

            if(json.has("secondaryOutput")) {
                ItemStack secondaryOutput = ShapedRecipe.outputFromJson(JsonHelper.getObject(json, "secondaryOutput"));
                return new SawmillRecipe(recipeID, output, secondaryOutput, input);
            }

            int sawdustAmount = json.get("sawdustAmount").getAsInt();

            return new SawmillRecipe(recipeID, output, input, sawdustAmount);
        }

        @Override
        public SawmillRecipe read(Identifier recipeID, PacketByteBuf buffer) {
            Ingredient input = Ingredient.fromPacket(buffer);
            ItemStack output = buffer.readItemStack();
            ItemStack secondaryOutput = buffer.readItemStack();

            return new SawmillRecipe(recipeID, output, secondaryOutput, input);
        }

        @Override
        public void write(PacketByteBuf buffer, SawmillRecipe recipe) {
            recipe.input.write(buffer);
            buffer.writeItemStack(recipe.output);
            buffer.writeItemStack(recipe.secondaryOutput);
        }
    }
}
