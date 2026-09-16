package me.jddev0.ep.block;

import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.AutoStonecutterBlockEntity;
import me.jddev0.ep.block.entity.EPBlockEntities;

public class AutoStonecutterBlock extends HorizontallyOrientableWorkerMachineBlock<AutoStonecutterBlockEntity> {
    public AutoStonecutterBlock(Properties props) {
        super(
                props,

                EPBlockEntities.AUTO_STONECUTTER_ENTITY,
                AutoStonecutterBlockEntity.class, AutoStonecutterBlockEntity::new, AutoStonecutterBlockEntity::tick
        );
    }
}
