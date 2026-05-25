package me.jddev0.ep.block;

import com.mojang.serialization.MapCodec;
import me.jddev0.ep.block.base.WorkerMachineBlock;
import me.jddev0.ep.block.entity.FluidPumpBlockEntity;
import me.jddev0.ep.block.entity.EPBlockEntities;
import net.minecraft.world.level.block.BaseEntityBlock;

public class FluidPumpBlock extends WorkerMachineBlock<FluidPumpBlockEntity> {
    public static final MapCodec<FluidPumpBlock> CODEC = simpleCodec(FluidPumpBlock::new);

    public FluidPumpBlock(Properties props) {
        super(
                props,

                () -> EPBlockEntities.FLUID_PUMP_ENTITY,
                FluidPumpBlockEntity.class, FluidPumpBlockEntity::new, FluidPumpBlockEntity::tick
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
