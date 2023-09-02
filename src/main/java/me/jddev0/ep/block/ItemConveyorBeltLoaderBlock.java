package me.jddev0.ep.block;

import me.jddev0.ep.block.entity.ItemConveyorBeltLoaderBlockEntity;
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
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ItemConveyorBeltLoaderBlock extends BlockWithEntity {
    public static final BooleanProperty ENABLED = Properties.ENABLED;
    public static final DirectionProperty FACING = Properties.FACING;

    protected ItemConveyorBeltLoaderBlock(FabricBlockSettings props) {
        super(props);

        this.setDefaultState(this.getStateManager().getDefaultState().with(FACING, Direction.NORTH).with(ENABLED, true));
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos blockPos, BlockState state) {
        return new ItemConveyorBeltLoaderBlockEntity(blockPos, state);
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
        if(!(blockEntity instanceof ItemConveyorBeltLoaderBlockEntity itemConveyorBeltLoaderBlockEntity))
            return super.getComparatorOutput(state, level, blockPos);

        return itemConveyorBeltLoaderBlockEntity.getRedstoneOutput();
    }

    @Override
    public void onStateReplaced(BlockState state, World level, BlockPos blockPos, BlockState newState, boolean isMoving) {
        if(state.getBlock() == newState.getBlock())
            return;

        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if(!(blockEntity instanceof ItemConveyorBeltLoaderBlockEntity))
            return;

        ((ItemConveyorBeltLoaderBlockEntity)blockEntity).drops(level, blockPos);

        super.onStateReplaced(state, level, blockPos, newState, isMoving);
    }

    @Override
    public ActionResult onUse(BlockState state, World level, BlockPos blockPos, PlayerEntity player, Hand handItem, BlockHitResult hit) {
        if(level.isClient())
            return ActionResult.SUCCESS;

        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if(!(blockEntity instanceof ItemConveyorBeltLoaderBlockEntity))
            throw new IllegalStateException("Container is invalid");

        player.openHandledScreen((ItemConveyorBeltLoaderBlockEntity)blockEntity);

        return ActionResult.SUCCESS;
    }

    @Override
    public void neighborUpdate(BlockState selfState, World level, BlockPos selfPos, Block fromBlock, BlockPos fromPos, boolean isMoving) {
        super.neighborUpdate(selfState, level, selfPos, fromBlock, fromPos, isMoving);

        if(level.isClient())
            return;

        boolean isPowered = level.isReceivingRedstonePower(selfPos);
        if(isPowered == selfState.get(ENABLED))
            level.setBlockState(selfPos, selfState.with(ENABLED, !isPowered), 3);
    }

    @Override
    public void onBlockAdded(BlockState selfState, World level, BlockPos selfPos, BlockState oldState, boolean isMoving) {
        if(level.isClient())
            return;

        boolean isPowered = level.isReceivingRedstonePower(selfPos);
        if(isPowered == selfState.get(ENABLED))
            level.setBlockState(selfPos, selfState.with(ENABLED, !isPowered), 2);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        return this.getDefaultState().with(FACING, context.getPlayerLookDirection().getOpposite());
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
        stateBuilder.add(ENABLED, FACING);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World level, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.ITEM_CONVEYOR_BELT_LOADER_ENTITY, ItemConveyorBeltLoaderBlockEntity::tick);
    }
}
