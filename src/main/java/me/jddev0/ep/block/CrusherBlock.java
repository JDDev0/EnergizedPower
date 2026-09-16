package me.jddev0.ep.block;

import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.CrusherBlockEntity;
import me.jddev0.ep.block.entity.EPBlockEntities;

public class CrusherBlock extends HorizontallyOrientableWorkerMachineBlock<CrusherBlockEntity> {
    public CrusherBlock(Properties props) {
        super(
                props,

                EPBlockEntities.CRUSHER_ENTITY,
                CrusherBlockEntity.class, CrusherBlockEntity::new, CrusherBlockEntity::tick
        );
    }
}
