package me.jddev0.ep.block;

import com.mojang.serialization.MapCodec;
import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.EliteChargerBlockEntity;
import me.jddev0.ep.block.entity.EPBlockEntities;
import net.minecraft.world.level.block.BaseEntityBlock;

public class EliteChargerBlock extends HorizontallyOrientableWorkerMachineBlock<EliteChargerBlockEntity> {
    public static final MapCodec<EliteChargerBlock> CODEC = simpleCodec(EliteChargerBlock::new);

    public EliteChargerBlock(Properties props) {
        super(
                props,

                EPBlockEntities.ELITE_CHARGER_ENTITY,
                EliteChargerBlockEntity.class, EliteChargerBlockEntity::new, EliteChargerBlockEntity::tick
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
