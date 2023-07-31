package me.jddev0.ep.loading;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.screen.EnergizedPowerBookScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.slf4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
public class EnergizedPowerBookReloadListener extends JsonDataLoader implements IdentifiableResourceReloadListener {
    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public Identifier getFabricId() {
        return new Identifier(EnergizedPowerMod.MODID, "energizedpowerbook");
    }

    public EnergizedPowerBookReloadListener() {
        super(new GsonBuilder().create(), "book_pages");
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> elements, ResourceManager resourceManager, Profiler profilerFiller) {
        List<EnergizedPowerBookScreen.PageContent> pages = new LinkedList<>();

        List<Map.Entry<Identifier, JsonElement>> elementEntries = elements.entrySet().stream().
                sorted(Comparator.comparing(o -> o.getKey().getPath())).
                toList();

        outer:
        for(Map.Entry<Identifier, JsonElement> elementEntry:elementEntries) {
            Identifier pageId = elementEntry.getKey();
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
                    Identifier pageToRemove = Identifier.tryParse(pageToRemoveElement.getAsJsonPrimitive().getAsString());
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

                Text chapterTitleComponent = null;
                if(object.has("title"))
                    chapterTitleComponent = Text.Serializer.fromJson(object.get("title"));

                Text contentComponent = null;
                if(object.has("content"))
                    contentComponent = Text.Serializer.fromJson(object.get("content"));

                Identifier[] imageResourceLocations = null;
                if(object.has("image")) {
                    JsonElement imageElement = object.get("image");
                    if(imageElement.isJsonPrimitive()) {
                        if(!imageElement.getAsJsonPrimitive().isString()) {
                            LOGGER.error(String.format("Failed to load energized power book page '%s' from data pack '%s': image must be a string primitive or an array of string primitives",
                                    pageId.getPath(), pageId.getNamespace()));

                            continue;
                        }

                        imageResourceLocations = new Identifier[] {
                                Identifier.tryParse(imageElement.getAsJsonPrimitive().getAsString())
                        };
                    }else if(imageElement.isJsonArray()) {
                        JsonArray imageJsonArray = imageElement.getAsJsonArray();

                        if(imageJsonArray.isEmpty()) {
                            LOGGER.error(String.format("Failed to load energized power book page '%s' from data pack '%s': Image array must contain at least one element",
                                    pageId.getPath(), pageId.getNamespace()));

                            continue;
                        }

                        List<Identifier> imageResourceLocationList = new ArrayList<>(imageJsonArray.size());
                        for(JsonElement imageJsonEle:imageJsonArray) {
                            if(!imageJsonEle.isJsonPrimitive() || !imageJsonEle.getAsJsonPrimitive().isString()) {
                                LOGGER.error(String.format("Failed to load energized power book page '%s' from data pack '%s': image must be a string primitive or an array of string primitives",
                                        pageId.getPath(), pageId.getNamespace()));

                                continue outer;
                            }

                            imageResourceLocationList.add(Identifier.tryParse(imageJsonEle.getAsJsonPrimitive().getAsString()));
                        }

                        imageResourceLocations = imageResourceLocationList.toArray(new Identifier[0]);
                    }else {
                        LOGGER.error(String.format("Failed to load energized power book page '%s' from data pack '%s': image must be a string primitive or an array of string primitives",
                                pageId.getPath(), pageId.getNamespace()));

                        continue;
                    }
                }

                Identifier[] blockResourceLocations = null;
                if(object.has("block")) {
                    JsonElement blockElement = object.get("block");
                    if(blockElement.isJsonPrimitive()) {
                        if(!blockElement.getAsJsonPrimitive().isString()) {
                            LOGGER.error(String.format("Failed to load energized power book page '%s' from data pack '%s': block must be a string primitive or an array of string primitives",
                                    pageId.getPath(), pageId.getNamespace()));

                            continue;
                        }

                        blockResourceLocations = new Identifier[] {
                                Identifier.tryParse(blockElement.getAsJsonPrimitive().getAsString())
                        };
                    }else if(blockElement.isJsonArray()) {
                        JsonArray blockJsonArray = blockElement.getAsJsonArray();

                        if(blockJsonArray.isEmpty()) {
                            LOGGER.error(String.format("Failed to load energized power book page '%s' from data pack '%s': Block array must contain at least one element",
                                    pageId.getPath(), pageId.getNamespace()));

                            continue;
                        }

                        List<Identifier> blockResourceLocationsList = new ArrayList<>(blockJsonArray.size());
                        for(JsonElement blockJsonEle:blockJsonArray) {
                            if(!blockJsonEle.isJsonPrimitive() || !blockJsonEle.getAsJsonPrimitive().isString()) {
                                LOGGER.error(String.format("Failed to load energized power book page '%s' from data pack '%s': block must be a string primitive or an array of string primitives",
                                        pageId.getPath(), pageId.getNamespace()));

                                continue outer;
                            }

                            blockResourceLocationsList.add(Identifier.tryParse(blockJsonEle.getAsJsonPrimitive().getAsString()));
                        }

                        blockResourceLocations = blockResourceLocationsList.toArray(new Identifier[0]);
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
