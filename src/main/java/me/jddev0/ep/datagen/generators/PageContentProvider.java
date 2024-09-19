package me.jddev0.ep.datagen.generators;

import com.google.gson.JsonObject;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;

public abstract  class PageContentProvider implements DataProvider {
    private final Map<String, PageContent> data = new TreeMap<>();
    private final PackOutput output;
    private final String modid;
    private final ExistingFileHelper existingFileHelper;

    public PageContentProvider(PackOutput output, String modid, ExistingFileHelper existingFileHelper) {
        this.output = output;
        this.modid = modid;
        this.existingFileHelper = existingFileHelper;
    }

    protected abstract void registerPageContent();

    @Override
    public CompletableFuture<?> run(final CachedOutput cache) {
        data.clear();

        registerPageContent();

        CompletableFuture<?>[] futures = new CompletableFuture[data.size()];
        int i = 0;
        for(Map.Entry<String, PageContent> entry:data.entrySet())
            futures[i++] = savePageContent(cache, entry.getKey(), entry.getValue());

        return CompletableFuture.allOf(futures);
    }

    private CompletableFuture<?> savePageContent(CachedOutput cache, String pageId, PageContent content) {
        Path target = output.getOutputFolder(PackOutput.Target.RESOURCE_PACK).resolve(this.modid).
                resolve("book_pages").resolve(pageId + ".json");

        JsonObject json = content.toJson().getAsJsonObject();

        return DataProvider.saveStable(cache, json, target);
    }

    @Override
    public String getName() {
        return "Book Pages: " + modid;
    }

    protected MutableComponent addLinkToComponent(MutableComponent component, String link) {
        return component.withStyle(ChatFormatting.BLUE, ChatFormatting.UNDERLINE).withStyle(Style.EMPTY.
                withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, link)).
                withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        Component.translatable("book.energizedpower.tooltip.link"))));
    }

    protected PageContent addSimplePage(String pageId, @Nullable Component content) {
        return addPage(pageId, null, content, null, null);
    }
    protected PageContent addSimplePage(String pageId, @Nullable Component content,
                                        @Nullable ResourceLocation image) {
        return addPage(pageId, null, content, image == null?null:new ResourceLocation[] {
                image
        }, null);
    }
    protected PageContent addSimplePage(String pageId, @Nullable Component content,
                                        @Nullable ResourceLocation[] image) {
        return addPage(pageId, null, content, image, null);
    }
    protected PageContent addSimplePage(String pageId, @Nullable Component content,
                                        Block block) {
        return addSimplePage(pageId, content, new Block[] {
                block
        });
    }
    protected PageContent addSimplePage(String pageId, @Nullable Component content,
                                        Block[] block) {
        ResourceLocation[] blockIds = new ResourceLocation[block.length];
        for(int i = 0;i < blockIds.length;i++)
            blockIds[i] = BuiltInRegistries.BLOCK.getKey(block[i]);

        return addPage(pageId, null, content, null, blockIds);
    }

    protected PageContent addChapterPage(String pageId, @Nullable Component title, @Nullable Component content,
                                         Block block) {
        return addChapterPage(pageId, title, content, new Block[] {
                block
        });
    }
    protected PageContent addChapterPage(String pageId, @Nullable Component title, @Nullable Component content,
                                         Block[] block) {
        ResourceLocation[] blockIds = new ResourceLocation[block.length];
        for(int i = 0;i < blockIds.length;i++)
            blockIds[i] = BuiltInRegistries.BLOCK.getKey(block[i]);

        return addPage(pageId, title, content, null, blockIds);
    }
    protected PageContent addChapterPage(String pageId, @Nullable Component title, @Nullable Component content) {
        return addChapterPage(pageId, title, content, (ResourceLocation[])null);
    }
    protected PageContent addChapterPage(String pageId, @Nullable Component title, @Nullable Component content,
                                         @Nullable ResourceLocation image) {
        return addPage(pageId, title, content, image == null?null:new ResourceLocation[] {
                image
        }, null);
    }
    protected PageContent addChapterPage(String pageId, @Nullable Component title, @Nullable Component content,
                                         @Nullable ResourceLocation[] image) {
        return addPage(pageId, title, content, image, null);
    }

    protected PageContent addPage(String pageId, @Nullable Component title, @Nullable Component content,
                                  @Nullable ResourceLocation[] image, @Nullable ResourceLocation[] block) {
        PageContent pageContent = new PageContent(new ResourceLocation(modid, pageId),
                title, content, image, block);
        data.put(pageId, pageContent);
        return pageContent;
    }
}
