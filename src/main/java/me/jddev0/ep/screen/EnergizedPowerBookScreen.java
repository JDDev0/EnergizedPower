package me.jddev0.ep.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.PopEnergizedPowerBookFromLecternC2SPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.PageButton;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class EnergizedPowerBookScreen extends Screen {
    public static final ResourceLocation TEXTURE = new ResourceLocation(EnergizedPowerMod.MODID, "textures/gui/book/energized_power_book.png");
    public static final ResourceLocation FRONT_COVER = new ResourceLocation(EnergizedPowerMod.MODID, "textures/gui/book/front_cover.png");
    public static final ResourceLocation BACK_COVER = new ResourceLocation(EnergizedPowerMod.MODID, "textures/gui/book/back_cover.png");

    public static final ResourceLocation ENERGIZED_COPPER_INGOT = new ResourceLocation(EnergizedPowerMod.MODID, "textures/item/energized_copper_ingot.png");

    public static final int IMAGE_CYCLE_DELAY = ModConfigs.CLIENT_ENERGIZED_POWER_BOOK_IMAGE_CYCLE_DELAY.getValue();

    private static final int MAX_CHARS_PER_LINE = 148;
    private static final int MAX_LINES = 19;

    private static List<PageContent> pages = new LinkedList<>();

    private int currentTick;

    private PageButton forwardButton;
    private PageButton backButton;

    private final LecternBlockEntity lecternBlockEntity;
    private ResourceLocation openOnPageForBlock;

    private List<FormattedPageContent> formattedPages;

    private int currentPage;
    private Component currentPageNumberOutput = CommonComponents.EMPTY;
    private boolean isCurrentPageCached;

    public static void setPages(List<PageContent> pages) {
        EnergizedPowerBookScreen.pages = new ArrayList<>(pages);
    }

    public EnergizedPowerBookScreen() {
        this(null, null);
    }

    public EnergizedPowerBookScreen(ResourceLocation openOnPageForBlock) {
        this(null, openOnPageForBlock);
    }

    public EnergizedPowerBookScreen(LecternBlockEntity lecternBlockEntity) {
        this(lecternBlockEntity, null);
    }

    public EnergizedPowerBookScreen(LecternBlockEntity lecternBlockEntity, ResourceLocation openOnPageForBlock) {
        super(GameNarrator.NO_TITLE);

        this.lecternBlockEntity = lecternBlockEntity;
        this.openOnPageForBlock = openOnPageForBlock;
    }

    @Override
    protected void init() {
        this.createMenuControls();
        this.createPageControlButtons();

        List<FormattedPageContent> formattedPages = new LinkedList<>();
        formattedPages.add(new FormattedPageContent(new ResourceLocation(EnergizedPowerMod.MODID, "front_cover"),
                null,
                font.split(Component.translatable("book.energizedpower.front.cover.text").
                        withStyle(ChatFormatting.GRAY), MAX_CHARS_PER_LINE), null, null));
        for(PageContent pageContent:pages) {
            ResourceLocation pageId = pageContent.getPageId();
            Component chapterTitleComponent = pageContent.getChapterTitleComponent();
            ResourceLocation[] imageResourceLocations = pageContent.getImageResourceLocations();
            ResourceLocation[] blockResourceLocations = pageContent.getBlockResourceLocations();

            List<FormattedCharSequence> formattedPageComponents = pageContent.getPageComponent() == null?new ArrayList<>(0):
                    font.split(pageContent.getPageComponent(), MAX_CHARS_PER_LINE);

            if(chapterTitleComponent != null) {
                formattedPages.add(new FormattedPageContent(pageId, chapterTitleComponent, formattedPageComponents,
                        imageResourceLocations, blockResourceLocations));

                continue;
            }

            //Automatically split pages into multiple
            int maxLineCountFirstPage = MAX_LINES - (imageResourceLocations != null || blockResourceLocations != null?7:0);

            formattedPages.add(new FormattedPageContent(pageId, chapterTitleComponent,
                    formattedPageComponents.subList(0, Math.min(maxLineCountFirstPage, formattedPageComponents.size())),
                    imageResourceLocations, blockResourceLocations));

            for(int i = maxLineCountFirstPage, splitPageCount = 2;i < formattedPageComponents.size();i += MAX_LINES, splitPageCount++) {
                ResourceLocation tmpPageId = new ResourceLocation(pageId.getNamespace(), pageId.getPath() + "/tmp_page_" + splitPageCount);

                formattedPages.add(new FormattedPageContent(tmpPageId, null,
                        formattedPageComponents.subList(i, Math.min(i + MAX_LINES, formattedPageComponents.size())),
                        null, null));
            }
        }
        formattedPages.add(new FormattedPageContent(new ResourceLocation(EnergizedPowerMod.MODID, "back_cover"),
                null, new ArrayList<>(0), null, null));

        this.formattedPages = new ArrayList<>(formattedPages);

        updateButtonVisibility();

        if(openOnPageForBlock != null) {
            outer:
            for(int i = 0;i < formattedPages.size();i++) {
                ResourceLocation[] blockResourceLocations = formattedPages.get(i).getBlockResourceLocations();
                if(blockResourceLocations != null) {
                    for(ResourceLocation block:blockResourceLocations) {
                        if(block.equals(openOnPageForBlock)) {
                            setPage(i);

                            break outer;
                        }
                    }
                }
            }

            openOnPageForBlock = null;
        }
    }

    private void createMenuControls() {
        boolean showTakeButton = lecternBlockEntity != null && minecraft.player.mayBuild();

        addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> onClose()).
                bounds(width / 2 - 116, 232, showTakeButton?116:236, 20).build());

        if(showTakeButton)
            addRenderableWidget(Button.builder(Component.translatable("lectern.take_book"), button -> {
                ModMessages.sendToServer(new PopEnergizedPowerBookFromLecternC2SPacket(lecternBlockEntity.getBlockPos()));
                onClose();
            }).bounds(width / 2 + 2, 232, 116, 20).build());
    }

    private void createPageControlButtons() {
        int startX = (width - 226) / 2;

        forwardButton = addRenderableWidget(new PageButton(startX + 150, 193, true, button -> pageForward(), true));
        backButton = addRenderableWidget(new PageButton(startX + 43, 193, false, button -> pageBack(), true));

        updateButtonVisibility();
    }

    private int getPageCount() {
        return formattedPages == null?0:formattedPages.size();
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
                    minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.f));

                yield true;
            }
            case 267 -> {
                forwardButton.onPress();

                if(currentPage != oldCurrentPage)
                    minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.f));

                yield true;
            }

            default -> false;
        };
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if(mouseButton == 0) {
            Style style = getComponentStyleAt(mouseX, mouseY);
            if(style != null && handleComponentClicked(style))
                return true;
        }

        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean handleComponentClicked(Style style) {
        ClickEvent clickEvent = style.getClickEvent();
        if(clickEvent == null || formattedPages == null)
            return false;

        if(clickEvent.getAction() == ClickEvent.Action.CHANGE_PAGE) {
            int oldCurrentPage = currentPage;
            try {
                return setPage(Integer.parseInt(clickEvent.getValue()));
            }catch(NumberFormatException e) {
                String pageIdString = clickEvent.getValue();

                ResourceLocation pageId = ResourceLocation.tryParse(pageIdString);
                if(pageId == null)
                    return false;

                boolean containsKeyFlag = false;
                int i = 0;
                for(;i < formattedPages.size();i++) {
                    if(formattedPages.get(i).getPageId().equals(pageId)) {
                        containsKeyFlag = true;

                        break;
                    }
                }

                if(!containsKeyFlag)
                    return false;

                return setPage(i);
            }finally {
                if(currentPage != oldCurrentPage)
                    minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.f));
            }
        }

        boolean flag = super.handleComponentClicked(style);
        if(flag && clickEvent.getAction() == ClickEvent.Action.RUN_COMMAND)
            onClose();

        return flag;
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        super.renderBackground(guiGraphics, mouseX, mouseY, delta);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        if(formattedPages == null)
            return;

        int startX = (width - 226) / 2;
        if(currentPage == 0) {
            guiGraphics.blit(FRONT_COVER, startX, 2, 0, 0, 226, 230);
        }else if(currentPage == getPageCount() - 1) {
            guiGraphics.blit(BACK_COVER, startX, 2, 0, 0, 226, 230);
        }else {
            guiGraphics.blit(TEXTURE, startX, 2, 0, 0, 226, 230);
        }

        if(!isCurrentPageCached) {
            //First page is front cover (Number = 0)
            //Last page is back cover (Number = page count - 1)
            currentPageNumberOutput = Component.translatable("book.pageIndicator", currentPage, Math.max(getPageCount() - 1, 1));

            isCurrentPageCached = true;
        }

        int textWidth = font.width(currentPageNumberOutput);
        guiGraphics.drawString(font, currentPageNumberOutput, (int)((width - textWidth) / 2.f), 222, 0xFFFFFFFF, false);

        if(currentPage == 0) {
            renderFrontCover(guiGraphics);

            return;
        }else if(currentPage == getPageCount() - 1) {
            renderImageCentered(guiGraphics, ENERGIZED_COPPER_INGOT, -1);

            return;
        }

        int yOffset = 0;

        ResourceLocation[] images = formattedPages.get(currentPage).getImageResourceLocations();
        ResourceLocation[] blocks = formattedPages.get(currentPage).getBlockResourceLocations();

        Component chapterTitleComponent = formattedPages.get(currentPage).getChapterTitleComponent();
        if(chapterTitleComponent != null) {
            float scaleFactor = 1.5f;

            yOffset = (int)((230 / scaleFactor - font.lineHeight -
                    (formattedPages.get(currentPage).getPageFormattedTexts().isEmpty()?0:
                            ((formattedPages.get(currentPage).getPageFormattedTexts().size() + 1) * font.lineHeight / scaleFactor))) * .5f);

            if(images != null)
                yOffset -= 60 * .5f / scaleFactor;

            if(blocks != null)
                yOffset -= 60 * .5f / scaleFactor;

            guiGraphics.pose().scale(scaleFactor, scaleFactor, 1.f);
            guiGraphics.drawString(font, chapterTitleComponent, (int)((width / scaleFactor - font.width(chapterTitleComponent)) * .5f),
                    yOffset, 0, false);
            guiGraphics.pose().scale(1/scaleFactor, 1/scaleFactor, 1.f);

            yOffset *= scaleFactor;
        }

        if(images != null) {
            renderImageCentered(guiGraphics, images[(currentTick / IMAGE_CYCLE_DELAY) % images.length], yOffset + 15);

            yOffset += 60;
        }

        if(blocks != null) {
            renderBlockCentered(guiGraphics, blocks[(currentTick / IMAGE_CYCLE_DELAY) % blocks.length], yOffset + 15);

            yOffset += 60;
        }

        if(!formattedPages.get(currentPage).getPageFormattedTexts().isEmpty()) {
            for(int i = 0;i < formattedPages.get(currentPage).getPageFormattedTexts().size();i++) {
                FormattedCharSequence formattedCharSequence = formattedPages.get(currentPage).getPageFormattedTexts().get(i);

                float x;
                if(chapterTitleComponent == null)
                    x = startX + 36.f;
                else
                    x = (width - font.width(formattedCharSequence)) * .5f;

                guiGraphics.drawString(font, formattedCharSequence, (int)x, 20 + yOffset + 9 * i, 0, false);
            }

            Style style = getComponentStyleAt(mouseX, mouseY);
            if(style != null)
                guiGraphics.renderComponentHoverEffect(font, style, mouseX, mouseY);
        }
    }

    private void renderFrontCover(GuiGraphics guiGraphics) {
        int startX = (width - 226) / 2;

        float scaleFactor = 1.35f;

        Component component = Component.literal("Energized Power").withStyle(ChatFormatting.GOLD);
        int textWidth = font.width(component);

        guiGraphics.pose().scale(scaleFactor, scaleFactor, 1.f);
        guiGraphics.drawString(font, component, (int)((width / scaleFactor - textWidth) * .5f), (int)(30 / scaleFactor), 0, false);
        guiGraphics.pose().scale(1/scaleFactor, 1/scaleFactor, 1.f);

        component = Component.translatable("book.byAuthor", "JDDev0").withStyle(ChatFormatting.GOLD);
        textWidth = font.width(component);
        guiGraphics.drawString(font, component, (int)((width - textWidth) * .5f), 192 - 45, 0, false);

        for(int i = 0;i < formattedPages.get(currentPage).getPageFormattedTexts().size();i++) {
            FormattedCharSequence formattedCharSequence = formattedPages.get(currentPage).getPageFormattedTexts().get(i);
            guiGraphics.drawString(font, formattedCharSequence, startX + 36, 120 + 9 * i, 0, false);
        }

        renderImageCentered(guiGraphics, ENERGIZED_COPPER_INGOT, 48);
    }

    private void renderImageCentered(GuiGraphics guiGraphics, ResourceLocation image, int y) {
        float scaleFactor = .25f;

        if(y == -1) //Centered
            y = (int)((230 - 256 * scaleFactor) * .5f) + 2;

        guiGraphics.pose().scale(scaleFactor, scaleFactor, 1.f);
        guiGraphics.blit(image, (int)((width / scaleFactor - 256) * .5f), (int)(y / scaleFactor), 0, 0, 256, 256);
        guiGraphics.pose().scale(1/scaleFactor, 1/scaleFactor, 1.f);
    }

    private void renderBlockCentered(GuiGraphics guiGraphics, ResourceLocation blockResourceLocation, int y) {
        if(y == -1) //Centered
            y = (int)((230 - 64) * .5f) + 2;

        Block block = BuiltInRegistries.BLOCK.get(blockResourceLocation);
        ItemStack itemStack = new ItemStack(block);

        ItemRenderer itemRenderer = minecraft.getItemRenderer();
        TextureManager textureManager = minecraft.getTextureManager();

        BakedModel bakedModel = itemRenderer.getModel(itemStack, null, null, 0);

        textureManager.getTexture(InventoryMenu.BLOCK_ATLAS).setFilter(false, false);
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        RenderSystem.setShaderColor(1.f, 1.f, 1.f, 1.f);

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate((int)(width * .5f), y + 64.f * .5f, 50.f);
        guiGraphics.pose().scale(64.f, -64.f, 1.f);

        MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
        Lighting.setupForEntityInInventory();

        itemRenderer.render(itemStack, ItemDisplayContext.GUI, false, guiGraphics.pose(), bufferSource,
                LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, bakedModel);

        bufferSource.endBatch();
        RenderSystem.enableDepthTest();

        guiGraphics.pose().popPose();
    }

    private Style getComponentStyleAt(double x, double y) {
        if(formattedPages == null || formattedPages.get(currentPage).getPageFormattedTexts().isEmpty())
            return null;

        int componentX = Mth.floor(x - (width - 226) * .5 - 36.);
        int componentY = Mth.floor(y - 20.);

        //Translate for chapter pages and pages with graphics
        if(currentPage > 0 && currentPage < getPageCount() - 1) { //Ignore front and back cover pages
            ResourceLocation[] images = formattedPages.get(currentPage).getImageResourceLocations();
            ResourceLocation[] blocks = formattedPages.get(currentPage).getBlockResourceLocations();

            Component chapterTitleComponent = formattedPages.get(currentPage).getChapterTitleComponent();
            if(chapterTitleComponent != null) {
                float scaleFactor = 1.5f;

                componentY = -(int)((230 / scaleFactor - font.lineHeight -
                        (formattedPages.get(currentPage).getPageFormattedTexts().isEmpty()?0:
                                ((formattedPages.get(currentPage).getPageFormattedTexts().size() + 1) * font.lineHeight / scaleFactor))) * .5f);

                if(images != null)
                    componentY += 60 * .5f / scaleFactor;

                if(blocks != null)
                    componentY += 60 * .5f / scaleFactor;

                componentY *= scaleFactor;

                componentY += Mth.floor(y - 20.);
            }

            if(images != null)
                componentY -= 60;

            if(blocks != null)
                componentY -= 60;

            if(chapterTitleComponent != null) {
                int componentIndex = componentY / 9;
                if(componentIndex < 0 || componentIndex >= formattedPages.get(currentPage).getPageFormattedTexts().size())
                    return null;

                FormattedCharSequence formattedCharSequence = formattedPages.get(currentPage).getPageFormattedTexts().get(componentIndex);
                componentX = Mth.floor(x - (width - font.width(formattedCharSequence)) * .5f);
            }
        }

        if(componentX < 0 || componentY < 0)
            return null;

        int componentCount = formattedPages.get(currentPage).getPageFormattedTexts().size();
        if(componentX > 178 || componentY >= 9 * componentCount + componentCount)
            return null;

        int componentIndex = componentY / 9;
        if(componentIndex >= formattedPages.get(currentPage).getPageFormattedTexts().size())
            return null;

        return minecraft.font.getSplitter().componentStyleAtWidth(formattedPages.get(currentPage).getPageFormattedTexts().get(componentIndex), componentX);
    }

    @Override
    public boolean isPauseScreen() {
        return lecternBlockEntity == null;
    }

    @Override
    public void tick() {
        currentTick++;
    }

    @OnlyIn(Dist.CLIENT)
    public static class PageContent {
        private final ResourceLocation pageId;
        private final Component chapterTitleComponent;
        private final Component pageComponent;
        private final ResourceLocation[] imageResourceLocations;
        private final ResourceLocation[] blockResourceLocations;

        public PageContent(ResourceLocation pageId, Component chapterTitleComponent, Component pageComponent,
                           ResourceLocation[] imageResourceLocations, ResourceLocation[] blockResourceLocations) {
            this.pageId = pageId;
            this.chapterTitleComponent = chapterTitleComponent;
            this.pageComponent = pageComponent;
            this.imageResourceLocations = imageResourceLocations;
            this.blockResourceLocations = blockResourceLocations;
        }

        public ResourceLocation getPageId() {
            return pageId;
        }

        public Component getChapterTitleComponent() {
            return chapterTitleComponent;
        }

        public Component getPageComponent() {
            return pageComponent;
        }

        public ResourceLocation[] getImageResourceLocations() {
            return imageResourceLocations;
        }

        public ResourceLocation[] getBlockResourceLocations() {
            return blockResourceLocations;
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static class FormattedPageContent {
        private final ResourceLocation pageId;
        private final Component chapterTitleComponent;
        private final List<FormattedCharSequence> pageFormattedTexts;
        private final ResourceLocation[] imageResourceLocations;
        private final ResourceLocation[] blockResourceLocations;

        public FormattedPageContent(ResourceLocation pageId, Component chapterTitleComponent,
                                    List<FormattedCharSequence> pageFormattedTexts, ResourceLocation[] imageResourceLocations,
                                    ResourceLocation[] blockResourceLocations) {
            this.pageId = pageId;
            this.chapterTitleComponent = chapterTitleComponent;
            this.pageFormattedTexts = pageFormattedTexts;
            this.imageResourceLocations = imageResourceLocations;
            this.blockResourceLocations = blockResourceLocations;
        }

        public ResourceLocation getPageId() {
            return pageId;
        }

        public Component getChapterTitleComponent() {
            return chapterTitleComponent;
        }

        public List<FormattedCharSequence> getPageFormattedTexts() {
            return pageFormattedTexts;
        }

        public ResourceLocation[] getImageResourceLocations() {
            return imageResourceLocations;
        }

        public ResourceLocation[] getBlockResourceLocations() {
            return blockResourceLocations;
        }
    }
}
