package me.jddev0.ep.recipe;

import com.google.gson.JsonObject;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.World;

public class MetalPressRecipe implements Recipe<Inventory> {
    private final Identifier id;
    private final ItemStack output;
    private final ItemStack pressMold;
    private final Ingredient input;
    private final int inputCount;

    public MetalPressRecipe(Identifier id, ItemStack output, ItemStack pressMold, Ingredient input) {
        this(id, output, pressMold, input, 1);
    }

    public MetalPressRecipe(Identifier id, ItemStack output, ItemStack pressMold, Ingredient input, int inputCount) {
        this.id = id;
        this.output = output;
        this.pressMold = pressMold;
        this.input = input;
        this.inputCount = inputCount;
    }

    public ItemStack getOutputItem() {
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
    public boolean matches(Inventory container, World level) {
        if(level.isClient())
            return false;

        return input.test(container.getStack(0)) && container.getStack(0).getCount() >= inputCount &&
                ItemStack.canCombine(pressMold, container.getStack(1));
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
    public ItemStack createIcon() {
        return new ItemStack(ModBlocks.METAL_PRESS_ITEM);
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

    public static final class Type implements RecipeType<MetalPressRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "metal_press";
    }

    public static final class Serializer implements RecipeSerializer<MetalPressRecipe> {
        private Serializer() {}

        public static final Serializer INSTANCE = new Serializer();
        public static final Identifier ID = new Identifier(EnergizedPowerMod.MODID, "metal_press");

        @Override
        public MetalPressRecipe read(Identifier recipeID, JsonObject json) {
            Ingredient input = Ingredient.fromJson(json.get("ingredient"));
            ItemStack pressMold = ShapedRecipe.outputFromJson(JsonHelper.getObject(json, "pressMold"));
            ItemStack output = ShapedRecipe.outputFromJson(JsonHelper.getObject(json, "output"));


            if(json.has("inputCount")) {
                int inputCount = JsonHelper.getInt(json, "inputCount");;

                return new MetalPressRecipe(recipeID, output, pressMold, input, inputCount);
            }

            return new MetalPressRecipe(recipeID, output, pressMold, input);
        }

        @Override
        public MetalPressRecipe read(Identifier recipeID, PacketByteBuf buffer) {
            Ingredient input = Ingredient.fromPacket(buffer);
            int inputCount = buffer.readInt();
            ItemStack pressMold = buffer.readItemStack();
            ItemStack output = buffer.readItemStack();

            return new MetalPressRecipe(recipeID, output, pressMold, input, inputCount);
        }

        @Override
        public void write(PacketByteBuf buffer, MetalPressRecipe recipe) {
            recipe.input.write(buffer);
            buffer.writeInt(recipe.inputCount);
            buffer.writeItemStack(recipe.pressMold);
            buffer.writeItemStack(recipe.output);
        }
    }
}
