package me.jddev0.ep.block;

import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.FluidDrainerBlockEntity;
import me.jddev0.ep.block.entity.EPBlockEntities;

public class FluidDrainerBlock extends HorizontallyOrientableWorkerMachineBlock<FluidDrainerBlockEntity> {
    public FluidDrainerBlock(Properties props) {
        super(
                props,

                EPBlockEntities.FLUID_DRAINER_ENTITY,
                FluidDrainerBlockEntity.class, FluidDrainerBlockEntity::new, FluidDrainerBlockEntity::tick
        );
    }
}
