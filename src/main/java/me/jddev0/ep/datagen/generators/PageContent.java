package me.jddev0.ep.datagen.generators;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.Identifier;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public record PageContent(
        Identifier pageId, Component chapterTitleComponent, Component pageComponent,
        Identifier[] imageIdentifiers, Identifier[] blockIdentifiers,
        Map<Integer, Identifier> changePageIntToId
) {
    public PageContent(Component chapterTitleComponent, Component pageComponent, Identifier[] imageIdentifiers,
                       Identifier[] blockIdentifiers, Map<Integer, Identifier> changePageIntToId) {
        this(null, chapterTitleComponent, pageComponent, imageIdentifiers, blockIdentifiers,
                changePageIntToId == null?null:new HashMap<>(changePageIntToId));
    }

    public PageContent withPageId(Identifier pageId) {
        return new PageContent(pageId, chapterTitleComponent, pageComponent, imageIdentifiers, blockIdentifiers,
                changePageIntToId == null?null:new HashMap<>(changePageIntToId));
    }

    public static final Codec<PageContent> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(ComponentSerialization.CODEC.optionalFieldOf("title").
                                forGetter(pageContent -> Optional.ofNullable(pageContent.chapterTitleComponent)),
                        ComponentSerialization.CODEC.optionalFieldOf("content").
                                forGetter(pageContent -> Optional.ofNullable(pageContent.pageComponent)),
                        Codec.either(Identifier.CODEC, Codec.list(Identifier.CODEC)).optionalFieldOf("image").
                                forGetter(pageContent -> {
                                    if(pageContent.imageIdentifiers == null)
                                        return Optional.empty();

                                    if(pageContent.imageIdentifiers.length == 1)
                                        return Optional.of(Either.left(pageContent.imageIdentifiers[0]));

                                    return Optional.of(Either.right(Arrays.asList(pageContent.imageIdentifiers)));
                                }),
                        Codec.either(Identifier.CODEC, Codec.list(Identifier.CODEC)).optionalFieldOf("block").
                                forGetter(pageContent -> {
                                    if(pageContent.blockIdentifiers == null)
                                        return Optional.empty();

                                    if(pageContent.blockIdentifiers.length == 1)
                                        return Optional.of(Either.left(pageContent.blockIdentifiers[0]));

                                    return Optional.of(Either.right(Arrays.asList(pageContent.blockIdentifiers)));
                                }),
                        Codec.unboundedMap(Codec.STRING, Identifier.CODEC).optionalFieldOf("changePageIntToId").
                                forGetter(pageContent -> {
                                    if(pageContent.changePageIntToId == null || pageContent.changePageIntToId.isEmpty())
                                        return Optional.empty();

                                    return Optional.of(pageContent.changePageIntToId.entrySet().stream().
                                            collect(Collectors.toMap(entry -> entry.getKey().toString(), Map.Entry::getValue)));
                                })).
                apply(instance, (title, content, image, block, changePageIntToId) -> new PageContent(
                        title.orElse(null), content.orElse(null),
                        image.map(either -> either.map(singleImage -> new Identifier[] {
                                singleImage
                        }, imageList -> imageList.toArray(Identifier[]::new))).orElse(null),
                        block.map(either -> either.map(singleImage -> new Identifier[] {
                                singleImage
                        }, imageList -> imageList.toArray(Identifier[]::new))).orElse(null),
                        changePageIntToId.map(map -> map.entrySet().stream().
                                collect(Collectors.toMap(entry -> Integer.parseInt(entry.getKey()), Map.Entry::getValue))).orElse(null)
                ));
    });

}
