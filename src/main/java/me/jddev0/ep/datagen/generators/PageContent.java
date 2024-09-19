package me.jddev0.ep.datagen.generators;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public record PageContent(
        ResourceLocation pageId, Component chapterTitleComponent, Component pageComponent,
        ResourceLocation[] imageResourceLocations, ResourceLocation[] blockResourceLocations
) {
    public PageContent(Component chapterTitleComponent, Component pageComponent, ResourceLocation[] imageResourceLocations,
                       ResourceLocation[] blockResourceLocations) {
        this(null, chapterTitleComponent, pageComponent, imageResourceLocations, blockResourceLocations);
    }

    public PageContent withPageId(ResourceLocation pageId) {
        return new PageContent(pageId, chapterTitleComponent, pageComponent, imageResourceLocations, blockResourceLocations);
    }

    public JsonElement toJson() {
        JsonObject jsonObject = new JsonObject();

        if(chapterTitleComponent != null)
            jsonObject.add("title", Component.Serializer.toJsonTree(chapterTitleComponent));

        if(pageComponent != null)
            jsonObject.add("content", Component.Serializer.toJsonTree(pageComponent));

        if(imageResourceLocations != null) {
            if(imageResourceLocations.length == 1) {
                jsonObject.addProperty("image", imageResourceLocations[0].toString());
            }else {
                JsonArray imageJson = new JsonArray();

                for(ResourceLocation imageResourceLocation:imageResourceLocations)
                    imageJson.add(imageResourceLocation.toString());

                jsonObject.add("image", imageJson);
            }
        }

        if(blockResourceLocations != null) {
            if(blockResourceLocations.length == 1) {
                jsonObject.addProperty("block", blockResourceLocations[0].toString());
            }else {
                JsonArray imageJson = new JsonArray();

                for(ResourceLocation blockResourceLocation:blockResourceLocations)
                    imageJson.add(blockResourceLocation.toString());

                jsonObject.add("block", imageJson);
            }
        }

        return jsonObject;
    }
}
