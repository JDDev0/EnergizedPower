package me.jddev0.ep.block.entity.renderer;

import com.mojang.blaze3d.vertex.*;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.FluidTankBlock;
import me.jddev0.ep.block.entity.AbstractFluidTankBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class FluidTankBlockEntityRenderer implements BlockEntityRenderer<AbstractFluidTankBlockEntity<?>> {
    private final BlockEntityRendererProvider.Context context;

    public FluidTankBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }

    @Override
    public void render(AbstractFluidTankBlockEntity<?> blockEntity, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay, Vec3 cameraPos) {
        int capacity = blockEntity.getTankCapacity(0);
        FluidStack fluidStack = blockEntity.getFluid(0);

        if(fluidStack.isEmpty())
            return;

        float height = ((fluidStack.getAmount() <= 0 || capacity == 0)?0:
                (Math.min(fluidStack.getAmount(), capacity - 1) * 14.f / capacity));

        Direction facing = blockEntity.getBlockState().getValue(FluidTankBlock.FACING);

        poseStack.pushPose();

        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.translucentMovingBlock());

        poseStack.mulPose(facing.getRotation());
        poseStack.mulPose(new Quaternionf().rotationX((float)Math.PI * .5f));

        switch(facing) {
            case NORTH:
                poseStack.translate(-1.f, -1.f + (16 - height - 1) / 16.f, .0625f);
                break;
            case SOUTH:
                poseStack.translate(0.f, -1.f + (16 - height - 1) / 16.f, -.9375f);
                break;
            case EAST:
                poseStack.translate(-1.f, -1.f + (16 - height - 1) / 16.f, -.9375f);
                break;
            case WEST:
                poseStack.translate(0.f, -1.f + (16 - height - 1) / 16.f, .0625f);
                break;

            case UP:
            case DOWN:
                break;
        }

        Fluid fluid = fluidStack.getFluid();
        IClientFluidTypeExtensions fluidTypeExtensions = IClientFluidTypeExtensions.of(fluid);
        ResourceLocation stillFluidImageId = fluidTypeExtensions.getStillTexture(fluidStack);
        if(stillFluidImageId == null)
            stillFluidImageId = ResourceLocation.withDefaultNamespace("air");
        TextureAtlasSprite stillFluidSprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).
                apply(stillFluidImageId);

        int fluidColorTint = fluidTypeExtensions.getTintColor(fluidStack);

        Matrix4f mat = poseStack.last().pose();

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
                    .setOverlay(packedOverlay)
                    .setLight(LightTexture.FULL_BRIGHT)
                    .setNormal(poseStack.last(), 0.f, 0.f, 0.f);

            vertexConsumer.addVertex(mat, .9375f, height * .0625f, 0.f)
                    .setColor(fluidColorTint)
                    .setUv(u1, v1)
                    .setOverlay(packedOverlay)
                    .setLight(LightTexture.FULL_BRIGHT)
                    .setNormal(poseStack.last(), 0.f, 0.f, 0.f);

            vertexConsumer.addVertex(mat, .9375f, 0.f, 0.f)
                    .setColor(fluidColorTint)
                    .setUv(u1, v0)
                    .setOverlay(packedOverlay)
                    .setLight(LightTexture.FULL_BRIGHT)
                    .setNormal(poseStack.last(), 0.f, 0.f, 0.f);

            vertexConsumer.addVertex(mat, .0625f, 0.f, 0.f)
                    .setColor(fluidColorTint)
                    .setUv(u0, v0)
                    .setOverlay(packedOverlay)
                    .setLight(LightTexture.FULL_BRIGHT)
                    .setNormal(poseStack.last(), 0.f, 0.f, 0.f);
        }

        //Indicator bar
        {
            TextureAtlasSprite indicatorBarSprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).
                    apply(EPAPI.id("block/fluid_tank_indicator_bar"));

            float translateForMinMaxIndicatorBarHeight = height < 2?(height - 2) / 16.f:(height > 12?(height - 12) / 16.f:0.f);

            poseStack.translate(0.f, translateForMinMaxIndicatorBarHeight, 0.f);

            float ibu0 = indicatorBarSprite.getU0();
            float ibu1 = indicatorBarSprite.getU1();
            float ibv0 = indicatorBarSprite.getV0();
            float ibv1 = indicatorBarSprite.getV1();

            ibu1 = ibu0 + .0625f * (ibu1 - ibu0);
            ibv1 = ibv0 + .0625f * (ibv1 - ibv0);

            vertexConsumer.addVertex(mat, .375f, .015f, -.05f)
                    .setColor(255, 255, 255, 255)
                    .setUv(ibu0, ibv1)
                    .setOverlay(packedOverlay)
                    .setLight(LightTexture.FULL_BRIGHT)
                    .setNormal(poseStack.last(), 0.f, 0.f, 0.f);

            vertexConsumer.addVertex(mat, .625f, .015f, -.05f)
                    .setColor(255, 255, 255, 255)
                    .setUv(ibu1, ibv1)
                    .setOverlay(packedOverlay)
                    .setLight(LightTexture.FULL_BRIGHT)
                    .setNormal(poseStack.last(), 0.f, 0.f, 0.f);

            vertexConsumer.addVertex(mat, .625f, -.015f, -.05f)
                    .setColor(255, 255, 255, 255)
                    .setUv(ibu1, ibv0)
                    .setOverlay(packedOverlay)
                    .setLight(LightTexture.FULL_BRIGHT)
                    .setNormal(poseStack.last(), 0.f, 0.f, 0.f);

            vertexConsumer.addVertex(mat, .375f, -.015f, -.05f)
                    .setColor(255, 255, 255, 255)
                    .setUv(ibu0, ibv0)
                    .setOverlay(packedOverlay)
                    .setLight(LightTexture.FULL_BRIGHT)
                    .setNormal(poseStack.last(), 0.f, 0.f, 0.f);

            poseStack.translate(0.f, -translateForMinMaxIndicatorBarHeight, 0.f);
        }

        v1 = v1Orig;

        poseStack.translate(0.f, 1.f - (16 - height - 1) / 16.f, 0.f);
        poseStack.mulPose(new Quaternionf().rotationX((float)Math.PI * -.5f));
        poseStack.translate(0.f, -.875f, -1 + (16 - height - 1) / 16.f);

        //Top
        {
            vertexConsumer.addVertex(mat, .0625f, .875f, 0.f)
                    .setColor(fluidColorTint)
                    .setUv(u0, v1)
                    .setOverlay(packedOverlay)
                    .setLight(LightTexture.FULL_BRIGHT)
                    .setNormal(poseStack.last(), 0.f, 0.f, 0.f);

            vertexConsumer.addVertex(mat, .9375f, .875f, 0.f)
                    .setColor(fluidColorTint)
                    .setUv(u1, v1)
                    .setOverlay(packedOverlay)
                    .setLight(LightTexture.FULL_BRIGHT)
                    .setNormal(poseStack.last(), 0.f, 0.f, 0.f);

            vertexConsumer.addVertex(mat, .9375f, 0.f, 0.f)
                    .setColor(fluidColorTint)
                    .setUv(u1, v0)
                    .setOverlay(packedOverlay)
                    .setLight(LightTexture.FULL_BRIGHT)
                    .setNormal(poseStack.last(), 0.f, 0.f, 0.f);

            vertexConsumer.addVertex(mat, .0625f, 0.f, 0.f)
                    .setColor(fluidColorTint)
                    .setUv(u0, v0)
                    .setOverlay(packedOverlay)
                    .setLight(LightTexture.FULL_BRIGHT)
                    .setNormal(poseStack.last(), 0.f, 0.f, 0.f);
        }

        poseStack.popPose();
    }
}
