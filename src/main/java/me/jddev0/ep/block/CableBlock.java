package me.jddev0.ep.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.block.entity.CableBlockEntity;
import me.jddev0.ep.machine.tier.CableTier;
import me.jddev0.ep.util.EnergyUtils;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Formatting;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.block.WireOrientation;
import net.minecraft.world.tick.ScheduledTickView;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.function.Consumer;

public class CableBlock extends BlockWithEntity implements Waterloggable {
    public static final MapCodec<CableBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(Codecs.NON_EMPTY_STRING.xmap(CableTier::valueOf, CableTier::toString).fieldOf("tier").
                forGetter(CableBlock::getTier),
                Settings.CODEC.fieldOf("properties").forGetter(AbstractBlock::getSettings)).
                apply(instance, CableBlock::new);
    });

    public static final BooleanProperty UP = Properties.UP;
    public static final BooleanProperty DOWN = Properties.DOWN;
    public static final BooleanProperty NORTH = Properties.NORTH;
    public static final BooleanProperty SOUTH = Properties.SOUTH;
    public static final BooleanProperty EAST = Properties.EAST;
    public static final BooleanProperty WEST = Properties.WEST;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    private static final VoxelShape SHAPE_CORE = Block.createCuboidShape(6.d, 6.d, 6.d, 10.d, 10.d, 10.d);
    private static final VoxelShape SHAPE_UP = Block.createCuboidShape(6.d, 10.d, 6.d, 10.d, 16.d, 10.d);
    private static final VoxelShape SHAPE_DOWN = Block.createCuboidShape(6.d, 0.d, 6.d, 10.d, 6.d, 10.d);
    private static final VoxelShape SHAPE_NORTH = Block.createCuboidShape(6.d, 6.d, 0.d, 10.d, 10.d, 6.d);
    private static final VoxelShape SHAPE_SOUTH = Block.createCuboidShape(6.d, 6.d, 10.d, 10.d, 10.d, 16.d);
    private static final VoxelShape SHAPE_EAST = Block.createCuboidShape(10.d, 6.d, 6.d, 16.d, 10.d, 10.d);
    private static final VoxelShape SHAPE_WEST = Block.createCuboidShape(0.d, 6.d, 6.d, 6.d, 10.d, 10.d);

    private final CableTier tier;

    public CableBlock(CableTier tier, Settings props) {
        super(props);

        this.setDefaultState(this.getStateManager().getDefaultState().with(UP, false).with(DOWN, false).
                with(NORTH, false).with(SOUTH, false).with(EAST, false).with(WEST, false).
                with(WATERLOGGED, false));

        this.tier = tier;
    }

    public CableTier getTier() {
        return tier;
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext blockPlaceContext) {
        World level = blockPlaceContext.getWorld();
        BlockPos selfPos = blockPlaceContext.getBlockPos();
        FluidState fluidState = level.getFluidState(selfPos);

        return getDefaultState().
                with(UP, shouldConnectTo(level, selfPos, Direction.UP)).
                with(DOWN, shouldConnectTo(level, selfPos, Direction.DOWN)).
                with(NORTH, shouldConnectTo(level, selfPos, Direction.NORTH)).
                with(SOUTH, shouldConnectTo(level, selfPos, Direction.SOUTH)).
                with(EAST, shouldConnectTo(level, selfPos, Direction.EAST)).
                with(WEST, shouldConnectTo(level, selfPos, Direction.WEST)).
                with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        switch(rotation) {
            case CLOCKWISE_90:
                return state.
                        with(NORTH, state.get(WEST)).
                        with(SOUTH, state.get(EAST)).
                        with(EAST, state.get(NORTH)).
                        with(WEST, state.get(SOUTH));
            case CLOCKWISE_180:
                return state.
                        with(NORTH, state.get(SOUTH)).
                        with(SOUTH, state.get(NORTH)).
                        with(EAST, state.get(WEST)).
                        with(WEST, state.get(EAST));
            case COUNTERCLOCKWISE_90:
                return state.
                        with(NORTH, state.get(EAST)).
                        with(SOUTH, state.get(WEST)).
                        with(EAST, state.get(SOUTH)).
                        with(WEST, state.get(NORTH));
            default:
                return state;
        }
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        switch(mirror) {
            case LEFT_RIGHT:
                return state.
                        with(NORTH, state.get(SOUTH)).
                        with(SOUTH, state.get(NORTH));
            case FRONT_BACK:
                return state.
                        with(EAST, state.get(WEST)).
                        with(WEST, state.get(EAST));
            default:
                return state;
        }
    }

    @Override
    public VoxelShape getOutlineShape(BlockState blockState, BlockView blockGetter, BlockPos blockPos, ShapeContext collisionContext) {
        VoxelShape shape = SHAPE_CORE;

        if(blockState.get(UP))
            shape = VoxelShapes.union(shape, SHAPE_UP);

        if(blockState.get(DOWN))
            shape = VoxelShapes.union(shape, SHAPE_DOWN);

        if(blockState.get(NORTH))
            shape = VoxelShapes.union(shape, SHAPE_NORTH);

        if(blockState.get(SOUTH))
            shape = VoxelShapes.union(shape, SHAPE_SOUTH);

        if(blockState.get(EAST))
            shape = VoxelShapes.union(shape, SHAPE_EAST);

        if(blockState.get(WEST))
            shape = VoxelShapes.union(shape, SHAPE_WEST);

        return shape;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED)?Fluids.WATER.getStill(false):super.getFluidState(state);
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView level, ScheduledTickView tickView, BlockPos selfPos, Direction facing,
                                                   BlockPos facingPos, BlockState facingState, Random random) {
        if(state.get(WATERLOGGED))
            tickView.scheduleFluidTick(selfPos, Fluids.WATER, Fluids.WATER.getTickRate(level));

        return super.getStateForNeighborUpdate(state, level, tickView, selfPos, facing, facingPos, facingState, random);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(UP).add(DOWN).add(NORTH).add(SOUTH).add(EAST).add(WEST).add(WATERLOGGED);
    }

    @Override
    public void neighborUpdate(BlockState selfState, World level, BlockPos selfPos, Block fromBlock, @Nullable WireOrientation wireOrientation, boolean isMoving) {
        super.neighborUpdate(selfState, level, selfPos, fromBlock, wireOrientation, isMoving);

        if(level.isClient())
            return;

        FluidState fluidState = level.getFluidState(selfPos);

        BlockState newState = getDefaultState().
                with(UP, shouldConnectTo(level, selfPos, Direction.UP)).
                with(DOWN, shouldConnectTo(level, selfPos, Direction.DOWN)).
                with(NORTH, shouldConnectTo(level, selfPos, Direction.NORTH)).
                with(SOUTH, shouldConnectTo(level, selfPos, Direction.SOUTH)).
                with(EAST, shouldConnectTo(level, selfPos, Direction.EAST)).
                with(WEST, shouldConnectTo(level, selfPos, Direction.WEST)).
                with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);

        level.setBlockState(selfPos, newState);


        BlockEntity blockEntity = level.getBlockEntity(selfPos);
        if(!(blockEntity instanceof CableBlockEntity))
            return;

        CableBlockEntity.updateConnections(level, selfPos, newState, (CableBlockEntity)blockEntity);
    }

    private boolean shouldConnectTo(World level, BlockPos selfPos, Direction direction) {
        BlockPos toPos = selfPos.offset(direction);
        BlockEntity blockEntity = level.getBlockEntity(toPos);
        if(blockEntity == null)
            return false;

        if(blockEntity instanceof CableBlockEntity cableBlockEntity && cableBlockEntity.getTier() != this.getTier())
            return false;

        EnergyStorage energyStorage = EnergyStorage.SIDED.find(level, toPos, direction.getOpposite());
        return energyStorage != null;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos blockPos, BlockState state) {
        return new CableBlockEntity(blockPos, state, tier);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World level, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, tier.getEntityTypeFromTier(), CableBlockEntity::tick);
    }

    public static class Item extends BlockItem {
        private final CableTier tier;

        public Item(Block block, Item.Settings props, CableTier tier) {
            super(block, props);

            this.tier = tier;
        }

        public CableTier getTier() {
            return tier;
        }

        @Override
        public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> tooltip, TooltipType type) {
            if(MinecraftClient.getInstance().isShiftPressed()) {
                tooltip.accept(Text.translatable("tooltip.energizedpower.cable.txt.shift.1",
                        EnergyUtils.getEnergyWithPrefix(tier.getMaxTransfer())).formatted(Formatting.GRAY));
                tooltip.accept(Text.translatable("tooltip.energizedpower.cable.txt.shift.2").
                        formatted(Formatting.GRAY, Formatting.ITALIC));
            }else {
                tooltip.accept(Text.translatable("tooltip.energizedpower.shift_details.txt").formatted(Formatting.YELLOW));
            }
        }
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
