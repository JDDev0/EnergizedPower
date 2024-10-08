package me.jddev0.ep.recipe;

import com.google.gson.JsonObject;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
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

public class EnergizerRecipe implements Recipe<Inventory> {
    private final Identifier id;
    private final ItemStack output;
    private final Ingredient input;
    private final int energyConsumption;

    public EnergizerRecipe(Identifier id, ItemStack output, Ingredient input, int energyConsumption) {
        this.id = id;
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
    public boolean matches(Inventory container, World level) {
        if(level.isClient())
            return false;

        return input.test(container.getStack(0));
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
        return new ItemStack(EPBlocks.ENERGIZER_ITEM);
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

    public static final class Type implements RecipeType<EnergizerRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "energizer";
    }

    public static final class Serializer implements RecipeSerializer<EnergizerRecipe> {
        private Serializer() {}

        public static final Serializer INSTANCE = new Serializer();
        public static final Identifier ID = EPAPI.id("energizer");

        @Override
        public EnergizerRecipe read(Identifier recipeID, JsonObject json) {
            Ingredient input = Ingredient.fromJson(json.get("ingredient"));
            int energyConsumption = JsonHelper.getInt(json, "energy");
            ItemStack output = ItemStackUtils.fromJson(JsonHelper.getObject(json, "output"));

            return new EnergizerRecipe(recipeID, output, input, energyConsumption);
        }

        @Override
        public EnergizerRecipe read(Identifier recipeID, PacketByteBuf buffer) {
            Ingredient input = Ingredient.fromPacket(buffer);
            int energyConsumption = buffer.readInt();
            ItemStack output = buffer.readItemStack();

            return new EnergizerRecipe(recipeID, output, input, energyConsumption);
        }

        @Override
        public void write(PacketByteBuf buffer, EnergizerRecipe recipe) {
            recipe.input.write(buffer);
            buffer.writeInt(recipe.energyConsumption);
            buffer.writeItemStack(recipe.output);
        }
    }
}
