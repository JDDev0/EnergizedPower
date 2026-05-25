package me.jddev0.ep.block;

import com.mojang.serialization.MapCodec;
import me.jddev0.ep.block.base.FullyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.BlockPlacerBlockEntity;
import me.jddev0.ep.block.entity.EPBlockEntities;
import net.minecraft.world.level.block.*;

public class BlockPlacerBlock extends FullyOrientableWorkerMachineBlock<BlockPlacerBlockEntity> {
    public static final MapCodec<BlockPlacerBlock> CODEC = simpleCodec(BlockPlacerBlock::new);

    protected BlockPlacerBlock(Properties props) {
        super(
                props,

                () -> EPBlockEntities.BLOCK_PLACER_ENTITY,
                BlockPlacerBlockEntity.class, BlockPlacerBlockEntity::new, BlockPlacerBlockEntity::tick
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
