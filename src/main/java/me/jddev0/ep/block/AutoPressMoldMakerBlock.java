package me.jddev0.ep.block;

import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.AutoPressMoldMakerBlockEntity;
import me.jddev0.ep.block.entity.EPBlockEntities;

public class AutoPressMoldMakerBlock extends HorizontallyOrientableWorkerMachineBlock<AutoPressMoldMakerBlockEntity> {
    public AutoPressMoldMakerBlock(Properties props) {
        super(
                props,

                () -> EPBlockEntities.AUTO_PRESS_MOLD_MAKER_ENTITY,
                AutoPressMoldMakerBlockEntity.class, AutoPressMoldMakerBlockEntity::new, AutoPressMoldMakerBlockEntity::tick
        );
    }
}
