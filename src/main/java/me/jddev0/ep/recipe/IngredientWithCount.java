package me.jddev0.ep.recipe;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;

public record IngredientWithCount(Ingredient input, int count) {
    public static final IngredientWithCount EMPTY = new IngredientWithCount(Ingredient.EMPTY, 0);

    public boolean isEmpty() {
        return input.isEmpty() || count <= 0;
    }

    public static IngredientWithCount fromJson(JsonObject json) {
        Ingredient input = Ingredient.fromJson(json.get("input"));
        int count = json.has("count")?GsonHelper.getAsInt(json, "count"):1;

        return new IngredientWithCount(input, count);
    }

    public static IngredientWithCount fromNetwork(FriendlyByteBuf buffer) {
        int count = buffer.readInt();
        if(count <= 0)
            return IngredientWithCount.EMPTY;

        Ingredient input = Ingredient.fromNetwork(buffer);

        return new IngredientWithCount(input, count);
    }

    public void toNetwork(FriendlyByteBuf buffer) {
        if(isEmpty()) {
            buffer.writeInt(0);
        }else {
            buffer.writeInt(count);
            input.toNetwork(buffer);
        }
    }
}
