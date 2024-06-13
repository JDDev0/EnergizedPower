package me.jddev0.ep.block.entity.renderer;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.FluidTankBlock;
import me.jddev0.ep.block.entity.FluidTankBlockEntity;
import me.jddev0.ep.fluid.FluidStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.Fluid;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

@Environment(EnvType.CLIENT)
public class FluidTankBlockEntityRenderer implements BlockEntityRenderer<FluidTankBlockEntity> {
    private final BlockEntityRendererFactory.Context context;

    public FluidTankBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.context = context;
    }

    @Override
    public void render(FluidTankBlockEntity blockEntity, float partialTick, MatrixStack poseStack, VertexConsumerProvider bufferSource, int packedLight, int packedOverlay) {
        long capacity = blockEntity.getTankCapacity(0);
        FluidStack fluidStack = blockEntity.getFluid(0);

        if(fluidStack.isEmpty())
            return;

        float height = ((fluidStack.getDropletsAmount() <= 0 || capacity == 0)?0:
                (Math.min(fluidStack.getDropletsAmount(), capacity - 1) * 14.f / capacity));

        Direction facing = blockEntity.getCachedState().get(FluidTankBlock.FACING);

        poseStack.push();

        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderLayer.getTranslucent());

        poseStack.multiply(facing.getRotationQuaternion());
        poseStack.multiply(new Quaternionf().rotationX((float)Math.PI * .5f));

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
        Sprite stillFluidSprite = FluidVariantRendering.getSprite(fluidStack.getFluidVariant());
        if(stillFluidSprite == null)
            stillFluidSprite = MinecraftClient.getInstance().getSpriteAtlas(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).
                    apply(MissingSprite.getMissingSpriteId());

        int fluidColorTint = FluidVariantRendering.getColor(fluidStack.getFluidVariant());

        Matrix4f mat = poseStack.peek().getPositionMatrix();

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
                    .overlay(packedOverlay)
                    .light(LightmapTextureManager.MAX_LIGHT_COORDINATE)
                    .normal(poseStack.peek(), 0.f, 0.f, 0.f);

            vertexConsumer.vertex(mat, .9375f, height * .0625f, 0.f)
                    .color(fluidColorTint)
                    .texture(u1, v1)
                    .overlay(packedOverlay)
                    .light(LightmapTextureManager.MAX_LIGHT_COORDINATE)
                    .normal(poseStack.peek(), 0.f, 0.f, 0.f);

            vertexConsumer.vertex(mat, .9375f, 0.f, 0.f)
                    .color(fluidColorTint)
                    .texture(u1, v0)
                    .overlay(packedOverlay)
                    .light(LightmapTextureManager.MAX_LIGHT_COORDINATE)
                    .normal(poseStack.peek(), 0.f, 0.f, 0.f);

            vertexConsumer.vertex(mat, .0625f, 0.f, 0.f)
                    .color(fluidColorTint)
                    .texture(u0, v0)
                    .overlay(packedOverlay)
                    .light(LightmapTextureManager.MAX_LIGHT_COORDINATE)
                    .normal(poseStack.peek(), 0.f, 0.f, 0.f);
        }

        //Indicator bar
        {
            Sprite indicatorBarSprite = MinecraftClient.getInstance().getSpriteAtlas(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).
                    apply(Identifier.of(EnergizedPowerMod.MODID, "block/fluid_tank_indicator_bar"));

            float translateForMinMaxIndicatorBarHeight = height < 2?(height - 2) / 16.f:(height > 12?(height - 12) / 16.f:0.f);

            poseStack.translate(0.f, translateForMinMaxIndicatorBarHeight, 0.f);

            float ibu0 = indicatorBarSprite.getMinU();
            float ibu1 = indicatorBarSprite.getMaxU();
            float ibv0 = indicatorBarSprite.getMinV();
            float ibv1 = indicatorBarSprite.getMaxV();

            ibu1 = ibu0 + .0625f * (ibu1 - ibu0);
            ibv1 = ibv0 + .0625f * (ibv1 - ibv0);

            vertexConsumer.vertex(mat, .375f, .015f, -.05f)
                    .color(255, 255, 255, 255)
                    .texture(ibu0, ibv1)
                    .overlay(packedOverlay)
                    .light(LightmapTextureManager.MAX_LIGHT_COORDINATE)
                    .normal(poseStack.peek(), 0.f, 0.f, 0.f);

            vertexConsumer.vertex(mat, .625f, .015f, -.05f)
                    .color(255, 255, 255, 255)
                    .texture(ibu1, ibv1)
                    .overlay(packedOverlay)
                    .light(LightmapTextureManager.MAX_LIGHT_COORDINATE)
                    .normal(poseStack.peek(), 0.f, 0.f, 0.f);

            vertexConsumer.vertex(mat, .625f, -.015f, -.05f)
                    .color(255, 255, 255, 255)
                    .texture(ibu1, ibv0)
                    .overlay(packedOverlay)
                    .light(LightmapTextureManager.MAX_LIGHT_COORDINATE)
                    .normal(poseStack.peek(), 0.f, 0.f, 0.f);

            vertexConsumer.vertex(mat, .375f, -.015f, -.05f)
                    .color(255, 255, 255, 255)
                    .texture(ibu0, ibv0)
                    .overlay(packedOverlay)
                    .light(LightmapTextureManager.MAX_LIGHT_COORDINATE)
                    .normal(poseStack.peek(), 0.f, 0.f, 0.f);

            poseStack.translate(0.f, -translateForMinMaxIndicatorBarHeight, 0.f);
        }

        v1 = v1Orig;

        poseStack.translate(0.f, 1.f - (16 - height - 1) / 16.f, 0.f);
        poseStack.multiply(new Quaternionf().rotationX((float)Math.PI * -.5f));
        poseStack.translate(0.f, -.875f, -1 + (16 - height - 1) / 16.f);

        //Top
        {
            vertexConsumer.vertex(mat, .0625f, .875f, 0.f)
                    .color(fluidColorTint)
                    .texture(u0, v1)
                    .overlay(packedOverlay)
                    .light(LightmapTextureManager.MAX_LIGHT_COORDINATE)
                    .normal(poseStack.peek(), 0.f, 0.f, 0.f);

            vertexConsumer.vertex(mat, .9375f, .875f, 0.f)
                    .color(fluidColorTint)
                    .texture(u1, v1)
                    .overlay(packedOverlay)
                    .light(LightmapTextureManager.MAX_LIGHT_COORDINATE)
                    .normal(poseStack.peek(), 0.f, 0.f, 0.f);

            vertexConsumer.vertex(mat, .9375f, 0.f, 0.f)
                    .color(fluidColorTint)
                    .texture(u1, v0)
                    .overlay(packedOverlay)
                    .light(LightmapTextureManager.MAX_LIGHT_COORDINATE)
                    .normal(poseStack.peek(), 0.f, 0.f, 0.f);

            vertexConsumer.vertex(mat, .0625f, 0.f, 0.f)
                    .color(fluidColorTint)
                    .texture(u0, v0)
                    .overlay(packedOverlay)
                    .light(LightmapTextureManager.MAX_LIGHT_COORDINATE)
                    .normal(poseStack.peek(), 0.f, 0.f, 0.f);
        }

        poseStack.pop();
    }
}
