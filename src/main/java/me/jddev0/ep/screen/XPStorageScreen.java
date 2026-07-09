package me.jddev0.ep.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.InsertExtractXPFromXPStorageC2SPacket;
import me.jddev0.ep.screen.base.EnergizedPowerBaseContainerScreen;
import me.jddev0.ep.util.XPUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;

public class XPStorageScreen extends EnergizedPowerBaseContainerScreen<XPStorageMenu> {
    private final ResourceLocation TEXTURE;

    public XPStorageScreen(XPStorageMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);

        TEXTURE = EPAPI.id("textures/gui/container/xp_storage.png");
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if(mouseButton == 0) {
            boolean clicked = false;
            if(isHovering(45, 16, 13, 14, mouseX, mouseY)) {
                //+1 button

                ModMessages.sendToServer(new InsertExtractXPFromXPStorageC2SPacket(menu.getBlockEntity().getBlockPos(), 1));
                clicked = true;
            }else if(isHovering(60, 16, 18, 14, mouseX, mouseY)) {
                //+10 button

                ModMessages.sendToServer(new InsertExtractXPFromXPStorageC2SPacket(menu.getBlockEntity().getBlockPos(), 10));
                clicked = true;
            }else if(isHovering(80, 16, 24, 14, mouseX, mouseY)) {
                //+100 button

                ModMessages.sendToServer(new InsertExtractXPFromXPStorageC2SPacket(menu.getBlockEntity().getBlockPos(), 100));
                clicked = true;
            }else if(isHovering(106, 16, 26, 14, mouseX, mouseY)) {
                //+MAX button

                ModMessages.sendToServer(new InsertExtractXPFromXPStorageC2SPacket(menu.getBlockEntity().getBlockPos(), Integer.MAX_VALUE));
                clicked = true;
            }else if(isHovering(45, 56, 13, 14, mouseX, mouseY)) {
                //-1 button

                ModMessages.sendToServer(new InsertExtractXPFromXPStorageC2SPacket(menu.getBlockEntity().getBlockPos(), -1));
                clicked = true;
            }else if(isHovering(60, 56, 18, 14, mouseX, mouseY)) {
                //-10 button

                ModMessages.sendToServer(new InsertExtractXPFromXPStorageC2SPacket(menu.getBlockEntity().getBlockPos(), -10));
                clicked = true;
            }else if(isHovering(80, 56, 24, 14, mouseX, mouseY)) {
                //-100 button

                ModMessages.sendToServer(new InsertExtractXPFromXPStorageC2SPacket(menu.getBlockEntity().getBlockPos(), -100));
                clicked = true;
            }else if(isHovering(106, 56, 26, 14, mouseX, mouseY)) {
                //-MAX button

                //"-Integer.MAX_VALUE": 1 larger than min value, but will be inverted in BlockEntity and would overflow otherwise
                ModMessages.sendToServer(new InsertExtractXPFromXPStorageC2SPacket(menu.getBlockEntity().getBlockPos(), -Integer.MAX_VALUE));
                clicked = true;
            }

            if(clicked)
                minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.f));
        }

        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.f, 1.f, 1.f, 1.f);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight, 256, 256);

        renderXPBar(guiGraphics, x, y);

        renderButtons(guiGraphics, x, y, mouseX, mouseY);
    }

    private void renderXPBar(GuiGraphics guiGraphics, int x, int y) {
        int xpLevel = XPUtils.getLevelFromTotalXP(menu.getXPAmount());
        int maxXPLevel = XPUtils.getLevelFromTotalXP(menu.getXPCapacity());

        Component component = Component.translatable("tooltip.energizedpower.xp_storage.amount", xpLevel, maxXPLevel).withColor(0x487127);

        int componentWidth = font.width(component);

        guiGraphics.drawString(font, component, (int)(x + (176 - componentWidth) * .5f), y + 36, 0xFF000000, false);

        int xpBarProgress = (int)Math.max(0, (double)(menu.getXPAmount() - XPUtils.getTotalXPFromLevel(xpLevel)) / XPUtils.getXpNeededForNextLevel(xpLevel) * 162);
        if(xpBarProgress > 0)
            guiGraphics.blit(MACHINE_SPRITES_TEXTURE, x + 7, y + 49, 0, 251, xpBarProgress, 5, 256, 256);
    }

    private void renderButtons(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        for(int i = 0;i < 2;i++) {
            int yPosDelta = i * 40;
            int vPosDelta = i * 28;

            if(isHovering(45, 16 + yPosDelta, 13, 14, mouseX, mouseY)) {
                //+/-1 buttons

                guiGraphics.blit(MACHINE_SPRITES_TEXTURE, x + 45, y + 16 + yPosDelta, 92, 193 + vPosDelta, 13, 14, 256, 256);
            }else if(isHovering(60, 16 + yPosDelta, 18, 14, mouseX, mouseY)) {
                //+/-10 buttons

                guiGraphics.blit(MACHINE_SPRITES_TEXTURE, x + 60, y + 16 + yPosDelta, 105, 193 + vPosDelta, 18, 14, 256, 256);
            }else if(isHovering(80, 16 + yPosDelta, 24, 14, mouseX, mouseY)) {
                //+/-100 buttons

                guiGraphics.blit(MACHINE_SPRITES_TEXTURE, x + 80, y + 16 + yPosDelta, 123, 193 + vPosDelta, 24, 14, 256, 256);
            }else if(isHovering(106, 16 + yPosDelta, 26, 14, mouseX, mouseY)) {
                //+/-MAX buttons

                guiGraphics.blit(MACHINE_SPRITES_TEXTURE, x + 106, y + 16 + yPosDelta, 147, 193 + vPosDelta, 26, 14, 256, 256);
            }
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        super.render(guiGraphics, mouseX, mouseY, delta);

        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
