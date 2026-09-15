package me.jddev0.ep.block;

import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.EPBlockEntities;
import me.jddev0.ep.block.entity.AdvancedFluidPumpBlockEntity;

public class AdvancedFluidPumpBlock extends HorizontallyOrientableWorkerMachineBlock<AdvancedFluidPumpBlockEntity> {
    public AdvancedFluidPumpBlock(Properties props) {
        super(
                props,

                () -> EPBlockEntities.ADVANCED_FLUID_PUMP_ENTITY,
                AdvancedFluidPumpBlockEntity.class, AdvancedFluidPumpBlockEntity::new, AdvancedFluidPumpBlockEntity::tick
        );
    }
}
