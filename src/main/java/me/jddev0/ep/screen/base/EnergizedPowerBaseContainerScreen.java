package me.jddev0.ep.screen.base;

import com.mojang.blaze3d.textures.GpuTextureView;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.client.rendering.FluidTankRenderState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import org.joml.Matrix3x2f;

public abstract class EnergizedPowerBaseContainerScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {
    protected final ResourceLocation MACHINE_SPRITES_TEXTURE = EPAPI.id("textures/gui/container/sprites/machine_sprites.png");

    public EnergizedPowerBaseContainerScreen(T menu, Inventory inventory, Component titleComponent) {
        super(menu, inventory, titleComponent);
    }

    protected void renderFluidMeterContent(GuiGraphics guiGraphics, FluidStack fluidStack, int tankCapacity, int x, int y,
                                         int w, int h) {
        guiGraphics.pose().pushMatrix();

        guiGraphics.pose().translate(x, y);

        renderFluidStack(guiGraphics, fluidStack, tankCapacity, w, h);

        guiGraphics.pose().popMatrix();
    }

    private void renderFluidStack(GuiGraphics guiGraphics, FluidStack fluidStack, int tankCapacity, int w, int h) {
        if(fluidStack.isEmpty())
            return;

        Fluid fluid = fluidStack.getFluid();
        IClientFluidTypeExtensions fluidTypeExtensions = IClientFluidTypeExtensions.of(fluid);
        ResourceLocation stillFluidImageId = fluidTypeExtensions.getStillTexture(fluidStack);
        TextureAtlasSprite stillFluidSprite = Minecraft.getInstance().getAtlasManager().get(new Material(
                TextureAtlas.LOCATION_BLOCKS, stillFluidImageId));

        int fluidColorTint = fluidTypeExtensions.getTintColor(fluidStack);

        int fluidMeterPos = tankCapacity == -1 || (fluidStack.getAmount() > 0 && fluidStack.getAmount() == tankCapacity)?
                0:(h - ((fluidStack.getAmount() <= 0 || tankCapacity == 0)?0:
                (Math.min(fluidStack.getAmount(), tankCapacity - 1) * h / tankCapacity + 1)));

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

                GpuTextureView gpuTextureView = this.minecraft.getTextureManager().getTexture(stillFluidSprite.atlasLocation()).getTextureView();
                guiGraphics.guiRenderState.submitGuiElement(new FluidTankRenderState(
                        RenderPipelines.GUI_TEXTURED, TextureSetup.singleTexture(gpuTextureView),
                        new Matrix3x2f(guiGraphics.pose()),
                        xOffset, yOffset, width, height,
                        u0, u1, v0, v1, fluidColorTint,
                        guiGraphics.scissorStack.peek()
                ));
            }
        }
    }
}
