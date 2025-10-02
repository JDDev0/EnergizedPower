package me.jddev0.ep.block;

import com.mojang.serialization.MapCodec;
import me.jddev0.ep.block.entity.PressMoldMakerBlockEntity;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class PressMoldMakerBlock extends BlockWithEntity {
    public static final MapCodec<PressMoldMakerBlock> CODEC = createCodec(PressMoldMakerBlock::new);

    public PressMoldMakerBlock(AbstractBlock.Settings props) {
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
    protected int getComparatorOutput(BlockState state, World level, BlockPos blockPos, Direction direction) {
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if(!(blockEntity instanceof PressMoldMakerBlockEntity pressMoldMakerBlockEntity))
            return super.getComparatorOutput(state, level, blockPos, direction);

        return pressMoldMakerBlockEntity.getRedstoneOutput();
    }

    @Override
    protected void onStateReplaced(BlockState state, ServerWorld level, BlockPos blockPos, boolean moved) {
        ItemScatterer.onStateReplaced(state, level, blockPos);
    }

    @Override
    public ActionResult onUse(BlockState state, World level, BlockPos blockPos, PlayerEntity player, BlockHitResult hit) {
        if(level.isClient())
            return ActionResult.SUCCESS;

        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if(!(blockEntity instanceof PressMoldMakerBlockEntity))
            throw new IllegalStateException("Container is invalid");

        player.openHandledScreen((PressMoldMakerBlockEntity)blockEntity);

        return ActionResult.SUCCESS;
    }
}
