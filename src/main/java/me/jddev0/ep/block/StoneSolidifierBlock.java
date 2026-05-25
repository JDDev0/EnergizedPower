package me.jddev0.ep.block;

import com.mojang.serialization.MapCodec;
import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.EPBlockEntities;
import me.jddev0.ep.block.entity.StoneSolidifierBlockEntity;
import net.minecraft.world.level.block.*;

public class StoneSolidifierBlock extends HorizontallyOrientableWorkerMachineBlock<StoneSolidifierBlockEntity> {
    public static final MapCodec<StoneSolidifierBlock> CODEC = simpleCodec(StoneSolidifierBlock::new);

    protected StoneSolidifierBlock(Properties props) {
        super(
                props,

                () -> EPBlockEntities.STONE_SOLIDIFIER_ENTITY,
                StoneSolidifierBlockEntity.class, StoneSolidifierBlockEntity::new, StoneSolidifierBlockEntity::tick
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
