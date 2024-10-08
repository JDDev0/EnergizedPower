package me.jddev0.ep.block;

import me.jddev0.ep.block.entity.FluidPipeBlockEntity;
import me.jddev0.ep.util.FluidUtils;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.network.packet.s2c.play.OverlayMessageS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import me.jddev0.ep.config.ModConfigs;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FluidPipeBlock extends BlockWithEntity implements Waterloggable, WrenchConfigurable {
    public static final EnumProperty<EPBlockStateProperties.PipeConnection> UP = EPBlockStateProperties.PIPE_CONNECTION_UP;
    public static final EnumProperty<EPBlockStateProperties.PipeConnection> DOWN = EPBlockStateProperties.PIPE_CONNECTION_DOWN;
    public static final EnumProperty<EPBlockStateProperties.PipeConnection> NORTH = EPBlockStateProperties.PIPE_CONNECTION_NORTH;
    public static final EnumProperty<EPBlockStateProperties.PipeConnection> SOUTH = EPBlockStateProperties.PIPE_CONNECTION_SOUTH;
    public static final EnumProperty<EPBlockStateProperties.PipeConnection> EAST = EPBlockStateProperties.PIPE_CONNECTION_EAST;
    public static final EnumProperty<EPBlockStateProperties.PipeConnection> WEST = EPBlockStateProperties.PIPE_CONNECTION_WEST;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    private static final VoxelShape SHAPE_CORE = Block.createCuboidShape(4.d, 4.d, 4.d, 12.d, 12.d, 12.d);
    private static final VoxelShape SHAPE_UP = Block.createCuboidShape(4.d, 12.d, 4.d, 12.d, 16.d, 12.d);
    private static final VoxelShape SHAPE_DOWN = Block.createCuboidShape(4.d, 0.d, 4.d, 12.d, 4.d, 12.d);
    private static final VoxelShape SHAPE_NORTH = Block.createCuboidShape(4.d, 4.d, 0.d, 12.d, 12.d, 4.d);
    private static final VoxelShape SHAPE_SOUTH = Block.createCuboidShape(4.d, 4.d, 12.d, 12.d, 12.d, 16.d);
    private static final VoxelShape SHAPE_EAST = Block.createCuboidShape(12.d, 4.d, 4.d, 16.d, 12.d, 12.d);
    private static final VoxelShape SHAPE_WEST = Block.createCuboidShape(0.d, 4.d, 4.d, 4.d, 12.d, 12.d);

    @NotNull
    public static EnumProperty<EPBlockStateProperties.PipeConnection> getPipeConnectionPropertyFromDirection(@NotNull Direction dir) {
        return switch(dir) {
            case UP -> UP;
            case DOWN -> DOWN;
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case EAST -> EAST;
            case WEST -> WEST;
        };
    }

    private final Tier tier;

    public static Block getBlockFromTier(Tier tier) {
        return switch(tier) {
            case IRON -> EPBlocks.IRON_FLUID_PIPE;
            case GOLDEN -> EPBlocks.GOLDEN_FLUID_PIPE;
        };
    }

    public FluidPipeBlock(Tier tier) {
        super(tier.getProperties());

        this.tier = tier;

        this.setDefaultState(this.getStateManager().getDefaultState().with(UP, EPBlockStateProperties.PipeConnection.NOT_CONNECTED).
                with(DOWN, EPBlockStateProperties.PipeConnection.NOT_CONNECTED).
                with(NORTH, EPBlockStateProperties.PipeConnection.NOT_CONNECTED).
                with(SOUTH, EPBlockStateProperties.PipeConnection.NOT_CONNECTED).
                with(EAST, EPBlockStateProperties.PipeConnection.NOT_CONNECTED).
                with(WEST, EPBlockStateProperties.PipeConnection.NOT_CONNECTED).
                with(WATERLOGGED, false));
    }

    public Tier getTier() {
        return tier;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos blockPos, BlockState state) {
        return new FluidPipeBlockEntity(blockPos, state, tier);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    @NotNull
    public ActionResult onUseWrench(ItemUsageContext useOnContext, Direction selectedFace, boolean nextPreviousValue) {
        World level = useOnContext.getWorld();
        BlockPos blockPos = useOnContext.getBlockPos();

        if(level.isClient() || !(level.getBlockEntity(blockPos) instanceof FluidPipeBlockEntity))
            return ActionResult.SUCCESS;

        BlockState state = level.getBlockState(blockPos);

        BlockPos testPos = blockPos.offset(selectedFace);

        PlayerEntity player = useOnContext.getPlayer();

        BlockEntity testBlockEntity = level.getBlockEntity(testPos);
        if(testBlockEntity == null || testBlockEntity instanceof FluidPipeBlockEntity) {
            //Connections to non-fluid blocks nor connections to another pipe can not be modified

            if(player instanceof ServerPlayerEntity serverPlayer) {
                serverPlayer.networkHandler.sendPacket(new OverlayMessageS2CPacket(
                        Text.translatable("tooltip.energizedpower.fluid_pipe.wrench_configuration.face_change_not_possible",
                                Text.translatable("tooltip.energizedpower.direction." + selectedFace.asString()).
                                        formatted(Formatting.WHITE)
                        ).formatted(Formatting.RED)
                ));
            }

            return ActionResult.SUCCESS;
        }

        Storage<FluidVariant> fluidStorage = FluidStorage.SIDED.find(level, testPos, selectedFace.getOpposite());
        if(fluidStorage == null) {
            if(player instanceof ServerPlayerEntity serverPlayer) {
                serverPlayer.networkHandler.sendPacket(new OverlayMessageS2CPacket(
                        Text.translatable("tooltip.energizedpower.fluid_pipe.wrench_configuration.face_change_not_possible",
                                Text.translatable("tooltip.energizedpower.direction." + selectedFace.asString()).
                                        formatted(Formatting.WHITE)
                        ).formatted(Formatting.RED)
                ));
            }

            return ActionResult.SUCCESS;
        }

        //If first has no next, no tanks are present
        if(!fluidStorage.iterator().hasNext()) {
            if(player instanceof ServerPlayerEntity serverPlayer) {
                serverPlayer.networkHandler.sendPacket(new OverlayMessageS2CPacket(
                        Text.translatable("tooltip.energizedpower.fluid_pipe.wrench_configuration.face_change_not_possible",
                                Text.translatable("tooltip.energizedpower.direction." + selectedFace.asString()).
                                        formatted(Formatting.WHITE)
                        ).formatted(Formatting.RED)
                ));
            }

            return ActionResult.SUCCESS;
        }

        EnumProperty<EPBlockStateProperties.PipeConnection> pipeConnectionProperty =
                FluidPipeBlock.getPipeConnectionPropertyFromDirection(selectedFace);

        int diff = player != null && player.isSneaking()?-1:1;

        EPBlockStateProperties.PipeConnection pipeConnection = state.get(pipeConnectionProperty);
        pipeConnection = EPBlockStateProperties.PipeConnection.values()[(pipeConnection.ordinal() + diff +
                EPBlockStateProperties.PipeConnection.values().length) %
                EPBlockStateProperties.PipeConnection.values().length];

        level.setBlockState(blockPos, state.with(pipeConnectionProperty, pipeConnection), 3);

        if(player instanceof ServerPlayerEntity serverPlayer) {
            serverPlayer.networkHandler.sendPacket(new OverlayMessageS2CPacket(
                    Text.translatable("tooltip.energizedpower.fluid_pipe.wrench_configuration.face_changed",
                            Text.translatable("tooltip.energizedpower.direction." + selectedFace.asString()).
                                    formatted(Formatting.WHITE),
                            Text.translatable(pipeConnection.getTranslationKey()).
                                    formatted(Formatting.WHITE, Formatting.BOLD)
                    ).formatted(Formatting.GREEN)
            ));
        }

        return ActionResult.SUCCESS;
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext blockPlaceContext) {
        World level = blockPlaceContext.getWorld();
        BlockPos selfPos = blockPlaceContext.getBlockPos();
        FluidState fluidState = level.getFluidState(selfPos);

        return getDefaultState().
                with(UP, shouldConnectTo(level, selfPos, getDefaultState(), Direction.UP)).
                with(DOWN, shouldConnectTo(level, selfPos, getDefaultState(), Direction.DOWN)).
                with(NORTH, shouldConnectTo(level, selfPos, getDefaultState(), Direction.NORTH)).
                with(SOUTH, shouldConnectTo(level, selfPos, getDefaultState(), Direction.SOUTH)).
                with(EAST, shouldConnectTo(level, selfPos, getDefaultState(), Direction.EAST)).
                with(WEST, shouldConnectTo(level, selfPos, getDefaultState(), Direction.WEST)).
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

        if(blockState.get(UP).isConnected())
            shape = VoxelShapes.union(shape, SHAPE_UP);

        if(blockState.get(DOWN).isConnected())
            shape = VoxelShapes.union(shape, SHAPE_DOWN);

        if(blockState.get(NORTH).isConnected())
            shape = VoxelShapes.union(shape, SHAPE_NORTH);

        if(blockState.get(SOUTH).isConnected())
            shape = VoxelShapes.union(shape, SHAPE_SOUTH);

        if(blockState.get(EAST).isConnected())
            shape = VoxelShapes.union(shape, SHAPE_EAST);

        if(blockState.get(WEST).isConnected())
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

        int dx = fromPos.getX() - selfPos.getX();
        int dy = fromPos.getY() - selfPos.getY();
        int dz = fromPos.getZ() - selfPos.getZ();
        Direction dir = Direction.fromVector(dx, dy, dz);

        BlockState newState;
        if(dir == null) {
            newState = getDefaultState().
                    with(UP, selfState.get(UP)).
                    with(DOWN, selfState.get(DOWN)).
                    with(NORTH, selfState.get(NORTH)).
                    with(SOUTH, selfState.get(SOUTH)).
                    with(EAST, selfState.get(EAST)).
                    with(WEST, selfState.get(WEST)).
                    with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
        }else {
            EnumProperty<EPBlockStateProperties.PipeConnection> pipeConnectionProperty = getPipeConnectionPropertyFromDirection(dir);

            newState = getDefaultState().
                    with(UP, selfState.get(UP)).
                    with(DOWN, selfState.get(DOWN)).
                    with(NORTH, selfState.get(NORTH)).
                    with(SOUTH, selfState.get(SOUTH)).
                    with(EAST, selfState.get(EAST)).
                    with(WEST, selfState.get(WEST)).
                    with(pipeConnectionProperty, shouldConnectTo(level, selfPos, selfState, dir)).
                    with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
        }
        level.setBlockState(selfPos, newState);


        BlockEntity blockEntity = level.getBlockEntity(selfPos);
        if(blockEntity == null || !(blockEntity instanceof FluidPipeBlockEntity))
            return;

        FluidPipeBlockEntity.updateConnections(level, selfPos, newState, (FluidPipeBlockEntity)blockEntity);
    }

    private EPBlockStateProperties.PipeConnection shouldConnectTo(World level, BlockPos selfPos, BlockState selfState, Direction direction) {
        BlockPos toPos = selfPos.offset(direction);
        BlockEntity blockEntity = level.getBlockEntity(toPos);
        if(blockEntity == null)
            return EPBlockStateProperties.PipeConnection.NOT_CONNECTED;

        if(blockEntity instanceof FluidPipeBlockEntity fluidPipeBlockEntity && fluidPipeBlockEntity.getTier() != this.getTier())
            return EPBlockStateProperties.PipeConnection.NOT_CONNECTED;

        EPBlockStateProperties.PipeConnection currentConnectionState =
                selfState.get(getPipeConnectionPropertyFromDirection(direction));
        if(currentConnectionState == EPBlockStateProperties.PipeConnection.NOT_CONNECTED)
            currentConnectionState = EPBlockStateProperties.PipeConnection.CONNECTED;

        Storage<FluidVariant> fluidStorage = FluidStorage.SIDED.find(level, toPos, direction.getOpposite());
        return fluidStorage == null? EPBlockStateProperties.PipeConnection.NOT_CONNECTED:currentConnectionState;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World level, BlockState state, BlockEntityType<T> type) {
        return checkType(type, FluidPipeBlockEntity.getEntityTypeFromTier(tier), FluidPipeBlockEntity::tick);
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
                tooltip.add(Text.translatable("tooltip.energizedpower.wrench_configurable").
                        formatted(Formatting.GRAY, Formatting.ITALIC));

                tooltip.add(Text.translatable("tooltip.energizedpower.fluid_pipe.max_extraction",
                                FluidUtils.getFluidAmountWithPrefix(FluidUtils.convertDropletsToMilliBuckets(
                                        tier.getTransferRate()))).
                        formatted(Formatting.GRAY));
            }else {
                tooltip.add(Text.translatable("tooltip.energizedpower.shift_details.txt").formatted(Formatting.YELLOW));
            }
        }
    }

    public enum Tier {
        IRON("fluid_pipe", FluidUtils.convertMilliBucketsToDroplets(
                ModConfigs.COMMON_IRON_FLUID_PIPE_FLUID_TRANSFER_RATE.getValue()),
                FabricBlockSettings.of(Material.METAL).
                        requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)),
        GOLDEN("golden_fluid_pipe", FluidUtils.convertMilliBucketsToDroplets(
                ModConfigs.COMMON_GOLDEN_FLUID_PIPE_FLUID_TRANSFER_RATE.getValue()),
                FabricBlockSettings.of(Material.METAL).
                        requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL));

        private final String resourceId;
        private final long transferRate;
        private final FabricBlockSettings props;

        Tier(String resourceId, long transferRate, FabricBlockSettings props) {
            this.resourceId = resourceId;
            this.transferRate = transferRate;
            this.props = props;
        }

        public String getResourceId() {
            return resourceId;
        }

        public long getTransferRate() {
            return transferRate;
        }

        public FabricBlockSettings getProperties() {
            return props;
        }
    }
}
