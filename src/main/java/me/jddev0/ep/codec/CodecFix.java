package me.jddev0.ep.codec;

import com.mojang.serialization.Codec;
import net.minecraft.world.item.ItemStack;

public final class CodecFix {
    private CodecFix() {}

    public static final Codec<ItemStack> ITEM_STACK_CODEC = ItemStack.CODEC;
}
