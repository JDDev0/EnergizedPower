package me.jddev0.ep.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class AssemblingMachineRecipe implements Recipe<Container> {
    private final ResourceLocation id;
    private final ItemStack output;
    private final IngredientWithCount[] inputs;

    public AssemblingMachineRecipe(ResourceLocation id, ItemStack output, IngredientWithCount[] inputs) {
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
    public boolean matches(Container container, Level level) {
        if(level.isClientSide)
            return false;

        boolean[] usedIndices = new boolean[4];
        for(int i = 0;i < 4;i++)
            usedIndices[i] = container.getItem(i).isEmpty();

        int len = Math.min(inputs.length, 4);
        for(int i = 0;i < len;i++) {
            IngredientWithCount input = inputs[i];

            int indexMinCount = -1;
            int minCount = Integer.MAX_VALUE;

            for(int j = 0;j < 4;j++) {
                if(usedIndices[j])
                    continue;

                ItemStack item = container.getItem(j);

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
    public ItemStack assemble(Container container) {
        return output;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem() {
        return output.copy();
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(ModBlocks.ASSEMBLING_MACHINE_ITEM.get());
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

    public static final class Type implements RecipeType<AssemblingMachineRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "assembling_machine";
    }

    public static final class Serializer implements RecipeSerializer<AssemblingMachineRecipe> {
        private Serializer() {}

        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation(EnergizedPowerMod.MODID, "assembling_machine");

        @Override
        public AssemblingMachineRecipe fromJson(ResourceLocation recipeID, JsonObject json) {
            JsonArray inputsJson = GsonHelper.getAsJsonArray(json, "inputs");
            IngredientWithCount[] inputs = new IngredientWithCount[inputsJson.size()];
            for(int i = 0;i < inputsJson.size();i++) {
                JsonObject inputJson = inputsJson.get(i).getAsJsonObject();

                Ingredient input = Ingredient.fromJson(inputJson.get("input"));
                int count = inputJson.has("count")?GsonHelper.getAsInt(inputJson, "count"):1;

                inputs[i] = new IngredientWithCount(input, count);
            }

            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "output"));


            return new AssemblingMachineRecipe(recipeID, output, inputs);
        }

        @Override
        public AssemblingMachineRecipe fromNetwork(ResourceLocation recipeID, FriendlyByteBuf buffer) {
            int len = buffer.readInt();
            IngredientWithCount[] inputs = new IngredientWithCount[len];
            for(int i = 0;i < len;i++) {
                Ingredient input = Ingredient.fromNetwork(buffer);
                int count = buffer.readInt();

                inputs[i] = new IngredientWithCount(input, count);
            }

            ItemStack output = buffer.readItem();

            return new AssemblingMachineRecipe(recipeID, output, inputs);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, AssemblingMachineRecipe recipe) {
            buffer.writeInt(recipe.inputs.length);
            for(int i = 0; i < recipe.inputs.length; i++) {
                recipe.inputs[i].input.toNetwork(buffer);
                buffer.writeInt(recipe.inputs[i].count);
            }

            buffer.writeItemStack(recipe.output, false);
        }
    }

    public record IngredientWithCount(Ingredient input, int count) {}
}
