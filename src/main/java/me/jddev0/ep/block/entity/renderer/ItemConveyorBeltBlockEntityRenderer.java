package me.jddev0.ep.block.entity.renderer;

import it.unimi.dsi.fastutil.HashCommon;
import me.jddev0.ep.block.ItemConveyorBeltBlock;
import me.jddev0.ep.block.EPBlockStateProperties;
import me.jddev0.ep.block.entity.ItemConveyorBeltBlockEntity;
import me.jddev0.ep.block.entity.renderer.state.ItemConveyorBeltBlockEntityRenderState;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.command.ModelCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.util.HeldItemContext;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Quaternionf;

import javax.annotation.Nullable;

@Environment(EnvType.CLIENT)
public class ItemConveyorBeltBlockEntityRenderer implements BlockEntityRenderer<ItemConveyorBeltBlockEntity, ItemConveyorBeltBlockEntityRenderState> {
    private final BlockEntityRendererFactory.Context context;

    public ItemConveyorBeltBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.context = context;
    }

    @Override
    public ItemConveyorBeltBlockEntityRenderState createRenderState() {
        return new ItemConveyorBeltBlockEntityRenderState();
    }

    @Override
    public void updateRenderState(ItemConveyorBeltBlockEntity blockEntity, ItemConveyorBeltBlockEntityRenderState state, float tickProgress, Vec3d cameraPos, @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlay) {
        BlockEntityRenderer.super.updateRenderState(blockEntity, state, tickProgress, cameraPos, crumblingOverlay);

        state.facing = blockEntity.getCachedState().get(ItemConveyorBeltBlock.FACING);
        if(state.itemRenderStates != null) {
            for(ItemRenderState itemRenderState:state.itemRenderStates) {
                itemRenderState.clear();
            }
        }

        int seed = HashCommon.long2int(blockEntity.getPos().asLong());

        state.itemRenderStates = new ItemRenderState[blockEntity.getSlotCount()];
        for(int i = 0;i < blockEntity.getSlotCount();i++) {
            ItemRenderState itemRenderState = new ItemRenderState();
            context.itemModelManager().clearAndUpdate(itemRenderState, blockEntity.getStack(i), ItemDisplayContext.GROUND, blockEntity.getWorld(), new HeldItemContext() {
                @Override
                public World getEntityWorld() {
                    return blockEntity.getWorld();
                }

                @Override
                public Vec3d getEntityPos() {
                    return blockEntity.getPos().toCenterPos();
                }

                @Override
                public float getBodyYaw() {
                    return blockEntity.getCachedState().get(ItemConveyorBeltBlock.FACING).getDirection().getOpposite().getPositiveHorizontalDegrees();
                }
            }, seed + i);
            state.itemRenderStates[i] = itemRenderState;
        }
    }

    @Override
    public void render(ItemConveyorBeltBlockEntityRenderState state, MatrixStack poseStack, OrderedRenderCommandQueue queue, CameraRenderState cameraRenderState) {
        poseStack.push();
        poseStack.translate(.0f, .2f, .0f);

        EPBlockStateProperties.ConveyorBeltDirection facing = state.facing;
        Direction facingDirection = facing.getDirection();
        Boolean slope = facing.getSlope();

        if(facingDirection == Direction.NORTH) {
            poseStack.translate(.5f, 0.f, 1.f);

            if(facing.isAscending()) {
                poseStack.multiply(new Quaternionf().rotateX((float)Math.PI * .25f));
            }else if(facing.isDescending()) {
                poseStack.translate(0.f, 1.f, 0.f);
                poseStack.multiply(new Quaternionf().rotateX((float)Math.PI * -.25f));
            }
        }else if(facingDirection == Direction.SOUTH) {
            poseStack.translate(.5f, 0.f, 0.f);

            if(facing.isAscending()) {
                poseStack.multiply(new Quaternionf().rotateX((float)Math.PI * -.25f));
            }else if(facing.isDescending()) {
                poseStack.translate(0.f, 1.f, 0.f);
                poseStack.multiply(new Quaternionf().rotateX((float)Math.PI * .25f));
            }
        }else if(facingDirection == Direction.EAST) {
            poseStack.translate(0.f, 0.f, .5f);

            if(facing.isAscending()) {
                poseStack.multiply(new Quaternionf().rotateZ((float)Math.PI * .25f));
            }else if(facing.isDescending()) {
                poseStack.translate(0.f, 1.f, 0.f);
                poseStack.multiply(new Quaternionf().rotateZ((float)Math.PI * -.25f));
            }
        }else if(facingDirection == Direction.WEST) {
            poseStack.translate(1.f, 0.f, .5f);

            if(facing.isAscending()) {
                poseStack.multiply(new Quaternionf().rotateZ((float)Math.PI * -.25f));
            }else if(facing.isDescending()) {
                poseStack.translate(0.f, 1.f, 0.f);
                poseStack.multiply(new Quaternionf().rotateZ((float)Math.PI * .25f));
            }
        }

        poseStack.multiply(facingDirection.getRotationQuaternion());

        poseStack.scale(.75f, .75f, .75f);
        poseStack.translate(0.f, -.2f * (slope == null?1.f:1.41f), 0.f);
        for(int i = 0;i < state.itemRenderStates.length;i++) {
            poseStack.translate(0.f, .333f * (slope == null?1.f:1.41f), 0.f);

            ItemRenderState itemRenderState = state.itemRenderStates[i];
            itemRenderState.render(poseStack, queue, state.lightmapCoordinates, OverlayTexture.DEFAULT_UV, 0);
        }

        poseStack.pop();
    }
}
