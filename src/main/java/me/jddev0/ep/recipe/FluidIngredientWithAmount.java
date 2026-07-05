package me.jddev0.ep.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.handler.codec.DecoderException;
import me.jddev0.ep.codec.CodecFix;
import me.jddev0.ep.fluid.FluidStack;
import me.jddev0.ep.util.FluidUtils;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;

public record FluidIngredientWithAmount(FluidIngredient fluid, long dropletsAmount) {
    public static final Codec<FluidIngredientWithAmount> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(FluidIngredient.CODEC.fieldOf("ingredient").forGetter((input) -> {
            return input.fluid;
        }), CodecFix.NON_NEGATIVE_LONG.fieldOf("amount").forGetter((input) -> {
            return FluidUtils.convertDropletsToMilliBuckets(input.dropletsAmount);
        }), CodecFix.NON_NEGATIVE_LONG.optionalFieldOf("leftoverDropletsAmount", 0L).forGetter(input -> {
            long milliBucketsAmount = FluidUtils.convertDropletsToMilliBuckets(input.dropletsAmount);
            return input.dropletsAmount - FluidUtils.convertMilliBucketsToDroplets(milliBucketsAmount);
        })).apply(instance, (ingredient, milliBucketsAmount, leftoverDropletsAmount) -> {
            long dropletsAmount = FluidUtils.convertMilliBucketsToDroplets(milliBucketsAmount) + leftoverDropletsAmount;

            return new FluidIngredientWithAmount(ingredient, dropletsAmount);
        });
    });

    public static final StreamCodec<RegistryFriendlyByteBuf, FluidIngredientWithAmount> STREAM_CODEC = new StreamCodec<>() {
        @Override
        @NotNull
        public FluidIngredientWithAmount decode(@NotNull RegistryFriendlyByteBuf buffer) {
            long dropletsAmount = buffer.readLong();
            if(dropletsAmount <= 0)
                throw new DecoderException("Empty FluidIngredientWithAmount not allowed");

            FluidIngredient fluid = FluidIngredient.STREAM_CODEC.decode(buffer);
            return new FluidIngredientWithAmount(fluid, dropletsAmount);
        }

        @Override
        public void encode(@NotNull RegistryFriendlyByteBuf buffer, FluidIngredientWithAmount ingredient) {
            if(ingredient.dropletsAmount <= 0)
                throw new DecoderException("Empty FluidIngredientWithAmount not allowed");

            buffer.writeLong(ingredient.dropletsAmount);
            FluidIngredient.STREAM_CODEC.encode(buffer, ingredient.fluid);
        }
    };

    public long milliBucketsAmount() {
        return FluidUtils.convertDropletsToMilliBuckets(dropletsAmount);
    }

    public boolean test(FluidStack fluid) {
        return fluid.getDropletsAmount() >= this.dropletsAmount && this.fluid.test(fluid);
    }

    public boolean test(FluidVariant fluid, long dropletsAmount) {
        return dropletsAmount >= this.dropletsAmount && this.fluid.test(fluid);
    }

    public boolean test(FluidState fluid, long dropletsAmount) {
        return dropletsAmount >= this.dropletsAmount && this.fluid.test(fluid);
    }
}
