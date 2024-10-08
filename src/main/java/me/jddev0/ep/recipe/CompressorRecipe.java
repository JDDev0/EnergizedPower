package me.jddev0.ep.recipe;

import com.google.gson.JsonObject;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.util.ItemStackUtils;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class CompressorRecipe implements Recipe<Inventory> {
    private final Identifier id;
    private final ItemStack output;
    private final Ingredient input;
    private final int inputCount;

    public CompressorRecipe(Identifier id, ItemStack output, Ingredient input, int inputCount) {
        this.id = id;
        this.output = output;
        this.input = input;
        this.inputCount = inputCount;
    }

    public ItemStack getOutputItem() {
        return output;
    }

    public Ingredient getInputItem() {
        return input;
    }

    public int getInputCount() {
        return inputCount;
    }

    @Override
    public boolean matches(Inventory container, World level) {
        if(level.isClient())
            return false;

        return input.test(container.getStack(0)) && container.getStack(0).getCount() >= inputCount;
    }

    @Override
    public ItemStack craft(Inventory container, DynamicRegistryManager registryManager) {
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
        return new ItemStack(ModBlocks.COMPRESSOR_ITEM);
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

    public static final class Type implements RecipeType<CompressorRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "compressor";
    }

    public static final class Serializer implements RecipeSerializer<CompressorRecipe> {
        private Serializer() {}

        public static final Serializer INSTANCE = new Serializer();
        public static final Identifier ID = EPAPI.id("compressor");

        @Override
        public CompressorRecipe read(Identifier recipeID, JsonObject json) {
            Ingredient input = Ingredient.fromJson(json.get("ingredient"));
            int inputCount = json.has("inputCount")?JsonHelper.getInt(json, "inputCount"):1;
            ItemStack output = ItemStackUtils.fromJson(JsonHelper.getObject(json, "output"));

            return new CompressorRecipe(recipeID, output, input, inputCount);
        }

        @Override
        public CompressorRecipe read(Identifier recipeID, PacketByteBuf buffer) {
            Ingredient input = Ingredient.fromPacket(buffer);
            int inputCount = buffer.readInt();
            ItemStack output = buffer.readItemStack();

            return new CompressorRecipe(recipeID, output, input, inputCount);
        }
        @Override
        public void write(PacketByteBuf buffer, CompressorRecipe recipe) {
            recipe.input.write(buffer);
            buffer.writeInt(recipe.inputCount);
            buffer.writeItemStack(recipe.output);
        }
    }
}
