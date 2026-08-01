package me.jddev0.ep.block;

import com.mojang.serialization.MapCodec;
import me.jddev0.ep.block.entity.TimeControllerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class TimeControllerBlock extends BaseEntityBlock {
    public static final MapCodec<TimeControllerBlock> CODEC = simpleCodec(TimeControllerBlock::new);

    public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;

    public TimeControllerBlock(Properties props) {
        super(props);

        this.registerDefaultState(this.stateDefinition.any().setValue(TRIGGERED, false));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState state) {
        return new TimeControllerBlockEntity(blockPos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos blockPos, Player player, BlockHitResult hit) {
        if(level.isClientSide())
            return InteractionResult.sidedSuccess(level.isClientSide());

        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if(!(blockEntity instanceof TimeControllerBlockEntity))
            throw new IllegalStateException("Container is invalid");

        player.openMenu((TimeControllerBlockEntity)blockEntity, blockPos);

        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public void neighborChanged(BlockState selfState, Level level, BlockPos selfPos, Block fromBlock, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(selfState, level, selfPos, fromBlock, fromPos, isMoving);

        if(level.isClientSide())
            return;

        boolean isPowered = level.hasNeighborSignal(selfPos);
        if(isPowered != selfState.getValue(TRIGGERED)) {
            if(isPowered) {
                BlockEntity blockEntity = level.getBlockEntity(selfPos);
                if(!(blockEntity instanceof TimeControllerBlockEntity timeControllerBlockEntity))
                    throw new IllegalStateException("Container is invalid");

                timeControllerBlockEntity.onRedstoneTriggered(level.getBestNeighborSignal(selfPos));
            }

            level.setBlock(selfPos, selfState.setValue(TRIGGERED, isPowered), 2);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(TRIGGERED);
    }
}
