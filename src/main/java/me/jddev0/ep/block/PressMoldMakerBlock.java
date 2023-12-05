package me.jddev0.ep.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.block.entity.PressMoldMakerBlockEntity;
import me.jddev0.ep.codec.CodecFix;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class PressMoldMakerBlock extends BlockWithEntity {
    public static final MapCodec<PressMoldMakerBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(CodecFix.FABRIC_BLOCK_SETTINGS_CODEC.fieldOf("properties").forGetter(block -> {
            return (FabricBlockSettings)block.getSettings();
        })).apply(instance, PressMoldMakerBlock::new);
    });

    public PressMoldMakerBlock(FabricBlockSettings props) {
        super(props);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos blockPos, BlockState state) {
        return new PressMoldMakerBlockEntity(blockPos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World level, BlockPos blockPos) {
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if(!(blockEntity instanceof PressMoldMakerBlockEntity pressMoldMakerBlockEntity))
            return super.getComparatorOutput(state, level, blockPos);

        return pressMoldMakerBlockEntity.getRedstoneOutput();
    }

    @Override
    public void onStateReplaced(BlockState state, World level, BlockPos blockPos, BlockState newState, boolean isMoving) {
        if(state.getBlock() == newState.getBlock())
            return;

        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if(!(blockEntity instanceof PressMoldMakerBlockEntity))
            return;

        ((PressMoldMakerBlockEntity)blockEntity).drops(level, blockPos);

        super.onStateReplaced(state, level, blockPos, newState, isMoving);
    }

    @Override
    public ActionResult onUse(BlockState state, World level, BlockPos blockPos, PlayerEntity player, Hand handItem, BlockHitResult hit) {
        if(level.isClient())
            return ActionResult.SUCCESS;

        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if(!(blockEntity instanceof PressMoldMakerBlockEntity))
            throw new IllegalStateException("Container is invalid");

        player.openHandledScreen((PressMoldMakerBlockEntity)blockEntity);

        return ActionResult.SUCCESS;
    }
}
