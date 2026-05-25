package me.jddev0.ep.block;

import com.mojang.serialization.MapCodec;
import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.EPBlockEntities;
import me.jddev0.ep.block.entity.FluidFreezerBlockEntity;
import net.minecraft.world.level.block.*;

public class FluidFreezerBlock extends HorizontallyOrientableWorkerMachineBlock<FluidFreezerBlockEntity> {
    public static final MapCodec<FluidFreezerBlock> CODEC = simpleCodec(FluidFreezerBlock::new);

    protected FluidFreezerBlock(Properties props) {
        super(
                props,

                () -> EPBlockEntities.FLUID_FREEZER_ENTITY,
                FluidFreezerBlockEntity.class, FluidFreezerBlockEntity::new, FluidFreezerBlockEntity::tick
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
