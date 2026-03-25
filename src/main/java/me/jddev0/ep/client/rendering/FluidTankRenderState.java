package me.jddev0.ep.client.rendering;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

public record FluidTankRenderState(
        RenderPipeline pipeline,
        TextureSetup textureSetup,
        Matrix3x2f pose,
        int x,
        int y,
        int width,
        int height,
        float u0,
        float u1,
        float v0,
        float v1,
        int fluidColorTint,
        @Nullable ScreenRectangle scissorArea,
        @Nullable ScreenRectangle bounds
) implements GuiElementRenderState {
    public FluidTankRenderState(
            RenderPipeline pipeline,
            TextureSetup textureSetup,
            Matrix3x2f pose,
            int x,
            int y,
            int width,
            int height,
            float u0,
            float u1,
            float v0,
            float v1,
            int fluidColorTint,
            @Nullable ScreenRectangle scissorArea
    ) {
        this(pipeline, textureSetup, pose, x, y, width, height, u0, u1, v0, v1, fluidColorTint, scissorArea, createBounds(x, y, width, height, pose, scissorArea));
    }

    @Override
    public void buildVertices(VertexConsumer bufferBuilder) {
        bufferBuilder.addVertexWith2DPose(pose, x, y).setColor(fluidColorTint).setUv(u0, v1);
        bufferBuilder.addVertexWith2DPose(pose, x + width, y).setColor(fluidColorTint).setUv(u1, v1);
        bufferBuilder.addVertexWith2DPose(pose, x + width, y - height).setColor(fluidColorTint).setUv(u1, v0);
        bufferBuilder.addVertexWith2DPose(pose, x, y - height).setColor(fluidColorTint).setUv(u0, v0);
    }

    @Nullable
    private static ScreenRectangle createBounds(int x, int y, int width, int height, Matrix3x2f pose, @Nullable ScreenRectangle scissorArea) {
        //Fixes rendering order for fluid tank overlay
        ScreenRectangle screenRect = new ScreenRectangle(x - 1, y - 1, width - 2, height - 2).transformMaxBounds(pose);
        return scissorArea != null?scissorArea.intersection(screenRect):screenRect;
    }
}
