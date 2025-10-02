package me.jddev0.ep.client.rendering;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.SimpleGuiElementRenderState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.TextureSetup;
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
        @Nullable ScreenRect scissorArea,
        @Nullable ScreenRect bounds
) implements SimpleGuiElementRenderState {
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
            @Nullable ScreenRect scissorArea
    ) {
        this(pipeline, textureSetup, pose, x, y, width, height, u0, u1, v0, v1, fluidColorTint, scissorArea, createBounds(x, y, width, height, pose, scissorArea));
    }

    @Override
    public void setupVertices(VertexConsumer bufferBuilder) {
        bufferBuilder.vertex(pose, x, y).color(fluidColorTint).texture(u0, v1);
        bufferBuilder.vertex(pose, x + width, y).color(fluidColorTint).texture(u1, v1);
        bufferBuilder.vertex(pose, x + width, y - height).color(fluidColorTint).texture(u1, v0);
        bufferBuilder.vertex(pose, x, y - height).color(fluidColorTint).texture(u0, v0);
    }

    @Nullable
    private static ScreenRect createBounds(int x, int y, int width, int height, Matrix3x2f pose, @Nullable ScreenRect scissorArea) {
        //Fixes rendering order for fluid tank overlay
        ScreenRect screenRect = new ScreenRect(x - 1, y - 1, width - 2, height - 2).transformEachVertex(pose);
        return scissorArea != null?scissorArea.intersection(screenRect):screenRect;
    }
}
