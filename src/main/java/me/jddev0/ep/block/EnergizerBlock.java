package me.jddev0.ep.block;

import me.jddev0.ep.block.entity.EnergizerBlockEntity;
import me.jddev0.ep.block.entity.EPBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.function.ToIntFunction;

public class EnergizerBlock extends BaseEntityBlock {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public static final ToIntFunction<BlockState> LIGHT_EMISSION =
            (state) -> state.getValue(LIT) ? 8 : 0;

    protected EnergizerBlock(Properties props) {
        super(props);

        this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, false).setValue(FACING, Direction.NORTH).setValue(LIT, false));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState state) {
        return new EnergizerBlockEntity(blockPos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos blockPos) {
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if(!(blockEntity instanceof EnergizerBlockEntity energizerBlockEntity))
            return super.getAnalogOutputSignal(state, level, blockPos);

        return energizerBlockEntity.getRedstoneOutput();
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos blockPos, BlockState newState, boolean isMoving) {
        if(state.getBlock() == newState.getBlock())
            return;

        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if(!(blockEntity instanceof EnergizerBlockEntity))
            return;

        ((EnergizerBlockEntity)blockEntity).drops(level, blockPos);

        super.onRemove(state, level, blockPos, newState, isMoving);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos blockPos, Player player, InteractionHand handItem, BlockHitResult hit) {
        if(level.isClientSide())
            return InteractionResult.sidedSuccess(level.isClientSide());

        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if(!(blockEntity instanceof EnergizerBlockEntity))
            throw new IllegalStateException("Container is invalid");

        ((ServerPlayer)player).openMenu((EnergizerBlockEntity)blockEntity, blockPos);

        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public void neighborChanged(BlockState selfState, Level level, BlockPos selfPos, Block fromBlock, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(selfState, level, selfPos, fromBlock, fromPos, isMoving);

        if(level.isClientSide())
            return;

        boolean isPowered = level.hasNeighborSignal(selfPos);
        if(isPowered != selfState.getValue(POWERED))
            level.setBlock(selfPos, selfState.setValue(POWERED, isPowered), 3);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()).
                setValue(POWERED, context.getLevel().hasNeighborSignal(context.getClickedPos()));
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
        stateBuilder.add(POWERED, FACING, LIT);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos blockPos, RandomSource randomSource) {
        if(state.getValue(LIT)) {
            double x = blockPos.getX() + .5;
            double y = blockPos.getY() + .4;
            double z = blockPos.getZ() + .5;

            Direction facing = state.getValue(FACING);
            for(Direction direction:Direction.values()) {
                if(direction.getAxis() == Direction.Axis.Y)
                    continue;

                for(int i = 0;i < (direction == facing?2:1);i++) {
                    double dxz = randomSource.nextDouble() * .6 - .3;

                    double dx = direction.getAxis() == Direction.Axis.X?direction.getStepX() * .52:dxz;
                    double dy = randomSource.nextDouble() * 6. / 16.;
                    double dz = direction.getAxis() == Direction.Axis.Z?direction.getStepZ() * .52:dxz;

                    level.addParticle(ParticleTypes.ELECTRIC_SPARK, x + dx, y + dy, z + dz, 0., 0., 0.);
                }
            }
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, EPBlockEntities.ENERGIZER_ENTITY.get(), EnergizerBlockEntity::tick);
    }
}
