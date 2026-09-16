package me.jddev0.ep.block;

import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.UnchargerBlockEntity;
import me.jddev0.ep.block.entity.EPBlockEntities;

public class UnchargerBlock extends HorizontallyOrientableWorkerMachineBlock<UnchargerBlockEntity> {
    public UnchargerBlock(Properties props) {
        super(
                props,

                EPBlockEntities.UNCHARGER_ENTITY,
                UnchargerBlockEntity.class, UnchargerBlockEntity::new, UnchargerBlockEntity::tick
        );
    }
}
