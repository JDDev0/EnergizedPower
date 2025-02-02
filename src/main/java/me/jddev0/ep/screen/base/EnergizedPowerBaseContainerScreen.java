package me.jddev0.ep.screen.base;

import com.mojang.blaze3d.systems.RenderSystem;
import me.jddev0.ep.fluid.FluidStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public abstract class EnergizedPowerBaseContainerScreen<T extends ScreenHandler> extends HandledScreen<T> {
    public EnergizedPowerBaseContainerScreen(T menu, PlayerInventory inventory, Text titleComponent) {
        super(menu, inventory, titleComponent);
    }

    protected void renderFluidMeterContent(DrawContext drawContext, FluidStack fluidStack, long tankCapacity, int x, int y,
                                           int w, int h) {
        RenderSystem.enableBlend();
        drawContext.getMatrices().push();

        drawContext.getMatrices().translate(x, y, 0);

        renderFluidStack(drawContext, fluidStack, tankCapacity, w, h);

        drawContext.getMatrices().pop();
        RenderSystem.setShaderColor(1.f, 1.f, 1.f, 1.f);
        RenderSystem.disableBlend();
    }

    private void renderFluidStack(DrawContext drawContext, FluidStack fluidStack, long tankCapacity, int w, int h) {
        if(fluidStack.isEmpty())
            return;

        Fluid fluid = fluidStack.getFluid();
        Sprite stillFluidSprite = FluidVariantRendering.getSprite(fluidStack.getFluidVariant());
        if(stillFluidSprite == null)
            stillFluidSprite = MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).
                    apply(MissingSprite.getMissingSpriteId());

        int fluidColorTint = FluidVariantRendering.getColor(fluidStack.getFluidVariant());

        int fluidMeterPos = tankCapacity == -1 || (fluidStack.getDropletsAmount() > 0 && fluidStack.getDropletsAmount() == tankCapacity)?
                0:(int)(h - ((fluidStack.getDropletsAmount() <= 0 || tankCapacity == 0)?0:
                (Math.min(fluidStack.getDropletsAmount(), tankCapacity - 1) * h / tankCapacity + 1)));

        RenderSystem.setShaderTexture(0, SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);

        RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX);

        Matrix4f mat = drawContext.getMatrices().peek().getPositionMatrix();

        for(int yOffset = h;yOffset > fluidMeterPos;yOffset -= 16) {
            for(int xOffset = 0;xOffset < w;xOffset += 16) {
                Sprite finalStillFluidSprite = stillFluidSprite;
                int finalXOffset = xOffset;
                int finalYOffset = yOffset;

                drawContext.draw(vertexConsumers -> {
                    int width = Math.min(w - finalXOffset, 16);
                    int height = Math.min(finalYOffset - fluidMeterPos, 16);

                    float u0 = finalStillFluidSprite.getMinU();
                    float u1 = finalStillFluidSprite.getMaxU();
                    float v0 = finalStillFluidSprite.getMinV();
                    float v1 = finalStillFluidSprite.getMaxV();
                    u1 = u1 - ((16 - width) / 16.f * (u1 - u0));
                    v0 = v0 - ((16 - height) / 16.f * (v0 - v1));

                    VertexConsumer bufferBuilder = vertexConsumers.getBuffer(RenderLayer.getGuiTextured(finalStillFluidSprite.getAtlasId()));
                    bufferBuilder.vertex(mat, finalXOffset, finalYOffset, 0).color(fluidColorTint).texture(u0, v1);
                    bufferBuilder.vertex(mat, finalXOffset + width, finalYOffset, 0).color(fluidColorTint).texture(u1, v1);
                    bufferBuilder.vertex(mat, finalXOffset + width, finalYOffset - height, 0).color(fluidColorTint).texture(u1, v0);
                    bufferBuilder.vertex(mat, finalXOffset, finalYOffset - height, 0).color(fluidColorTint).texture(u0, v0);
                });
            }
        }
    }
}
