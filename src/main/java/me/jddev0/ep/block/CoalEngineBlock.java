package me.jddev0.ep.block;

import me.jddev0.ep.block.entity.CoalEngineBlockEntity;
import me.jddev0.ep.block.entity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.function.ToIntFunction;

public class CoalEngineBlock extends BaseEntityBlock {
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public static final ToIntFunction<BlockState> LIGHT_EMISSION =
            (state) -> state.getValue(BlockStateProperties.LIT) ? 13 : 0;

    protected CoalEngineBlock(Properties props) {
        super(props);

        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(LIT, false));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState state) {
        return new CoalEngineBlockEntity(blockPos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos blockPos, BlockState newState, boolean isMoving) {
        if(state.getBlock() == newState.getBlock())
            return;

        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if(!(blockEntity instanceof CoalEngineBlockEntity))
            return;

        ((CoalEngineBlockEntity)blockEntity).drops(level, blockPos);

        super.onRemove(state, level, blockPos, newState, isMoving);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos blockPos, Player player, InteractionHand handItem, BlockHitResult hit) {
        if(level.isClientSide())
            return InteractionResult.sidedSuccess(level.isClientSide());

        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if(!(blockEntity instanceof CoalEngineBlockEntity))
            throw new IllegalStateException("Container is invalid");

        NetworkHooks.openScreen((ServerPlayer)player, (CoalEngineBlockEntity)blockEntity, blockPos);

        return InteractionResult.sidedSuccess(level.isClientSide());
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
        stateBuilder.add(FACING, LIT);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos blockPos, RandomSource randomSource) {
        if(state.getValue(LIT)) {
            double d0 = (double)blockPos.getX() + 0.5D;
            double d1 = (double)blockPos.getY();
            double d2 = (double)blockPos.getZ() + 0.5D;
            if (randomSource.nextDouble() < 0.1D) {
                level.playLocalSound(d0, d1, d2, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
            }

            Direction direction = state.getValue(FACING);
            Direction.Axis direction$axis = direction.getAxis();
            double d3 = 0.52D;
            double d4 = randomSource.nextDouble() * 0.6D - 0.3D;
            double d5 = direction$axis == Direction.Axis.X ? (double)direction.getStepX() * d3 : d4;
            double d6 = randomSource.nextDouble() * 6.0D / 16.0D;
            double d7 = direction$axis == Direction.Axis.Z ? (double)direction.getStepZ() * d3 : d4;
            level.addParticle(ParticleTypes.SMOKE, d0 + d5, d1 + d6, d2 + d7, 0.0D, 0.0D, 0.0D);
            level.addParticle(ParticleTypes.FLAME, d0 + d5, d1 + d6, d2 + d7, 0.0D, 0.0D, 0.0D);
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModBlockEntities.COAL_ENGINE_ENTITY.get(), CoalEngineBlockEntity::tick);
    }
}
