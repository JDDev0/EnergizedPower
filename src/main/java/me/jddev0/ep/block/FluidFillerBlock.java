package me.jddev0.ep.block;

import com.mojang.serialization.MapCodec;
import me.jddev0.ep.block.base.WorkerMachineBlock;
import me.jddev0.ep.block.entity.FluidFillerBlockEntity;
import me.jddev0.ep.block.entity.EPBlockEntities;
import net.minecraft.world.level.block.BaseEntityBlock;

public class FluidFillerBlock extends WorkerMachineBlock<FluidFillerBlockEntity> {
    public static final MapCodec<FluidFillerBlock> CODEC = simpleCodec(FluidFillerBlock::new);

    public FluidFillerBlock(Properties props) {
        super(
                props,

                () -> EPBlockEntities.FLUID_FILLER_ENTITY,
                FluidFillerBlockEntity.class, FluidFillerBlockEntity::new, FluidFillerBlockEntity::tick
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
