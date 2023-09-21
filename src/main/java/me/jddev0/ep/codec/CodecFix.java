package me.jddev0.ep.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;

import java.util.Optional;

public final class CodecFix {
    private CodecFix() {}

    public static final Codec<ItemStack> ITEM_STACK_CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Registries.ITEM.getCodec().fieldOf("item").forGetter(ItemStack::getItem),
                Codec.INT.optionalFieldOf("count", 1).forGetter(ItemStack::getCount),
                NbtCompound.CODEC.optionalFieldOf("tag").forGetter((stack) -> {
            return Optional.ofNullable(stack.getNbt());
        })).apply(instance, CodecFix::createItemStackFix);
    });

    private static ItemStack createItemStackFix(ItemConvertible item, int count, Optional<NbtCompound> nbt) {
        ItemStack itemStack = new ItemStack(item, count);
        nbt.ifPresent(itemStack::setNbt);
        return itemStack;
    }
}
