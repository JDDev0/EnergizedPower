package me.jddev0.ep.datagen.generators;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public record PageContent(
        Identifier pageId, Text chapterTitleComponent, Text pageComponent,
        Identifier[] imageResourceLocations, Identifier[] blockResourceLocations,
        Map<Integer, Identifier> changePageIntToId
) {
    public PageContent(Text chapterTitleComponent, Text pageComponent, Identifier[] imageResourceLocations,
                       Identifier[] blockResourceLocations, Map<Integer, Identifier> changePageIntToId) {
        this(null, chapterTitleComponent, pageComponent, imageResourceLocations, blockResourceLocations,
                changePageIntToId == null?null:new HashMap<>(changePageIntToId));
    }

    public PageContent withPageId(Identifier pageId) {
        return new PageContent(pageId, chapterTitleComponent, pageComponent, imageResourceLocations, blockResourceLocations,
                changePageIntToId == null?null:new HashMap<>(changePageIntToId));
    }

    public static final Codec<PageContent> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(TextCodecs.CODEC.optionalFieldOf("title").
                                forGetter(pageContent -> Optional.ofNullable(pageContent.chapterTitleComponent)),
                        TextCodecs.CODEC.optionalFieldOf("content").
                                forGetter(pageContent -> Optional.ofNullable(pageContent.pageComponent)),
                        Codec.either(Identifier.CODEC, Codec.list(Identifier.CODEC)).optionalFieldOf("image").
                                forGetter(pageContent -> {
                                    if(pageContent.imageResourceLocations == null)
                                        return Optional.empty();

                                    if(pageContent.imageResourceLocations.length == 1)
                                        return Optional.of(Either.left(pageContent.imageResourceLocations[0]));

                                    return Optional.of(Either.right(Arrays.asList(pageContent.imageResourceLocations)));
                                }),
                        Codec.either(Identifier.CODEC, Codec.list(Identifier.CODEC)).optionalFieldOf("block").
                                forGetter(pageContent -> {
                                    if(pageContent.blockResourceLocations == null)
                                        return Optional.empty();

                                    if(pageContent.blockResourceLocations.length == 1)
                                        return Optional.of(Either.left(pageContent.blockResourceLocations[0]));

                                    return Optional.of(Either.right(Arrays.asList(pageContent.blockResourceLocations)));
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
