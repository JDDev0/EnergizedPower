package me.jddev0.ep.block;

import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.AdvancedCrusherBlockEntity;
import me.jddev0.ep.block.entity.EPBlockEntities;

public class AdvancedCrusherBlock extends HorizontallyOrientableWorkerMachineBlock<AdvancedCrusherBlockEntity> {
    public AdvancedCrusherBlock(Properties props) {
        super(
                props,

                EPBlockEntities.ADVANCED_CRUSHER_ENTITY,
                AdvancedCrusherBlockEntity.class, AdvancedCrusherBlockEntity::new, AdvancedCrusherBlockEntity::tick
        );
    }
}
