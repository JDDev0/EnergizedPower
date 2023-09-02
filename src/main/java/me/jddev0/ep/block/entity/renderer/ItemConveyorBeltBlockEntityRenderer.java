package me.jddev0.ep.block.entity.renderer;

import me.jddev0.ep.block.ItemConveyorBeltBlock;
import me.jddev0.ep.block.ModBlockStateProperties;
import me.jddev0.ep.block.entity.ItemConveyorBeltBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import org.joml.Quaternionf;

@Environment(EnvType.CLIENT)
public class ItemConveyorBeltBlockEntityRenderer implements BlockEntityRenderer<ItemConveyorBeltBlockEntity> {
    private final BlockEntityRendererFactory.Context context;

    public ItemConveyorBeltBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.context = context;
    }

    @Override
    public void render(ItemConveyorBeltBlockEntity blockEntity, float partialTick, MatrixStack poseStack, VertexConsumerProvider bufferSource, int packedLight, int packedOverlay) {
        World level = blockEntity.getWorld();
        BlockPos pos = blockEntity.getPos();

        poseStack.push();
        poseStack.translate(.0f, .2f, .0f);

        ModBlockStateProperties.ConveyorBeltDirection facing = blockEntity.getCachedState().get(ItemConveyorBeltBlock.FACING);
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
        ItemRenderer itemRenderer = context.getItemRenderer();
        poseStack.translate(0.f, -.2f * (slope == null?1.f:1.41f), 0.f);
        for(int i = 0;i < blockEntity.getSlotCount();i++) {
            poseStack.translate(0.f, .333f * (slope == null?1.f:1.41f), 0.f);

            ItemStack itemStack = blockEntity.getStack(i);
            if(itemStack.isEmpty())
                continue;

            BakedModel bakedModel = itemRenderer.getModel(itemStack, null, null, 0);
            itemRenderer.renderItem(itemStack, ModelTransformationMode.GROUND, false, poseStack, bufferSource,
                    LightmapTextureManager.pack(level.getLightLevel(LightType.BLOCK, pos), level.getLightLevel(LightType.SKY, pos)),
                    OverlayTexture.DEFAULT_UV, bakedModel);
        }

        poseStack.pop();
    }
}
