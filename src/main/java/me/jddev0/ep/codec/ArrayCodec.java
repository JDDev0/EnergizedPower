package me.jddev0.ep.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

import java.util.Arrays;
import java.util.List;
import java.util.function.IntFunction;

public class ArrayCodec<A> implements Codec<A[]> {
    private final Codec<List<A>> listCodec;
    private final IntFunction<A[]> arrayGenerator;

    public ArrayCodec(Codec<A> elementCodec, IntFunction<A[]> arrayGenerator) {
        this.listCodec = elementCodec.listOf();
        this.arrayGenerator = arrayGenerator;
    }

    @Override
    public <T> DataResult<Pair<A[], T>> decode(DynamicOps<T> ops, T input) {
        return listCodec.decode(ops, input).map(res -> {
            return Pair.of(res.getFirst().toArray(arrayGenerator), res.getSecond());
        });
    }

    @Override
    public <T> DataResult<T> encode(A[] input, DynamicOps<T> ops, T prefix) {
        return listCodec.encode(Arrays.asList(input), ops, prefix);
    }
}
