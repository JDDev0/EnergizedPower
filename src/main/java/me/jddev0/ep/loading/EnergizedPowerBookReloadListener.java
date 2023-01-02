package me.jddev0.ep.loading;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import me.jddev0.ep.screen.EnergizedPowerBookScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class EnergizedPowerBookReloadListener extends SimpleJsonResourceReloadListener {
    private static final Logger LOGGER = LogUtils.getLogger();

    public EnergizedPowerBookReloadListener() {
        super(new GsonBuilder().create(), "book_pages");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> elements, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        List<EnergizedPowerBookScreen.PageContent> pages = new LinkedList<>();

        List<Map.Entry<ResourceLocation, JsonElement>> elementEntries = elements.entrySet().stream().
                sorted(Comparator.comparing(o -> o.getKey().getPath())).
                collect(Collectors.toList());

        for(Map.Entry<ResourceLocation, JsonElement> elementEntry:elementEntries) {
            ResourceLocation resourceLocation = elementEntry.getKey();
            JsonElement element = elementEntry.getValue();

            try {
                if(!element.isJsonObject()) {
                    LOGGER.error(String.format("Failed to load energized power book page '%s' from data pack '%s': Element must be a JSON Object",
                            resourceLocation.getPath(), resourceLocation.getNamespace()));

                    continue;
                }

                JsonObject object = element.getAsJsonObject();

                Component titleComponent = null;
                if(object.has("title"))
                    titleComponent = Component.Serializer.fromJson(object.get("title"));

                Component contentComponent;
                if(object.has("content"))
                    contentComponent = Component.Serializer.fromJson(object.get("content"));
                else
                    contentComponent = Component.empty();

                ResourceLocation imageResourceLocation = null;
                if(object.has("image")) {
                    JsonElement imageElement = object.get("image");
                    if(!imageElement.isJsonPrimitive() || !imageElement.getAsJsonPrimitive().isString()) {
                        LOGGER.error(String.format("Failed to load energized power book page '%s' from data pack '%s': image must be a string primitive",
                                resourceLocation.getPath(), resourceLocation.getNamespace()));

                        continue;
                    }

                    imageResourceLocation = ResourceLocation.tryParse(imageElement.getAsJsonPrimitive().getAsString());
                }

                ResourceLocation blockResourceLocation = null;
                if(object.has("block")) {
                    JsonElement imageElement = object.get("block");
                    if(!imageElement.isJsonPrimitive() || !imageElement.getAsJsonPrimitive().isString()) {
                        LOGGER.error(String.format("Failed to load energized power book page '%s' from data pack '%s': block must be a string primitive",
                                resourceLocation.getPath(), resourceLocation.getNamespace()));

                        continue;
                    }

                    blockResourceLocation = ResourceLocation.tryParse(imageElement.getAsJsonPrimitive().getAsString());
                }

                pages.add(new EnergizedPowerBookScreen.PageContent(titleComponent, contentComponent, imageResourceLocation, blockResourceLocation));
            }catch(Exception e) {
                LOGGER.error(String.format("Failed to load energized power book page '%s' from data pack '%s'",
                        resourceLocation.getPath(), resourceLocation.getNamespace()), e);
            }
        }

        EnergizedPowerBookScreen.pages = pages;
    }
}
