package me.jddev0.ep.block;

import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.EPBlockEntities;
import me.jddev0.ep.block.entity.SawmillBlockEntity;

public class SawmillBlock extends HorizontallyOrientableWorkerMachineBlock<SawmillBlockEntity> {
    public SawmillBlock(Properties props) {
        super(
                props,

                EPBlockEntities.SAWMILL_ENTITY,
                SawmillBlockEntity.class, SawmillBlockEntity::new, SawmillBlockEntity::tick
        );
    }
}
