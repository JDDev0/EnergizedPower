package me.jddev0.ep.block.entity.renderer;

import com.mojang.blaze3d.vertex.*;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.FluidTankBlock;
import me.jddev0.ep.block.entity.FluidTankBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

@OnlyIn(Dist.CLIENT)
public class FluidTankBlockEntityRenderer implements BlockEntityRenderer<FluidTankBlockEntity> {
    private final BlockEntityRendererProvider.Context context;

    public FluidTankBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }

    @Override
    public void render(FluidTankBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        int capacity = blockEntity.getTankCapacity(0);
        FluidStack fluidStack = blockEntity.getFluid(0);

        if(fluidStack.isEmpty())
            return;

        float height = ((fluidStack.getAmount() <= 0 || capacity == 0)?0:
                (Math.min(fluidStack.getAmount(), capacity - 1) * 14.f / capacity));

        Direction facing = blockEntity.getBlockState().getValue(FluidTankBlock.FACING);

        poseStack.pushPose();

        VertexConsumer vertexConsumer = bufferSource.getBuffer(Sheets.translucentCullBlockSheet());

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
        TextureAtlasSprite stillFluidSprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).
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

            vertexConsumer.vertex(mat, .0625f, height * .0625f, 0.f)
                    .color(fluidColorTint)
                    .uv(u0, v1)
                    .overlayCoords(packedOverlay)
                    .uv2(LightTexture.FULL_BRIGHT)
                    .normal(poseStack.last().normal(), 0.f, 0.f, 0.f)
                    .endVertex();

            vertexConsumer.vertex(mat, .9375f, height * .0625f, 0.f)
                    .color(fluidColorTint)
                    .uv(u1, v1)
                    .overlayCoords(packedOverlay)
                    .uv2(LightTexture.FULL_BRIGHT)
                    .normal(poseStack.last().normal(), 0.f, 0.f, 0.f)
                    .endVertex();

            vertexConsumer.vertex(mat, .9375f, 0.f, 0.f)
                    .color(fluidColorTint)
                    .uv(u1, v0)
                    .overlayCoords(packedOverlay)
                    .uv2(LightTexture.FULL_BRIGHT)
                    .normal(poseStack.last().normal(), 0.f, 0.f, 0.f)
                    .endVertex();

            vertexConsumer.vertex(mat, .0625f, 0.f, 0.f)
                    .color(fluidColorTint)
                    .uv(u0, v0)
                    .overlayCoords(packedOverlay)
                    .uv2(LightTexture.FULL_BRIGHT)
                    .normal(poseStack.last().normal(), 0.f, 0.f, 0.f)
                    .endVertex();
        }

        //Indicator bar
        {
            TextureAtlasSprite indicatorBarSprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).
                    apply(new ResourceLocation(EnergizedPowerMod.MODID, "block/fluid_tank_indicator_bar"));

            float translateForMinMaxIndicatorBarHeight = height < 2?(height - 2) / 16.f:(height > 12?(height - 12) / 16.f:0.f);

            poseStack.translate(0.f, translateForMinMaxIndicatorBarHeight, 0.f);

            float ibu0 = indicatorBarSprite.getU0();
            float ibu1 = indicatorBarSprite.getU1();
            float ibv0 = indicatorBarSprite.getV0();
            float ibv1 = indicatorBarSprite.getV1();

            ibu1 = ibu0 + .0625f * (ibu1 - ibu0);
            ibv1 = ibv0 + .0625f * (ibv1 - ibv0);

            vertexConsumer.vertex(mat, .375f, .015f, -.05f)
                    .color(255, 255, 255, 255)
                    .uv(ibu0, ibv1)
                    .overlayCoords(packedOverlay)
                    .uv2(LightTexture.FULL_BRIGHT)
                    .normal(poseStack.last().normal(), 0.f, 0.f, 0.f)
                    .endVertex();

            vertexConsumer.vertex(mat, .625f, .015f, -.05f)
                    .color(255, 255, 255, 255)
                    .uv(ibu1, ibv1)
                    .overlayCoords(packedOverlay)
                    .uv2(LightTexture.FULL_BRIGHT)
                    .normal(poseStack.last().normal(), 0.f, 0.f, 0.f)
                    .endVertex();

            vertexConsumer.vertex(mat, .625f, -.015f, -.05f)
                    .color(255, 255, 255, 255)
                    .uv(ibu1, ibv0)
                    .overlayCoords(packedOverlay)
                    .uv2(LightTexture.FULL_BRIGHT)
                    .normal(poseStack.last().normal(), 0.f, 0.f, 0.f)
                    .endVertex();

            vertexConsumer.vertex(mat, .375f, -.015f, -.05f)
                    .color(255, 255, 255, 255)
                    .uv(ibu0, ibv0)
                    .overlayCoords(packedOverlay)
                    .uv2(LightTexture.FULL_BRIGHT)
                    .normal(poseStack.last().normal(), 0.f, 0.f, 0.f)
                    .endVertex();

            poseStack.translate(0.f, -translateForMinMaxIndicatorBarHeight, 0.f);
        }

        v1 = v1Orig;

        poseStack.translate(0.f, 1.f - (16 - height - 1) / 16.f, 0.f);
        poseStack.mulPose(new Quaternionf().rotationX((float)Math.PI * -.5f));
        poseStack.translate(0.f, -.875f, -1 + (16 - height - 1) / 16.f);

        //Top
        {
            vertexConsumer.vertex(mat, .0625f, .875f, 0.f)
                    .color(fluidColorTint)
                    .uv(u0, v1)
                    .overlayCoords(packedOverlay)
                    .uv2(LightTexture.FULL_BRIGHT)
                    .normal(poseStack.last().normal(), 0.f, 0.f, 0.f)
                    .endVertex();

            vertexConsumer.vertex(mat, .9375f, .875f, 0.f)
                    .color(fluidColorTint)
                    .uv(u1, v1)
                    .overlayCoords(packedOverlay)
                    .uv2(LightTexture.FULL_BRIGHT)
                    .normal(poseStack.last().normal(), 0.f, 0.f, 0.f)
                    .endVertex();

            vertexConsumer.vertex(mat, .9375f, 0.f, 0.f)
                    .color(fluidColorTint)
                    .uv(u1, v0)
                    .overlayCoords(packedOverlay)
                    .uv2(LightTexture.FULL_BRIGHT)
                    .normal(poseStack.last().normal(), 0.f, 0.f, 0.f)
                    .endVertex();

            vertexConsumer.vertex(mat, .0625f, 0.f, 0.f)
                    .color(fluidColorTint)
                    .uv(u0, v0)
                    .overlayCoords(packedOverlay)
                    .uv2(LightTexture.FULL_BRIGHT)
                    .normal(poseStack.last().normal(), 0.f, 0.f, 0.f)
                    .endVertex();
        }

        poseStack.popPose();
    }
}
