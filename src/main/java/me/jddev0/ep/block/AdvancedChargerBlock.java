package me.jddev0.ep.block;

import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.AdvancedChargerBlockEntity;
import me.jddev0.ep.block.entity.EPBlockEntities;

public class AdvancedChargerBlock extends HorizontallyOrientableWorkerMachineBlock<AdvancedChargerBlockEntity> {
    public AdvancedChargerBlock(Properties props) {
        super(
                props,

                () -> EPBlockEntities.ADVANCED_CHARGER_ENTITY,
                AdvancedChargerBlockEntity.class, AdvancedChargerBlockEntity::new, AdvancedChargerBlockEntity::tick
        );
    }
}
