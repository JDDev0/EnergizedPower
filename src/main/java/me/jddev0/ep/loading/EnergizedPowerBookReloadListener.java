package me.jddev0.ep.loading;

import com.google.gson.*;
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

import java.util.*;

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
                toList();

        outer:
        for(Map.Entry<ResourceLocation, JsonElement> elementEntry:elementEntries) {
            ResourceLocation pageId = elementEntry.getKey();
            JsonElement element = elementEntry.getValue();

            try {
                if(!element.isJsonObject()) {
                    LOGGER.error(String.format("Failed to load energized power book page '%s' from data pack '%s': Element must be a JSON Object",
                            pageId.getPath(), pageId.getNamespace()));

                    continue;
                }

                JsonObject object = element.getAsJsonObject();

                //Remove page command (Will not be displayed)
                if(object.has("remove")) {
                    JsonElement pageToRemoveElement = object.get("remove");

                    if(!pageToRemoveElement.isJsonPrimitive() || !pageToRemoveElement.getAsJsonPrimitive().isString()) {
                        LOGGER.error(String.format("Failed to load energized power book page '%s' from data pack '%s': remove must be a string primitive",
                                pageId.getPath(), pageId.getNamespace()));

                        continue;
                    }
                    ResourceLocation pageToRemove = ResourceLocation.tryParse(pageToRemoveElement.getAsJsonPrimitive().getAsString());
                    if(pageToRemove == null)
                        continue;

                    boolean containsKeyFlag = false;
                    for(int i = pages.size() - 1;i >= 0;i--) {
                        if(pages.get(i).getPageId().equals(pageToRemove)) {
                            containsKeyFlag = true;
                            pages.remove(i);

                            break;
                        }
                    }

                    if(!containsKeyFlag) {
                        LOGGER.error(String.format("Failed to load energized power book page '%s' from data pack '%s': page to be removed was not found",
                                pageId.getPath(), pageId.getNamespace()));

                        continue;
                    }

                    continue;
                }

                Component chapterTitleComponent = null;
                if(object.has("title"))
                    chapterTitleComponent = Component.Serializer.fromJson(object.get("title"));

                Component contentComponent = null;
                if(object.has("content"))
                    contentComponent = Component.Serializer.fromJson(object.get("content"));

                ResourceLocation[] imageResourceLocations = null;
                if(object.has("image")) {
                    JsonElement imageElement = object.get("image");
                    if(imageElement.isJsonPrimitive()) {
                        if(!imageElement.getAsJsonPrimitive().isString()) {
                            LOGGER.error(String.format("Failed to load energized power book page '%s' from data pack '%s': image must be a string primitive or an array of string primitives",
                                    pageId.getPath(), pageId.getNamespace()));

                            continue;
                        }

                        imageResourceLocations = new ResourceLocation[] {
                                ResourceLocation.tryParse(imageElement.getAsJsonPrimitive().getAsString())
                        };
                    }else if(imageElement.isJsonArray()) {
                        JsonArray imageJsonArray = imageElement.getAsJsonArray();

                        if(imageJsonArray.isEmpty()) {
                            LOGGER.error(String.format("Failed to load energized power book page '%s' from data pack '%s': Image array must contain at least one element",
                                    pageId.getPath(), pageId.getNamespace()));

                            continue;
                        }

                        List<ResourceLocation> imageResourceLocationList = new ArrayList<>(imageJsonArray.size());
                        for(JsonElement imageJsonEle:imageJsonArray) {
                            if(!imageJsonEle.isJsonPrimitive() || !imageJsonEle.getAsJsonPrimitive().isString()) {
                                LOGGER.error(String.format("Failed to load energized power book page '%s' from data pack '%s': image must be a string primitive or an array of string primitives",
                                        pageId.getPath(), pageId.getNamespace()));

                                continue outer;
                            }

                            imageResourceLocationList.add(ResourceLocation.tryParse(imageJsonEle.getAsJsonPrimitive().getAsString()));
                        }

                        imageResourceLocations = imageResourceLocationList.toArray(new ResourceLocation[0]);
                    }else {
                        LOGGER.error(String.format("Failed to load energized power book page '%s' from data pack '%s': image must be a string primitive or an array of string primitives",
                                pageId.getPath(), pageId.getNamespace()));

                        continue;
                    }
                }

                ResourceLocation[] blockResourceLocations = null;
                if(object.has("block")) {
                    JsonElement blockElement = object.get("block");
                    if(blockElement.isJsonPrimitive()) {
                        if(!blockElement.getAsJsonPrimitive().isString()) {
                            LOGGER.error(String.format("Failed to load energized power book page '%s' from data pack '%s': block must be a string primitive or an array of string primitives",
                                    pageId.getPath(), pageId.getNamespace()));

                            continue;
                        }

                        blockResourceLocations = new ResourceLocation[] {
                                ResourceLocation.tryParse(blockElement.getAsJsonPrimitive().getAsString())
                        };
                    }else if(blockElement.isJsonArray()) {
                        JsonArray blockJsonArray = blockElement.getAsJsonArray();

                        if(blockJsonArray.isEmpty()) {
                            LOGGER.error(String.format("Failed to load energized power book page '%s' from data pack '%s': Block array must contain at least one element",
                                    pageId.getPath(), pageId.getNamespace()));

                            continue;
                        }

                        List<ResourceLocation> blockResourceLocationsList = new ArrayList<>(blockJsonArray.size());
                        for(JsonElement blockJsonEle:blockJsonArray) {
                            if(!blockJsonEle.isJsonPrimitive() || !blockJsonEle.getAsJsonPrimitive().isString()) {
                                LOGGER.error(String.format("Failed to load energized power book page '%s' from data pack '%s': block must be a string primitive or an array of string primitives",
                                        pageId.getPath(), pageId.getNamespace()));

                                continue outer;
                            }

                            blockResourceLocationsList.add(ResourceLocation.tryParse(blockJsonEle.getAsJsonPrimitive().getAsString()));
                        }

                        blockResourceLocations = blockResourceLocationsList.toArray(new ResourceLocation[0]);
                    }else {
                        LOGGER.error(String.format("Failed to load energized power book page '%s' from data pack '%s': block must be a string primitive or an array of string primitives",
                                pageId.getPath(), pageId.getNamespace()));

                        continue;
                    }
                }

                pages.add(new EnergizedPowerBookScreen.PageContent(pageId, chapterTitleComponent, contentComponent, imageResourceLocations, blockResourceLocations));
            }catch(Exception e) {
                LOGGER.error(String.format("Failed to load energized power book page '%s' from data pack '%s'",
                        pageId.getPath(), pageId.getNamespace()), e);
            }
        }

        EnergizedPowerBookScreen.pages = new ArrayList<>(pages);
    }
}
