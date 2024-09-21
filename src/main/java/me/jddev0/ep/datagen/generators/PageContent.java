package me.jddev0.ep.datagen.generators;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public record PageContent(
        Identifier pageId, Text chapterTitleComponent, Text pageComponent,
        Identifier[] imageResourceLocations, Identifier[] blockResourceLocations
) {
    public PageContent(Text chapterTitleComponent, Text pageComponent, Identifier[] imageResourceLocations,
                       Identifier[] blockResourceLocations) {
        this(null, chapterTitleComponent, pageComponent, imageResourceLocations, blockResourceLocations);
    }

    public PageContent withPageId(Identifier pageId) {
        return new PageContent(pageId, chapterTitleComponent, pageComponent, imageResourceLocations, blockResourceLocations);
    }

    public JsonElement toJson() {
        JsonObject jsonObject = new JsonObject();

        if(chapterTitleComponent != null)
            jsonObject.add("title", Text.Serializer.toJsonTree(chapterTitleComponent));

        if(pageComponent != null)
            jsonObject.add("content", Text.Serializer.toJsonTree(pageComponent));

        if(imageResourceLocations != null) {
            if(imageResourceLocations.length == 1) {
                jsonObject.addProperty("image", imageResourceLocations[0].toString());
            }else {
                JsonArray imageJson = new JsonArray();

                for(Identifier imageResourceLocation:imageResourceLocations)
                    imageJson.add(imageResourceLocation.toString());

                jsonObject.add("image", imageJson);
            }
        }

        if(blockResourceLocations != null) {
            if(blockResourceLocations.length == 1) {
                jsonObject.addProperty("block", blockResourceLocations[0].toString());
            }else {
                JsonArray imageJson = new JsonArray();

                for(Identifier blockResourceLocation:blockResourceLocations)
                    imageJson.add(blockResourceLocation.toString());

                jsonObject.add("block", imageJson);
            }
        }

        return jsonObject;
    }
}
