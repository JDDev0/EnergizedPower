package me.jddev0.ep.recipe;

import com.google.gson.JsonObject;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class ChargerRecipe implements Recipe<SimpleInventory> {
    private final Identifier id;
    private final ItemStack output;
    private final Ingredient input;
    private final int energyConsumption;

    public ChargerRecipe(Identifier id, ItemStack output, Ingredient input, int energyNeeds) {
        this.id = id;
        this.output = output;
        this.input = input;
        this.energyConsumption = energyNeeds;
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
    public boolean matches(SimpleInventory container, World level) {
        if(level.isClient())
            return false;

        return input.test(container.getStack(0));
    }

    @Override
    public ItemStack craft(SimpleInventory container) {
        return output;
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getOutput() {
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
        return new ItemStack(ModBlocks.CHARGER_ITEM);
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static final class Type implements RecipeType<ChargerRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "charger";
    }

    public static final class Serializer implements RecipeSerializer<ChargerRecipe> {
        private Serializer() {}

        public static final Serializer INSTANCE = new Serializer();
        public static final Identifier ID = new Identifier(EnergizedPowerMod.MODID, "charger");

        @Override
        public ChargerRecipe read(Identifier recipeID, JsonObject json) {
            Ingredient input = Ingredient.fromJson(json.get("ingredient"));
            int energyConsumption = JsonHelper.getInt(json, "energy");
            ItemStack output = ShapedRecipe.outputFromJson(JsonHelper.getObject(json, "output"));

            return new ChargerRecipe(recipeID, output, input, energyConsumption);
        }

        @Override
        public ChargerRecipe read(Identifier recipeID, PacketByteBuf buffer) {
            Ingredient input = Ingredient.fromPacket(buffer);
            int energyConsumption = buffer.readInt();
            ItemStack output = buffer.readItemStack();

            return new ChargerRecipe(recipeID, output, input, energyConsumption);
        }

        @Override
        public void write(PacketByteBuf buffer, ChargerRecipe recipe) {
            recipe.input.write(buffer);
            buffer.writeInt(recipe.energyConsumption);
            buffer.writeItemStack(recipe.output);
        }
    }
}
