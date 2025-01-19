package me.jddev0.ep.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.handler.codec.DecoderException;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.dynamic.Codecs;
import org.jetbrains.annotations.NotNull;

public record IngredientWithCount(Ingredient input, int count) {
    public IngredientWithCount(Ingredient input) {
        this(input, 1);
    }

    public static final Codec<IngredientWithCount> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Ingredient.CODEC.fieldOf("ingredient").forGetter((input) -> {
            return input.input;
        }), Codecs.POSITIVE_INT.optionalFieldOf("count", 1).forGetter((input) -> {
            return input.count;
        })).apply(instance, IngredientWithCount::new);
    });

    public static final PacketCodec<RegistryByteBuf, IngredientWithCount> STREAM_CODEC = new PacketCodec<>() {
        @Override
        @NotNull
        public IngredientWithCount decode(@NotNull RegistryByteBuf buffer) {
            int count = buffer.readInt();
            if(count <= 0)
                throw new DecoderException("Empty IngredientWithCount not allowed");

            Ingredient input = Ingredient.PACKET_CODEC.decode(buffer);
            return new IngredientWithCount(input, count);
        }

        @Override
        public void encode(@NotNull RegistryByteBuf buffer, IngredientWithCount ingredient) {
            if(ingredient.count <= 0)
                throw new DecoderException("Empty IngredientWithCount not allowed");

            buffer.writeInt(ingredient.count);
            Ingredient.PACKET_CODEC.encode(buffer, ingredient.input);
        }
    };
}
