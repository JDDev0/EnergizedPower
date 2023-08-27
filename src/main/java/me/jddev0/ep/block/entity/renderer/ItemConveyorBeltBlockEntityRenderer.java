package me.jddev0.ep.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import me.jddev0.ep.block.ItemConveyorBeltBlock;
import me.jddev0.ep.block.ModBlockStateProperties;
import me.jddev0.ep.block.entity.ItemConveyorBeltBlockEntity;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;

@OnlyIn(Dist.CLIENT)
public class ItemConveyorBeltBlockEntityRenderer implements BlockEntityRenderer<ItemConveyorBeltBlockEntity> {
    private final BlockEntityRendererProvider.Context context;

    public ItemConveyorBeltBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }

    @Override
    public void render(ItemConveyorBeltBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        Level level = blockEntity.getLevel();
        BlockPos pos = blockEntity.getBlockPos();

        poseStack.pushPose();
        poseStack.translate(.0f, .2f, .0f);

        ModBlockStateProperties.ConveyorBeltDirection facing = blockEntity.getBlockState().getValue(ItemConveyorBeltBlock.FACING);
        Direction facingDirection = facing.getDirection();
        Boolean slope = facing.getSlope();

        if(facingDirection == Direction.NORTH) {
            poseStack.translate(.5f, 0.f, 1.f);

            if(facing.isAscending()) {
                poseStack.mulPose(new Quaternionf().rotateX((float)Math.PI * .25f));
            }else if(facing.isDescending()) {
                poseStack.translate(0.f, 1.f, 0.f);
                poseStack.mulPose(new Quaternionf().rotateX((float)Math.PI * -.25f));
            }
        }else if(facingDirection == Direction.SOUTH) {
            poseStack.translate(.5f, 0.f, 0.f);

            if(facing.isAscending()) {
                poseStack.mulPose(new Quaternionf().rotateX((float)Math.PI * -.25f));
            }else if(facing.isDescending()) {
                poseStack.translate(0.f, 1.f, 0.f);
                poseStack.mulPose(new Quaternionf().rotateX((float)Math.PI * .25f));
            }
        }else if(facingDirection == Direction.EAST) {
            poseStack.translate(0.f, 0.f, .5f);

            if(facing.isAscending()) {
                poseStack.mulPose(new Quaternionf().rotateZ((float)Math.PI * .25f));
            }else if(facing.isDescending()) {
                poseStack.translate(0.f, 1.f, 0.f);
                poseStack.mulPose(new Quaternionf().rotateZ((float)Math.PI * -.25f));
            }
        }else if(facingDirection == Direction.WEST) {
            poseStack.translate(1.f, 0.f, .5f);

            if(facing.isAscending()) {
                poseStack.mulPose(new Quaternionf().rotateZ((float)Math.PI * -.25f));
            }else if(facing.isDescending()) {
                poseStack.translate(0.f, 1.f, 0.f);
                poseStack.mulPose(new Quaternionf().rotateZ((float)Math.PI * .25f));
            }
        }

        poseStack.mulPose(facingDirection.getRotation());

        poseStack.scale(.75f, .75f, .75f);
        ItemRenderer itemRenderer = context.getItemRenderer();
        poseStack.translate(0.f, -.2f * (slope == null?1.f:1.41f), 0.f);
        for(int i = 0;i < blockEntity.getSlotCount();i++) {
            poseStack.translate(0.f, .333f * (slope == null?1.f:1.41f), 0.f);

            ItemStack itemStack = blockEntity.getStack(i);
            if(itemStack.isEmpty())
                continue;

            BakedModel bakedModel = itemRenderer.getModel(itemStack, null, null, 0);
            itemRenderer.render(itemStack, ItemDisplayContext.GROUND, false, poseStack, bufferSource,
                    LightTexture.pack(level.getBrightness(LightLayer.BLOCK, pos), level.getBrightness(LightLayer.SKY, pos)),
                    OverlayTexture.NO_OVERLAY, bakedModel);
        }

        poseStack.popPose();
    }
}
