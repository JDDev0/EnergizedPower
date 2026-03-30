package me.jddev0.ep.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class CodecFix {
    private CodecFix() {}

    public static final Codec<ItemStack> SINGLE_ITEM_ITEM_STACK_CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(Item.CODEC_WITH_BOUND_COMPONENTS.fieldOf("id").forGetter(ItemStack::typeHolder),
                DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY).forGetter(ItemStack::getComponentsPatch)).
                apply(instance, (id, components) -> new ItemStack(id, 1, components));
    });

    public static final Codec<Long> NON_NEGATIVE_LONG = Codec.LONG.validate(value -> {
        if(value >= 0)
            return DataResult.success(value);

        return DataResult.error(() -> "Value must be non-negative: " + value);
    });
}
