package me.jddev0.ep.block;

import me.jddev0.ep.block.entity.ItemConveyorBeltSplitterBlockEntity;
import me.jddev0.ep.block.entity.EPBlockEntities;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ItemConveyorBeltSplitterBlock extends BlockWithEntity {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    protected ItemConveyorBeltSplitterBlock(FabricBlockSettings props) {
        super(props);

        this.setDefaultState(this.getStateManager().getDefaultState().with(FACING, Direction.NORTH));
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos blockPos, BlockState state) {
        return new ItemConveyorBeltSplitterBlockEntity(blockPos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        return this.getDefaultState().with(FACING, context.getPlayerFacing().getOpposite());
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
        stateBuilder.add(FACING);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World level, BlockState state, BlockEntityType<T> type) {
        return checkType(type, EPBlockEntities.ITEM_CONVEYOR_BELT_SPLITTER_ENTITY, ItemConveyorBeltSplitterBlockEntity::tick);
    }
}
