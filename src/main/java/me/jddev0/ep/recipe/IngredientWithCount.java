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

    public static final PacketCodec<RegistryByteBuf, IngredientWithCount> OPTIONAL_STREAM_CODEC = new PacketCodec<>() {
        @Override
        @NotNull
        public IngredientWithCount decode(@NotNull RegistryByteBuf buffer) {
            int count = buffer.readInt();
            if(count <= 0)
                return IngredientWithCount.EMPTY;

            Ingredient input = Ingredient.PACKET_CODEC.decode(buffer);
            return new IngredientWithCount(input, count);
        }

        @Override
        public void encode(@NotNull RegistryByteBuf buffer, IngredientWithCount ingredient) {
            if(ingredient.isEmpty()) {
                buffer.writeInt(0);
            }else {
                buffer.writeInt(ingredient.count);
                Ingredient.PACKET_CODEC.encode(buffer, ingredient.input);
            }
        }
    };

    public static final PacketCodec<RegistryByteBuf, IngredientWithCount> STREAM_CODEC = new PacketCodec<>() {
        @Override
        @NotNull
        public IngredientWithCount decode(@NotNull RegistryByteBuf buffer) {
            IngredientWithCount ingredient = IngredientWithCount.OPTIONAL_STREAM_CODEC.decode(buffer);
            if(ingredient.isEmpty())
                throw new DecoderException("Empty IngredientWithCount not allowed");

            return ingredient;
        }

        @Override
        public void encode(@NotNull RegistryByteBuf buffer, IngredientWithCount ingredient) {
            if(ingredient.isEmpty())
                throw new DecoderException("Empty IngredientWithCount not allowed");

            IngredientWithCount.OPTIONAL_STREAM_CODEC.encode(buffer, ingredient);
        }
    };
}
