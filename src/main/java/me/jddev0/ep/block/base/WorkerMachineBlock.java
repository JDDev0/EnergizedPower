package me.jddev0.ep.block.base;

import me.jddev0.ep.block.EPBlockStateProperties;
import me.jddev0.ep.machine.ItemDrop;
import me.jddev0.ep.machine.RedstoneOutput;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public abstract class WorkerMachineBlock<E extends BlockEntity & MenuProvider & RedstoneOutput & ItemDrop> extends BaseEntityBlock {
    private final Supplier<BlockEntityType<E>> blockEntityType;
    private final Class<E> typeParam;
    private final BiFunction<BlockPos, BlockState, E> blockEntityFactory;
    private final BlockEntityTicker<? super E> blockEntityTicker;

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty WORKING = EPBlockStateProperties.WORKING;

    protected WorkerMachineBlock(
            Properties props,

            Supplier<BlockEntityType<E>> blockEntityType,
            Class<E> typeParam,
            BiFunction<BlockPos, BlockState, E> blockEntityFactory,
            BlockEntityTicker<? super E> blockEntityTicker
    ) {
        super(props);

        this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, false).setValue(WORKING, false));

        this.blockEntityType = blockEntityType;
        this.typeParam = typeParam;
        this.blockEntityFactory = blockEntityFactory;
        this.blockEntityTicker = blockEntityTicker;
    }

    @Nullable
    @Override
    public final BlockEntity newBlockEntity(BlockPos blockPos, BlockState state) {
        return blockEntityFactory.apply(blockPos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public final boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos blockPos) {
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if(!(blockEntity instanceof RedstoneOutput redstoneOutput) || !typeParam.isAssignableFrom(blockEntity.getClass()))
            throw new IllegalStateException("Invalid BlockEntity (expected type: " + typeParam.getName() + " Type: " +
                    (blockEntity == null?null:blockEntity.getClass().getName()) + ")");

        return redstoneOutput.getRedstoneOutput();
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos blockPos, BlockState newState, boolean isMoving) {
        if(state.getBlock() == newState.getBlock())
            return;

        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if(!(blockEntity instanceof ItemDrop itemDrop) || !typeParam.isAssignableFrom(blockEntity.getClass()))
            throw new IllegalStateException("Invalid BlockEntity (expected type: " + typeParam.getName() + " Type: " +
                    (blockEntity == null?null:blockEntity.getClass().getName()) + ")");

        itemDrop.drops(level, blockPos);

        super.onRemove(state, level, blockPos, newState, isMoving);
    }

    @Override
    public final InteractionResult useWithoutItem(BlockState state, Level level, BlockPos blockPos, Player player, BlockHitResult hit) {
        if(level.isClientSide())
            return InteractionResult.sidedSuccess(level.isClientSide());

        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if(!(blockEntity instanceof MenuProvider menuProvider) || !typeParam.isAssignableFrom(blockEntity.getClass()))
            throw new IllegalStateException("Invalid BlockEntity (expected type: " + typeParam.getName() + " Type: " +
                    (blockEntity == null?null:blockEntity.getClass().getName()) + ")");

        player.openMenu(menuProvider, blockPos);

        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public void neighborChanged(BlockState selfState, Level level, BlockPos selfPos, Block fromBlock, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(selfState, level, selfPos, fromBlock, fromPos, isMoving);

        if(level.isClientSide())
            return;

        boolean isPowered = level.hasNeighborSignal(selfPos);
        if(isPowered != selfState.getValue(POWERED))
            level.setBlock(selfPos, selfState.setValue(POWERED, isPowered), 3);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(POWERED, context.getLevel().hasNeighborSignal(context.getClickedPos())).
                setValue(WORKING, false);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(POWERED, WORKING);
    }

    @Nullable
    @Override
    public final <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, blockEntityType.get(), blockEntityTicker);
    }
}
