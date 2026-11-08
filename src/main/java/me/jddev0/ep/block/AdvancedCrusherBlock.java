package me.jddev0.ep.block;

import com.mojang.serialization.MapCodec;
import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.AdvancedCrusherBlockEntity;
import me.jddev0.ep.block.entity.EPBlockEntities;
import net.minecraft.world.level.block.BaseEntityBlock;

public class AdvancedCrusherBlock extends HorizontallyOrientableWorkerMachineBlock<AdvancedCrusherBlockEntity> {
    public static final MapCodec<AdvancedCrusherBlock> CODEC = simpleCodec(AdvancedCrusherBlock::new);

    public AdvancedCrusherBlock(Properties props) {
        super(
                props,

                () -> EPBlockEntities.ADVANCED_CRUSHER_ENTITY,
                AdvancedCrusherBlockEntity.class, AdvancedCrusherBlockEntity::new, AdvancedCrusherBlockEntity::tick
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
