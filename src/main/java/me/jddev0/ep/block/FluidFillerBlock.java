package me.jddev0.ep.block;

import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.FluidFillerBlockEntity;
import me.jddev0.ep.block.entity.EPBlockEntities;

public class FluidFillerBlock extends HorizontallyOrientableWorkerMachineBlock<FluidFillerBlockEntity> {
    public FluidFillerBlock(Properties props) {
        super(
                props,

                EPBlockEntities.FLUID_FILLER_ENTITY,
                FluidFillerBlockEntity.class, FluidFillerBlockEntity::new, FluidFillerBlockEntity::tick
        );
    }
}
