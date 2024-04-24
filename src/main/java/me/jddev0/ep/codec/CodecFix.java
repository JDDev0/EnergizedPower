package me.jddev0.ep.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.ComponentChanges;
import net.minecraft.item.ItemStack;
import net.minecraft.util.dynamic.Codecs;

public final class CodecFix {
    private CodecFix() {}

    public static final Codec<ItemStack> ITEM_STACK_CODEC = Codec.lazyInitialized(() -> {
        return RecordCodecBuilder.create((instance) -> {
            return instance.group(ItemStack.ITEM_CODEC.fieldOf("item").forGetter(ItemStack::getRegistryEntry),
                    Codecs.POSITIVE_INT.optionalFieldOf("count", 1).forGetter(ItemStack::getCount),
                    ComponentChanges.CODEC.optionalFieldOf("components", ComponentChanges.EMPTY).forGetter(ItemStack::getComponentChanges)).
                    apply(instance, ItemStack::new);
        });
    });
}
