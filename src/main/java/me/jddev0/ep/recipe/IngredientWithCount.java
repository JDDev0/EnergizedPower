package me.jddev0.ep.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.dynamic.Codecs;

public record IngredientWithCount(Ingredient input, int count) {
    public static final IngredientWithCount EMPTY = new IngredientWithCount(Ingredient.EMPTY, 0);

    public boolean isEmpty() {
        return input.isEmpty() || count <= 0;
    }

    public static final Codec<IngredientWithCount> CODEC_NONEMPTY = RecordCodecBuilder.create((instance) -> {
        return instance.group(Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("input").forGetter((input) -> {
            return input.input;
        }), Codecs.POSITIVE_INT.optionalFieldOf("count", 1).forGetter((input) -> {
            return input.count;
        })).apply(instance, IngredientWithCount::new);
    });

    public static final Codec<IngredientWithCount> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Ingredient.ALLOW_EMPTY_CODEC.fieldOf("input").forGetter((input) -> {
            return input.input;
        }), Codec.INT.optionalFieldOf("count", 1).forGetter((input) -> {
            return input.count;
        })).apply(instance, IngredientWithCount::new);
    });

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