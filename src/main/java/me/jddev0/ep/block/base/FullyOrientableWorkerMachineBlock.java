package me.jddev0.ep.block.base;

import me.jddev0.ep.machine.RedstoneOutput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public abstract class FullyOrientableWorkerMachineBlock<E extends BlockEntity & MenuProvider & RedstoneOutput>
        extends WorkerMachineBlock<E>  {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;

    protected FullyOrientableWorkerMachineBlock(
            Properties props,

            Supplier<BlockEntityType<E>> blockEntityType,
            Class<E> typeParam,
            BiFunction<BlockPos, BlockState, E> blockEntityFactory,
            BlockEntityTicker<? super E> blockEntityTicker
    ) {
        super(props, blockEntityType, typeParam, blockEntityFactory, blockEntityTicker);

        this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, false).setValue(WORKING, false).setValue(FACING, Direction.NORTH));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return Objects.requireNonNull(super.getStateForPlacement(context)).
                setValue(FACING, context.getNearestLookingDirection().getOpposite());
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
        super.createBlockStateDefinition(stateBuilder);

        stateBuilder.add(FACING);
    }
}
