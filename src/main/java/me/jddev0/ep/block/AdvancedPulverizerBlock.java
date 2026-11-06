package me.jddev0.ep.block;

import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.AdvancedPulverizerBlockEntity;
import me.jddev0.ep.block.entity.EPBlockEntities;

public class AdvancedPulverizerBlock extends HorizontallyOrientableWorkerMachineBlock<AdvancedPulverizerBlockEntity> {
    public AdvancedPulverizerBlock(Properties props) {
        super(
                props,

                EPBlockEntities.ADVANCED_PULVERIZER_ENTITY,
                AdvancedPulverizerBlockEntity.class, AdvancedPulverizerBlockEntity::new, AdvancedPulverizerBlockEntity::tick
        );
    }
}
