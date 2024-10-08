package me.jddev0.ep.block;

import me.jddev0.ep.block.entity.FluidPipeBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.util.FluidUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
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

public class FluidPipeBlock extends BaseEntityBlock implements SimpleWaterloggedBlock, WrenchConfigurable {
    public static final EnumProperty<EPBlockStateProperties.PipeConnection> UP = EPBlockStateProperties.PIPE_CONNECTION_UP;
    public static final EnumProperty<EPBlockStateProperties.PipeConnection> DOWN = EPBlockStateProperties.PIPE_CONNECTION_DOWN;
    public static final EnumProperty<EPBlockStateProperties.PipeConnection> NORTH = EPBlockStateProperties.PIPE_CONNECTION_NORTH;
    public static final EnumProperty<EPBlockStateProperties.PipeConnection> SOUTH = EPBlockStateProperties.PIPE_CONNECTION_SOUTH;
    public static final EnumProperty<EPBlockStateProperties.PipeConnection> EAST = EPBlockStateProperties.PIPE_CONNECTION_EAST;
    public static final EnumProperty<EPBlockStateProperties.PipeConnection> WEST = EPBlockStateProperties.PIPE_CONNECTION_WEST;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private static final VoxelShape SHAPE_CORE = Block.box(4.d, 4.d, 4.d, 12.d, 12.d, 12.d);
    private static final VoxelShape SHAPE_UP = Block.box(4.d, 12.d, 4.d, 12.d, 16.d, 12.d);
    private static final VoxelShape SHAPE_DOWN = Block.box(4.d, 0.d, 4.d, 12.d, 4.d, 12.d);
    private static final VoxelShape SHAPE_NORTH = Block.box(4.d, 4.d, 0.d, 12.d, 12.d, 4.d);
    private static final VoxelShape SHAPE_SOUTH = Block.box(4.d, 4.d, 12.d, 12.d, 12.d, 16.d);
    private static final VoxelShape SHAPE_EAST = Block.box(12.d, 4.d, 4.d, 16.d, 12.d, 12.d);
    private static final VoxelShape SHAPE_WEST = Block.box(0.d, 4.d, 4.d, 4.d, 12.d, 12.d);

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
            case IRON -> EPBlocks.IRON_FLUID_PIPE.get();
            case GOLDEN -> EPBlocks.GOLDEN_FLUID_PIPE.get();
        };
    }

    public FluidPipeBlock(Tier tier) {
        super(tier.getProperties());

        this.tier = tier;

        this.registerDefaultState(this.stateDefinition.any().setValue(UP, EPBlockStateProperties.PipeConnection.NOT_CONNECTED).
                setValue(DOWN, EPBlockStateProperties.PipeConnection.NOT_CONNECTED).
                setValue(NORTH, EPBlockStateProperties.PipeConnection.NOT_CONNECTED).
                setValue(SOUTH, EPBlockStateProperties.PipeConnection.NOT_CONNECTED).
                setValue(EAST, EPBlockStateProperties.PipeConnection.NOT_CONNECTED).
                setValue(WEST, EPBlockStateProperties.PipeConnection.NOT_CONNECTED).
                setValue(WATERLOGGED, false));
    }

    public Tier getTier() {
        return tier;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState state) {
        return new FluidPipeBlockEntity(blockPos, state, tier);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    @NotNull
    public InteractionResult onUseWrench(UseOnContext useOnContext, Direction selectedFace, boolean nextPreviousValue) {
        Level level = useOnContext.getLevel();
        BlockPos blockPos = useOnContext.getClickedPos();

        if(level.isClientSide || !(level.getBlockEntity(blockPos) instanceof FluidPipeBlockEntity))
            return InteractionResult.SUCCESS;

        BlockState state = level.getBlockState(blockPos);

        BlockPos testPos = blockPos.relative(selectedFace);

        Player player = useOnContext.getPlayer();

        BlockEntity testBlockEntity = level.getBlockEntity(testPos);
        if(testBlockEntity == null || testBlockEntity instanceof FluidPipeBlockEntity) {
            //Connections to non-fluid blocks nor connections to another pipe can not be modified

            if(player instanceof ServerPlayer serverPlayer) {
                serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(
                        Component.translatable("tooltip.energizedpower.fluid_pipe.wrench_configuration.face_change_not_possible",
                                Component.translatable("tooltip.energizedpower.direction." + selectedFace.getSerializedName()).
                                        withStyle(ChatFormatting.WHITE)
                        ).withStyle(ChatFormatting.RED)
                ));
            }

            return InteractionResult.SUCCESS;
        }

        LazyOptional<IFluidHandler> fluidStorageLazyOptional = testBlockEntity.getCapability(ForgeCapabilities.FLUID_HANDLER, selectedFace.getOpposite());
        if(!fluidStorageLazyOptional.isPresent()) {
            if(player instanceof ServerPlayer serverPlayer) {
                serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(
                        Component.translatable("tooltip.energizedpower.fluid_pipe.wrench_configuration.face_change_not_possible",
                                Component.translatable("tooltip.energizedpower.direction." + selectedFace.getSerializedName()).
                                        withStyle(ChatFormatting.WHITE)
                        ).withStyle(ChatFormatting.RED)
                ));
            }

            return InteractionResult.SUCCESS;
        }

        IFluidHandler fluidStorage = fluidStorageLazyOptional.orElse(null);
        if(fluidStorage.getTanks() == 0) {
            if(player instanceof ServerPlayer serverPlayer) {
                serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(
                        Component.translatable("tooltip.energizedpower.fluid_pipe.wrench_configuration.face_change_not_possible",
                                Component.translatable("tooltip.energizedpower.direction." + selectedFace.getSerializedName()).
                                        withStyle(ChatFormatting.WHITE)
                        ).withStyle(ChatFormatting.RED)
                ));
            }

            return InteractionResult.SUCCESS;
        }

        EnumProperty<EPBlockStateProperties.PipeConnection> pipeConnectionProperty =
                FluidPipeBlock.getPipeConnectionPropertyFromDirection(selectedFace);

        int diff = nextPreviousValue?-1:1;

        EPBlockStateProperties.PipeConnection pipeConnection = state.getValue(pipeConnectionProperty);
        pipeConnection = EPBlockStateProperties.PipeConnection.values()[(pipeConnection.ordinal() + diff +
                EPBlockStateProperties.PipeConnection.values().length) %
                EPBlockStateProperties.PipeConnection.values().length];

        level.setBlock(blockPos, state.setValue(pipeConnectionProperty, pipeConnection), 3);

        if(player instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(
                    Component.translatable("tooltip.energizedpower.fluid_pipe.wrench_configuration.face_changed",
                            Component.translatable("tooltip.energizedpower.direction." + selectedFace.getSerializedName()).
                                    withStyle(ChatFormatting.WHITE),
                            Component.translatable(pipeConnection.getTranslationKey()).
                                    withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD)
                    ).withStyle(ChatFormatting.GREEN)
            ));
        }

        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        Level level = blockPlaceContext.getLevel();
        BlockPos selfPos = blockPlaceContext.getClickedPos();
        FluidState fluidState = level.getFluidState(selfPos);

        return defaultBlockState().
                setValue(UP, shouldConnectTo(level, selfPos, defaultBlockState(), Direction.UP)).
                setValue(DOWN, shouldConnectTo(level, selfPos, defaultBlockState(), Direction.DOWN)).
                setValue(NORTH, shouldConnectTo(level, selfPos, defaultBlockState(), Direction.NORTH)).
                setValue(SOUTH, shouldConnectTo(level, selfPos, defaultBlockState(), Direction.SOUTH)).
                setValue(EAST, shouldConnectTo(level, selfPos, defaultBlockState(), Direction.EAST)).
                setValue(WEST, shouldConnectTo(level, selfPos, defaultBlockState(), Direction.WEST)).
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
        Direction dir = Direction.fromDelta(dx, dy, dz);

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
            EnumProperty<EPBlockStateProperties.PipeConnection> pipeConnectionProperty = getPipeConnectionPropertyFromDirection(dir);

            newState = defaultBlockState().
                    setValue(UP, selfState.getValue(UP)).
                    setValue(DOWN, selfState.getValue(DOWN)).
                    setValue(NORTH, selfState.getValue(NORTH)).
                    setValue(SOUTH, selfState.getValue(SOUTH)).
                    setValue(EAST, selfState.getValue(EAST)).
                    setValue(WEST, selfState.getValue(WEST)).
                    setValue(pipeConnectionProperty, shouldConnectTo(level, selfPos, selfState, dir)).
                    setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
        }
        level.setBlockAndUpdate(selfPos, newState);


        BlockEntity blockEntity = level.getBlockEntity(selfPos);
        if(blockEntity == null || !(blockEntity instanceof FluidPipeBlockEntity))
            return;

        FluidPipeBlockEntity.updateConnections(level, selfPos, newState, (FluidPipeBlockEntity)blockEntity);
    }

    private EPBlockStateProperties.PipeConnection shouldConnectTo(Level level, BlockPos selfPos, BlockState selfState, Direction direction) {
        BlockPos toPos = selfPos.relative(direction);
        BlockEntity blockEntity = level.getBlockEntity(toPos);
        if(blockEntity == null)
            return EPBlockStateProperties.PipeConnection.NOT_CONNECTED;

        if(blockEntity instanceof FluidPipeBlockEntity fluidPipeBlockEntity && fluidPipeBlockEntity.getTier() != this.getTier())
            return EPBlockStateProperties.PipeConnection.NOT_CONNECTED;

        EPBlockStateProperties.PipeConnection currentConnectionState =
                selfState.getValue(getPipeConnectionPropertyFromDirection(direction));
        if(currentConnectionState == EPBlockStateProperties.PipeConnection.NOT_CONNECTED)
            currentConnectionState = EPBlockStateProperties.PipeConnection.CONNECTED;

        LazyOptional<IFluidHandler> fluidStorageLazyOptional = blockEntity.getCapability(ForgeCapabilities.FLUID_HANDLER, direction.getOpposite());
        return fluidStorageLazyOptional.isPresent()?currentConnectionState: EPBlockStateProperties.PipeConnection.NOT_CONNECTED;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, FluidPipeBlockEntity.getEntityTypeFromTier(tier), FluidPipeBlockEntity::tick);
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
                components.add(Component.translatable("tooltip.energizedpower.wrench_configurable").
                        withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));

                components.add(Component.translatable("tooltip.energizedpower.fluid_pipe.max_extraction",
                                FluidUtils.getFluidAmountWithPrefix(tier.getTransferRate())).
                        withStyle(ChatFormatting.GRAY));
            }else {
                components.add(Component.translatable("tooltip.energizedpower.shift_details.txt").withStyle(ChatFormatting.YELLOW));
            }
        }
    }

    public enum Tier {
        IRON("fluid_pipe", ModConfigs.COMMON_IRON_FLUID_PIPE_FLUID_TRANSFER_RATE.getValue(),
                BlockBehaviour.Properties.of().
                        requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)),
        GOLDEN("golden_fluid_pipe", ModConfigs.COMMON_GOLDEN_FLUID_PIPE_FLUID_TRANSFER_RATE.getValue(),
                Properties.of().
                        requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL));

        private final String resourceId;
        private final int transferRate;
        private final Properties props;

        Tier(String resourceId, int transferRate, Properties props) {
            this.resourceId = resourceId;
            this.transferRate = transferRate;
            this.props = props;
        }

        public String getResourceId() {
            return resourceId;
        }

        public int getTransferRate() {
            return transferRate;
        }

        public Properties getProperties() {
            return props;
        }
    }
}
