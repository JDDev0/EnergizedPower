package me.jddev0.ep.block;

import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.CompressorBlockEntity;
import me.jddev0.ep.block.entity.EPBlockEntities;

public class CompressorBlock extends HorizontallyOrientableWorkerMachineBlock<CompressorBlockEntity> {
    public CompressorBlock(Properties props) {
        super(
                props,

                () -> EPBlockEntities.COMPRESSOR_ENTITY,
                CompressorBlockEntity.class, CompressorBlockEntity::new, CompressorBlockEntity::tick
        );
    }
}
