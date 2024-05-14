package me.jddev0.ep.recipe;

import com.google.gson.JsonArray;
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

public class AssemblingMachineRecipe implements Recipe<Inventory> {
    private final Identifier id;
    private final ItemStack output;
    private final IngredientWithCount[] inputs;

    public AssemblingMachineRecipe(Identifier id, ItemStack output, IngredientWithCount[] inputs) {
        this.id = id;
        this.output = output;
        this.inputs = inputs;
    }

    public ItemStack getOutput() {
        return output;
    }

    public IngredientWithCount[] getInputs() {
        return inputs;
    }

    @Override
    public boolean matches(Inventory container, World level) {
        if(level.isClient())
            return false;

        boolean[] usedIndices = new boolean[4];
        for(int i = 0;i < 4;i++)
            usedIndices[i] = container.getStack(i).isEmpty();

        int len = Math.min(inputs.length, 4);
        for(int i = 0;i < len;i++) {
            IngredientWithCount input = inputs[i];

            int indexMinCount = -1;
            int minCount = Integer.MAX_VALUE;

            for(int j = 0;j < 4;j++) {
                if(usedIndices[j])
                    continue;

                ItemStack item = container.getStack(j);

                if((indexMinCount == -1 || item.getCount() < minCount) && input.input.test(item) &&
                        item.getCount() >= input.count) {
                    indexMinCount = j;
                    minCount = item.getCount();
                }
            }

            if(indexMinCount == -1)
                return false; //Ingredient did not match any item

            usedIndices[indexMinCount] = true;
        }

        for(boolean usedIndex:usedIndices)
            if(!usedIndex) //Unused items present
                return false;

        return true;
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
    public ItemStack getOutput(DynamicRegistryManager registryAccess) {
        return output.copy();
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(ModBlocks.ASSEMBLING_MACHINE_ITEM);
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

    public static final class Type implements RecipeType<AssemblingMachineRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "assembling_machine";
    }

    public static final class Serializer implements RecipeSerializer<AssemblingMachineRecipe> {
        private Serializer() {}

        public static final Serializer INSTANCE = new Serializer();
        public static final Identifier ID = new Identifier(EnergizedPowerMod.MODID, "assembling_machine");

        @Override
        public AssemblingMachineRecipe read(Identifier recipeID, JsonObject json) {
            JsonArray inputsJson = JsonHelper.getArray(json, "inputs");
            IngredientWithCount[] inputs = new IngredientWithCount[inputsJson.size()];
            for(int i = 0;i < inputsJson.size();i++) {
                JsonObject inputJson = inputsJson.get(i).getAsJsonObject();

                Ingredient input = Ingredient.fromJson(inputJson.get("input"));
                int count = inputJson.has("count")?JsonHelper.getInt(inputJson, "count"):1;

                inputs[i] = new IngredientWithCount(input, count);
            }

            ItemStack output = ShapedRecipe.outputFromJson(JsonHelper.getObject(json, "output"));


            return new AssemblingMachineRecipe(recipeID, output, inputs);
        }

        @Override
        public AssemblingMachineRecipe read(Identifier recipeID, PacketByteBuf buffer) {
            int len = buffer.readInt();
            IngredientWithCount[] inputs = new IngredientWithCount[len];
            for(int i = 0;i < len;i++) {
                Ingredient input = Ingredient.fromPacket(buffer);
                int count = buffer.readInt();

                inputs[i] = new IngredientWithCount(input, count);
            }

            ItemStack output = buffer.readItemStack();

            return new AssemblingMachineRecipe(recipeID, output, inputs);
        }

        @Override
        public void write(PacketByteBuf buffer, AssemblingMachineRecipe recipe) {
            buffer.writeInt(recipe.inputs.length);
            for(int i = 0; i < recipe.inputs.length; i++) {
                recipe.inputs[i].input.write(buffer);
                buffer.writeInt(recipe.inputs[i].count);
            }

            buffer.writeItemStack(recipe.output);
        }
    }

    public record IngredientWithCount(Ingredient input, int count) {}
}
