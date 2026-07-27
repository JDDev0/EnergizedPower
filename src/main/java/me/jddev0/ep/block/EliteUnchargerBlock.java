package me.jddev0.ep.block;

import com.mojang.serialization.MapCodec;
import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.EliteUnchargerBlockEntity;
import me.jddev0.ep.block.entity.EPBlockEntities;
import net.minecraft.world.level.block.BaseEntityBlock;

public class EliteUnchargerBlock extends HorizontallyOrientableWorkerMachineBlock<EliteUnchargerBlockEntity> {
    public static final MapCodec<EliteUnchargerBlock> CODEC = simpleCodec(EliteUnchargerBlock::new);

    public EliteUnchargerBlock(Properties props) {
        super(
                props,

                EPBlockEntities.ELITE_UNCHARGER_ENTITY,
                EliteUnchargerBlockEntity.class, EliteUnchargerBlockEntity::new, EliteUnchargerBlockEntity::tick
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
