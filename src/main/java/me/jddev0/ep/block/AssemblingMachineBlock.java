package me.jddev0.ep.block;

import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.AssemblingMachineBlockEntity;
import me.jddev0.ep.block.entity.EPBlockEntities;

public class AssemblingMachineBlock extends HorizontallyOrientableWorkerMachineBlock<AssemblingMachineBlockEntity> {
    protected AssemblingMachineBlock(Properties props) {
        super(
                props,

                EPBlockEntities.ASSEMBLING_MACHINE_ENTITY,
                AssemblingMachineBlockEntity.class, AssemblingMachineBlockEntity::new, AssemblingMachineBlockEntity::tick
        );
    }
}
