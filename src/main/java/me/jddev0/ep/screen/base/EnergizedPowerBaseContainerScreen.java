package me.jddev0.ep.screen.base;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.client.rendering.FluidTankRenderState;
import me.jddev0.ep.fluid.FluidStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.TextureSetup;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Matrix3x2f;

@Environment(EnvType.CLIENT)
public abstract class EnergizedPowerBaseContainerScreen<T extends ScreenHandler> extends HandledScreen<T> {
    protected final Identifier MACHINE_SPRITES_TEXTURE = EPAPI.id("textures/gui/container/sprites/machine_sprites.png");

    public EnergizedPowerBaseContainerScreen(T menu, PlayerInventory inventory, Text titleComponent) {
        super(menu, inventory, titleComponent);
    }

    protected void renderFluidMeterContent(DrawContext drawContext, FluidStack fluidStack, long tankCapacity, int x, int y,
                                           int w, int h) {
        drawContext.getMatrices().pushMatrix();

        drawContext.getMatrices().translate(x, y);

        renderFluidStack(drawContext, fluidStack, tankCapacity, w, h);

        drawContext.getMatrices().popMatrix();
    }

    private void renderFluidStack(DrawContext drawContext, FluidStack fluidStack, long tankCapacity, int w, int h) {
        if(fluidStack.isEmpty())
            return;

        Fluid fluid = fluidStack.getFluid();
        Sprite stillFluidSprite = FluidVariantRendering.getSprite(fluidStack.getFluidVariant());

        int fluidColorTint = FluidVariantRendering.getColor(fluidStack.getFluidVariant());

        int fluidMeterPos = tankCapacity == -1 || (fluidStack.getDropletsAmount() > 0 && fluidStack.getDropletsAmount() == tankCapacity)?
                0:(int)(h - ((fluidStack.getDropletsAmount() <= 0 || tankCapacity == 0)?0:
                (Math.min(fluidStack.getDropletsAmount(), tankCapacity - 1) * h / tankCapacity + 1)));

        for(int yOffset = h;yOffset > fluidMeterPos;yOffset -= 16) {
            for(int xOffset = 0;xOffset < w;xOffset += 16) {
                int width = Math.min(w - xOffset, 16);
                int height = Math.min(yOffset - fluidMeterPos, 16);

                float u0 = stillFluidSprite.getMinU();
                float u1 = stillFluidSprite.getMaxU();
                float v0 = stillFluidSprite.getMinV();
                float v1 = stillFluidSprite.getMaxV();
                u1 = u1 - ((16 - width) / 16.f * (u1 - u0));
                v0 = v0 - ((16 - height) / 16.f * (v0 - v1));

                AbstractTexture abstractTexture = this.client.getTextureManager().getTexture(stillFluidSprite.getAtlasId());
                drawContext.state.addSimpleElement(new FluidTankRenderState(
                        RenderPipelines.GUI_TEXTURED, TextureSetup.of(abstractTexture.getGlTextureView(), abstractTexture.getSampler()),
                        new Matrix3x2f(drawContext.getMatrices()),
                        xOffset, yOffset, width, height,
                        u0, u1, v0, v1, fluidColorTint,
                        drawContext.scissorStack.peekLast()
                ));
            }
        }
    }
}
