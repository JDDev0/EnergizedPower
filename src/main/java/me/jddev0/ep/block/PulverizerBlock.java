package me.jddev0.ep.block;

import com.mojang.serialization.MapCodec;
import me.jddev0.ep.block.base.WorkerMachineBlock;
import me.jddev0.ep.block.entity.EPBlockEntities;
import me.jddev0.ep.block.entity.PulverizerBlockEntity;
import net.minecraft.world.level.block.BaseEntityBlock;

public class PulverizerBlock extends WorkerMachineBlock<PulverizerBlockEntity> {
    public static final MapCodec<PulverizerBlock> CODEC = simpleCodec(PulverizerBlock::new);

    public PulverizerBlock(Properties props) {
        super(
                props,

                () -> EPBlockEntities.PULVERIZER_ENTITY,
                PulverizerBlockEntity.class, PulverizerBlockEntity::new, PulverizerBlockEntity::tick
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
