package me.jddev0.ep.block;

import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.MetalPressBlockEntity;
import me.jddev0.ep.block.entity.EPBlockEntities;

public class MetalPressBlock extends HorizontallyOrientableWorkerMachineBlock<MetalPressBlockEntity> {
    public MetalPressBlock(Properties props) {
        super(
                props,

                () -> EPBlockEntities.METAL_PRESS_ENTITY,
                MetalPressBlockEntity.class, MetalPressBlockEntity::new, MetalPressBlockEntity::tick
        );
    }
}
