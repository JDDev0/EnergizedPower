package me.jddev0.ep.fluid;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public abstract class DirtyWaterFluid extends FlowableFluid {
    @Override
    public Fluid getStill() {
        return EPFluids.DIRTY_WATER;
    }

    @Override
    public Fluid getFlowing() {
        return EPFluids.FLOWING_DIRTY_WATER;
    }

    @Override
    public Item getBucketItem() {
        return EPFluids.DIRTY_WATER_BUCKET_ITEM;
    }

    @Nullable
    @Override
    protected ParticleEffect getParticle() {
        return ParticleTypes.DRIPPING_WATER;
    }

    @Override
    protected boolean isInfinite(ServerWorld world) {
        return false;
    }

    protected void beforeBreakingBlock(WorldAccess world, BlockPos pos, BlockState state) {
        BlockEntity blockEntity = state.hasBlockEntity()?world.getBlockEntity(pos):null;
        Block.dropStacks(state, world, pos, blockEntity);
    }

    public int getMaxFlowDistance(WorldView world) {
        return 4;
    }

    @Override
    protected BlockState toBlockState(FluidState state) {
        return EPFluids.DIRTY_WATER_BLOCK.getDefaultState().with(FluidBlock.LEVEL, getBlockStateLevel(state));
    }

    @Override
    public boolean matchesType(Fluid fluid) {
        return fluid == getStill() || fluid == getFlowing();
    }

    @Override
    protected int getLevelDecreasePerBlock(WorldView world) {
        return 1;
    }

    @Override
    public int getTickRate(WorldView world) {
        return 5;
    }

    @Override
    protected boolean canBeReplacedWith(FluidState state, BlockView world, BlockPos pos, Fluid fluid, Direction direction) {
        return direction == Direction.DOWN && !this.matchesType(fluid);
    }

    @Override
    protected float getBlastResistance() {
        return 100.f;
    }

    public Optional<SoundEvent> getBucketFillSound() {
        return Optional.of(SoundEvents.ITEM_BUCKET_FILL);
    }

    public static class Flowing extends DirtyWaterFluid {
        public Flowing() {}

        protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder) {
            super.appendProperties(builder);

            builder.add(LEVEL);
        }

        public int getLevel(FluidState state) {
            return state.get(LEVEL);
        }

        public boolean isStill(FluidState state) {
            return false;
        }
    }

    public static class Source extends DirtyWaterFluid {
        public Source() {}

        public int getLevel(FluidState state) {
            return 8;
        }

        public boolean isStill(FluidState state) {
            return true;
        }
    }
}
