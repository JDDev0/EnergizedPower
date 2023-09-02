package me.jddev0.ep.block;

import me.jddev0.ep.block.entity.ItemConveyorBeltSorterBlockEntity;
import me.jddev0.ep.block.entity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class ItemConveyorBeltSorterBlock extends BaseEntityBlock {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    protected ItemConveyorBeltSorterBlock(Properties props) {
        super(props);

        this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, false).setValue(FACING, Direction.NORTH));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState state) {
        return new ItemConveyorBeltSorterBlockEntity(blockPos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public int getSignal(BlockState state, BlockGetter getter, BlockPos blockPos, Direction direction) {
        return state.getValue(POWERED)?15:0;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos blockPos, Player player, InteractionHand handItem, BlockHitResult hit) {
        if(level.isClientSide())
            return InteractionResult.sidedSuccess(level.isClientSide());

        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if(!(blockEntity instanceof ItemConveyorBeltSorterBlockEntity))
            throw new IllegalStateException("Container is invalid");

        NetworkHooks.openScreen((ServerPlayer)player, (ItemConveyorBeltSorterBlockEntity)blockEntity, blockPos);

        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public void neighborChanged(BlockState selfState, Level level, BlockPos selfPos, Block fromBlock, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(selfState, level, selfPos, fromBlock, fromPos, isMoving);

        if(level.isClientSide())
            return;

        int dx = fromPos.getX() - selfPos.getX();
        int dy = fromPos.getY() - selfPos.getY();
        int dz = fromPos.getZ() - selfPos.getZ();
        Direction dir = Direction.fromNormal(dx, dy, dz);
        if(dir == Direction.UP || dir == Direction.DOWN || dir == selfState.getValue(FACING))
            return;

        updateOutputBeltConnectionStateOfDirection(level, selfPos, selfState, dir);
    }

    @Override
    public void onPlace(BlockState selfState, Level level, BlockPos selfPos, BlockState oldState, boolean isMoving) {
        if(level.isClientSide())
            return;

        Direction facing = selfState.getValue(FACING);

        for(int i = 0;i < 3;i++)
            updateOutputBeltConnectionStateOfDirection(level, selfPos, selfState, switch(i) {
                case 0 -> facing.getClockWise();
                case 1 -> facing.getOpposite();
                case 2 -> facing.getCounterClockWise();
                default -> null;
            });
    }

    private void updateOutputBeltConnectionStateOfDirection(Level level, BlockPos blockPos, BlockState state, Direction outputBeltDirection) {
        Direction facing = state.getValue(FACING);

        int index;
        if(outputBeltDirection == facing.getClockWise())
            index = 0;
        else if(outputBeltDirection == facing.getOpposite())
            index = 1;
        else if(outputBeltDirection == facing.getCounterClockWise())
            index = 2;
        else
            return;

        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if(!(blockEntity instanceof ItemConveyorBeltSorterBlockEntity itemConveyorBeltSorterBlockEntity))
            return;

        BlockState outputBeltState = level.getBlockState(blockPos.relative(outputBeltDirection));
        itemConveyorBeltSorterBlockEntity.setOutputBeltConnected(index, outputBeltState.is(ModBlocks.ITEM_CONVEYOR_BELT.get()));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(POWERED, FACING);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModBlockEntities.ITEM_CONVEYOR_BELT_SORTER_ENTITY.get(), ItemConveyorBeltSorterBlockEntity::tick);
    }
}
