package me.jddev0.ep.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.handler.codec.DecoderException;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import org.jetbrains.annotations.NotNull;

public record FluidIngredientWithAmount(FluidIngredient fluid, int amount) {
    public static final Codec<FluidIngredientWithAmount> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(FluidIngredient.CODEC.fieldOf("ingredient").forGetter((input) -> {
            return input.fluid;
        }), ExtraCodecs.POSITIVE_INT.fieldOf("amount").forGetter((input) -> {
            return input.amount;
        })).apply(instance, FluidIngredientWithAmount::new);
    });

    public static final StreamCodec<RegistryFriendlyByteBuf, FluidIngredientWithAmount> STREAM_CODEC = new StreamCodec<>() {
        @Override
        @NotNull
        public FluidIngredientWithAmount decode(@NotNull RegistryFriendlyByteBuf buffer) {
            int amount = buffer.readInt();
            if(amount <= 0)
                throw new DecoderException("Empty FluidIngredientWithAmount not allowed");

            FluidIngredient fluid = FluidIngredient.STREAM_CODEC.decode(buffer);
            return new FluidIngredientWithAmount(fluid, amount);
        }

        @Override
        public void encode(@NotNull RegistryFriendlyByteBuf buffer, FluidIngredientWithAmount ingredient) {
            if(ingredient.amount <= 0)
                throw new DecoderException("Empty FluidIngredientWithAmount not allowed");

            buffer.writeInt(ingredient.amount);
            FluidIngredient.STREAM_CODEC.encode(buffer, ingredient.fluid);
        }
    };

    public boolean test(FluidStack fluid) {
        return fluid.getAmount() >= this.amount && this.fluid.test(fluid);
    }

    public boolean test(FluidResource fluid, int amount) {
        return amount >= this.amount && this.fluid.test(fluid);
    }

    public boolean test(FluidState fluid, int amount) {
        return amount >= this.amount && this.fluid.test(fluid);
    }
}
