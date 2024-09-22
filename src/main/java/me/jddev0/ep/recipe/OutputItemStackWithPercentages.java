package me.jddev0.ep.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.jddev0.ep.util.ItemStackUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.JsonHelper;

public record OutputItemStackWithPercentages(ItemStack output, double[] percentages) {
    public static final OutputItemStackWithPercentages EMPTY = new OutputItemStackWithPercentages(ItemStack.EMPTY, new double[0]);

    public OutputItemStackWithPercentages(ItemStack output, double percentage) {
        this(output, new double[] {
                percentage
        });
    }

    public OutputItemStackWithPercentages(ItemStack output) {
        this(output, 1.);
    }

    public boolean isEmpty() {
        return output.isEmpty() || percentages.length == 0;
    }

    public static OutputItemStackWithPercentages fromJson(JsonObject json) {
        ItemStack output = ItemStackUtils.fromJson(JsonHelper.getObject(json, "output"));

        JsonArray percentagesJson = JsonHelper.getArray(json, "percentages");
        double[] percentages = new double[percentagesJson.size()];
        for(int j = 0;j < percentagesJson.size();j++) {
            double value = percentagesJson.get(j).getAsDouble();

            percentages[j] = value;
        }

        return new OutputItemStackWithPercentages(output, percentages);
    }

    public static OutputItemStackWithPercentages fromNetwork(PacketByteBuf buffer) {
        int percentageCount = buffer.readInt();
        if(percentageCount <= 0)
            return OutputItemStackWithPercentages.EMPTY;

        double[] percentages = new double[percentageCount];
        for(int j = 0;j < percentageCount;j++)
            percentages[j] = buffer.readDouble();

        ItemStack output = buffer.readItemStack();

        return new OutputItemStackWithPercentages(output, percentages);
    }

    public void toNetwork(PacketByteBuf buffer) {
        if(isEmpty()) {
            buffer.writeInt(0);
        }else {
            buffer.writeInt(percentages.length);
            for(double percentage:percentages)
                buffer.writeDouble(percentage);

            buffer.writeItemStack(output);
        }
    }
}