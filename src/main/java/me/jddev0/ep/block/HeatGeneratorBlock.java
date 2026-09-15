package me.jddev0.ep.block;

import me.jddev0.ep.block.base.WorkerMachineBlock;
import me.jddev0.ep.block.entity.HeatGeneratorBlockEntity;
import me.jddev0.ep.block.entity.EPBlockEntities;

public class HeatGeneratorBlock extends WorkerMachineBlock<HeatGeneratorBlockEntity> {
    public HeatGeneratorBlock(Properties props) {
        super(
                props,

                () -> EPBlockEntities.HEAT_GENERATOR_ENTITY,
                HeatGeneratorBlockEntity.class, HeatGeneratorBlockEntity::new, HeatGeneratorBlockEntity::tick
        );
    }
}
