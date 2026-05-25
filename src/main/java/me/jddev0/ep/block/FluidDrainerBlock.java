package me.jddev0.ep.block;

import com.mojang.serialization.MapCodec;
import me.jddev0.ep.block.base.WorkerMachineBlock;
import me.jddev0.ep.block.entity.FluidDrainerBlockEntity;
import me.jddev0.ep.block.entity.EPBlockEntities;
import net.minecraft.world.level.block.BaseEntityBlock;

public class FluidDrainerBlock extends WorkerMachineBlock<FluidDrainerBlockEntity> {
    public static final MapCodec<FluidDrainerBlock> CODEC = simpleCodec(FluidDrainerBlock::new);

    public FluidDrainerBlock(Properties props) {
        super(
                props,

                () -> EPBlockEntities.FLUID_DRAINER_ENTITY,
                FluidDrainerBlockEntity.class, FluidDrainerBlockEntity::new, FluidDrainerBlockEntity::tick
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
