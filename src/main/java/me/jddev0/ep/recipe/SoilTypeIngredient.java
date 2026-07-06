package me.jddev0.ep.recipe;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import me.jddev0.ep.codec.CodecFix;
import me.jddev0.ep.codec.StreamCodecFix;
import me.jddev0.ep.registry.EPRegistries;
import me.jddev0.ep.soil.SoilType;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class SoilTypeIngredient {
     public static final Codec<SoilTypeIngredient> CODEC = CodecFix.listOrSingleResourceKeyOrSingleTagKeyCodec(EPRegistries.SOIL_TYPE).
             xmap(value -> new SoilTypeIngredient(value.mapBoth(
                     soilTypes -> soilTypes.map(soilTypeList -> soilTypeList, Collections::singletonList),
                     soilType -> soilType
             )), ingredient -> ingredient.soilType.mapBoth(
                     soilTypes -> soilTypes.size() == 1?Either.right(soilTypes.getFirst()):Either.left(soilTypes), soilType -> soilType)
             );

    private static final StreamCodec<ByteBuf, ResourceKey<SoilType>> SOIL_TYPE_RESOURCE_KEY_STREAM_CODEC = ResourceKey.streamCodec(EPRegistries.SOIL_TYPE);
    private static final StreamCodec<ByteBuf, TagKey<SoilType>> SOIL_TYPE_TAG_KEY_STREAM_CODEC = StreamCodecFix.tagKeyStreamCodec(EPRegistries.SOIL_TYPE);

    public static final StreamCodec<RegistryFriendlyByteBuf, SoilTypeIngredient> STREAM_CODEC = StreamCodec.of((buffer, soilTypeIngredient) -> {
        buffer.writeBoolean(soilTypeIngredient.soilType.left().isPresent());
        soilTypeIngredient.soilType.map(
                soilTypes -> {
                    buffer.writeInt(soilTypes.size());
                    soilTypes.forEach(soilType ->
                            SOIL_TYPE_RESOURCE_KEY_STREAM_CODEC.encode(buffer, soilType));

                    return null;
                },
                soilType -> {
                    SOIL_TYPE_TAG_KEY_STREAM_CODEC.encode(buffer, soilType);

                    return null;
                }
        );
    }, buffer -> {
        if(buffer.readBoolean()) {
            int soilTypeCount = buffer.readInt();
            List<ResourceKey<SoilType>> soilTypes = new ArrayList<>(soilTypeCount);

            for(int i = 0;i < soilTypeCount;i++)
                soilTypes.add(SOIL_TYPE_RESOURCE_KEY_STREAM_CODEC.decode(buffer));

            return new SoilTypeIngredient(Either.left(soilTypes));
        }else {
            return new SoilTypeIngredient(Either.right(SOIL_TYPE_TAG_KEY_STREAM_CODEC.decode(buffer)));
        }
    });

    public static SoilTypeIngredient of(ResourceKey<SoilType> soilType) {
        return of(Collections.singletonList(soilType));
    }

    public static SoilTypeIngredient of(ResourceKey<SoilType>[] soilType) {
        return of(Arrays.asList(soilType));
    }

    public static SoilTypeIngredient of(List<ResourceKey<SoilType>> soilType) {
        return of(Either.left(soilType));
    }

    public static SoilTypeIngredient of(TagKey<SoilType> soilType) {
        return of(Either.right(soilType));
    }

    public static SoilTypeIngredient of(Either<List<ResourceKey<SoilType>>, TagKey<SoilType>> soilType) {
        return new SoilTypeIngredient(soilType);
    }

    private final Either<List<ResourceKey<SoilType>>, TagKey<SoilType>> soilType;

    private SoilTypeIngredient(Either<List<ResourceKey<SoilType>>, TagKey<SoilType>> soilType) {
        this.soilType = soilType;
    }

    public Either<List<ResourceKey<SoilType>>, TagKey<SoilType>> getSoilType() {
        return soilType;
    }

    public boolean test(ResourceKey<SoilType> soilType, HolderLookup.Provider registries) {
        return this.soilType.map(
                st -> st.stream().anyMatch(sti -> sti.location().equals(soilType.location())),
                st -> registries.lookupOrThrow(EPRegistries.SOIL_TYPE).getOrThrow(soilType).is(st)
        );
    }
}
