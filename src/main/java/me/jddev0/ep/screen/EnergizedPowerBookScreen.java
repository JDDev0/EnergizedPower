package me.jddev0.ep.screen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.PopEnergizedPowerBookFromLecternC2SPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.entity.LecternBlockEntity;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.PageTurnWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
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

    private PageTurnWidget forwardButton;
    private PageTurnWidget backButton;

    private final LecternBlockEntity lecternBlockEntity;
    private Identifier openOnPageForBlock;

    private List<FormattedPageContent> formattedPages;

    private int currentPage;
    private Text currentPageNumberOutput = Text.empty();
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
        super(NarratorManager.EMPTY);

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
                textRenderer.wrapLines(Text.translatable("book.energizedpower.front.cover.text").
                        formatted(Formatting.GRAY), MAX_CHARS_PER_LINE), null, null, null));
        for(PageContent pageContent:pages) {
            Identifier pageId = pageContent.getPageId();
            Text chapterTitleComponent = pageContent.getChapterTitleComponent();
            Identifier[] imageResourceLocations = pageContent.getImageResourceLocations();
            Identifier[] blockResourceLocations = pageContent.getBlockResourceLocations();
            Map<Integer, Identifier> changePageIntToId = pageContent.getChangePageIntToId();

            List<OrderedText> formattedPageComponents = pageContent.getPageComponent() == null?new ArrayList<>(0):
                    textRenderer.wrapLines(pageContent.getPageComponent(), MAX_CHARS_PER_LINE);

            if(chapterTitleComponent != null) {
                formattedPages.add(new FormattedPageContent(pageId, chapterTitleComponent, formattedPageComponents,
                        imageResourceLocations, blockResourceLocations, changePageIntToId));

                continue;
            }

            //Automatically split pages into multiple
            int maxLineCountFirstPage = MAX_LINES - (imageResourceLocations != null || blockResourceLocations != null?7:0);

            formattedPages.add(new FormattedPageContent(pageId, chapterTitleComponent,
                    formattedPageComponents.subList(0, Math.min(maxLineCountFirstPage, formattedPageComponents.size())),
                    imageResourceLocations, blockResourceLocations, changePageIntToId));

            for(int i = maxLineCountFirstPage, splitPageCount = 2;i < formattedPageComponents.size();i += MAX_LINES, splitPageCount++) {
                Identifier tmpPageId = Identifier.of(pageId.getNamespace(), pageId.getPath() + "/tmp_page_" + splitPageCount);

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
                Identifier[] blockResourceLocations = formattedPages.get(i).getBlockResourceLocations();
                if(blockResourceLocations != null) {
                    for(Identifier block:blockResourceLocations) {
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
        boolean showTakeButton = lecternBlockEntity != null && client.player.canModifyBlocks();

        addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> close()).
                dimensions(width / 2 - 116, 232, showTakeButton?116:236, 20).build());

        if(showTakeButton)
            addDrawableChild(ButtonWidget.builder(Text.translatable("lectern.take_book"), button -> {
                ModMessages.sendClientPacketToServer(new PopEnergizedPowerBookFromLecternC2SPacket(lecternBlockEntity.getPos()));
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
    public boolean keyPressed(KeyInput input) {
        int keyCode = input.key();

        if(super.keyPressed(input))
            return true;

        int oldCurrentPage = currentPage;
        return switch(keyCode) {
            case 266 -> {
                backButton.onPress(input);

                if(currentPage != oldCurrentPage)
                    client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.ITEM_BOOK_PAGE_TURN, 1.f));

                yield true;
            }
            case 267 -> {
                forwardButton.onPress(input);

                if(currentPage != oldCurrentPage)
                    client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.ITEM_BOOK_PAGE_TURN, 1.f));

                yield true;
            }

            default -> false;
        };
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        double mouseX = click.x();
        double mouseY = click.y();
        int mouseButton = click.button();

        if(mouseButton == 0) {
            Style style = getComponentStyleAt(mouseX, mouseY);
            if(style != null && handleTextClick(style))
                return true;
        }

        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean handleTextClick(Style style) {
        ClickEvent clickEvent = style.getClickEvent();
        if(clickEvent == null || formattedPages == null)
            return false;

        if(clickEvent.getAction() == ClickEvent.Action.CHANGE_PAGE) {
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
                    client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.ITEM_BOOK_PAGE_TURN, 1.f));
            }
        }

        boolean flag = super.handleTextClick(style);
        if(flag && clickEvent.getAction() == ClickEvent.Action.RUN_COMMAND)
            close();

        return flag;
    }

    @Override
    public void renderBackground(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        super.renderBackground(drawContext, mouseX, mouseY, delta);

        if(formattedPages == null)
            return;

        int startX = (width - 226) / 2;
        if(currentPage == 0) {
            drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, FRONT_COVER, startX, 2, 0, 0, 226, 230, 256, 256);
        }else if(currentPage == getPageCount() - 1) {
            drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, BACK_COVER, startX, 2, 0, 0, 226, 230, 256, 256);
        }else {
            drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, startX, 2, 0, 0, 226, 230, 256, 256);
        }

        if(!isCurrentPageCached) {
            //First page is front cover (Number = 0)
            //Last page is back cover (Number = page count - 1)
            currentPageNumberOutput = Text.translatable("book.pageIndicator", currentPage, Math.max(getPageCount() - 1, 1));

            isCurrentPageCached = true;
        }

        int textWidth = textRenderer.getWidth(currentPageNumberOutput);
        drawContext.drawText(textRenderer, currentPageNumberOutput, (int)((width - textWidth) / 2.f), 222, 0xFFFFFFFF, false);

        if(currentPage == 0) {
            renderFrontCover(drawContext);

            return;
        }else if(currentPage == getPageCount() - 1) {
            renderImageCentered(drawContext, ENERGIZED_COPPER_INGOT, -1);

            return;
        }

        int yOffset = 0;

        Identifier[] images = formattedPages.get(currentPage).getImageResourceLocations();
        Identifier[] blocks = formattedPages.get(currentPage).getBlockResourceLocations();

        Text chapterTitleComponent = formattedPages.get(currentPage).getChapterTitleComponent();
        if(chapterTitleComponent != null) {
            float scaleFactor = 1.5f;

            yOffset = (int)((230 / scaleFactor - textRenderer.fontHeight -
                    (formattedPages.get(currentPage).getPageFormattedTexts().isEmpty()?0:
                            ((formattedPages.get(currentPage).getPageFormattedTexts().size() + 1) * textRenderer.fontHeight / scaleFactor))) * .5f);

            if(images != null)
                yOffset -= 60 * .5f / scaleFactor;

            if(blocks != null)
                yOffset -= 60 * .5f / scaleFactor;

            drawContext.getMatrices().scale(scaleFactor, scaleFactor);
            drawContext.drawText(textRenderer, chapterTitleComponent, (int)((width / scaleFactor - textRenderer.getWidth(chapterTitleComponent)) * .5f),
                    yOffset, 0xFF000000, false);
            drawContext.getMatrices().scale(1/scaleFactor, 1/scaleFactor);

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

        if(!formattedPages.get(currentPage).getPageFormattedTexts().isEmpty()) {
            for(int i = 0;i < formattedPages.get(currentPage).getPageFormattedTexts().size();i++) {
                OrderedText formattedCharSequence = formattedPages.get(currentPage).getPageFormattedTexts().get(i);

                float x;
                if(chapterTitleComponent == null)
                    x = startX + 36.f;
                else
                    x = (width - textRenderer.getWidth(formattedCharSequence)) * .5f;

                drawContext.drawText(textRenderer, formattedCharSequence, (int)x, 20 + yOffset + 9 * i, 0xFF000000, false);
            }

            Style style = getComponentStyleAt(mouseX, mouseY);
            if(style != null)
                drawContext.drawHoverEvent(textRenderer, style, mouseX, mouseY);
        }
    }

    private void renderFrontCover(DrawContext drawContext) {
        int startX = (width - 226) / 2;

        float scaleFactor = 1.35f;

        Text component = Text.literal("Energized Power").formatted(Formatting.GOLD);
        int textWidth = textRenderer.getWidth(component);

        drawContext.getMatrices().scale(scaleFactor, scaleFactor);
        drawContext.drawText(textRenderer, component, (int)((width / scaleFactor - textWidth) * .5f),
                (int)(30 / scaleFactor), 0xFF000000, false);
        drawContext.getMatrices().scale(1/scaleFactor, 1/scaleFactor);

        component = Text.translatable("book.byAuthor", "JDDev0").formatted(Formatting.GOLD);
        textWidth = textRenderer.getWidth(component);
        drawContext.drawText(textRenderer, component, (int)((width - textWidth) * .5f), 192 - 45, 0xFF000000, false);

        for(int i = 0;i < formattedPages.get(currentPage).getPageFormattedTexts().size();i++) {
            OrderedText formattedCharSequence = formattedPages.get(currentPage).getPageFormattedTexts().get(i);
            drawContext.drawText(textRenderer, formattedCharSequence, startX + 36, 120 + 9 * i, 0xFF000000, false);
        }

        renderImageCentered(drawContext, ENERGIZED_COPPER_INGOT, 48);
    }

    private void renderImageCentered(DrawContext drawContext, Identifier image, int y) {
        float scaleFactor = .25f;

        if(y == -1) //Centered
            y = (int)((230 - 256 * scaleFactor) * .5f) + 2;

        drawContext.getMatrices().scale(scaleFactor, scaleFactor);
        drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, image, (int)((width / scaleFactor - 256) * .5f), (int)(y / scaleFactor), 0, 0, 256, 256, 256, 256);
        drawContext.getMatrices().scale(1/scaleFactor, 1/scaleFactor);
    }

    private void renderBlockCentered(DrawContext drawContext, Identifier blockResourceLocation, int y) {
        if(y == -1) //Centered
            y = (int)((230 - 64) * .5f) + 2;

        Block block = Registries.BLOCK.get(blockResourceLocation);
        ItemStack itemStack = new ItemStack(block);

        drawContext.getMatrices().pushMatrix();
        drawContext.getMatrices().translate((width - 16.f * 4.f) * .5f, y - 4.f * .5f);
        drawContext.getMatrices().scale(4.f, 4.f);

        drawContext.drawItemWithoutEntity(itemStack, 0, 0);

        drawContext.getMatrices().popMatrix();
    }

    private Style getComponentStyleAt(double x, double y) {
        if(formattedPages == null || formattedPages.get(currentPage).getPageFormattedTexts().isEmpty())
            return null;

        int componentX = MathHelper.floor(x - (width - 226) * .5 - 36.);
        int componentY = MathHelper.floor(y - 20.);

        //Translate for chapter pages and pages with graphics
        if(currentPage > 0 && currentPage < getPageCount() - 1) { //Ignore front and back cover pages
            Identifier[] images = formattedPages.get(currentPage).getImageResourceLocations();
            Identifier[] blocks = formattedPages.get(currentPage).getBlockResourceLocations();

            Text chapterTitleComponent = formattedPages.get(currentPage).getChapterTitleComponent();
            if(chapterTitleComponent != null) {
                float scaleFactor = 1.5f;

                componentY = -(int)((230 / scaleFactor - textRenderer.fontHeight -
                        (formattedPages.get(currentPage).getPageFormattedTexts().isEmpty()?0:
                                ((formattedPages.get(currentPage).getPageFormattedTexts().size() + 1) * textRenderer.fontHeight / scaleFactor))) * .5f);

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
                if(componentIndex < 0 || componentIndex >= formattedPages.get(currentPage).getPageFormattedTexts().size())
                    return null;

                OrderedText formattedCharSequence = formattedPages.get(currentPage).getPageFormattedTexts().get(componentIndex);
                componentX = MathHelper.floor(x - (width - textRenderer.getWidth(formattedCharSequence)) * .5f);
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

        return client.textRenderer.getTextHandler().getStyleAt(formattedPages.get(currentPage).getPageFormattedTexts().get(componentIndex), componentX);
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
        private final Map<Integer, Identifier> changePageIntToId;

        public PageContent(Identifier pageId, Text chapterTitleComponent, Text pageComponent,
                           Identifier[] imageResourceLocations, Identifier[] blockResourceLocations,
                           Map<Integer, Identifier> changePageIntToId) {
            this.pageId = pageId;
            this.chapterTitleComponent = chapterTitleComponent;
            this.pageComponent = pageComponent;
            this.imageResourceLocations = imageResourceLocations;
            this.blockResourceLocations = blockResourceLocations;
            this.changePageIntToId = changePageIntToId;
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

        public Map<Integer, Identifier> getChangePageIntToId() {
            return changePageIntToId;
        }
    }

    @Environment(EnvType.CLIENT)
    private static class FormattedPageContent {
        private final Identifier pageId;
        private final Text chapterTitleComponent;
        private final List<OrderedText> pageFormattedTexts;
        private final Identifier[] imageResourceLocations;
        private final Identifier[] blockResourceLocations;
        private final Map<Integer, Identifier> changePageIntToId;

        public FormattedPageContent(Identifier pageId, Text chapterTitleComponent,
                                    List<OrderedText> pageFormattedTexts, Identifier[] imageResourceLocations,
                                    Identifier[] blockResourceLocations,
                                    Map<Integer, Identifier> changePageIntToId) {
            this.pageId = pageId;
            this.chapterTitleComponent = chapterTitleComponent;
            this.pageFormattedTexts = pageFormattedTexts;
            this.imageResourceLocations = imageResourceLocations;
            this.blockResourceLocations = blockResourceLocations;
            this.changePageIntToId = changePageIntToId;
        }

        public Identifier getPageId() {
            return pageId;
        }

        public Text getChapterTitleComponent() {
            return chapterTitleComponent;
        }

        public List<OrderedText> getPageFormattedTexts() {
            return pageFormattedTexts;
        }

        public Identifier[] getImageResourceLocations() {
            return imageResourceLocations;
        }

        public Identifier[] getBlockResourceLocations() {
            return blockResourceLocations;
        }

        public Map<Integer, Identifier> getChangePageIntToId() {
            return changePageIntToId;
        }
    }
}
