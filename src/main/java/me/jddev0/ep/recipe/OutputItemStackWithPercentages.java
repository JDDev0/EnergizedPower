package me.jddev0.ep.recipe;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.codec.CodecFix;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;

import java.util.Arrays;
import java.util.List;

public record OutputItemStackWithPercentages(ItemStack output, double[] percentages) {
    public static final OutputItemStackWithPercentages EMPTY = new OutputItemStackWithPercentages(ItemStack.EMPTY, new double[0]);

    public boolean isEmpty() {
        return output.isEmpty() || percentages.length == 0;
    }

    private static final Codec<double[]> DOUBLE_ARRAY_CODEC = new Codec<>() {
        private static final Codec<List<Double>> DOUBLE_LIST_CODEC = Codec.doubleRange(0, 1).listOf();

        @Override
        public <T> DataResult<Pair<double[], T>> decode(DynamicOps<T> ops, T input) {
            return DOUBLE_LIST_CODEC.decode(ops, input).map(res -> {
                return Pair.of(res.getFirst().stream().mapToDouble(Double::doubleValue).toArray(), res.getSecond());
            });
        }

        @Override
        public <T> DataResult<T> encode(double[] input, DynamicOps<T> ops, T prefix) {
            return DOUBLE_LIST_CODEC.encode(Arrays.stream(input).boxed().toList(), ops, prefix);
        }
    };

    public static final Codec<OutputItemStackWithPercentages> CODEC_NONEMPTY = RecordCodecBuilder.create((instance) -> {
        return instance.group(CodecFix.ITEM_STACK_CODEC.fieldOf("output").forGetter((output) -> {
            return output.output;
        }), DOUBLE_ARRAY_CODEC.fieldOf("percentages").forGetter((output) -> {
            return output.percentages;
        })).apply(instance, OutputItemStackWithPercentages::new);
    });

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