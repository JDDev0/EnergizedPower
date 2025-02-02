package me.jddev0.ep.screen.base;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.CoreShaders;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public abstract class EnergizedPowerBaseContainerScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {
    public EnergizedPowerBaseContainerScreen(T menu, Inventory inventory, Component titleComponent) {
        super(menu, inventory, titleComponent);
    }

    protected void renderFluidMeterContent(GuiGraphics guiGraphics, FluidStack fluidStack, int tankCapacity, int x, int y,
                                         int w, int h) {
        RenderSystem.enableBlend();
        guiGraphics.pose().pushPose();

        guiGraphics.pose().translate(x, y, 0);

        renderFluidStack(guiGraphics, fluidStack, tankCapacity, w, h);

        guiGraphics.pose().popPose();
        RenderSystem.setShaderColor(1.f, 1.f, 1.f, 1.f);
        RenderSystem.disableBlend();
    }

    private void renderFluidStack(GuiGraphics guiGraphics, FluidStack fluidStack, int tankCapacity, int w, int h) {
        if(fluidStack.isEmpty())
            return;

        Fluid fluid = fluidStack.getFluid();
        IClientFluidTypeExtensions fluidTypeExtensions = IClientFluidTypeExtensions.of(fluid);
        ResourceLocation stillFluidImageId = fluidTypeExtensions.getStillTexture(fluidStack);
        if(stillFluidImageId == null)
            stillFluidImageId = ResourceLocation.withDefaultNamespace("air");
        TextureAtlasSprite stillFluidSprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).
                apply(stillFluidImageId);

        int fluidColorTint = fluidTypeExtensions.getTintColor(fluidStack);

        int fluidMeterPos = tankCapacity == -1 || (fluidStack.getAmount() > 0 && fluidStack.getAmount() == tankCapacity)?
                0:(h - ((fluidStack.getAmount() <= 0 || tankCapacity == 0)?0:
                (Math.min(fluidStack.getAmount(), tankCapacity - 1) * h / tankCapacity + 1)));

        RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);

        RenderSystem.setShader(CoreShaders.POSITION_TEX);

        Matrix4f mat = guiGraphics.pose().last().pose();

        for(int yOffset = h;yOffset > fluidMeterPos;yOffset -= 16) {
            for(int xOffset = 0;xOffset < w;xOffset += 16) {
                int finalXOffset = xOffset;
                int finalYOffset = yOffset;

                guiGraphics.drawSpecial(vertexConsumers -> {
                    int width = Math.min(w - finalXOffset, 16);
                    int height = Math.min(finalYOffset - fluidMeterPos, 16);

                    float u0 = stillFluidSprite.getU0();
                    float u1 = stillFluidSprite.getU1();
                    float v0 = stillFluidSprite.getV0();
                    float v1 = stillFluidSprite.getV1();
                    u1 = u1 - ((16 - width) / 16.f * (u1 - u0));
                    v0 = v0 - ((16 - height) / 16.f * (v0 - v1));

                    VertexConsumer bufferBuilder = vertexConsumers.getBuffer(RenderType.guiTextured(stillFluidSprite.atlasLocation()));
                    bufferBuilder.addVertex(mat, finalXOffset, finalYOffset, 0).setColor(fluidColorTint).setUv(u0, v1);
                    bufferBuilder.addVertex(mat, finalXOffset + width, finalYOffset, 0).setColor(fluidColorTint).setUv(u1, v1);
                    bufferBuilder.addVertex(mat, finalXOffset + width, finalYOffset - height, 0).setColor(fluidColorTint).setUv(u1, v0);
                    bufferBuilder.addVertex(mat, finalXOffset, finalYOffset - height, 0).setColor(fluidColorTint).setUv(u0, v0);
                });
            }
        }
    }
}
