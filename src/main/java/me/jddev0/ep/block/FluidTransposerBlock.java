package me.jddev0.ep.block;

import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.EPBlockEntities;
import me.jddev0.ep.block.entity.FluidTransposerBlockEntity;

public class FluidTransposerBlock extends HorizontallyOrientableWorkerMachineBlock<FluidTransposerBlockEntity> {
    protected FluidTransposerBlock(Properties props) {
        super(
                props,

                EPBlockEntities.FLUID_TRANSPOSER_ENTITY,
                FluidTransposerBlockEntity.class, FluidTransposerBlockEntity::new, FluidTransposerBlockEntity::tick
        );
    }
}
