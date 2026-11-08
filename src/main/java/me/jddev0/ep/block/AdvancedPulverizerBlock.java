package me.jddev0.ep.block;

import com.mojang.serialization.MapCodec;
import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.AdvancedPulverizerBlockEntity;
import me.jddev0.ep.block.entity.EPBlockEntities;
import net.minecraft.world.level.block.BaseEntityBlock;

public class AdvancedPulverizerBlock extends HorizontallyOrientableWorkerMachineBlock<AdvancedPulverizerBlockEntity> {
    public static final MapCodec<AdvancedPulverizerBlock> CODEC = simpleCodec(AdvancedPulverizerBlock::new);

    public AdvancedPulverizerBlock(Properties props) {
        super(
                props,

                () -> EPBlockEntities.ADVANCED_PULVERIZER_ENTITY,
                AdvancedPulverizerBlockEntity.class, AdvancedPulverizerBlockEntity::new, AdvancedPulverizerBlockEntity::tick
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
