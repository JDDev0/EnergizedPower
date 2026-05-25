package me.jddev0.ep.block;

import com.mojang.serialization.MapCodec;
import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.CoalEngineBlockEntity;
import me.jddev0.ep.block.entity.EPBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.ToIntFunction;

public class CoalEngineBlock extends HorizontallyOrientableWorkerMachineBlock<CoalEngineBlockEntity> {
    public static final MapCodec<CoalEngineBlock> CODEC = simpleCodec(CoalEngineBlock::new);

    public static final ToIntFunction<BlockState> LIGHT_EMISSION =
            (state) -> state.getValue(WORKING) ? 13 : 0;

    protected CoalEngineBlock(Properties props) {
        super(
                props,

                () -> EPBlockEntities.COAL_ENGINE_ENTITY,
                CoalEngineBlockEntity.class, CoalEngineBlockEntity::new, CoalEngineBlockEntity::tick
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos blockPos, RandomSource randomSource) {
        if(state.getValue(WORKING)) {
            double x = blockPos.getX() + .5;
            double y = blockPos.getY();
            double z = blockPos.getZ() + .5;

            if(randomSource.nextDouble() < .1)
                level.playLocalSound(x, y, z, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.f, 1.f, false);

            Direction direction = state.getValue(FACING);
            double dxz = randomSource.nextDouble() * .6 - .3;

            double dx = direction.getAxis() == Direction.Axis.X?direction.getStepX() * .52:dxz;
            double dy = randomSource.nextDouble() * 6. / 16.;
            double dz = direction.getAxis() == Direction.Axis.Z?direction.getStepZ() * .52:dxz;

            level.addParticle(ParticleTypes.SMOKE, x + dx, y + dy, z + dz, 0., 0., 0.);
            level.addParticle(ParticleTypes.FLAME, x + dx, y + dy, z + dz, 0., 0., 0.);
        }
    }
}
