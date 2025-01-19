package me.jddev0.ep.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.item.ItemStack;

public final class CodecFix {
    private CodecFix() {}

    public static final Codec<ItemStack> ITEM_STACK_CODEC = ItemStack.CODEC;

    public static final Codec<Long> NON_NEGATIVE_LONG = Codec.LONG.validate(value -> {
        if(value >= 0)
            return DataResult.success(value);

        return DataResult.error(() -> "Value must be non-negative: " + value);
    });
}
