package me.jddev0.ep.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.PopEnergizedPowerBookFromLecternC2SPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.PageButton;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class EnergizedPowerBookScreen extends Screen {
    public static final ResourceLocation TEXTURE = new ResourceLocation(EnergizedPowerMod.MODID, "textures/gui/energized_power_book.png");
    public static final ResourceLocation FRONT_COVER = new ResourceLocation(EnergizedPowerMod.MODID, "textures/gui/book/front_cover.png");
    public static final ResourceLocation BACK_COVER = new ResourceLocation(EnergizedPowerMod.MODID, "textures/gui/book/back_cover.png");

    public static final ResourceLocation ENERGIZED_COPPER_INGOT = new ResourceLocation(EnergizedPowerMod.MODID, "textures/item/energized_copper_ingot.png");

    private PageButton forwardButton;
    private PageButton backButton;

    private final LecternBlockEntity lecternBlockEntity;

    public static List<PageContent> pages = new LinkedList<>();
    private int currentPage;
    private Component currentPageNumberOutput = TextComponent.EMPTY;
    private boolean isCurrentPageCached;
    private List<FormattedCharSequence> cachedPageComponents = Collections.emptyList();

    public EnergizedPowerBookScreen() {
        this(null);
    }

    public EnergizedPowerBookScreen(LecternBlockEntity lecternBlockEntity) {
        super(NarratorChatListener.NO_TITLE);

        this.lecternBlockEntity = lecternBlockEntity;
    }

    @Override
    protected void init() {
        this.createMenuControls();
        this.createPageControlButtons();
    }

    private void createMenuControls() {
        boolean showTakeButton = lecternBlockEntity != null && minecraft.player.mayBuild();

        addRenderableWidget(new Button(width / 2 - 100, 196, showTakeButton?98:200, 20, CommonComponents.GUI_DONE, button -> onClose()));

        if(showTakeButton)
            addRenderableWidget(new Button(width / 2 + 2, 196, 98, 20, new TranslatableComponent("lectern.take_book"), button -> {
                ModMessages.sendToServer(new PopEnergizedPowerBookFromLecternC2SPacket(lecternBlockEntity.getBlockPos()));
                onClose();
            }));
    }

    private void createPageControlButtons() {
        int startX = (width - 192) / 2;

        forwardButton = addRenderableWidget(new PageButton(startX + 116, 159, true, button -> pageForward(), true));
        backButton = addRenderableWidget(new PageButton(startX + 43, 159, false, button -> pageBack(), true));

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
        if(clickEvent == null)
            return false;

        if(clickEvent.getAction() == ClickEvent.Action.CHANGE_PAGE) {
            try {
                return setPage(Integer.parseInt(clickEvent.getValue()) - 1);
            }catch(NumberFormatException e) {
                return false;
            }
        }

        boolean flag = super.handleComponentClicked(style);
        if(flag && clickEvent.getAction() == ClickEvent.Action.RUN_COMMAND)
            onClose();

        return flag;
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(poseStack);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        int startX = (width - 192) / 2;
        if(currentPage == 0) {
            RenderSystem.setShaderTexture(0, FRONT_COVER);
            blit(poseStack, startX, 2, 0, 0, 192, 192);
            RenderSystem.setShaderTexture(0, TEXTURE);
        }else if(currentPage == getPageCount() - 1) {
            RenderSystem.setShaderTexture(0, BACK_COVER);
            blit(poseStack, startX, 2, 0, 0, 192, 192);
            RenderSystem.setShaderTexture(0, TEXTURE);
        }else {
            RenderSystem.setShaderTexture(0, TEXTURE);
            blit(poseStack, startX, 2, 0, 0, 192, 192);
        }

        if(!isCurrentPageCached) {
            if(currentPage == 0) {
                cachedPageComponents = font.split(new TranslatableComponent("book.energizedpower.front.cover.text").withStyle(ChatFormatting.GRAY), 114);
            }else if(currentPage == getPageCount() - 1) {
                cachedPageComponents = Collections.emptyList();
            }else {
                //Front cover and back cover are not included
                if(pages.get(currentPage - 1).getPageComponent() != null)
                    cachedPageComponents = font.split(pages.get(currentPage - 1).getPageComponent(), 114);
            }
            //First page is front cover (Number = 0)
            //Last page is back cover (Number = page count - 1)
            currentPageNumberOutput = new TranslatableComponent("book.pageIndicator", currentPage, Math.max(getPageCount() - 1, 1));
        }

        isCurrentPageCached = true;

        int textWidth = font.width(currentPageNumberOutput);
        font.draw(poseStack, currentPageNumberOutput, (width - textWidth) / 2.f, 185, 0xFFFFFFFF);

        if(currentPage == 0) {
            renderFrontCover(poseStack);

            super.render(poseStack, mouseX, mouseY, partialTicks);

            return;
        }else if(currentPage == getPageCount() - 1) {
            renderImageCentered(poseStack, ENERGIZED_COPPER_INGOT, -1);

            super.render(poseStack, mouseX, mouseY, partialTicks);

            return;
        }

        int yOffset = 0;

        ResourceLocation image = pages.get(currentPage - 1).getImageResourceLocation();
        ResourceLocation block = pages.get(currentPage - 1).getBlockResourceLocation();

        Component chapterTitleComponent = pages.get(currentPage - 1).getChapterTitleComponent();
        if(chapterTitleComponent != null) {
            float scaleFactor = 1.5f;

            yOffset = (int)((192 / scaleFactor - font.lineHeight -
                    (cachedPageComponents == null?0:((cachedPageComponents.size() + 1) * font.lineHeight / scaleFactor))) * .5f);

            if(image != null)
                yOffset -= 60 * .5f / scaleFactor;

            if(block != null)
                yOffset -= 60 * .5f / scaleFactor;

            poseStack.scale(scaleFactor, scaleFactor, 1.f);
            font.draw(poseStack, chapterTitleComponent, (width / scaleFactor - font.width(chapterTitleComponent)) * .5f,
                    yOffset, 0);
            poseStack.scale(1/scaleFactor, 1/scaleFactor, 1.f);

            yOffset *= scaleFactor;
        }

        if(image != null) {
            renderImageCentered(poseStack, image, yOffset + 15);

            yOffset += 60;
        }

        if(block != null) {
            renderBlockCentered(poseStack, block, yOffset + 15);

            yOffset += 60;
        }

        if(cachedPageComponents != null) {
            for(int i = 0;i < cachedPageComponents.size();i++) {
                FormattedCharSequence formattedCharSequence = cachedPageComponents.get(i);

                float x;
                if(chapterTitleComponent == null)
                    x = startX + 36.f;
                else
                    x = (width - font.width(formattedCharSequence)) * .5f;

                font.draw(poseStack, formattedCharSequence, x, 20.f + yOffset + 9 * i, 0);
            }

            Style style = getComponentStyleAt(mouseX, mouseY);
            if(style != null)
                renderComponentHoverEffect(poseStack, style, mouseX, mouseY);
        }

        super.render(poseStack, mouseX, mouseY, partialTicks);
    }

    private void renderFrontCover(PoseStack poseStack) {
        int startX = (width - 192) / 2;

        float scaleFactor = 1.35f;

        Component component = new TextComponent("Energized Power").withStyle(ChatFormatting.GOLD);
        int textWidth = font.width(component);

        poseStack.scale(scaleFactor, scaleFactor, 1.f);
        font.draw(poseStack, component, (width / scaleFactor - textWidth) * .5f, 30 / scaleFactor, 0);
        poseStack.scale(1/scaleFactor, 1/scaleFactor, 1.f);

        component = new TranslatableComponent("book.byAuthor", "JDDev0").withStyle(ChatFormatting.GOLD);
        textWidth = font.width(component);
        font.draw(poseStack, component, (width - textWidth) * .5f, 192 - 45.f, 0);

        for(int i = 0;i < cachedPageComponents.size();i++) {
            FormattedCharSequence formattedCharSequence = cachedPageComponents.get(i);
            font.draw(poseStack, formattedCharSequence, startX + 36.f, 120.f + 9 * i, 0);
        }

        renderImageCentered(poseStack, ENERGIZED_COPPER_INGOT, 48);
    }

    private void renderImageCentered(PoseStack poseStack, ResourceLocation image, int y) {
        float scaleFactor = .25f;

        if(y == -1) //Centered
            y = (int)((192 - 256 * scaleFactor) * .5f) + 2;

        RenderSystem.setShaderTexture(0, image);
        poseStack.scale(scaleFactor, scaleFactor, 1.f);
        blit(poseStack, (int)((width / scaleFactor - 256) * .5f), (int)(y / scaleFactor), 0, 0, 256, 256);
        poseStack.scale(1/scaleFactor, 1/scaleFactor, 1.f);
    }

    private void renderBlockCentered(PoseStack poseStack, ResourceLocation blockResourceLocation, int y) {
        if(y == -1) //Centered
            y = (int)((192 - 64) * .5f) + 2;

        Block block = ForgeRegistries.BLOCKS.getValue(blockResourceLocation);
        ItemStack itemStack = new ItemStack(block);

        ItemRenderer itemRenderer = minecraft.getItemRenderer();
        TextureManager textureManager = minecraft.getTextureManager();

        BakedModel bakedModel = itemRenderer.getModel(itemStack, null, null, 0);

        textureManager.getTexture(InventoryMenu.BLOCK_ATLAS).setFilter(false, false);
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        RenderSystem.setShaderColor(1.f, 1.f, 1.f, 1.f);

        poseStack.pushPose();
        poseStack.translate((int)(width * .5f), y + 64.f * .5f, 50.f);
        poseStack.scale(64.f, -64.f, 1.f);

        MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
        Lighting.setupForEntityInInventory();

        itemRenderer.render(itemStack, ItemTransforms.TransformType.GUI, false, poseStack, bufferSource,
                LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, bakedModel);

        bufferSource.endBatch();
        RenderSystem.enableDepthTest();

        poseStack.popPose();
    }

    private Style getComponentStyleAt(double x, double y) {
        if(cachedPageComponents.isEmpty())
            return null;

        int componentX = Mth.floor(x - (width - 192) * .5 - 36.);
        int componentY = Mth.floor(y - 20.);
        if(componentX < 0 || componentY < 0)
            return null;

        int componentCount = cachedPageComponents.size();
        if(componentX > 144 || componentY >= 9 * componentCount + componentCount)
            return null;

        int componentIndex = componentY / 9;
        if(componentIndex >= cachedPageComponents.size())
            return null;

        return minecraft.font.getSplitter().componentStyleAtWidth(cachedPageComponents.get(componentIndex), componentX);
    }

    @Override
    public boolean isPauseScreen() {
        return lecternBlockEntity == null;
    }

    @OnlyIn(Dist.CLIENT)
    public static class PageContent {
        private final Component chapterTitleComponent;
        private final Component pageComponent;
        private final ResourceLocation imageResourceLocation;
        private final ResourceLocation blockResourceLocation;

        public PageContent(Component chapterTitleComponent, Component pageComponent, ResourceLocation imageResourceLocation, ResourceLocation blockResourceLocation) {
            this.chapterTitleComponent = chapterTitleComponent;
            this.pageComponent = pageComponent;
            this.imageResourceLocation = imageResourceLocation;
            this.blockResourceLocation = blockResourceLocation;
        }

        public Component getChapterTitleComponent() {
            return chapterTitleComponent;
        }

        public Component getPageComponent() {
            return pageComponent;
        }

        public ResourceLocation getImageResourceLocation() {
            return imageResourceLocation;
        }

        public ResourceLocation getBlockResourceLocation() {
            return blockResourceLocation;
        }
    }
}
