package me.jddev0.ep.screen.base;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.client.rendering.FluidTankRenderState;
import me.jddev0.ep.fluid.FluidStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.material.Fluid;
import org.joml.Matrix3x2f;

@Environment(EnvType.CLIENT)
public abstract class EnergizedPowerBaseContainerScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {
    protected final Identifier MACHINE_SPRITES_TEXTURE = EPAPI.id("textures/gui/container/sprites/machine_sprites.png");

    public EnergizedPowerBaseContainerScreen(T menu, Inventory inventory, Component titleComponent) {
        super(menu, inventory, titleComponent);
    }

    protected void renderFluidMeterContent(GuiGraphics drawContext, FluidStack fluidStack, long tankCapacity, int x, int y,
                                           int w, int h) {
        drawContext.pose().pushMatrix();

        drawContext.pose().translate(x, y);

        renderFluidStack(drawContext, fluidStack, tankCapacity, w, h);

        drawContext.pose().popMatrix();
    }

    private void renderFluidStack(GuiGraphics drawContext, FluidStack fluidStack, long tankCapacity, int w, int h) {
        if(fluidStack.isEmpty())
            return;

        Fluid fluid = fluidStack.getFluid();
        TextureAtlasSprite stillFluidSprite = FluidVariantRendering.getSprite(fluidStack.getFluidVariant());

        int fluidColorTint = FluidVariantRendering.getColor(fluidStack.getFluidVariant());

        int fluidMeterPos = tankCapacity == -1 || (fluidStack.getDropletsAmount() > 0 && fluidStack.getDropletsAmount() == tankCapacity)?
                0:(int)(h - ((fluidStack.getDropletsAmount() <= 0 || tankCapacity == 0)?0:
                (Math.min(fluidStack.getDropletsAmount(), tankCapacity - 1) * h / tankCapacity + 1)));

        for(int yOffset = h;yOffset > fluidMeterPos;yOffset -= 16) {
            for(int xOffset = 0;xOffset < w;xOffset += 16) {
                int width = Math.min(w - xOffset, 16);
                int height = Math.min(yOffset - fluidMeterPos, 16);

                float u0 = stillFluidSprite.getU0();
                float u1 = stillFluidSprite.getU1();
                float v0 = stillFluidSprite.getV0();
                float v1 = stillFluidSprite.getV1();
                u1 = u1 - ((16 - width) / 16.f * (u1 - u0));
                v0 = v0 - ((16 - height) / 16.f * (v0 - v1));

                AbstractTexture abstractTexture = this.minecraft.getTextureManager().getTexture(stillFluidSprite.atlasLocation());
                drawContext.guiRenderState.submitGuiElement(new FluidTankRenderState(
                        RenderPipelines.GUI_TEXTURED, TextureSetup.singleTexture(abstractTexture.getTextureView(), abstractTexture.getSampler()),
                        new Matrix3x2f(drawContext.pose()),
                        xOffset, yOffset, width, height,
                        u0, u1, v0, v1, fluidColorTint,
                        drawContext.scissorStack.peek()
                ));
            }
        }
    }
}
