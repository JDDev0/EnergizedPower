package me.jddev0.ep.block;

import com.mojang.serialization.MapCodec;
import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.EPBlockEntities;
import me.jddev0.ep.block.entity.FluidTransposerBlockEntity;
import net.minecraft.world.level.block.*;

public class FluidTransposerBlock extends HorizontallyOrientableWorkerMachineBlock<FluidTransposerBlockEntity> {
    public static final MapCodec<FluidTransposerBlock> CODEC = simpleCodec(FluidTransposerBlock::new);

    protected FluidTransposerBlock(Properties props) {
        super(
                props,

                () -> EPBlockEntities.FLUID_TRANSPOSER_ENTITY,
                FluidTransposerBlockEntity.class, FluidTransposerBlockEntity::new, FluidTransposerBlockEntity::tick
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
