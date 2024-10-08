package me.jddev0.ep.block;

import com.mojang.serialization.MapCodec;
import me.jddev0.ep.block.entity.EPBlockEntities;
import me.jddev0.ep.block.entity.AlloyFurnaceBlockEntity;
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
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.function.ToIntFunction;

public class AlloyFurnaceBlock extends BlockWithEntity {
    public static final MapCodec<AlloyFurnaceBlock> CODEC = createCodec(AlloyFurnaceBlock::new);

    public static final BooleanProperty LIT = Properties.LIT;
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    public static final ToIntFunction<BlockState> LIGHT_EMISSION =
            (state) -> state.get(LIT) ? 13 : 0;

    protected AlloyFurnaceBlock(Settings props) {
        super(props);

        this.setDefaultState(this.getStateManager().getDefaultState().with(FACING, Direction.NORTH).with(LIT, false));
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos blockPos, BlockState state) {
        return new AlloyFurnaceBlockEntity(blockPos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World level, BlockPos blockPos) {
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if(!(blockEntity instanceof AlloyFurnaceBlockEntity alloyFurnaceBlockEntity))
            return super.getComparatorOutput(state, level, blockPos);

        return alloyFurnaceBlockEntity.getRedstoneOutput();
    }

    @Override
    public void onStateReplaced(BlockState state, World level, BlockPos blockPos, BlockState newState, boolean isMoving) {
        if(state.getBlock() == newState.getBlock())
            return;

        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if(!(blockEntity instanceof AlloyFurnaceBlockEntity))
            return;

        ((AlloyFurnaceBlockEntity)blockEntity).drops(level, blockPos);

        super.onStateReplaced(state, level, blockPos, newState, isMoving);
    }

    @Override
    public ActionResult onUse(BlockState state, World level, BlockPos blockPos, PlayerEntity player, BlockHitResult hit) {
        if(level.isClient())
            return ActionResult.SUCCESS;

        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if(!(blockEntity instanceof AlloyFurnaceBlockEntity))
            throw new IllegalStateException("Container is invalid");

        player.openHandledScreen((AlloyFurnaceBlockEntity)blockEntity);

        return ActionResult.SUCCESS;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        return this.getDefaultState().with(FACING, context.getHorizontalPlayerFacing().getOpposite());
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
            double x = blockPos.getX() + .5;
            double y = blockPos.getY();
            double z = blockPos.getZ() + .5;

            if(randomSource.nextDouble() < .1)
                level.playSound(x, y, z, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.f, 1.f, false);

            Direction direction = state.get(FACING);
            double dxz = randomSource.nextDouble() * .6 - .3;

            double dx = direction.getAxis() == Direction.Axis.X?direction.getOffsetX() * .52:dxz;
            double dy = randomSource.nextDouble() * 6. / 16.;
            double dz = direction.getAxis() == Direction.Axis.Z?direction.getOffsetZ() * .52:dxz;

            level.addParticle(ParticleTypes.SMOKE, x + dx, y + dy, z + dz, 0., 0., 0.);
            level.addParticle(ParticleTypes.FLAME, x + dx, y + dy, z + dz, 0., 0., 0.);
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World level, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, EPBlockEntities.ALLOY_FURNACE_ENTITY, AlloyFurnaceBlockEntity::tick);
    }
}
