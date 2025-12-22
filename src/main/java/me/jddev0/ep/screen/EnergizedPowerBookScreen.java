package me.jddev0.ep.screen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.PopEnergizedPowerBookFromLecternC2SPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.TextAlignment;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.PageButton;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.*;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.*;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.LecternBlockEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EnergizedPowerBookScreen extends Screen {
    public static final Identifier TEXTURE = EPAPI.id("textures/gui/book/energized_power_book.png");
    public static final Identifier FRONT_COVER = EPAPI.id("textures/gui/book/front_cover.png");
    public static final Identifier BACK_COVER = EPAPI.id("textures/gui/book/back_cover.png");

    public static final Identifier ENERGIZED_COPPER_INGOT = EPAPI.id("textures/item/energized_copper_ingot.png");

    public static final int IMAGE_CYCLE_DELAY = ModConfigs.CLIENT_ENERGIZED_POWER_BOOK_IMAGE_CYCLE_DELAY.getValue();

    private static final int MAX_CHARS_PER_LINE = 148;
    private static final int MAX_LINES = 19;

    private static List<PageContent> pages = new ArrayList<>();

    private int currentTick;

    private PageButton forwardButton;
    private PageButton backButton;

    private final LecternBlockEntity lecternBlockEntity;
    private Identifier openOnPageForBlock;

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

    public EnergizedPowerBookScreen(Identifier openOnPageForBlock) {
        this(null, openOnPageForBlock);
    }

    public EnergizedPowerBookScreen(LecternBlockEntity lecternBlockEntity) {
        this(lecternBlockEntity, null);
    }

    public EnergizedPowerBookScreen(LecternBlockEntity lecternBlockEntity, Identifier openOnPageForBlock) {
        super(GameNarrator.NO_TITLE);

        this.lecternBlockEntity = lecternBlockEntity;
        this.openOnPageForBlock = openOnPageForBlock;
    }

    @Override
    protected void init() {
        this.createMenuControls();
        this.createPageControlButtons();

        List<FormattedPageContent> formattedPages = new ArrayList<>();
        formattedPages.add(new FormattedPageContent(EPAPI.id("front_cover"),
                null,
                font.split(Component.translatable("book.energizedpower.front.cover.text").
                        withStyle(ChatFormatting.GRAY), MAX_CHARS_PER_LINE), null, null, null));
        for(PageContent pageContent:pages) {
            Identifier pageId = pageContent.getPageId();
            Component chapterTitleComponent = pageContent.getChapterTitleComponent();
            Identifier[] imageIdentifiers = pageContent.getImageIdentifiers();
            Identifier[] blockIdentifiers = pageContent.getBlockIdentifiers();
            Map<Integer, Identifier> changePageIntToId = pageContent.getChangePageIntToId();

            List<FormattedCharSequence> formattedPageComponents = pageContent.getPageComponent() == null?new ArrayList<>(0):
                    font.split(pageContent.getPageComponent().copy().withStyle(Style::withoutShadow).
                            withStyle(style -> {
                                if(style.getColor() == null)
                                    return style.withColor(0xFF000000);

                                return style;
                            }), MAX_CHARS_PER_LINE);

            if(chapterTitleComponent != null) {
                formattedPages.add(new FormattedPageContent(pageId, chapterTitleComponent, formattedPageComponents,
                        imageIdentifiers, blockIdentifiers, changePageIntToId));

                continue;
            }

            //Automatically split pages into multiple
            int maxLineCountFirstPage = MAX_LINES - (imageIdentifiers != null || blockIdentifiers != null?7:0);

            formattedPages.add(new FormattedPageContent(pageId, chapterTitleComponent,
                    formattedPageComponents.subList(0, Math.min(maxLineCountFirstPage, formattedPageComponents.size())),
                    imageIdentifiers, blockIdentifiers, changePageIntToId));

            for(int i = maxLineCountFirstPage, splitPageCount = 2;i < formattedPageComponents.size();i += MAX_LINES, splitPageCount++) {
                Identifier tmpPageId = Identifier.fromNamespaceAndPath(pageId.getNamespace(), pageId.getPath() + "/tmp_page_" + splitPageCount);

                formattedPages.add(new FormattedPageContent(tmpPageId, null,
                        formattedPageComponents.subList(i, Math.min(i + MAX_LINES, formattedPageComponents.size())),
                        null, null, changePageIntToId));
            }
        }
        formattedPages.add(new FormattedPageContent(EPAPI.id("back_cover"),
                null, new ArrayList<>(0), null, null, null));

        this.formattedPages = new ArrayList<>(formattedPages);

        updateButtonVisibility();

        if(openOnPageForBlock != null) {
            outer:
            for(int i = 0;i < formattedPages.size();i++) {
                Identifier[] blockIdentifiers = formattedPages.get(i).getBlockIdentifiers();
                if(blockIdentifiers != null) {
                    for(Identifier block:blockIdentifiers) {
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
    public boolean keyPressed(KeyEvent event) {
        int keyCode = event.key();

        if(super.keyPressed(event))
            return true;

        int oldCurrentPage = currentPage;
        return switch(keyCode) {
            case 266 -> {
                backButton.onPress(event);

                if(currentPage != oldCurrentPage)
                    minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.f));

                yield true;
            }
            case 267 -> {
                forwardButton.onPress(event);

                if(currentPage != oldCurrentPage)
                    minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.f));

                yield true;
            }

            default -> false;
        };
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        double mouseX = click.x();
        double mouseY = click.y();
        int mouseButton = click.button();

        ActiveTextCollector.ClickableStyleFinder clickHandler = new ActiveTextCollector.ClickableStyleFinder(this.font, (int)mouseX, (int)mouseY);
        renderText(clickHandler);

        if(mouseButton == 0) {
            Style style = clickHandler.result();
            if(style != null && handleComponentClicked(style))
                return true;
        }

        return super.mouseClicked(click, doubled);
    }

    public boolean handleComponentClicked(Style style) {
        ClickEvent clickEvent = style.getClickEvent();
        if(clickEvent == null || formattedPages == null)
            return false;

        if(clickEvent.action() == ClickEvent.Action.CHANGE_PAGE) {
            FormattedPageContent formattedPageContent = formattedPages.get(currentPage);
            Map<Integer, Identifier> changePageIntToId = formattedPageContent.getChangePageIntToId();
            ClickEvent.ChangePage changePageEvent = (ClickEvent.ChangePage)clickEvent;

            int oldCurrentPage = currentPage;
            try {
                int pageNum = changePageEvent.page();

                Identifier pageId = changePageIntToId == null?null:changePageIntToId.get(pageNum);
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

        if(clickEvent.action() == ClickEvent.Action.RUN_COMMAND) {
            ClickEvent.RunCommand runCommandEvent = (ClickEvent.RunCommand)clickEvent;

            onClose();
            clickCommandAction(minecraft.player, runCommandEvent.command(), null);
        }else {
            defaultHandleGameClickEvent(clickEvent, this.minecraft, this);
        }

        return false;
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        super.renderBackground(guiGraphics, mouseX, mouseY, delta);

        ActiveTextCollector textDrawer = guiGraphics.textRenderer(GuiGraphics.HoveredTextEffects.TOOLTIP_AND_CURSOR);

        if(formattedPages == null)
            return;

        int startX = (width - 226) / 2;
        if(currentPage == 0) {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, FRONT_COVER, startX, 2, 0, 0, 226, 230, 256, 256);
        }else if(currentPage == getPageCount() - 1) {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BACK_COVER, startX, 2, 0, 0, 226, 230, 256, 256);
        }else {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, startX, 2, 0, 0, 226, 230, 256, 256);
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

        Identifier[] images = formattedPages.get(currentPage).getImageIdentifiers();
        Identifier[] blocks = formattedPages.get(currentPage).getBlockIdentifiers();

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

            guiGraphics.pose().scale(scaleFactor, scaleFactor);
            guiGraphics.drawString(font, chapterTitleComponent, (int)((width / scaleFactor - font.width(chapterTitleComponent)) * .5f),
                    yOffset, 0xFF000000, false);
            guiGraphics.pose().scale(1/scaleFactor, 1/scaleFactor);

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

        renderText(textDrawer);
    }

    private void renderText(ActiveTextCollector textDrawer) {
        if(formattedPages == null)
            return;

        int startX = (width - 226) / 2;
        int yOffset = 0;

        Identifier[] images = formattedPages.get(currentPage).getImageIdentifiers();
        Identifier[] blocks = formattedPages.get(currentPage).getBlockIdentifiers();

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

            yOffset *= scaleFactor;
        }

        if(images != null) {
            yOffset += 60;
        }

        if(blocks != null) {
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

                textDrawer.accept(TextAlignment.LEFT, (int)x, 20 + yOffset + 9 * i, textDrawer.defaultParameters(), formattedCharSequence);
            }
        }
    }

    private void renderFrontCover(GuiGraphics guiGraphics) {
        int startX = (width - 226) / 2;

        float scaleFactor = 1.35f;

        Component component = Component.literal("Energized Power").withStyle(ChatFormatting.GOLD);
        int textWidth = font.width(component);

        guiGraphics.pose().scale(scaleFactor, scaleFactor);
        guiGraphics.drawString(font, component, (int)((width / scaleFactor - textWidth) * .5f), (int)(30 / scaleFactor), 0xFF000000, false);
        guiGraphics.pose().scale(1/scaleFactor, 1/scaleFactor);

        component = Component.translatable("book.byAuthor", "JDDev0").withStyle(ChatFormatting.GOLD);
        textWidth = font.width(component);
        guiGraphics.drawString(font, component, (int)((width - textWidth) * .5f), 192 - 45, 0xFF000000, false);

        for(int i = 0;i < formattedPages.get(currentPage).getPageFormattedTexts().size();i++) {
            FormattedCharSequence formattedCharSequence = formattedPages.get(currentPage).getPageFormattedTexts().get(i);
            guiGraphics.drawString(font, formattedCharSequence, startX + 36, 120 + 9 * i, 0xFF000000, false);
        }

        renderImageCentered(guiGraphics, ENERGIZED_COPPER_INGOT, 48);
    }

    private void renderImageCentered(GuiGraphics guiGraphics, Identifier image, int y) {
        float scaleFactor = .25f;

        if(y == -1) //Centered
            y = (int)((230 - 256 * scaleFactor) * .5f) + 2;

        guiGraphics.pose().scale(scaleFactor, scaleFactor);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, image, (int)((width / scaleFactor - 256) * .5f), (int)(y / scaleFactor), 0, 0, 256, 256, 256, 256);
        guiGraphics.pose().scale(1/scaleFactor, 1/scaleFactor);
    }

    private void renderBlockCentered(GuiGraphics guiGraphics, Identifier blockIdentifier, int y) {
        if(y == -1) //Centered
            y = (int)((230 - 64) * .5f) + 2;

        Block block = BuiltInRegistries.BLOCK.getValue(blockIdentifier);
        ItemStack itemStack = new ItemStack(block);

        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate((width - 16.f * 4.f) * .5f, y - 4.f * .5f);
        guiGraphics.pose().scale(4.f, 4.f);

        guiGraphics.renderFakeItem(itemStack, 0, 0);

        guiGraphics.pose().popMatrix();
    }

    @Override
    public boolean isPauseScreen() {
        return lecternBlockEntity == null;
    }

    @Override
    public void tick() {
        currentTick++;
    }

    public static class PageContent {
        private final Identifier pageId;
        private final Component chapterTitleComponent;
        private final Component pageComponent;
        private final Identifier[] imageIdentifiers;
        private final Identifier[] blockIdentifiers;
        private final Map<Integer, Identifier> changePageIntToId;

        public PageContent(Identifier pageId, Component chapterTitleComponent, Component pageComponent,
                           Identifier[] imageIdentifiers, Identifier[] blockIdentifiers,
                           Map<Integer, Identifier> changePageIntToId) {
            this.pageId = pageId;
            this.chapterTitleComponent = chapterTitleComponent;
            this.pageComponent = pageComponent;
            this.imageIdentifiers = imageIdentifiers;
            this.blockIdentifiers = blockIdentifiers;
            this.changePageIntToId = changePageIntToId;
        }

        public Identifier getPageId() {
            return pageId;
        }

        public Component getChapterTitleComponent() {
            return chapterTitleComponent;
        }

        public Component getPageComponent() {
            return pageComponent;
        }

        public Identifier[] getImageIdentifiers() {
            return imageIdentifiers;
        }

        public Identifier[] getBlockIdentifiers() {
            return blockIdentifiers;
        }

        public Map<Integer, Identifier> getChangePageIntToId() {
            return changePageIntToId;
        }
    }

    private static class FormattedPageContent {
        private final Identifier pageId;
        private final Component chapterTitleComponent;
        private final List<FormattedCharSequence> pageFormattedTexts;
        private final Identifier[] imageIdentifiers;
        private final Identifier[] blockIdentifiers;
        private final Map<Integer, Identifier> changePageIntToId;

        public FormattedPageContent(Identifier pageId, Component chapterTitleComponent,
                                    List<FormattedCharSequence> pageFormattedTexts, Identifier[] imageIdentifiers,
                                    Identifier[] blockIdentifiers, Map<Integer, Identifier> changePageIntToId) {
            this.pageId = pageId;
            this.chapterTitleComponent = chapterTitleComponent;
            this.pageFormattedTexts = pageFormattedTexts;
            this.imageIdentifiers = imageIdentifiers;
            this.blockIdentifiers = blockIdentifiers;
            this.changePageIntToId = changePageIntToId;
        }

        public Identifier getPageId() {
            return pageId;
        }

        public Component getChapterTitleComponent() {
            return chapterTitleComponent;
        }

        public List<FormattedCharSequence> getPageFormattedTexts() {
            return pageFormattedTexts;
        }

        public Identifier[] getImageIdentifiers() {
            return imageIdentifiers;
        }

        public Identifier[] getBlockIdentifiers() {
            return blockIdentifiers;
        }

        public Map<Integer, Identifier> getChangePageIntToId() {
            return changePageIntToId;
        }
    }
}
