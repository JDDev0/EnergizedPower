package me.jddev0.ep.block;

import com.mojang.serialization.MapCodec;
import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.EPBlockEntities;
import me.jddev0.ep.block.entity.ThermalGeneratorBlockEntity;
import net.minecraft.world.level.block.*;

public class ThermalGeneratorBlock extends HorizontallyOrientableWorkerMachineBlock<ThermalGeneratorBlockEntity> {
    public static final MapCodec<ThermalGeneratorBlock> CODEC = simpleCodec(ThermalGeneratorBlock::new);

    protected ThermalGeneratorBlock(Properties props) {
        super(
                props,

                EPBlockEntities.THERMAL_GENERATOR_ENTITY,
                ThermalGeneratorBlockEntity.class, ThermalGeneratorBlockEntity::new, ThermalGeneratorBlockEntity::tick
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
