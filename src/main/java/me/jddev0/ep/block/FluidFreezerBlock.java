package me.jddev0.ep.block;

import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.EPBlockEntities;
import me.jddev0.ep.block.entity.FluidFreezerBlockEntity;

public class FluidFreezerBlock extends HorizontallyOrientableWorkerMachineBlock<FluidFreezerBlockEntity> {
    protected FluidFreezerBlock(Properties props) {
        super(
                props,

                () -> EPBlockEntities.FLUID_FREEZER_ENTITY,
                FluidFreezerBlockEntity.class, FluidFreezerBlockEntity::new, FluidFreezerBlockEntity::tick
        );
    }
}
