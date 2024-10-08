package me.jddev0.ep.block;

import com.mojang.serialization.MapCodec;
import me.jddev0.ep.block.entity.CompressorBlockEntity;
import me.jddev0.ep.block.entity.EPBlockEntities;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CompressorBlock extends BlockWithEntity {
    public static final MapCodec<CompressorBlock> CODEC = createCodec(CompressorBlock::new);

    public static final BooleanProperty POWERED = Properties.POWERED;

    public CompressorBlock(AbstractBlock.Settings props) {
        super(props);

        this.setDefaultState(this.getStateManager().getDefaultState().with(POWERED, false));
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos blockPos, BlockState state) {
        return new CompressorBlockEntity(blockPos, state);
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
        if(!(blockEntity instanceof CompressorBlockEntity CompressorBlockEntity))
            return super.getComparatorOutput(state, level, blockPos);

        return CompressorBlockEntity.getRedstoneOutput();
    }

    @Override
    public void onStateReplaced(BlockState state, World level, BlockPos blockPos, BlockState newState, boolean isMoving) {
        if(state.getBlock() == newState.getBlock())
            return;

        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if(!(blockEntity instanceof CompressorBlockEntity))
            return;

        ((CompressorBlockEntity)blockEntity).drops(level, blockPos);

        super.onStateReplaced(state, level, blockPos, newState, isMoving);
    }

    @Override
    public ActionResult onUse(BlockState state, World level, BlockPos blockPos, PlayerEntity player, BlockHitResult hit) {
        if(level.isClient())
            return ActionResult.SUCCESS;

        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if(!(blockEntity instanceof CompressorBlockEntity))
            throw new IllegalStateException("Container is invalid");

        player.openHandledScreen((CompressorBlockEntity)blockEntity);

        return ActionResult.SUCCESS;
    }

    @Override
    public void neighborUpdate(BlockState selfState, World level, BlockPos selfPos, Block fromBlock, BlockPos fromPos, boolean isMoving) {
        super.neighborUpdate(selfState, level, selfPos, fromBlock, fromPos, isMoving);

        if(level.isClient())
            return;

        boolean isPowered = level.isReceivingRedstonePower(selfPos);
        if(isPowered != selfState.get(POWERED))
            level.setBlockState(selfPos, selfState.with(POWERED, isPowered), 3);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        return this.getDefaultState().with(POWERED, context.getWorld().isReceivingRedstonePower(context.getBlockPos()));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(POWERED);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World level, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, EPBlockEntities.COMPRESSOR_ENTITY, CompressorBlockEntity::tick);
    }
}
