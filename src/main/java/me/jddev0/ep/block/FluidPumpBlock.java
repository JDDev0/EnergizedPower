package me.jddev0.ep.block;

import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.FluidPumpBlockEntity;
import me.jddev0.ep.block.entity.EPBlockEntities;

public class FluidPumpBlock extends HorizontallyOrientableWorkerMachineBlock<FluidPumpBlockEntity> {
    public FluidPumpBlock(Properties props) {
        super(
                props,

                EPBlockEntities.FLUID_PUMP_ENTITY,
                FluidPumpBlockEntity.class, FluidPumpBlockEntity::new, FluidPumpBlockEntity::tick
        );
    }
}
