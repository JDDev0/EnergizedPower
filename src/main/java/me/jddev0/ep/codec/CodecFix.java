package me.jddev0.ep.codec;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.IntFunction;

public final class CodecFix {
    private CodecFix() {}

    public static final Codec<ItemStack> SINGLE_ITEM_ITEM_STACK_CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(Item.CODEC_WITH_BOUND_COMPONENTS.fieldOf("id").forGetter(ItemStack::typeHolder),
                        DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY).forGetter(ItemStack::getComponentsPatch)).
                apply(instance, (id, components) -> new ItemStack(id, 1, components));
    });

    public static final Codec<Double> POSITIVE_DOUBLE = Codec.DOUBLE.
            validate(value -> value > 0?DataResult.success(value):DataResult.error(() -> "Value must be positive: " + value));

    public static <T> Codec<TagKey<T>> tagKeyHashedCodec(ResourceKey<? extends Registry<T>> registryName) {
        return Codec.STRING.comapFlatMap(
                name -> name.startsWith("#")?
                        Identifier.read(name.substring(1)).map(id -> TagKey.create(registryName, id)):
                        DataResult.error(() -> "Not a tag id"),
                e -> "#" + e.location()
        );
    }

    public static <T> Codec<Either<T[], T>> arrayOrSingleValueCodec(Codec<T> elementCodec, IntFunction<T[]> arrayGenerator) {
        return Codec.either(new ArrayCodec<>(elementCodec, arrayGenerator), elementCodec);
    }

    public static <T> Codec<Either<List<T>, T>> listOrSingleValueCodec(Codec<T> elementCodec) {
        return Codec.either(Codec.list(elementCodec), elementCodec);
    }

    public static <T> Codec<Either<List<ResourceKey<T>>, ResourceKey<T>>> listOrSingleResourceKeyCodec(
            ResourceKey<? extends Registry<T>> registryName) {
        return listOrSingleValueCodec(ResourceKey.codec(registryName));
    }

    public static <T> Codec<Either<Either<List<ResourceKey<T>>, ResourceKey<T>>, TagKey<T>>> listOrSingleResourceKeyOrSingleTagKeyCodec(
            ResourceKey<? extends Registry<T>> registryName) {
        return Codec.either(listOrSingleResourceKeyCodec(registryName), tagKeyHashedCodec(registryName));
    }
}
