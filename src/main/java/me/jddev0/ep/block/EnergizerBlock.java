package me.jddev0.ep.block;

import com.mojang.serialization.MapCodec;
import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.EnergizerBlockEntity;
import me.jddev0.ep.block.entity.EPBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.ToIntFunction;

public class EnergizerBlock extends HorizontallyOrientableWorkerMachineBlock<EnergizerBlockEntity> {
    public static final MapCodec<EnergizerBlock> CODEC = simpleCodec(EnergizerBlock::new);

    public static final ToIntFunction<BlockState> LIGHT_EMISSION =
            (state) -> state.getValue(WORKING) ? 8 : 0;

    protected EnergizerBlock(Properties props) {
        super(
                props,

                EPBlockEntities.ENERGIZER_ENTITY,
                EnergizerBlockEntity.class, EnergizerBlockEntity::new, EnergizerBlockEntity::tick
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
}
