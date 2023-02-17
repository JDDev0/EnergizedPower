package me.jddev0.ep.loading;

import com.google.gson.GsonBuilder;
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
                collect(Collectors.toList());

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

                Identifier imageResourceLocation = null;
                if(object.has("image")) {
                    JsonElement imageElement = object.get("image");
                    if(!imageElement.isJsonPrimitive() || !imageElement.getAsJsonPrimitive().isString()) {
                        LOGGER.error(String.format("Failed to load energized power book page '%s' from data pack '%s': image must be a string primitive",
                                pageId.getPath(), pageId.getNamespace()));

                        continue;
                    }

                    imageResourceLocation = Identifier.tryParse(imageElement.getAsJsonPrimitive().getAsString());
                }

                Identifier blockResourceLocation = null;
                if(object.has("block")) {
                    JsonElement imageElement = object.get("block");
                    if(!imageElement.isJsonPrimitive() || !imageElement.getAsJsonPrimitive().isString()) {
                        LOGGER.error(String.format("Failed to load energized power book page '%s' from data pack '%s': block must be a string primitive",
                                pageId.getPath(), pageId.getNamespace()));

                        continue;
                    }

                    blockResourceLocation = Identifier.tryParse(imageElement.getAsJsonPrimitive().getAsString());
                }

                pages.add(new EnergizedPowerBookScreen.PageContent(pageId, chapterTitleComponent, contentComponent, imageResourceLocation, blockResourceLocation));
            }catch(Exception e) {
                LOGGER.error(String.format("Failed to load energized power book page '%s' from data pack '%s'",
                        pageId.getPath(), pageId.getNamespace()), e);
            }
        }

        EnergizedPowerBookScreen.pages = new ArrayList<>(pages);
    }
}
