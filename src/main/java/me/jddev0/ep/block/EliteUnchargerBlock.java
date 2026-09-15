package me.jddev0.ep.block;

import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.EliteUnchargerBlockEntity;
import me.jddev0.ep.block.entity.EPBlockEntities;

public class EliteUnchargerBlock extends HorizontallyOrientableWorkerMachineBlock<EliteUnchargerBlockEntity> {
    public EliteUnchargerBlock(Properties props) {
        super(
                props,

                () -> EPBlockEntities.ELITE_UNCHARGER_ENTITY,
                EliteUnchargerBlockEntity.class, EliteUnchargerBlockEntity::new, EliteUnchargerBlockEntity::tick
        );
    }
}
