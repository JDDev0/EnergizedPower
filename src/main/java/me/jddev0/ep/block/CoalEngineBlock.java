package me.jddev0.ep.block;

import me.jddev0.ep.block.entity.CoalEngineBlockEntity;
import me.jddev0.ep.block.entity.ModBlockEntities;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Random;
import java.util.function.ToIntFunction;

public class CoalEngineBlock extends BlockWithEntity {
    public static final BooleanProperty LIT = Properties.LIT;
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    public static final ToIntFunction<BlockState> LIGHT_EMISSION =
            (state) -> state.get(Properties.LIT) ? 13 : 0;

    protected CoalEngineBlock(FabricBlockSettings props) {
        super(props);

        this.setDefaultState(this.getStateManager().getDefaultState().with(FACING, Direction.NORTH).with(LIT, false));
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos blockPos, BlockState state) {
        return new CoalEngineBlockEntity(blockPos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void onStateReplaced(BlockState state, World level, BlockPos blockPos, BlockState newState, boolean isMoving) {
        if(state.getBlock() == newState.getBlock())
            return;

        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if(!(blockEntity instanceof CoalEngineBlockEntity))
            return;

        ((CoalEngineBlockEntity)blockEntity).drops(level, blockPos);

        super.onStateReplaced(state, level, blockPos, newState, isMoving);
    }

    @Override
    public ActionResult onUse(BlockState state, World level, BlockPos blockPos, PlayerEntity player, Hand handItem, BlockHitResult hit) {
        if(level.isClient())
            return ActionResult.SUCCESS;

        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if(!(blockEntity instanceof CoalEngineBlockEntity))
            throw new IllegalStateException("Container is invalid");

        player.openHandledScreen((CoalEngineBlockEntity)blockEntity);

        return ActionResult.SUCCESS;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        return this.getDefaultState().with(FACING, context.getPlayerFacing().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(FACING, LIT);
    }

    @Override
    public void randomDisplayTick(BlockState state, World level, BlockPos blockPos, Random randomSource) {
        if(state.get(LIT)) {
            double d0 = (double)blockPos.getX() + 0.5D;
            double d1 = (double)blockPos.getY();
            double d2 = (double)blockPos.getZ() + 0.5D;
            if (randomSource.nextDouble() < 0.1D) {
                level.playSound(d0, d1, d2, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
            }

            Direction direction = state.get(FACING);
            Direction.Axis direction$axis = direction.getAxis();
            double d3 = 0.52D;
            double d4 = randomSource.nextDouble() * 0.6D - 0.3D;
            double d5 = direction$axis == Direction.Axis.X ? (double)direction.getOffsetX() * d3 : d4;
            double d6 = randomSource.nextDouble() * 6.0D / 16.0D;
            double d7 = direction$axis == Direction.Axis.Z ? (double)direction.getOffsetZ() * d3 : d4;
            level.addParticle(ParticleTypes.SMOKE, d0 + d5, d1 + d6, d2 + d7, 0.0D, 0.0D, 0.0D);
            level.addParticle(ParticleTypes.FLAME, d0 + d5, d1 + d6, d2 + d7, 0.0D, 0.0D, 0.0D);
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World level, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.COAL_ENGINE_ENTITY, CoalEngineBlockEntity::tick);
    }
}
