package me.jddev0.ep.datagen.generators;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public record PageContent(
        ResourceLocation pageId, Component chapterTitleComponent, Component pageComponent,
        ResourceLocation[] imageResourceLocations, ResourceLocation[] blockResourceLocations,
        Map<Integer, ResourceLocation> changePageIntToId
) {
    public PageContent(Component chapterTitleComponent, Component pageComponent, ResourceLocation[] imageResourceLocations,
                       ResourceLocation[] blockResourceLocations, Map<Integer, ResourceLocation> changePageIntToId) {
        this(null, chapterTitleComponent, pageComponent, imageResourceLocations, blockResourceLocations,
                changePageIntToId == null?null:new HashMap<>(changePageIntToId));
    }

    public PageContent withPageId(ResourceLocation pageId) {
        return new PageContent(pageId, chapterTitleComponent, pageComponent, imageResourceLocations, blockResourceLocations,
                changePageIntToId == null?null:new HashMap<>(changePageIntToId));
    }

    public static final Codec<PageContent> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(ComponentSerialization.CODEC.optionalFieldOf("title").
                                forGetter(pageContent -> Optional.ofNullable(pageContent.chapterTitleComponent)),
                        ComponentSerialization.CODEC.optionalFieldOf("content").
                                forGetter(pageContent -> Optional.ofNullable(pageContent.pageComponent)),
                        Codec.either(ResourceLocation.CODEC, Codec.list(ResourceLocation.CODEC)).optionalFieldOf("image").
                                forGetter(pageContent -> {
                                    if(pageContent.imageResourceLocations == null)
                                        return Optional.empty();

                                    if(pageContent.imageResourceLocations.length == 1)
                                        return Optional.of(Either.left(pageContent.imageResourceLocations[0]));

                                    return Optional.of(Either.right(Arrays.asList(pageContent.imageResourceLocations)));
                                }),
                        Codec.either(ResourceLocation.CODEC, Codec.list(ResourceLocation.CODEC)).optionalFieldOf("block").
                                forGetter(pageContent -> {
                                    if(pageContent.blockResourceLocations == null)
                                        return Optional.empty();

                                    if(pageContent.blockResourceLocations.length == 1)
                                        return Optional.of(Either.left(pageContent.blockResourceLocations[0]));

                                    return Optional.of(Either.right(Arrays.asList(pageContent.blockResourceLocations)));
                                }),
                        Codec.unboundedMap(Codec.STRING, ResourceLocation.CODEC).optionalFieldOf("changePageIntToId").
                                forGetter(pageContent -> {
                                    if(pageContent.changePageIntToId == null || pageContent.changePageIntToId.isEmpty())
                                        return Optional.empty();

                                    return Optional.of(pageContent.changePageIntToId.entrySet().stream().
                                            collect(Collectors.toMap(entry -> entry.getKey().toString(), Map.Entry::getValue)));
                                })).
                apply(instance, (title, content, image, block, changePageIntToId) -> new PageContent(
                        title.orElse(null), content.orElse(null),
                        image.map(either -> either.map(singleImage -> new ResourceLocation[] {
                                singleImage
                        }, imageList -> imageList.toArray(ResourceLocation[]::new))).orElse(null),
                        block.map(either -> either.map(singleImage -> new ResourceLocation[] {
                                singleImage
                        }, imageList -> imageList.toArray(ResourceLocation[]::new))).orElse(null),
                        changePageIntToId.map(map -> map.entrySet().stream().
                                collect(Collectors.toMap(entry -> Integer.parseInt(entry.getKey()), Map.Entry::getValue))).orElse(null)
                ));
    });

}
