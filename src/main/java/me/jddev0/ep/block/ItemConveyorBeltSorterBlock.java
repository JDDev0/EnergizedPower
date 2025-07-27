package me.jddev0.ep.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.block.entity.ItemConveyorBeltSorterBlockEntity;
import me.jddev0.ep.machine.tier.ConveyorBeltTier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class ItemConveyorBeltSorterBlock extends BaseEntityBlock {
    public static final MapCodec<ItemConveyorBeltSorterBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(
                ExtraCodecs.NON_EMPTY_STRING.xmap(ConveyorBeltTier::valueOf, ConveyorBeltTier::toString).fieldOf("tier").
                        forGetter(ItemConveyorBeltSorterBlock::getTier),
                Properties.CODEC.fieldOf("properties").forGetter(Block::properties)
        ).apply(instance, ItemConveyorBeltSorterBlock::new);
    });

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

    private final ConveyorBeltTier tier;

    protected ItemConveyorBeltSorterBlock(ConveyorBeltTier tier, Properties props) {
        super(props);

        this.tier = tier;

        this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, false).setValue(FACING, Direction.NORTH));
    }

    public ConveyorBeltTier getTier() {
        return tier;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState state) {
        return new ItemConveyorBeltSorterBlockEntity(blockPos, state, tier);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public int getSignal(BlockState state, BlockGetter getter, BlockPos blockPos, Direction direction) {
        return state.getValue(POWERED)?15:0;
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos blockPos, Player player, BlockHitResult hit) {
        if(level.isClientSide())
            return InteractionResult.SUCCESS;

        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if(!(blockEntity instanceof ItemConveyorBeltSorterBlockEntity))
            throw new IllegalStateException("Container is invalid");

        player.openMenu((ItemConveyorBeltSorterBlockEntity)blockEntity, blockPos);

        return InteractionResult.SUCCESS;
    }

    @Override
    public void neighborChanged(BlockState selfState, Level level, BlockPos selfPos, Block fromBlock, @Nullable Orientation orientation, boolean isMoving) {
        super.neighborChanged(selfState, level, selfPos, fromBlock, orientation, isMoving);

        if(level.isClientSide())
            return;

        for(Direction dir:Direction.values()) {
            if(dir == Direction.UP || dir == Direction.DOWN || dir == selfState.getValue(FACING))
                continue;

            updateOutputBeltConnectionStateOfDirection(level, selfPos, selfState, dir);
        }
    }

    @Override
    public void onPlace(BlockState selfState, Level level, BlockPos selfPos, BlockState oldState, boolean isMoving) {
        if(level.isClientSide())
            return;

        Direction facing = selfState.getValue(FACING);

        for(int i = 0;i < 3;i++)
            updateOutputBeltConnectionStateOfDirection(level, selfPos, selfState, switch(i) {
                case 0 -> facing.getClockWise();
                case 1 -> facing.getOpposite();
                case 2 -> facing.getCounterClockWise();
                default -> null;
            });
    }

    private void updateOutputBeltConnectionStateOfDirection(Level level, BlockPos blockPos, BlockState state, Direction outputBeltDirection) {
        Direction facing = state.getValue(FACING);

        int index;
        if(outputBeltDirection == facing.getClockWise())
            index = 0;
        else if(outputBeltDirection == facing.getOpposite())
            index = 1;
        else if(outputBeltDirection == facing.getCounterClockWise())
            index = 2;
        else
            return;

        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if(!(blockEntity instanceof ItemConveyorBeltSorterBlockEntity itemConveyorBeltSorterBlockEntity))
            return;

        BlockState outputBeltState = level.getBlockState(blockPos.relative(outputBeltDirection));
        itemConveyorBeltSorterBlockEntity.setOutputBeltConnected(index, outputBeltState.getBlock() instanceof ItemConveyorBeltBlock);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
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
        stateBuilder.add(POWERED, FACING);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, tier.getItemConveyorBeltSorterBlockEntityFromTier(), ItemConveyorBeltSorterBlockEntity::tick);
    }
}
