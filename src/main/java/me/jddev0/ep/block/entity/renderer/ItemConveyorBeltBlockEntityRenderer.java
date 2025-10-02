package me.jddev0.ep.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.HashCommon;
import me.jddev0.ep.block.ItemConveyorBeltBlock;
import me.jddev0.ep.block.EPBlockStateProperties;
import me.jddev0.ep.block.entity.ItemConveyorBeltBlockEntity;
import me.jddev0.ep.block.entity.renderer.state.ItemConveyorBeltBlockEntityRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

import javax.annotation.Nullable;

public class ItemConveyorBeltBlockEntityRenderer implements BlockEntityRenderer<ItemConveyorBeltBlockEntity, ItemConveyorBeltBlockEntityRenderState> {
    private final BlockEntityRendererProvider.Context context;

    public ItemConveyorBeltBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }

    @Override
    public ItemConveyorBeltBlockEntityRenderState createRenderState() {
        return new ItemConveyorBeltBlockEntityRenderState();
    }

    @Override
    public void extractRenderState(ItemConveyorBeltBlockEntity blockEntity, ItemConveyorBeltBlockEntityRenderState state, float tickProgress, Vec3 cameraPos, @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, tickProgress, cameraPos, crumblingOverlay);

        state.facing = blockEntity.getBlockState().getValue(ItemConveyorBeltBlock.FACING);
        if(state.itemRenderStates != null) {
            for(ItemStackRenderState itemRenderState:state.itemRenderStates) {
                itemRenderState.clear();
            }
        }

        int seed = HashCommon.long2int(blockEntity.getBlockPos().asLong());

        state.itemRenderStates = new ItemStackRenderState[blockEntity.getSlotCount()];
        for(int i = 0;i < blockEntity.getSlotCount();i++) {
            ItemStackRenderState itemRenderState = new ItemStackRenderState();
            context.itemModelResolver().updateForTopItem(itemRenderState, blockEntity.getStack(i), ItemDisplayContext.GROUND, blockEntity.getLevel(), new ItemOwner() {
                @Override
                public Level level() {
                    return blockEntity.getLevel();
                }

                @Override
                public Vec3 position() {
                    return blockEntity.getBlockPos().getCenter();
                }

                @Override
                public float getVisualRotationYInDegrees() {
                    return blockEntity.getBlockState().getValue(ItemConveyorBeltBlock.FACING).getDirection().getOpposite().toYRot();
                }
            }, seed + i);
            state.itemRenderStates[i] = itemRenderState;
        }
    }

    @Override
    public void submit(ItemConveyorBeltBlockEntityRenderState state, PoseStack poseStack, SubmitNodeCollector queue, CameraRenderState cameraRenderState) {
        poseStack.pushPose();
        poseStack.translate(.0f, .2f, .0f);

        EPBlockStateProperties.ConveyorBeltDirection facing = state.facing;
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
        poseStack.translate(0.f, -.2f * (slope == null?1.f:1.41f), 0.f);
        for(int i = 0;i < state.itemRenderStates.length;i++) {
            poseStack.translate(0.f, .333f * (slope == null?1.f:1.41f), 0.f);

            ItemStackRenderState itemRenderState = state.itemRenderStates[i];
            itemRenderState.submit(poseStack, queue, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        }

        poseStack.popPose();
    }
}
