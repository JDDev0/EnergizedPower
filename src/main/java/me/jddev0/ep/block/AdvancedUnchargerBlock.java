package me.jddev0.ep.block;

import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.AdvancedUnchargerBlockEntity;
import me.jddev0.ep.block.entity.EPBlockEntities;

public class AdvancedUnchargerBlock extends HorizontallyOrientableWorkerMachineBlock<AdvancedUnchargerBlockEntity> {
    public AdvancedUnchargerBlock(Properties props) {
        super(
                props,

                EPBlockEntities.ADVANCED_UNCHARGER_ENTITY,
                AdvancedUnchargerBlockEntity.class, AdvancedUnchargerBlockEntity::new, AdvancedUnchargerBlockEntity::tick
        );
    }
}
