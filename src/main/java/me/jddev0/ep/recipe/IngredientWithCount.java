package me.jddev0.ep.recipe;

import com.google.gson.JsonObject;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.JsonHelper;

public record IngredientWithCount(Ingredient input, int count) {
    public static final IngredientWithCount EMPTY = new IngredientWithCount(Ingredient.EMPTY, 0);

    public boolean isEmpty() {
        return input.isEmpty() || count <= 0;
    }

    public static IngredientWithCount fromJson(JsonObject json) {
        Ingredient input = Ingredient.fromJson(json.get("input"));
        int count = json.has("count")?JsonHelper.getInt(json, "count"):1;

        return new IngredientWithCount(input, count);
    }

    public static IngredientWithCount fromNetwork(PacketByteBuf buffer) {
        int count = buffer.readInt();
        if(count <= 0)
            return IngredientWithCount.EMPTY;

        Ingredient input = Ingredient.fromPacket(buffer);

        return new IngredientWithCount(input, count);
    }

    public void toNetwork(PacketByteBuf buffer) {
        if(isEmpty()) {
            buffer.writeInt(0);
        }else {
            buffer.writeInt(count);
            input.write(buffer);
        }
    }
}