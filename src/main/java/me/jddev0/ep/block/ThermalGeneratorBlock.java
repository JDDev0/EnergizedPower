package me.jddev0.ep.block;

import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.EPBlockEntities;
import me.jddev0.ep.block.entity.ThermalGeneratorBlockEntity;

public class ThermalGeneratorBlock extends HorizontallyOrientableWorkerMachineBlock<ThermalGeneratorBlockEntity> {
    protected ThermalGeneratorBlock(Properties props) {
        super(
                props,

                EPBlockEntities.THERMAL_GENERATOR_ENTITY,
                ThermalGeneratorBlockEntity.class, ThermalGeneratorBlockEntity::new, ThermalGeneratorBlockEntity::tick
        );
    }
}
