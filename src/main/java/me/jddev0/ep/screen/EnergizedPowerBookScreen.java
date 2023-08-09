package me.jddev0.ep.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.networking.ModMessages;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.block.Block;
import net.minecraft.block.entity.LecternBlockEntity;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.PageTurnWidget;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class EnergizedPowerBookScreen extends Screen {
    public static final Identifier TEXTURE = new Identifier(EnergizedPowerMod.MODID, "textures/gui/book/energized_power_book.png");
    public static final Identifier FRONT_COVER = new Identifier(EnergizedPowerMod.MODID, "textures/gui/book/front_cover.png");
    public static final Identifier BACK_COVER = new Identifier(EnergizedPowerMod.MODID, "textures/gui/book/back_cover.png");

    public static final Identifier ENERGIZED_COPPER_INGOT = new Identifier(EnergizedPowerMod.MODID, "textures/item/energized_copper_ingot.png");

    public static final int IMAGE_CYCLE_DELAY = ModConfigs.CLIENT_ENERGIZED_POWER_BOOK_IMAGE_CYCLE_DELAY.getValue();

    private int currentTick;

    private PageTurnWidget forwardButton;
    private PageTurnWidget backButton;

    private final LecternBlockEntity lecternBlockEntity;

    public static List<PageContent> pages = new LinkedList<>();
    private int currentPage;
    private Text currentPageNumberOutput = Text.empty();
    private boolean isCurrentPageCached;
    private List<OrderedText> cachedPageComponents = Collections.emptyList();

    public EnergizedPowerBookScreen() {
        this(null);
    }

    public EnergizedPowerBookScreen(LecternBlockEntity lecternBlockEntity) {
        super(NarratorManager.EMPTY);

        this.lecternBlockEntity = lecternBlockEntity;
    }

    @Override
    protected void init() {
        this.createMenuControls();
        this.createPageControlButtons();
    }

    private void createMenuControls() {
        boolean showTakeButton = lecternBlockEntity != null && client.player.canModifyBlocks();

        addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> close()).
                dimensions(width / 2 - 116, 232, showTakeButton?116:236, 20).build());

        if(showTakeButton)
            addDrawableChild(ButtonWidget.builder(Text.translatable("lectern.take_book"), button -> {
                ClientPlayNetworking.send(ModMessages.POP_ENERGIZED_POWER_BOOK_FROM_LECTERN_ID, PacketByteBufs.create().writeBlockPos(lecternBlockEntity.getPos()));
                close();
            }).dimensions(width / 2 + 2, 232, 116, 20).build());
    }

    private void createPageControlButtons() {
        int startX = (width - 226) / 2;

        forwardButton = addDrawableChild(new PageTurnWidget(startX + 150, 193, true, button -> pageForward(), true));
        backButton = addDrawableChild(new PageTurnWidget(startX + 43, 193, false, button -> pageBack(), true));

        updateButtonVisibility();
    }

    private int getPageCount() {
        return pages.size() + 2;  //"+ 2": Front cover and back cover are not in the content list
    }

    private void pageForward() {
        if(currentPage < getPageCount() - 1) {
            currentPage++;
            isCurrentPageCached = false;
        }

        updateButtonVisibility();
    }

    private void pageBack() {
        if(currentPage > 0) {
            currentPage--;
            isCurrentPageCached = false;
        }

        updateButtonVisibility();
    }

    private boolean setPage(int page) {
        if(page < 0 || page >= getPageCount() || page == currentPage)
            return false;

        currentPage = page;
        isCurrentPageCached = false;

        updateButtonVisibility();

        return true;
    }

    private void updateButtonVisibility() {
        forwardButton.visible = currentPage < getPageCount() - 1;
        backButton.visible = currentPage > 0;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(super.keyPressed(keyCode, scanCode, modifiers))
            return true;

        int oldCurrentPage = currentPage;
        return switch(keyCode) {
            case 266 -> {
                backButton.onPress();

                if(currentPage != oldCurrentPage)
                    client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.ITEM_BOOK_PAGE_TURN, 1.f));

                yield true;
            }
            case 267 -> {
                forwardButton.onPress();

                if(currentPage != oldCurrentPage)
                    client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.ITEM_BOOK_PAGE_TURN, 1.f));

                yield true;
            }

            default -> false;
        };
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if(mouseButton == 0) {
            Style style = getComponentStyleAt(mouseX, mouseY);
            if(style != null && handleTextClick(style))
                return true;
        }

        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean handleTextClick(Style style) {
        ClickEvent clickEvent = style.getClickEvent();
        if(clickEvent == null)
            return false;

        if(clickEvent.getAction() == ClickEvent.Action.CHANGE_PAGE) {
            int oldCurrentPage = currentPage;
            try {
                return setPage(Integer.parseInt(clickEvent.getValue()) - 1);
            }catch(NumberFormatException e) {
                String pageIdString = clickEvent.getValue();

                Identifier pageId = Identifier.tryParse(pageIdString);
                if(pageId == null)
                    return false;

                boolean containsKeyFlag = false;
                int i = 0;
                for(;i < pages.size();i++) {
                    if(pages.get(i).getPageId().equals(pageId)) {
                        containsKeyFlag = true;

                        break;
                    }
                }

                if(!containsKeyFlag)
                    return false;

                return setPage(i + 1); //"+ 1": Front cover is not contained in the pages list
            }finally {
                if(currentPage != oldCurrentPage)
                    client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.ITEM_BOOK_PAGE_TURN, 1.f));
            }
        }

        boolean flag = super.handleTextClick(style);
        if(flag && clickEvent.getAction() == ClickEvent.Action.RUN_COMMAND)
            close();

        return flag;
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float partialTicks) {
        renderBackground(drawContext);
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        int startX = (width - 226) / 2;
        if(currentPage == 0) {
            drawContext.drawTexture(FRONT_COVER, startX, 2, 0, 0, 226, 230);
        }else if(currentPage == getPageCount() - 1) {
            drawContext.drawTexture(BACK_COVER, startX, 2, 0, 0, 226, 230);
        }else {
            drawContext.drawTexture(TEXTURE, startX, 2, 0, 0, 226, 230);
        }

        if(!isCurrentPageCached) {
            if(currentPage == 0) {
                cachedPageComponents = textRenderer.wrapLines(Text.translatable("book.energizedpower.front.cover.text").formatted(Formatting.GRAY), 148);
            }else if(currentPage == getPageCount() - 1) {
                cachedPageComponents = Collections.emptyList();
            }else {
                //Front cover and back cover are not included
                if(pages.get(currentPage - 1).getPageComponent() != null)
                    cachedPageComponents = textRenderer.wrapLines(pages.get(currentPage - 1).getPageComponent(), 148);
            }
            //First page is front cover (Number = 0)
            //Last page is back cover (Number = page count - 1)
            currentPageNumberOutput = Text.translatable("book.pageIndicator", currentPage, Math.max(getPageCount() - 1, 1));
        }

        isCurrentPageCached = true;

        int textWidth = textRenderer.getWidth(currentPageNumberOutput);
        drawContext.drawText(textRenderer, currentPageNumberOutput, (int)((width - textWidth) / 2.f), 222, 0xFFFFFFFF, false);

        if(currentPage == 0) {
            renderFrontCover(drawContext);

            super.render(drawContext, mouseX, mouseY, partialTicks);

            return;
        }else if(currentPage == getPageCount() - 1) {
            renderImageCentered(drawContext, ENERGIZED_COPPER_INGOT, -1);

            super.render(drawContext, mouseX, mouseY, partialTicks);

            return;
        }

        int yOffset = 0;

        Identifier[] images = pages.get(currentPage - 1).getImageResourceLocations();
        Identifier[] blocks = pages.get(currentPage - 1).getBlockResourceLocations();

        Text chapterTitleComponent = pages.get(currentPage - 1).getChapterTitleComponent();
        if(chapterTitleComponent != null) {
            float scaleFactor = 1.5f;

            yOffset = (int)((230 / scaleFactor - textRenderer.fontHeight -
                    (cachedPageComponents == null?0:((cachedPageComponents.size() + 1) * textRenderer.fontHeight / scaleFactor))) * .5f);

            if(images != null)
                yOffset -= 60 * .5f / scaleFactor;

            if(blocks != null)
                yOffset -= 60 * .5f / scaleFactor;

            drawContext.getMatrices().scale(scaleFactor, scaleFactor, 1.f);
            drawContext.drawText(textRenderer, chapterTitleComponent, (int)((width / scaleFactor - textRenderer.getWidth(chapterTitleComponent)) * .5f),
                    yOffset, 0, false);
            drawContext.getMatrices().scale(1/scaleFactor, 1/scaleFactor, 1.f);

            yOffset *= scaleFactor;
        }

        if(images != null) {
            renderImageCentered(drawContext, images[(currentTick / IMAGE_CYCLE_DELAY) % images.length], yOffset + 15);

            yOffset += 60;
        }

        if(blocks != null) {
            renderBlockCentered(drawContext, blocks[(currentTick / IMAGE_CYCLE_DELAY) % blocks.length], yOffset + 15);

            yOffset += 60;
        }

        if(cachedPageComponents != null) {
            for(int i = 0;i < cachedPageComponents.size();i++) {
                OrderedText formattedCharSequence = cachedPageComponents.get(i);

                float x;
                if(chapterTitleComponent == null)
                    x = startX + 36.f;
                else
                    x = (width - textRenderer.getWidth(formattedCharSequence)) * .5f;

                drawContext.drawText(textRenderer, formattedCharSequence, (int)x, 20 + yOffset + 9 * i, 0, false);
            }

            Style style = getComponentStyleAt(mouseX, mouseY);
            if(style != null)
                drawContext.drawHoverEvent(textRenderer, style, mouseX, mouseY);
        }

        super.render(drawContext, mouseX, mouseY, partialTicks);
    }

    private void renderFrontCover(DrawContext drawContext) {
        int startX = (width - 226) / 2;

        float scaleFactor = 1.35f;

        Text component = Text.literal("Energized Power").formatted(Formatting.GOLD);
        int textWidth = textRenderer.getWidth(component);

        drawContext.getMatrices().scale(scaleFactor, scaleFactor, 1.f);
        drawContext.drawText(textRenderer, component, (int)((width / scaleFactor - textWidth) * .5f),
                (int)(30 / scaleFactor), 0, false);
        drawContext.getMatrices().scale(1/scaleFactor, 1/scaleFactor, 1.f);

        component = Text.translatable("book.byAuthor", "JDDev0").formatted(Formatting.GOLD);
        textWidth = textRenderer.getWidth(component);
        drawContext.drawText(textRenderer, component, (int)((width - textWidth) * .5f), 192 - 45, 0, false);

        for(int i = 0;i < cachedPageComponents.size();i++) {
            OrderedText formattedCharSequence = cachedPageComponents.get(i);
            drawContext.drawText(textRenderer, formattedCharSequence, startX + 36, 120 + 9 * i, 0, false);
        }

        renderImageCentered(drawContext, ENERGIZED_COPPER_INGOT, 48);
    }

    private void renderImageCentered(DrawContext drawContext, Identifier image, int y) {
        float scaleFactor = .25f;

        if(y == -1) //Centered
            y = (int)((230 - 256 * scaleFactor) * .5f) + 2;

        drawContext.getMatrices().scale(scaleFactor, scaleFactor, 1.f);
        drawContext.drawTexture(image, (int)((width / scaleFactor - 256) * .5f), (int)(y / scaleFactor), 0, 0, 256, 256);
        drawContext.getMatrices().scale(1/scaleFactor, 1/scaleFactor, 1.f);
    }

    private void renderBlockCentered(DrawContext drawContext, Identifier blockResourceLocation, int y) {
        if(y == -1) //Centered
            y = (int)((230 - 64) * .5f) + 2;

        Block block = Registries.BLOCK.get(blockResourceLocation);
        ItemStack itemStack = new ItemStack(block);

        ItemRenderer itemRenderer = client.getItemRenderer();
        TextureManager textureManager = client.getTextureManager();

        BakedModel bakedModel = itemRenderer.getModel(itemStack, null, null, 0);

        textureManager.getTexture(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).setFilter(false, false);
        RenderSystem.setShaderTexture(0, PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);

        RenderSystem.setShaderColor(1.f, 1.f, 1.f, 1.f);

        drawContext.getMatrices().push();
        drawContext.getMatrices().translate((int)(width * .5f), y + 64.f * .5f, 50.f);
        drawContext.getMatrices().scale(64.f, -64.f, 1.f);

        VertexConsumerProvider.Immediate bufferSource = client.getBufferBuilders().getEntityVertexConsumers();
        DiffuseLighting.method_34742();

        itemRenderer.renderItem(itemStack, ModelTransformationMode.GUI, false, drawContext.getMatrices(), bufferSource,
                15728880, OverlayTexture.DEFAULT_UV, bakedModel);

        bufferSource.draw();
        RenderSystem.enableDepthTest();

        drawContext.getMatrices().pop();
    }

    private Style getComponentStyleAt(double x, double y) {
        if(cachedPageComponents.isEmpty())
            return null;

        int componentX = MathHelper.floor(x - (width - 226) * .5 - 36.);
        int componentY = MathHelper.floor(y - 20.);

        //Translate for chapter pages and pages with graphics
        if(currentPage > 0 && currentPage < getPageCount() - 1) { //Ignore front and back cover pages
            Identifier[] images = pages.get(currentPage - 1).getImageResourceLocations();
            Identifier[] blocks = pages.get(currentPage - 1).getBlockResourceLocations();

            Text chapterTitleComponent = pages.get(currentPage - 1).getChapterTitleComponent();
            if(chapterTitleComponent != null) {
                float scaleFactor = 1.5f;

                componentY = -(int)((230 / scaleFactor - textRenderer.fontHeight -
                        (cachedPageComponents == null?0:((cachedPageComponents.size() + 1) * textRenderer.fontHeight / scaleFactor))) * .5f);

                if(images != null)
                    componentY += 60 * .5f / scaleFactor;

                if(blocks != null)
                    componentY += 60 * .5f / scaleFactor;

                componentY *= scaleFactor;

                componentY += MathHelper.floor(y - 20.);
            }

            if(images != null)
                componentY -= 60;

            if(blocks != null)
                componentY -= 60;

            if(chapterTitleComponent != null) {
                int componentIndex = componentY / 9;
                if(componentIndex < 0 || componentIndex >= cachedPageComponents.size())
                    return null;

                OrderedText formattedCharSequence = cachedPageComponents.get(componentIndex);
                componentX = MathHelper.floor(x - (width - textRenderer.getWidth(formattedCharSequence)) * .5f);
            }
        }

        if(componentX < 0 || componentY < 0)
            return null;

        int componentCount = cachedPageComponents.size();
        if(componentX > 178 || componentY >= 9 * componentCount + componentCount)
            return null;

        int componentIndex = componentY / 9;
        if(componentIndex >= cachedPageComponents.size())
            return null;

        return client.textRenderer.getTextHandler().getStyleAt(cachedPageComponents.get(componentIndex), componentX);
    }

    @Override
    public boolean shouldPause() {
        return lecternBlockEntity == null;
    }

    @Override
    public void tick() {
        currentTick++;
    }

    @Environment(EnvType.CLIENT)
    public static class PageContent {
        private final Identifier pageId;
        private final Text chapterTitleComponent;
        private final Text pageComponent;
        private final Identifier[] imageResourceLocations;
        private final Identifier[] blockResourceLocations;

        public PageContent(Identifier pageId, Text chapterTitleComponent, Text pageComponent,
                           Identifier[] imageResourceLocations, Identifier[] blockResourceLocations) {
            this.pageId = pageId;
            this.chapterTitleComponent = chapterTitleComponent;
            this.pageComponent = pageComponent;
            this.imageResourceLocations = imageResourceLocations;
            this.blockResourceLocations = blockResourceLocations;
        }

        public Identifier getPageId() {
            return pageId;
        }

        public Text getChapterTitleComponent() {
            return chapterTitleComponent;
        }

        public Text getPageComponent() {
            return pageComponent;
        }

        public Identifier[] getImageResourceLocations() {
            return imageResourceLocations;
        }

        public Identifier[] getBlockResourceLocations() {
            return blockResourceLocations;
        }
    }
}
