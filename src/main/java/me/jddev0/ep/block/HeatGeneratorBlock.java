package me.jddev0.ep.block;

import com.mojang.serialization.MapCodec;
import me.jddev0.ep.block.base.WorkerMachineBlock;
import me.jddev0.ep.block.entity.HeatGeneratorBlockEntity;
import me.jddev0.ep.block.entity.EPBlockEntities;
import net.minecraft.world.level.block.*;

public class HeatGeneratorBlock extends WorkerMachineBlock<HeatGeneratorBlockEntity> {
    public static final MapCodec<HeatGeneratorBlock> CODEC = simpleCodec(HeatGeneratorBlock::new);

    public HeatGeneratorBlock(Properties props) {
        super(
                props,

                () -> EPBlockEntities.HEAT_GENERATOR_ENTITY,
                HeatGeneratorBlockEntity.class, HeatGeneratorBlockEntity::new, HeatGeneratorBlockEntity::tick
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
