package me.jddev0.ep.datagen.generators;

import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.block.Block;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;

public abstract  class PageContentProvider implements DataProvider {
    private final Map<String, PageContent> data = new TreeMap<>();
    private final FabricDataOutput output;
    private final CompletableFuture<RegistryWrapper.WrapperLookup> lookupProvider;

    public PageContentProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> lookupProvider) {
        this.output = output;
        this.lookupProvider = lookupProvider;
    }

    protected abstract void registerPageContent();

    @Override
    public CompletableFuture<?> run(DataWriter cache) {
        return lookupProvider.thenCompose(lookupProvider -> run(cache, lookupProvider));
    }

    private CompletableFuture<?> run(final DataWriter cache, final RegistryWrapper.WrapperLookup lookupProvider) {
        data.clear();

        registerPageContent();

        CompletableFuture<?>[] futures = new CompletableFuture[data.size()];
        int i = 0;
        for(Map.Entry<String, PageContent> entry:data.entrySet())
            futures[i++] = savePageContent(cache, entry.getKey(), entry.getValue(), lookupProvider);

        return CompletableFuture.allOf(futures);
    }

    private CompletableFuture<?> savePageContent(DataWriter cache, String pageId, PageContent content,
                                                 RegistryWrapper.WrapperLookup lookupProvider) {
        Path target = output.getResolver(DataOutput.OutputType.RESOURCE_PACK, "book_pages").
                resolveJson(Identifier.of(output.getModId(), pageId));

        JsonObject json = content.toJson().getAsJsonObject();

        return DataProvider.writeToPath(cache, json, target);
    }

    @Override
    public String getName() {
        return "Book Pages: " + output.getModId();
    }

    protected MutableText addLinkToComponent(MutableText component, String link) {
        return component.formatted(Formatting.BLUE, Formatting.UNDERLINE).fillStyle(Style.EMPTY.
                withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, link)).
                withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        Text.translatable("book.energizedpower.tooltip.link"))));
    }

    protected PageContent addSimplePage(String pageId, @Nullable Text content) {
        return addPage(pageId, null, content, null, null);
    }
    protected PageContent addSimplePage(String pageId, @Nullable Text content,
                                        @Nullable Identifier image) {
        return addPage(pageId, null, content, image == null?null:new Identifier[] {
                image
        }, null);
    }
    protected PageContent addSimplePage(String pageId, @Nullable Text content,
                                        @Nullable Identifier[] image) {
        return addPage(pageId, null, content, image, null);
    }
    protected PageContent addSimplePage(String pageId, @Nullable Text content,
                                        Block block) {
        return addSimplePage(pageId, content, new Block[] {
                block
        });
    }
    protected PageContent addSimplePage(String pageId, @Nullable Text content,
                                        Block[] block) {
        Identifier[] blockIds = new Identifier[block.length];
        for(int i = 0;i < blockIds.length;i++)
            blockIds[i] = Registries.BLOCK.getId(block[i]);

        return addPage(pageId, null, content, null, blockIds);
    }

    protected PageContent addChapterPage(String pageId, @Nullable Text title, @Nullable Text content,
                                         Block block) {
        return addChapterPage(pageId, title, content, new Block[] {
                block
        });
    }
    protected PageContent addChapterPage(String pageId, @Nullable Text title, @Nullable Text content,
                                         Block[] block) {
        Identifier[] blockIds = new Identifier[block.length];
        for(int i = 0;i < blockIds.length;i++)
            blockIds[i] = Registries.BLOCK.getId(block[i]);

        return addPage(pageId, title, content, null, blockIds);
    }
    protected PageContent addChapterPage(String pageId, @Nullable Text title, @Nullable Text content) {
        return addChapterPage(pageId, title, content, (Identifier[])null);
    }
    protected PageContent addChapterPage(String pageId, @Nullable Text title, @Nullable Text content,
                                         @Nullable Identifier image) {
        return addPage(pageId, title, content, image == null?null:new Identifier[] {
                image
        }, null);
    }
    protected PageContent addChapterPage(String pageId, @Nullable Text title, @Nullable Text content,
                                         @Nullable Identifier[] image) {
        return addPage(pageId, title, content, image, null);
    }

    protected PageContent addPage(String pageId, @Nullable Text title, @Nullable Text content,
                                  @Nullable Identifier[] image, @Nullable Identifier[] block) {
        PageContent pageContent = new PageContent(Identifier.of(output.getModId(), pageId),
                title, content, image, block);
        data.put(pageId, pageContent);
        return pageContent;
    }
}
