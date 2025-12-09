package me.jddev0.ep.block.entity.renderer;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.FluidTankBlock;
import me.jddev0.ep.block.entity.AbstractFluidTankBlockEntity;
import me.jddev0.ep.block.entity.renderer.state.FluidTankBlockEntityRenderState;
import me.jddev0.ep.fluid.FluidStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.command.ModelCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

@Environment(EnvType.CLIENT)
public class FluidTankBlockEntityRenderer<F extends Storage<FluidVariant>>
        implements BlockEntityRenderer<AbstractFluidTankBlockEntity<F>, FluidTankBlockEntityRenderState> {
    private final BlockEntityRendererFactory.Context context;

    public FluidTankBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.context = context;
    }

    @Override
    public FluidTankBlockEntityRenderState createRenderState() {
        return new FluidTankBlockEntityRenderState();
    }

    @Override
    public void updateRenderState(AbstractFluidTankBlockEntity<F> blockEntity, FluidTankBlockEntityRenderState state, float tickProgress, Vec3d cameraPos, @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlay) {
        BlockEntityRenderer.super.updateRenderState(blockEntity, state, tickProgress, cameraPos, crumblingOverlay);

        state.facing = blockEntity.getCachedState().get(FluidTankBlock.FACING);
        state.tankCapacity = blockEntity.getTankCapacity(0);
        state.fluidStack = blockEntity.getFluid(0);
    }

    @Override
    public void render(FluidTankBlockEntityRenderState state, MatrixStack poseStack, OrderedRenderCommandQueue queue, CameraRenderState cameraRenderState) {
        queue.submitCustom(poseStack, RenderLayers.translucentMovingBlock(), (matrixEntry, vertexConsumer) -> {
            long capacity = state.tankCapacity;
            FluidStack fluidStack = state.fluidStack;

            if(fluidStack.isEmpty())
                return;

            float height = ((fluidStack.getDropletsAmount() <= 0 || capacity == 0)?0:
                    (Math.min(fluidStack.getDropletsAmount(), capacity - 1) * 14.f / capacity));

            Direction facing = state.facing;

            matrixEntry.rotate(facing.getRotationQuaternion());
            matrixEntry.rotate(new Quaternionf().rotationX((float)Math.PI * .5f));

            switch(facing) {
                case NORTH:
                    matrixEntry.translate(-1.f, -1.f + (16 - height - 1) / 16.f, .0625f);
                    break;
                case SOUTH:
                    matrixEntry.translate(0.f, -1.f + (16 - height - 1) / 16.f, -.9375f);
                    break;
                case EAST:
                    matrixEntry.translate(-1.f, -1.f + (16 - height - 1) / 16.f, -.9375f);
                    break;
                case WEST:
                    matrixEntry.translate(0.f, -1.f + (16 - height - 1) / 16.f, .0625f);
                    break;

                case UP:
                case DOWN:
                    break;
            }

            Fluid fluid = fluidStack.getFluid();
            Sprite stillFluidSprite = FluidVariantRendering.getSprite(fluidStack.getFluidVariant());

            int fluidColorTint = FluidVariantRendering.getColor(fluidStack.getFluidVariant());

            Matrix4f mat = matrixEntry.getPositionMatrix();

            float u0 = stillFluidSprite.getMinU();
            float u1 = stillFluidSprite.getMaxU();
            float v0 = stillFluidSprite.getMinV();
            float v1 = stillFluidSprite.getMaxV();

            float du = u1 - u0;
            float dv = v1 - v0;

            u0 = u0 + .0625f * du;
            u1 = u1 - .0625f * du;

            v0 = v0 + .0625f * dv;
            v1 = v1 - .0625f * dv;

            float v1Orig = v1;

            //Side
            {
                v1 = v1 - ((14 - height) / 16.f * dv);

                vertexConsumer.vertex(mat, .0625f, height * .0625f, 0.f)
                        .color(fluidColorTint)
                        .texture(u0, v1)
                        .overlay(OverlayTexture.DEFAULT_UV)
                        .light(LightmapTextureManager.MAX_LIGHT_COORDINATE)
                        .normal(matrixEntry, 0.f, 0.f, 0.f);

                vertexConsumer.vertex(mat, .9375f, height * .0625f, 0.f)
                        .color(fluidColorTint)
                        .texture(u1, v1)
                        .overlay(OverlayTexture.DEFAULT_UV)
                        .light(LightmapTextureManager.MAX_LIGHT_COORDINATE)
                        .normal(matrixEntry, 0.f, 0.f, 0.f);

                vertexConsumer.vertex(mat, .9375f, 0.f, 0.f)
                        .color(fluidColorTint)
                        .texture(u1, v0)
                        .overlay(OverlayTexture.DEFAULT_UV)
                        .light(LightmapTextureManager.MAX_LIGHT_COORDINATE)
                        .normal(matrixEntry, 0.f, 0.f, 0.f);

                vertexConsumer.vertex(mat, .0625f, 0.f, 0.f)
                        .color(fluidColorTint)
                        .texture(u0, v0)
                        .overlay(OverlayTexture.DEFAULT_UV)
                        .light(LightmapTextureManager.MAX_LIGHT_COORDINATE)
                        .normal(matrixEntry, 0.f, 0.f, 0.f);
            }

            //Indicator bar
            {
                Sprite indicatorBarSprite = MinecraftClient.getInstance().getAtlasManager().getSprite(new SpriteIdentifier(
                        SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, EPAPI.id("block/fluid_tank_indicator_bar")));

                float translateForMinMaxIndicatorBarHeight = height < 2?(height - 2) / 16.f:(height > 12?(height - 12) / 16.f:0.f);

                matrixEntry.translate(0.f, translateForMinMaxIndicatorBarHeight, 0.f);

                float ibu0 = indicatorBarSprite.getMinU();
                float ibu1 = indicatorBarSprite.getMaxU();
                float ibv0 = indicatorBarSprite.getMinV();
                float ibv1 = indicatorBarSprite.getMaxV();

                ibu1 = ibu0 + .0625f * (ibu1 - ibu0);
                ibv1 = ibv0 + .0625f * (ibv1 - ibv0);

                vertexConsumer.vertex(mat, .375f, .015f, -.05f)
                        .color(255, 255, 255, 255)
                        .texture(ibu0, ibv1)
                        .overlay(OverlayTexture.DEFAULT_UV)
                        .light(LightmapTextureManager.MAX_LIGHT_COORDINATE)
                        .normal(matrixEntry, 0.f, 0.f, 0.f);

                vertexConsumer.vertex(mat, .625f, .015f, -.05f)
                        .color(255, 255, 255, 255)
                        .texture(ibu1, ibv1)
                        .overlay(OverlayTexture.DEFAULT_UV)
                        .light(LightmapTextureManager.MAX_LIGHT_COORDINATE)
                        .normal(matrixEntry, 0.f, 0.f, 0.f);

                vertexConsumer.vertex(mat, .625f, -.015f, -.05f)
                        .color(255, 255, 255, 255)
                        .texture(ibu1, ibv0)
                        .overlay(OverlayTexture.DEFAULT_UV)
                        .light(LightmapTextureManager.MAX_LIGHT_COORDINATE)
                        .normal(matrixEntry, 0.f, 0.f, 0.f);

                vertexConsumer.vertex(mat, .375f, -.015f, -.05f)
                        .color(255, 255, 255, 255)
                        .texture(ibu0, ibv0)
                        .overlay(OverlayTexture.DEFAULT_UV)
                        .light(LightmapTextureManager.MAX_LIGHT_COORDINATE)
                        .normal(matrixEntry, 0.f, 0.f, 0.f);

                matrixEntry.translate(0.f, -translateForMinMaxIndicatorBarHeight, 0.f);
            }

            v1 = v1Orig;

            matrixEntry.translate(0.f, 1.f - (16 - height - 1) / 16.f, 0.f);
            matrixEntry.rotate(new Quaternionf().rotationX((float)Math.PI * -.5f));
            matrixEntry.translate(0.f, -.875f, -1 + (16 - height - 1) / 16.f);

            //Top
            {
                vertexConsumer.vertex(mat, .0625f, .875f, 0.f)
                        .color(fluidColorTint)
                        .texture(u0, v1)
                        .overlay(OverlayTexture.DEFAULT_UV)
                        .light(LightmapTextureManager.MAX_LIGHT_COORDINATE)
                        .normal(matrixEntry, 0.f, 0.f, 0.f);

                vertexConsumer.vertex(mat, .9375f, .875f, 0.f)
                        .color(fluidColorTint)
                        .texture(u1, v1)
                        .overlay(OverlayTexture.DEFAULT_UV)
                        .light(LightmapTextureManager.MAX_LIGHT_COORDINATE)
                        .normal(matrixEntry, 0.f, 0.f, 0.f);

                vertexConsumer.vertex(mat, .9375f, 0.f, 0.f)
                        .color(fluidColorTint)
                        .texture(u1, v0)
                        .overlay(OverlayTexture.DEFAULT_UV)
                        .light(LightmapTextureManager.MAX_LIGHT_COORDINATE)
                        .normal(matrixEntry, 0.f, 0.f, 0.f);

                vertexConsumer.vertex(mat, .0625f, 0.f, 0.f)
                        .color(fluidColorTint)
                        .texture(u0, v0)
                        .overlay(OverlayTexture.DEFAULT_UV)
                        .light(LightmapTextureManager.MAX_LIGHT_COORDINATE)
                        .normal(matrixEntry, 0.f, 0.f, 0.f);
            }
        });
    }
}
