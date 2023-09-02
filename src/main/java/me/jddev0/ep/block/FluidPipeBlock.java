package me.jddev0.ep.block;

import me.jddev0.ep.block.entity.FluidPipeBlockEntity;
import me.jddev0.ep.block.entity.ModBlockEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FluidPipeBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
    public static final EnumProperty<ModBlockStateProperties.PipeConnection> UP = ModBlockStateProperties.PIPE_CONNECTION_UP;
    public static final EnumProperty<ModBlockStateProperties.PipeConnection> DOWN = ModBlockStateProperties.PIPE_CONNECTION_DOWN;
    public static final EnumProperty<ModBlockStateProperties.PipeConnection> NORTH = ModBlockStateProperties.PIPE_CONNECTION_NORTH;
    public static final EnumProperty<ModBlockStateProperties.PipeConnection> SOUTH = ModBlockStateProperties.PIPE_CONNECTION_SOUTH;
    public static final EnumProperty<ModBlockStateProperties.PipeConnection> EAST = ModBlockStateProperties.PIPE_CONNECTION_EAST;
    public static final EnumProperty<ModBlockStateProperties.PipeConnection> WEST = ModBlockStateProperties.PIPE_CONNECTION_WEST;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private static final VoxelShape SHAPE_CORE = Block.box(4.d, 4.d, 4.d, 12.d, 12.d, 12.d);
    private static final VoxelShape SHAPE_UP = Block.box(4.d, 12.d, 4.d, 12.d, 16.d, 12.d);
    private static final VoxelShape SHAPE_DOWN = Block.box(4.d, 0.d, 4.d, 12.d, 4.d, 12.d);
    private static final VoxelShape SHAPE_NORTH = Block.box(4.d, 4.d, 0.d, 12.d, 12.d, 4.d);
    private static final VoxelShape SHAPE_SOUTH = Block.box(4.d, 4.d, 12.d, 12.d, 12.d, 16.d);
    private static final VoxelShape SHAPE_EAST = Block.box(12.d, 4.d, 4.d, 16.d, 12.d, 12.d);
    private static final VoxelShape SHAPE_WEST = Block.box(0.d, 4.d, 4.d, 4.d, 12.d, 12.d);

    @NotNull
    public static EnumProperty<ModBlockStateProperties.PipeConnection> getPipeConnectionPropertyFromDirection(@NotNull Direction dir) {
        return switch(dir) {
            case UP -> UP;
            case DOWN -> DOWN;
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case EAST -> EAST;
            case WEST -> WEST;
        };
    }

    public FluidPipeBlock(Properties props) {
        super(props);

        this.registerDefaultState(this.stateDefinition.any().setValue(UP, ModBlockStateProperties.PipeConnection.NOT_CONNECTED).
                setValue(DOWN, ModBlockStateProperties.PipeConnection.NOT_CONNECTED).
                setValue(NORTH, ModBlockStateProperties.PipeConnection.NOT_CONNECTED).
                setValue(SOUTH, ModBlockStateProperties.PipeConnection.NOT_CONNECTED).
                setValue(EAST, ModBlockStateProperties.PipeConnection.NOT_CONNECTED).
                setValue(WEST, ModBlockStateProperties.PipeConnection.NOT_CONNECTED).
                setValue(WATERLOGGED, false));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        Level level = blockPlaceContext.getLevel();
        BlockPos selfPos = blockPlaceContext.getClickedPos();
        FluidState fluidState = level.getFluidState(selfPos);

        return defaultBlockState().
                setValue(UP, shouldConnectTo(level, selfPos, Direction.UP)).
                setValue(DOWN, shouldConnectTo(level, selfPos, Direction.DOWN)).
                setValue(NORTH, shouldConnectTo(level, selfPos, Direction.NORTH)).
                setValue(SOUTH, shouldConnectTo(level, selfPos, Direction.SOUTH)).
                setValue(EAST, shouldConnectTo(level, selfPos, Direction.EAST)).
                setValue(WEST, shouldConnectTo(level, selfPos, Direction.WEST)).
                setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        switch(rotation) {
            case CLOCKWISE_90:
                return state.
                        setValue(NORTH, state.getValue(WEST)).
                        setValue(SOUTH, state.getValue(EAST)).
                        setValue(EAST, state.getValue(NORTH)).
                        setValue(WEST, state.getValue(SOUTH));
            case CLOCKWISE_180:
                return state.
                        setValue(NORTH, state.getValue(SOUTH)).
                        setValue(SOUTH, state.getValue(NORTH)).
                        setValue(EAST, state.getValue(WEST)).
                        setValue(WEST, state.getValue(EAST));
            case COUNTERCLOCKWISE_90:
                return state.
                        setValue(NORTH, state.getValue(EAST)).
                        setValue(SOUTH, state.getValue(WEST)).
                        setValue(EAST, state.getValue(SOUTH)).
                        setValue(WEST, state.getValue(NORTH));
            default:
                return state;
        }
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        switch(mirror) {
            case LEFT_RIGHT:
                return state.
                        setValue(NORTH, state.getValue(SOUTH)).
                        setValue(SOUTH, state.getValue(NORTH));
            case FRONT_BACK:
                return state.
                        setValue(EAST, state.getValue(WEST)).
                        setValue(WEST, state.getValue(EAST));
            default:
                return state;
        }
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        VoxelShape shape = SHAPE_CORE;

        if(blockState.getValue(UP).isConnected())
            shape = Shapes.or(shape, SHAPE_UP);

        if(blockState.getValue(DOWN).isConnected())
            shape = Shapes.or(shape, SHAPE_DOWN);

        if(blockState.getValue(NORTH).isConnected())
            shape = Shapes.or(shape, SHAPE_NORTH);

        if(blockState.getValue(SOUTH).isConnected())
            shape = Shapes.or(shape, SHAPE_SOUTH);

        if(blockState.getValue(EAST).isConnected())
            shape = Shapes.or(shape, SHAPE_EAST);

        if(blockState.getValue(WEST).isConnected())
            shape = Shapes.or(shape, SHAPE_WEST);

        return shape;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED)?Fluids.WATER.getSource(false):super.getFluidState(state);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos selfPos, BlockPos facingPos) {
        if(state.getValue(WATERLOGGED))
            level.scheduleTick(selfPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));

        return super.updateShape(state, facing, facingState, level, selfPos, facingPos);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(UP).add(DOWN).add(NORTH).add(SOUTH).add(EAST).add(WEST).add(WATERLOGGED);
    }

    @Override
    public void neighborChanged(BlockState selfState, Level level, BlockPos selfPos, Block fromBlock, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(selfState, level, selfPos, fromBlock, fromPos, isMoving);

        if(level.isClientSide())
            return;

        FluidState fluidState = level.getFluidState(selfPos);

        int dx = fromPos.getX() - selfPos.getX();
        int dy = fromPos.getY() - selfPos.getY();
        int dz = fromPos.getZ() - selfPos.getZ();
        Direction dir = Direction.fromNormal(dx, dy, dz);

        BlockState newState;
        if(dir == null) {
            newState = defaultBlockState().
                    setValue(UP, selfState.getValue(UP)).
                    setValue(DOWN, selfState.getValue(DOWN)).
                    setValue(NORTH, selfState.getValue(NORTH)).
                    setValue(SOUTH, selfState.getValue(SOUTH)).
                    setValue(EAST, selfState.getValue(EAST)).
                    setValue(WEST, selfState.getValue(WEST)).
                    setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
        }else {
            EnumProperty<ModBlockStateProperties.PipeConnection> pipeConnectionProperty = getPipeConnectionPropertyFromDirection(dir);

            newState = defaultBlockState().
                    setValue(UP, selfState.getValue(UP)).
                    setValue(DOWN, selfState.getValue(DOWN)).
                    setValue(NORTH, selfState.getValue(NORTH)).
                    setValue(SOUTH, selfState.getValue(SOUTH)).
                    setValue(EAST, selfState.getValue(EAST)).
                    setValue(WEST, selfState.getValue(WEST)).
                    setValue(pipeConnectionProperty, shouldConnectTo(level, selfPos, dir)).
                    setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
        }
        level.setBlockAndUpdate(selfPos, newState);


        BlockEntity blockEntity = level.getBlockEntity(selfPos);
        if(blockEntity == null || !(blockEntity instanceof FluidPipeBlockEntity))
            return;

        FluidPipeBlockEntity.updateConnections(level, selfPos, newState, (FluidPipeBlockEntity)blockEntity);
    }

    private ModBlockStateProperties.PipeConnection shouldConnectTo(Level level, BlockPos selfPos, Direction direction) {
        BlockPos toPos = selfPos.relative(direction);
        BlockEntity blockEntity = level.getBlockEntity(toPos);
        if(blockEntity == null)
            return ModBlockStateProperties.PipeConnection.NOT_CONNECTED;

        LazyOptional<IFluidHandler> fluidStorageLazyOptional = blockEntity.getCapability(ForgeCapabilities.FLUID_HANDLER, direction.getOpposite());
        return fluidStorageLazyOptional.isPresent()?ModBlockStateProperties.PipeConnection.CONNECTED:ModBlockStateProperties.PipeConnection.NOT_CONNECTED;
    }

    public static class Item extends BlockItem {

        public Item(Block block, Properties props) {
            super(block, props);
        }

        @Override
        public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
            if(Screen.hasShiftDown()) {
                components.add(Component.translatable("tooltip.energizedpower.wrench_configurable").
                        withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            }else {
                components.add(Component.translatable("tooltip.energizedpower.shift_details.txt").withStyle(ChatFormatting.YELLOW));
            }
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState state) {
        return new FluidPipeBlockEntity(blockPos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModBlockEntities.FLUID_PIPE_ENTITY.get(), FluidPipeBlockEntity::tick);
    }
}
