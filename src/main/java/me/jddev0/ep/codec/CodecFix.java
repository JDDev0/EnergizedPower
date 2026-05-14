package me.jddev0.ep.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;

public final class CodecFix {
    private CodecFix() {}

    public static final Codec<ItemStack> ITEM_STACK_CODEC = Codec.lazyInitialized(() -> {
        return RecordCodecBuilder.create((instance) -> {
            return instance.group(ItemStack.ITEM_NON_AIR_CODEC.fieldOf("item").forGetter(ItemStack::getItemHolder),
                    ExtraCodecs.POSITIVE_INT.optionalFieldOf("count", 1).forGetter(ItemStack::getCount),
                    DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY).forGetter(ItemStack::getComponentsPatch)).
                    apply(instance, ItemStack::new);
        });
    });

    public static final Codec<Long> NON_NEGATIVE_LONG = Codec.LONG.validate(value -> {
        if(value >= 0)
            return DataResult.success(value);

        return DataResult.error(() -> "Value must be non-negative: " + value);
    });
}
