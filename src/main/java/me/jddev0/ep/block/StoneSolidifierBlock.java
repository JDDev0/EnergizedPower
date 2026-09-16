package me.jddev0.ep.block;

import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.EPBlockEntities;
import me.jddev0.ep.block.entity.StoneSolidifierBlockEntity;

public class StoneSolidifierBlock extends HorizontallyOrientableWorkerMachineBlock<StoneSolidifierBlockEntity> {
    protected StoneSolidifierBlock(Properties props) {
        super(
                props,

                EPBlockEntities.STONE_SOLIDIFIER_ENTITY,
                StoneSolidifierBlockEntity.class, StoneSolidifierBlockEntity::new, StoneSolidifierBlockEntity::tick
        );
    }
}
