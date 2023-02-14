package me.jddev0.ep.block;

import me.jddev0.ep.block.entity.CableBlockEntity;
import me.jddev0.ep.util.EnergyUtils;
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
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CableBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
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

    private final Tier tier;

    public CableBlock(Tier tier) {
        super(tier.getProperties());

        this.registerDefaultState(this.stateDefinition.any().setValue(UP, false).setValue(DOWN, false).
                setValue(NORTH, false).setValue(SOUTH, false).setValue(EAST, false).setValue(WEST, false).
                setValue(WATERLOGGED, false));

        this.tier = tier;
    }

    public Tier getTier() {
        return tier;
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

        level.setBlockAndUpdate(selfPos, defaultBlockState().
                setValue(UP, shouldConnectTo(level, selfPos, Direction.UP)).
                setValue(DOWN, shouldConnectTo(level, selfPos, Direction.DOWN)).
                setValue(NORTH, shouldConnectTo(level, selfPos, Direction.NORTH)).
                setValue(SOUTH, shouldConnectTo(level, selfPos, Direction.SOUTH)).
                setValue(EAST, shouldConnectTo(level, selfPos, Direction.EAST)).
                setValue(WEST, shouldConnectTo(level, selfPos, Direction.WEST)).
                setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER)
        );


        BlockEntity blockEntity = level.getBlockEntity(selfPos);
        if(blockEntity == null || !(blockEntity instanceof CableBlockEntity))
            return;

        CableBlockEntity.updateConnections(level, selfPos, selfState, (CableBlockEntity)blockEntity);
    }

    private boolean shouldConnectTo(Level level, BlockPos selfPos, Direction direction) {
        BlockPos toPos = selfPos.relative(direction);
        BlockEntity blockEntity = level.getBlockEntity(toPos);
        if(blockEntity == null)
            return false;

        if(blockEntity instanceof CableBlockEntity cableBlockEntity && cableBlockEntity.getTier() != this.getTier())
            return false;

        LazyOptional<IEnergyStorage> energyStorageLazyOptional = blockEntity.getCapability(ForgeCapabilities.ENERGY, direction.getOpposite());
        return energyStorageLazyOptional.isPresent();
    }

    public static class Item extends BlockItem {
        private final Tier tier;

        public Item(Block block, Properties props, Tier tier) {
            super(block, props);

            this.tier = tier;
        }

        public Tier getTier() {
            return tier;
        }

        @Override
        public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
            if(Screen.hasShiftDown()) {
                components.add(Component.translatable("tooltip.energizedpower.cable.txt.shift.1",
                        EnergyUtils.getEnergyWithPrefix(tier.getMaxTransfer())).withStyle(ChatFormatting.GRAY));
                components.add(Component.translatable("tooltip.energizedpower.cable.txt.shift.2").
                        withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            }else {
                components.add(Component.translatable("tooltip.energizedpower.shift_details.txt").withStyle(ChatFormatting.YELLOW));
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
        return createTickerHelper(type, CableBlockEntity.getEntityTypeFromTier(tier), CableBlockEntity::tick);
    }

    public enum Tier {
        TIER_COPPER("copper_cable", 1024,
                BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_GRAY).strength(.5f).sound(SoundType.WOOL)),
        TIER_GOLD("gold_cable", 16384,
                BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_GRAY).strength(.5f).sound(SoundType.WOOL)),
        TIER_ENERGIZED_COPPER("energized_copper_cable", 131072,
                BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_GRAY).strength(.5f).sound(SoundType.WOOL));

        private final String resourceId;
        private final int maxTransfer;
        private final Properties props;

        Tier(String resourceId, int maxTransfer, Properties props) {
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

        public Properties getProperties() {
            return props;
        }
    }
}
