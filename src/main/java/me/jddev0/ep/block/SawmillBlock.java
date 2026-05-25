package me.jddev0.ep.block;

import com.mojang.serialization.MapCodec;
import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.EPBlockEntities;
import me.jddev0.ep.block.entity.SawmillBlockEntity;
import net.minecraft.world.level.block.BaseEntityBlock;

public class SawmillBlock extends HorizontallyOrientableWorkerMachineBlock<SawmillBlockEntity> {
    public static final MapCodec<SawmillBlock> CODEC = simpleCodec(SawmillBlock::new);

    public SawmillBlock(Properties props) {
        super(
                props,

                () -> EPBlockEntities.SAWMILL_ENTITY,
                SawmillBlockEntity.class, SawmillBlockEntity::new, SawmillBlockEntity::tick
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
