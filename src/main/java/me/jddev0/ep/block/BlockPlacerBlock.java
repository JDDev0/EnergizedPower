package me.jddev0.ep.block;

import me.jddev0.ep.block.base.FullyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.BlockPlacerBlockEntity;
import me.jddev0.ep.block.entity.EPBlockEntities;

public class BlockPlacerBlock extends FullyOrientableWorkerMachineBlock<BlockPlacerBlockEntity> {
    protected BlockPlacerBlock(Properties props) {
        super(
                props,

                () -> EPBlockEntities.BLOCK_PLACER_ENTITY,
                BlockPlacerBlockEntity.class, BlockPlacerBlockEntity::new, BlockPlacerBlockEntity::tick
        );
    }
}
