package me.jddev0.ep.block;

import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.ChargerBlockEntity;
import me.jddev0.ep.block.entity.EPBlockEntities;

public class ChargerBlock extends HorizontallyOrientableWorkerMachineBlock<ChargerBlockEntity> {
    public ChargerBlock(Properties props) {
        super(
                props,

                () -> EPBlockEntities.CHARGER_ENTITY,
                ChargerBlockEntity.class, ChargerBlockEntity::new, ChargerBlockEntity::tick
        );
    }
}
