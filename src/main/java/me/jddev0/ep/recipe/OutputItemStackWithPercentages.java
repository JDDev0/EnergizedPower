package me.jddev0.ep.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.ShapedRecipe;

public record OutputItemStackWithPercentages(ItemStack output, double[] percentages) {
    public static final OutputItemStackWithPercentages EMPTY = new OutputItemStackWithPercentages(ItemStack.EMPTY, new double[0]);

    public boolean isEmpty() {
        return output.isEmpty() || percentages.length == 0;
    }

    public static OutputItemStackWithPercentages fromJson(JsonObject json) {
        ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "output"));

        JsonArray percentagesJson = GsonHelper.getAsJsonArray(json, "percentages");
        double[] percentages = new double[percentagesJson.size()];
        for(int j = 0;j < percentagesJson.size();j++) {
            double value = percentagesJson.get(j).getAsDouble();

            percentages[j] = value;
        }

        return new OutputItemStackWithPercentages(output, percentages);
    }

    public static OutputItemStackWithPercentages fromNetwork(FriendlyByteBuf buffer) {
        int percentageCount = buffer.readInt();
        if(percentageCount <= 0)
            return OutputItemStackWithPercentages.EMPTY;

        double[] percentages = new double[percentageCount];
        for(int j = 0;j < percentageCount;j++)
            percentages[j] = buffer.readDouble();

        ItemStack output = buffer.readItem();

        return new OutputItemStackWithPercentages(output, percentages);
    }

    public void toNetwork(FriendlyByteBuf buffer) {
        if(isEmpty()) {
            buffer.writeInt(0);
        }else {
            buffer.writeInt(percentages.length);
            for(double percentage:percentages)
                buffer.writeDouble(percentage);

            buffer.writeItemStack(output, false);
        }
    }
}
