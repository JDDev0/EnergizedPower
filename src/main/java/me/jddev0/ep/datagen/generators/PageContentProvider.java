package me.jddev0.ep.datagen.generators;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.*;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;

public abstract  class PageContentProvider implements DataProvider {
    private final Map<String, PageContent> data = new TreeMap<>();
    private final PackOutput output;
    private final CompletableFuture<HolderLookup.Provider> lookupProvider;
    private final String modid;

    public PageContentProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modid) {
        this.output = output;
        this.lookupProvider = lookupProvider;
        this.modid = modid;
    }

    protected abstract void registerPageContent();

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        return lookupProvider.thenCompose(lookupProvider -> run(cache, lookupProvider));
    }

    private CompletableFuture<?> run(final CachedOutput cache, final HolderLookup.Provider lookupProvider) {
        data.clear();

        registerPageContent();

        CompletableFuture<?>[] futures = new CompletableFuture[data.size()];
        int i = 0;
        for(Map.Entry<String, PageContent> entry:data.entrySet())
            futures[i++] = savePageContent(cache, entry.getKey(), entry.getValue(), lookupProvider);

        return CompletableFuture.allOf(futures);
    }

    private CompletableFuture<?> savePageContent(CachedOutput cache, String pageId, PageContent content,
                                                 HolderLookup.Provider lookupProvider) {
        Path target = output.getOutputFolder(PackOutput.Target.RESOURCE_PACK).resolve(this.modid).
                resolve("book_pages").resolve(pageId + ".json");

        JsonObject json = PageContent.CODEC.stable().encode(content, lookupProvider.
                createSerializationContext(JsonOps.INSTANCE), new JsonObject()).getOrThrow().getAsJsonObject();

        return DataProvider.saveStable(cache, json, target);
    }

    @Override
    public String getName() {
        return "Book Pages: " + modid;
    }

    protected MutableComponent addLinkToComponent(MutableComponent component, String link) {
        return component.withStyle(ChatFormatting.BLUE, ChatFormatting.UNDERLINE).withStyle(Style.EMPTY.
                withClickEvent(new ClickEvent.OpenUrl(URI.create(link))).
                withHoverEvent(new HoverEvent.ShowText(Component.translatable("book.energizedpower.tooltip.link"))));
    }

    protected PageContent addSimplePage(String pageId, @Nullable Component content, @Nullable Map<Integer, Identifier> changePageIntToId) {
        return addPage(pageId, null, content, null, null, changePageIntToId);
    }
    protected PageContent addSimplePage(String pageId, @Nullable Component content,
                                        @Nullable Identifier image, @Nullable Map<Integer, Identifier> changePageIntToId) {
        return addPage(pageId, null, content, image == null?null:new Identifier[] {
                image
        }, null, changePageIntToId);
    }
    protected PageContent addSimplePage(String pageId, @Nullable Component content,
                                        @Nullable Identifier[] image, @Nullable Map<Integer, Identifier> changePageIntToId) {
        return addPage(pageId, null, content, image, null, changePageIntToId);
    }
    protected PageContent addSimplePage(String pageId, @Nullable Component content,
                                        Block block, @Nullable Map<Integer, Identifier> changePageIntToId) {
        return addSimplePage(pageId, content, new Block[] {
                block
        }, changePageIntToId);
    }
    protected PageContent addSimplePage(String pageId, @Nullable Component content,
                                        Block[] block, @Nullable Map<Integer, Identifier> changePageIntToId) {
        Identifier[] blockIds = new Identifier[block.length];
        for(int i = 0;i < blockIds.length;i++)
            blockIds[i] = BuiltInRegistries.BLOCK.getKey(block[i]);

        return addPage(pageId, null, content, null, blockIds, changePageIntToId);
    }

    protected PageContent addChapterPage(String pageId, @Nullable Component title, @Nullable Component content,
                                         Block block, @Nullable Map<Integer, Identifier> changePageIntToId) {
        return addChapterPage(pageId, title, content, new Block[] {
                block
        }, changePageIntToId);
    }
    protected PageContent addChapterPage(String pageId, @Nullable Component title, @Nullable Component content,
                                         Block[] block, @Nullable Map<Integer, Identifier> changePageIntToId) {
        Identifier[] blockIds = new Identifier[block.length];
        for(int i = 0;i < blockIds.length;i++)
            blockIds[i] = BuiltInRegistries.BLOCK.getKey(block[i]);

        return addPage(pageId, title, content, null, blockIds, changePageIntToId);
    }
    protected PageContent addChapterPage(String pageId, @Nullable Component title, @Nullable Component content,
                                         @Nullable Map<Integer, Identifier> changePageIntToId) {
        return addChapterPage(pageId, title, content, (Identifier[])null, changePageIntToId);
    }
    protected PageContent addChapterPage(String pageId, @Nullable Component title, @Nullable Component content,
                                         @Nullable Identifier image, @Nullable Map<Integer, Identifier> changePageIntToId) {
        return addPage(pageId, title, content, image == null?null:new Identifier[] {
                image
        }, null, changePageIntToId);
    }
    protected PageContent addChapterPage(String pageId, @Nullable Component title, @Nullable Component content,
                                         @Nullable Identifier[] image, @Nullable Map<Integer, Identifier> changePageIntToId) {
        return addPage(pageId, title, content, image, null, changePageIntToId);
    }

    protected PageContent addPage(String pageId, @Nullable Component title, @Nullable Component content,
                                  @Nullable Identifier[] image, @Nullable Identifier[] block,
                                  @Nullable Map<Integer, Identifier> changePageIntToId) {
        PageContent pageContent = new PageContent(Identifier.fromNamespaceAndPath(modid, pageId),
                title, content, image, block, changePageIntToId);
        data.put(pageId, pageContent);
        return pageContent;
    }
}
