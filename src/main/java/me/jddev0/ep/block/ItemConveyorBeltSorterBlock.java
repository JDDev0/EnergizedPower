package me.jddev0.ep.block;

import me.jddev0.ep.block.entity.ItemConveyorBeltSorterBlockEntity;
import me.jddev0.ep.block.entity.ModBlockEntities;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ItemConveyorBeltSorterBlock extends BlockWithEntity {
    public static final BooleanProperty POWERED = Properties.POWERED;
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    protected ItemConveyorBeltSorterBlock(FabricBlockSettings props) {
        super(props);

        this.setDefaultState(this.getStateManager().getDefaultState().with(POWERED, false).with(FACING, Direction.NORTH));
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos blockPos, BlockState state) {
        return new ItemConveyorBeltSorterBlockEntity(blockPos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView getter, BlockPos blockPos, Direction direction) {
        return state.get(POWERED)?15:0;
    }

    @Override
    public ActionResult onUse(BlockState state, World level, BlockPos blockPos, PlayerEntity player, Hand handItem, BlockHitResult hit) {
        if(level.isClient())
            return ActionResult.SUCCESS;

        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if(!(blockEntity instanceof ItemConveyorBeltSorterBlockEntity))
            throw new IllegalStateException("Container is invalid");

        player.openHandledScreen((ItemConveyorBeltSorterBlockEntity)blockEntity);

        return ActionResult.SUCCESS;
    }

    @Override
    public void neighborUpdate(BlockState selfState, World level, BlockPos selfPos, Block fromBlock, BlockPos fromPos, boolean isMoving) {
        super.neighborUpdate(selfState, level, selfPos, fromBlock, fromPos, isMoving);

        if(level.isClient())
            return;

        int dx = fromPos.getX() - selfPos.getX();
        int dy = fromPos.getY() - selfPos.getY();
        int dz = fromPos.getZ() - selfPos.getZ();
        Direction dir = Direction.fromVector(dx, dy, dz);
        if(dir == Direction.UP || dir == Direction.DOWN || dir == selfState.get(FACING))
            return;

        updateOutputBeltConnectionStateOfDirection(level, selfPos, selfState, dir);
    }

    @Override
    public void onBlockAdded(BlockState selfState, World level, BlockPos selfPos, BlockState oldState, boolean isMoving) {
        if(level.isClient())
            return;

        Direction facing = selfState.get(FACING);

        for(int i = 0;i < 3;i++)
            updateOutputBeltConnectionStateOfDirection(level, selfPos, selfState, switch(i) {
                case 0 -> facing.rotateYClockwise();
                case 1 -> facing.getOpposite();
                case 2 -> facing.rotateYCounterclockwise();
                default -> null;
            });
    }

    private void updateOutputBeltConnectionStateOfDirection(World level, BlockPos blockPos, BlockState state, Direction outputBeltDirection) {
        Direction facing = state.get(FACING);

        int index;
        if(outputBeltDirection == facing.rotateYClockwise())
            index = 0;
        else if(outputBeltDirection == facing.getOpposite())
            index = 1;
        else if(outputBeltDirection == facing.rotateYCounterclockwise())
            index = 2;
        else
            return;

        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if(!(blockEntity instanceof ItemConveyorBeltSorterBlockEntity itemConveyorBeltSorterBlockEntity))
            return;

        BlockState outputBeltState = level.getBlockState(blockPos.offset(outputBeltDirection));
        itemConveyorBeltSorterBlockEntity.setOutputBeltConnected(index, outputBeltState.isOf(ModBlocks.ITEM_CONVEYOR_BELT));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        return this.getDefaultState().with(FACING, context.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(POWERED, FACING);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World level, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.ITEM_CONVEYOR_BELT_SORTER_ENTITY, ItemConveyorBeltSorterBlockEntity::tick);
    }
}
