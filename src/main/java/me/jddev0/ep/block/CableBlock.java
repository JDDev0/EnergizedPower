package me.jddev0.ep.block;

import me.jddev0.ep.block.entity.CableBlockEntity;
import me.jddev0.ep.util.EnergyUtils;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.List;

public class CableBlock extends BlockWithEntity implements Waterloggable {
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

    private final Tier tier;

    public CableBlock(Tier tier) {
        super(tier.getProperties());

        this.setDefaultState(this.getStateManager().getDefaultState().with(UP, false).with(DOWN, false).
                with(NORTH, false).with(SOUTH, false).with(EAST, false).with(WEST, false).
                with(WATERLOGGED, false));

        this.tier = tier;
    }

    public Tier getTier() {
        return tier;
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
    public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState facingState, WorldAccess level, BlockPos selfPos, BlockPos facingPos) {
        if(state.get(WATERLOGGED))
            level.createAndScheduleFluidTick(selfPos, Fluids.WATER, Fluids.WATER.getTickRate(level));

        return super.getStateForNeighborUpdate(state, facing, facingState, level, selfPos, facingPos);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(UP).add(DOWN).add(NORTH).add(SOUTH).add(EAST).add(WEST).add(WATERLOGGED);
    }

    @Override
    public void neighborUpdate(BlockState selfState, World level, BlockPos selfPos, Block fromBlock, BlockPos fromPos, boolean isMoving) {
        super.neighborUpdate(selfState, level, selfPos, fromBlock, fromPos, isMoving);

        if(level.isClient())
            return;

        FluidState fluidState = level.getFluidState(selfPos);

        level.setBlockState(selfPos, getDefaultState().
                with(UP, shouldConnectTo(level, selfPos, Direction.UP)).
                with(DOWN, shouldConnectTo(level, selfPos, Direction.DOWN)).
                with(NORTH, shouldConnectTo(level, selfPos, Direction.NORTH)).
                with(SOUTH, shouldConnectTo(level, selfPos, Direction.SOUTH)).
                with(EAST, shouldConnectTo(level, selfPos, Direction.EAST)).
                with(WEST, shouldConnectTo(level, selfPos, Direction.WEST)).
                with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER)
        );


        BlockEntity blockEntity = level.getBlockEntity(selfPos);
        if(blockEntity == null || !(blockEntity instanceof CableBlockEntity))
            return;

        CableBlockEntity.updateConnections(level, selfPos, selfState, (CableBlockEntity)blockEntity);
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
        return checkType(type, CableBlockEntity.getEntityTypeFromTier(tier), CableBlockEntity::tick);
    }

    public static class Item extends BlockItem {
        private final Tier tier;

        public Item(Block block, FabricItemSettings props, Tier tier) {
            super(block, props);

            this.tier = tier;
        }

        public Tier getTier() {
            return tier;
        }

        @Override
        public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
            if(Screen.hasShiftDown()) {
                tooltip.add(new TranslatableText("tooltip.energizedpower.cable.txt.shift.1",
                        EnergyUtils.getEnergyWithPrefix(tier.getMaxTransfer())).formatted(Formatting.GRAY));
                tooltip.add(new TranslatableText("tooltip.energizedpower.cable.txt.shift.2").
                        formatted(Formatting.GRAY, Formatting.ITALIC));
            }else {
                tooltip.add(new TranslatableText("tooltip.energizedpower.shift_details.txt").formatted(Formatting.YELLOW));
            }
        }
    }

    public enum Tier {
        TIER_COPPER("copper_cable", 1024,
                FabricBlockSettings.of(Material.WOOL, MapColor.GRAY).strength(.5f).sounds(BlockSoundGroup.WOOL)),
        TIER_ENERGIZED_COPPER("energized_copper_cable", 262144,
                FabricBlockSettings.of(Material.WOOL, MapColor.GRAY).strength(.5f).sounds(BlockSoundGroup.WOOL));

        private final String resourceId;
        private final int maxTransfer;
        private final FabricBlockSettings props;

        Tier(String resourceId, int maxTransfer, FabricBlockSettings props) {
            this.resourceId = resourceId;
            this.maxTransfer = maxTransfer;
            this.props = props;
        }

        public String getResourceId() {
            return resourceId;
        }

        public int getMaxTransfer() {
            return maxTransfer;
        }

        public FabricBlockSettings getProperties() {
            return props;
        }
    }
}
