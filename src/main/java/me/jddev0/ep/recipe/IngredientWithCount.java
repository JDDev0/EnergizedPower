package me.jddev0.ep.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.crafting.Ingredient;

public record IngredientWithCount(Ingredient input, int count) {
    public static final IngredientWithCount EMPTY = new IngredientWithCount(Ingredient.EMPTY, 0);

    public boolean isEmpty() {
        return input.isEmpty() || count <= 0;
    }

    public static final Codec<IngredientWithCount> CODEC_NONEMPTY = RecordCodecBuilder.create((instance) -> {
        return instance.group(Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter((input) -> {
            return input.input;
        }), ExtraCodecs.POSITIVE_INT.optionalFieldOf("count", 1).forGetter((input) -> {
            return input.count;
        })).apply(instance, IngredientWithCount::new);
    });

    public static final Codec<IngredientWithCount> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Ingredient.CODEC.fieldOf("input").forGetter((input) -> {
            return input.input;
        }), Codec.INT.optionalFieldOf("count", 1).forGetter((input) -> {
            return input.count;
        })).apply(instance, IngredientWithCount::new);
    });

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
