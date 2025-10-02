package me.jddev0.ep.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.block.entity.CableBlockEntity;
import me.jddev0.ep.machine.tier.CableTier;
import me.jddev0.ep.util.EnergyUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class CableBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
    public static final MapCodec<CableBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(ExtraCodecs.NON_EMPTY_STRING.xmap(CableTier::valueOf, CableTier::toString).fieldOf("tier").
                forGetter(CableBlock::getTier),
                        Properties.CODEC.fieldOf("properties").forGetter(Block::properties)).
                apply(instance, CableBlock::new);
    });

    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private static final VoxelShape SHAPE_CORE = Block.box(6.d, 6.d, 6.d, 10.d, 10.d, 10.d);
    private static final VoxelShape SHAPE_UP = Block.box(6.d, 10.d, 6.d, 10.d, 16.d, 10.d);
    private static final VoxelShape SHAPE_DOWN = Block.box(6.d, 0.d, 6.d, 10.d, 6.d, 10.d);
    private static final VoxelShape SHAPE_NORTH = Block.box(6.d, 6.d, 0.d, 10.d, 10.d, 6.d);
    private static final VoxelShape SHAPE_SOUTH = Block.box(6.d, 6.d, 10.d, 10.d, 10.d, 16.d);
    private static final VoxelShape SHAPE_EAST = Block.box(10.d, 6.d, 6.d, 16.d, 10.d, 10.d);
    private static final VoxelShape SHAPE_WEST = Block.box(0.d, 6.d, 6.d, 6.d, 10.d, 10.d);

    private final CableTier tier;

    public CableBlock(CableTier tier, Properties properties) {
        super(properties);

        this.registerDefaultState(this.stateDefinition.any().setValue(UP, false).setValue(DOWN, false).
                setValue(NORTH, false).setValue(SOUTH, false).setValue(EAST, false).setValue(WEST, false).
                setValue(WATERLOGGED, false));

        this.tier = tier;
    }

    public CableTier getTier() {
        return tier;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
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

        if(blockState.getValue(UP))
            shape = Shapes.or(shape, SHAPE_UP);

        if(blockState.getValue(DOWN))
            shape = Shapes.or(shape, SHAPE_DOWN);

        if(blockState.getValue(NORTH))
            shape = Shapes.or(shape, SHAPE_NORTH);

        if(blockState.getValue(SOUTH))
            shape = Shapes.or(shape, SHAPE_SOUTH);

        if(blockState.getValue(EAST))
            shape = Shapes.or(shape, SHAPE_EAST);

        if(blockState.getValue(WEST))
            shape = Shapes.or(shape, SHAPE_WEST);

        return shape;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED)?Fluids.WATER.getSource(false):super.getFluidState(state);
    }

    @Override
    public BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickView, BlockPos selfPos, Direction facing,
                                  BlockPos facingPos, BlockState facingState, RandomSource random) {
        if(state.getValue(WATERLOGGED))
            tickView.scheduleTick(selfPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));

        return super.updateShape(state, level, tickView, selfPos, facing, facingPos, facingState, random);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(UP).add(DOWN).add(NORTH).add(SOUTH).add(EAST).add(WEST).add(WATERLOGGED);
    }

    @Override
    public void neighborChanged(BlockState selfState, Level level, BlockPos selfPos, Block fromBlock, @Nullable Orientation orientation, boolean isMoving) {
        super.neighborChanged(selfState, level, selfPos, fromBlock, orientation, isMoving);

        if(level.isClientSide())
            return;

        FluidState fluidState = level.getFluidState(selfPos);

        BlockState newState = defaultBlockState().
                setValue(UP, shouldConnectTo(level, selfPos, Direction.UP)).
                setValue(DOWN, shouldConnectTo(level, selfPos, Direction.DOWN)).
                setValue(NORTH, shouldConnectTo(level, selfPos, Direction.NORTH)).
                setValue(SOUTH, shouldConnectTo(level, selfPos, Direction.SOUTH)).
                setValue(EAST, shouldConnectTo(level, selfPos, Direction.EAST)).
                setValue(WEST, shouldConnectTo(level, selfPos, Direction.WEST)).
                setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);

        level.setBlockAndUpdate(selfPos, newState);


        BlockEntity blockEntity = level.getBlockEntity(selfPos);
        if(!(blockEntity instanceof CableBlockEntity))
            return;

        CableBlockEntity.updateConnections(level, selfPos, newState, (CableBlockEntity)blockEntity);
    }

    private boolean shouldConnectTo(Level level, BlockPos selfPos, Direction direction) {
        BlockPos toPos = selfPos.relative(direction);
        BlockEntity blockEntity = level.getBlockEntity(toPos);

        if(blockEntity instanceof CableBlockEntity cableBlockEntity && cableBlockEntity.getTier() != this.getTier())
            return false;

        IEnergyStorage energyStorage = level.getCapability(Capabilities.EnergyStorage.BLOCK,
                toPos, level.getBlockState(toPos), blockEntity, direction.getOpposite());
        return energyStorage != null;
    }

    public static class Item extends BlockItem {
        private final CableTier tier;

        public Item(Block block, Properties props, CableTier tier) {
            super(block, props);

            this.tier = tier;
        }

        public CableTier getTier() {
            return tier;
        }

        @Override
        public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> components, TooltipFlag tooltipFlag) {
            if(Minecraft.getInstance().hasShiftDown()) {
                components.accept(Component.translatable("tooltip.energizedpower.cable.txt.shift.1",
                        EnergyUtils.getEnergyWithPrefix(tier.getMaxTransfer())).withStyle(ChatFormatting.GRAY));
                components.accept(Component.translatable("tooltip.energizedpower.cable.txt.shift.2").
                        withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            }else {
                components.accept(Component.translatable("tooltip.energizedpower.shift_details.txt").withStyle(ChatFormatting.YELLOW));
            }
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState state) {
        return new CableBlockEntity(blockPos, state, tier);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, tier.getEntityTypeFromTier(), CableBlockEntity::tick);
    }

    public enum EnergyExtractionMode {
        PUSH, PULL, BOTH;

        public boolean isPush() {
            return this == PUSH || this == BOTH;
        }

        public boolean isPull() {
            return this == PULL || this == BOTH;
        }
    }
}
