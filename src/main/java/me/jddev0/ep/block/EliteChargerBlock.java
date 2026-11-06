package me.jddev0.ep.block;

import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.EliteChargerBlockEntity;
import me.jddev0.ep.block.entity.EPBlockEntities;

public class EliteChargerBlock extends HorizontallyOrientableWorkerMachineBlock<EliteChargerBlockEntity> {
    public EliteChargerBlock(Properties props) {
        super(
                props,

                EPBlockEntities.ELITE_CHARGER_ENTITY,
                EliteChargerBlockEntity.class, EliteChargerBlockEntity::new, EliteChargerBlockEntity::tick
        );
    }
}
