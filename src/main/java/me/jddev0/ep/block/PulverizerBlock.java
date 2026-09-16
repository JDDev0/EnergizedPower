package me.jddev0.ep.block;

import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.EPBlockEntities;
import me.jddev0.ep.block.entity.PulverizerBlockEntity;

public class PulverizerBlock extends HorizontallyOrientableWorkerMachineBlock<PulverizerBlockEntity> {
    public PulverizerBlock(Properties props) {
        super(
                props,

                EPBlockEntities.PULVERIZER_ENTITY,
                PulverizerBlockEntity.class, PulverizerBlockEntity::new, PulverizerBlockEntity::tick
        );
    }
}
