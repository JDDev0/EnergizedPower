package me.jddev0.ep.recipe;

import com.google.gson.JsonObject;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.ModBlocks;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class ChargerRecipe implements Recipe<Container> {
    private final ResourceLocation id;
    private final ItemStack output;
    private final Ingredient input;
    private final int energyConsumption;

    public ChargerRecipe(ResourceLocation id, ItemStack output, Ingredient input, int energyConsumption) {
        this.id = id;
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
        return new ItemStack(ModBlocks.CHARGER_ITEM.get());
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

    public static final class Type implements RecipeType<ChargerRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "charger";
    }

    public static final class Serializer implements RecipeSerializer<ChargerRecipe> {
        private Serializer() {}

        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = EPAPI.id("charger");

        @Override
        public ChargerRecipe fromJson(ResourceLocation recipeID, JsonObject json) {
            Ingredient input = Ingredient.fromJson(json.get("ingredient"));
            int energyConsumption = GsonHelper.getAsInt(json, "energy");
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "output"));

            return new ChargerRecipe(recipeID, output, input, energyConsumption);
        }

        @Override
        public ChargerRecipe fromNetwork(ResourceLocation recipeID, FriendlyByteBuf buffer) {
            Ingredient input = Ingredient.fromNetwork(buffer);
            int energyConsumption = buffer.readInt();
            ItemStack output = buffer.readItem();

            return new ChargerRecipe(recipeID, output, input, energyConsumption);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, ChargerRecipe recipe) {
            recipe.input.toNetwork(buffer);
            buffer.writeInt(recipe.energyConsumption);
            buffer.writeItemStack(recipe.output, false);
        }
    }
}
