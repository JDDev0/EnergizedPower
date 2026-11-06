package me.jddev0.ep.block;

import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.ChargingStationBlockEntity;
import me.jddev0.ep.block.entity.EPBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.ToIntFunction;

public class ChargingStationBlock extends HorizontallyOrientableWorkerMachineBlock<ChargingStationBlockEntity> {
    public static final ToIntFunction<BlockState> LIGHT_EMISSION =
            (state) -> state.getValue(WORKING) ? 8 : 0;

    public ChargingStationBlock(Properties props) {
        super(
                props,

                EPBlockEntities.CHARGING_STATION_ENTITY,
                ChargingStationBlockEntity.class, ChargingStationBlockEntity::new, ChargingStationBlockEntity::tick
        );
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos blockPos, RandomSource randomSource) {
        if(state.getValue(WORKING)) {
            double x = blockPos.getX() + .5;
            double y = blockPos.getY() + .9;
            double z = blockPos.getZ() + .5;

            for(Direction direction:Direction.values()) {
                if(direction.getAxis() == Direction.Axis.Y)
                    continue;

                double dxz = randomSource.nextDouble() * .3 - .15;

                double dx = direction.getAxis() == Direction.Axis.X?direction.getStepX() * .35:dxz;
                double dy = randomSource.nextDouble() * .2;
                double dz = direction.getAxis() == Direction.Axis.Z?direction.getStepZ() * .35:dxz;

                level.addParticle(ParticleTypes.ELECTRIC_SPARK, x + dx, y + dy, z + dz, 0., 0., 0.);
            }
        }
    }
}
