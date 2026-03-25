package me.jddev0.ep.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
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
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

@Environment(EnvType.CLIENT)
public class FluidTankBlockEntityRenderer<F extends Storage<FluidVariant>>
        implements BlockEntityRenderer<AbstractFluidTankBlockEntity<F>, FluidTankBlockEntityRenderState> {
    private final BlockEntityRendererProvider.Context context;

    public FluidTankBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }

    @Override
    public FluidTankBlockEntityRenderState createRenderState() {
        return new FluidTankBlockEntityRenderState();
    }

    @Override
    public void extractRenderState(AbstractFluidTankBlockEntity<F> blockEntity, FluidTankBlockEntityRenderState state, float tickProgress, Vec3 cameraPos, @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, tickProgress, cameraPos, crumblingOverlay);

        state.facing = blockEntity.getBlockState().getValue(FluidTankBlock.FACING);
        state.tankCapacity = blockEntity.getTankCapacity(0);
        state.fluidStack = blockEntity.getFluid(0);
    }

    @Override
    public void submit(FluidTankBlockEntityRenderState state, PoseStack poseStack, SubmitNodeCollector queue, CameraRenderState cameraRenderState) {
        queue.submitCustomGeometry(poseStack, RenderTypes.translucentMovingBlock(), (matrixEntry, vertexConsumer) -> {
            long capacity = state.tankCapacity;
            FluidStack fluidStack = state.fluidStack;

            if(fluidStack.isEmpty())
                return;

            float height = ((fluidStack.getDropletsAmount() <= 0 || capacity == 0)?0:
                    (Math.min(fluidStack.getDropletsAmount(), capacity - 1) * 14.f / capacity));

            Direction facing = state.facing;

            matrixEntry.rotate(facing.getRotation());
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
            TextureAtlasSprite stillFluidSprite = FluidVariantRendering.getSprite(fluidStack.getFluidVariant());

            int fluidColorTint = FluidVariantRendering.getColor(fluidStack.getFluidVariant());

            Matrix4f mat = matrixEntry.pose();

            float u0 = stillFluidSprite.getU0();
            float u1 = stillFluidSprite.getU1();
            float v0 = stillFluidSprite.getV0();
            float v1 = stillFluidSprite.getV1();

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

                vertexConsumer.addVertex(mat, .0625f, height * .0625f, 0.f)
                        .setColor(fluidColorTint)
                        .setUv(u0, v1)
                        .setOverlay(OverlayTexture.NO_OVERLAY)
                        .setLight(LightTexture.FULL_BRIGHT)
                        .setNormal(matrixEntry, 0.f, 0.f, 0.f);

                vertexConsumer.addVertex(mat, .9375f, height * .0625f, 0.f)
                        .setColor(fluidColorTint)
                        .setUv(u1, v1)
                        .setOverlay(OverlayTexture.NO_OVERLAY)
                        .setLight(LightTexture.FULL_BRIGHT)
                        .setNormal(matrixEntry, 0.f, 0.f, 0.f);

                vertexConsumer.addVertex(mat, .9375f, 0.f, 0.f)
                        .setColor(fluidColorTint)
                        .setUv(u1, v0)
                        .setOverlay(OverlayTexture.NO_OVERLAY)
                        .setLight(LightTexture.FULL_BRIGHT)
                        .setNormal(matrixEntry, 0.f, 0.f, 0.f);

                vertexConsumer.addVertex(mat, .0625f, 0.f, 0.f)
                        .setColor(fluidColorTint)
                        .setUv(u0, v0)
                        .setOverlay(OverlayTexture.NO_OVERLAY)
                        .setLight(LightTexture.FULL_BRIGHT)
                        .setNormal(matrixEntry, 0.f, 0.f, 0.f);
            }

            //Indicator bar
            {
                TextureAtlasSprite indicatorBarSprite = Minecraft.getInstance().getAtlasManager().get(new Material(
                        TextureAtlas.LOCATION_BLOCKS, EPAPI.id("block/fluid_tank_indicator_bar")));

                float translateForMinMaxIndicatorBarHeight = height < 2?(height - 2) / 16.f:(height > 12?(height - 12) / 16.f:0.f);

                matrixEntry.translate(0.f, translateForMinMaxIndicatorBarHeight, 0.f);

                float ibu0 = indicatorBarSprite.getU0();
                float ibu1 = indicatorBarSprite.getU1();
                float ibv0 = indicatorBarSprite.getV0();
                float ibv1 = indicatorBarSprite.getV1();

                ibu1 = ibu0 + .0625f * (ibu1 - ibu0);
                ibv1 = ibv0 + .0625f * (ibv1 - ibv0);

                vertexConsumer.addVertex(mat, .375f, .015f, -.05f)
                        .setColor(255, 255, 255, 255)
                        .setUv(ibu0, ibv1)
                        .setOverlay(OverlayTexture.NO_OVERLAY)
                        .setLight(LightTexture.FULL_BRIGHT)
                        .setNormal(matrixEntry, 0.f, 0.f, 0.f);

                vertexConsumer.addVertex(mat, .625f, .015f, -.05f)
                        .setColor(255, 255, 255, 255)
                        .setUv(ibu1, ibv1)
                        .setOverlay(OverlayTexture.NO_OVERLAY)
                        .setLight(LightTexture.FULL_BRIGHT)
                        .setNormal(matrixEntry, 0.f, 0.f, 0.f);

                vertexConsumer.addVertex(mat, .625f, -.015f, -.05f)
                        .setColor(255, 255, 255, 255)
                        .setUv(ibu1, ibv0)
                        .setOverlay(OverlayTexture.NO_OVERLAY)
                        .setLight(LightTexture.FULL_BRIGHT)
                        .setNormal(matrixEntry, 0.f, 0.f, 0.f);

                vertexConsumer.addVertex(mat, .375f, -.015f, -.05f)
                        .setColor(255, 255, 255, 255)
                        .setUv(ibu0, ibv0)
                        .setOverlay(OverlayTexture.NO_OVERLAY)
                        .setLight(LightTexture.FULL_BRIGHT)
                        .setNormal(matrixEntry, 0.f, 0.f, 0.f);

                matrixEntry.translate(0.f, -translateForMinMaxIndicatorBarHeight, 0.f);
            }

            v1 = v1Orig;

            matrixEntry.translate(0.f, 1.f - (16 - height - 1) / 16.f, 0.f);
            matrixEntry.rotate(new Quaternionf().rotationX((float)Math.PI * -.5f));
            matrixEntry.translate(0.f, -.875f, -1 + (16 - height - 1) / 16.f);

            //Top
            {
                vertexConsumer.addVertex(mat, .0625f, .875f, 0.f)
                        .setColor(fluidColorTint)
                        .setUv(u0, v1)
                        .setOverlay(OverlayTexture.NO_OVERLAY)
                        .setLight(LightTexture.FULL_BRIGHT)
                        .setNormal(matrixEntry, 0.f, 0.f, 0.f);

                vertexConsumer.addVertex(mat, .9375f, .875f, 0.f)
                        .setColor(fluidColorTint)
                        .setUv(u1, v1)
                        .setOverlay(OverlayTexture.NO_OVERLAY)
                        .setLight(LightTexture.FULL_BRIGHT)
                        .setNormal(matrixEntry, 0.f, 0.f, 0.f);

                vertexConsumer.addVertex(mat, .9375f, 0.f, 0.f)
                        .setColor(fluidColorTint)
                        .setUv(u1, v0)
                        .setOverlay(OverlayTexture.NO_OVERLAY)
                        .setLight(LightTexture.FULL_BRIGHT)
                        .setNormal(matrixEntry, 0.f, 0.f, 0.f);

                vertexConsumer.addVertex(mat, .0625f, 0.f, 0.f)
                        .setColor(fluidColorTint)
                        .setUv(u0, v0)
                        .setOverlay(OverlayTexture.NO_OVERLAY)
                        .setLight(LightTexture.FULL_BRIGHT)
                        .setNormal(matrixEntry, 0.f, 0.f, 0.f);
            }
        });
    }
}
