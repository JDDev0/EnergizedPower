package me.jddev0.ep.block;

import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.EPBlockEntities;
import me.jddev0.ep.block.entity.StoneLiquefierBlockEntity;

public class StoneLiquefierBlock extends HorizontallyOrientableWorkerMachineBlock<StoneLiquefierBlockEntity> {
    protected StoneLiquefierBlock(Properties props) {
        super(
                props,

                EPBlockEntities.STONE_LIQUEFIER_ENTITY,
                StoneLiquefierBlockEntity.class, StoneLiquefierBlockEntity::new, StoneLiquefierBlockEntity::tick
        );
    }
}
