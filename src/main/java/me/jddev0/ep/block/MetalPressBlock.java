package me.jddev0.ep.block;

import com.mojang.serialization.MapCodec;
import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.MetalPressBlockEntity;
import me.jddev0.ep.block.entity.EPBlockEntities;
import net.minecraft.world.level.block.BaseEntityBlock;

public class MetalPressBlock extends HorizontallyOrientableWorkerMachineBlock<MetalPressBlockEntity> {
    public static final MapCodec<MetalPressBlock> CODEC = simpleCodec(MetalPressBlock::new);

    public MetalPressBlock(Properties props) {
        super(
                props,

                EPBlockEntities.METAL_PRESS_ENTITY,
                MetalPressBlockEntity.class, MetalPressBlockEntity::new, MetalPressBlockEntity::tick
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
