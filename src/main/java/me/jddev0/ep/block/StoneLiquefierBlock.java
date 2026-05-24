package me.jddev0.ep.block;

import com.mojang.serialization.MapCodec;
import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.EPBlockEntities;
import me.jddev0.ep.block.entity.StoneLiquefierBlockEntity;
import net.minecraft.world.level.block.*;

public class StoneLiquefierBlock extends HorizontallyOrientableWorkerMachineBlock<StoneLiquefierBlockEntity> {
    public static final MapCodec<StoneLiquefierBlock> CODEC = simpleCodec(StoneLiquefierBlock::new);

    protected StoneLiquefierBlock(Properties props) {
        super(
                props,

                EPBlockEntities.STONE_LIQUEFIER_ENTITY,
                StoneLiquefierBlockEntity.class, StoneLiquefierBlockEntity::new, StoneLiquefierBlockEntity::tick
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
