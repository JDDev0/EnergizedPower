package me.jddev0.ep.codec;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.Registry;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

public final class StreamCodecFix {
    private StreamCodecFix() {}

    public static <T> StreamCodec<ByteBuf, TagKey<T>> tagKeyStreamCodec(ResourceKey<? extends Registry<T>> registryName) {
        return ResourceLocation.STREAM_CODEC.map(id -> TagKey.create(registryName, id), TagKey::location);
    }
}
