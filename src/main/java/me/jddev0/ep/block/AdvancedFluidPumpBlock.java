package me.jddev0.ep.block;

import com.mojang.serialization.MapCodec;
import me.jddev0.ep.block.base.WorkerMachineBlock;
import me.jddev0.ep.block.entity.EPBlockEntities;
import me.jddev0.ep.block.entity.AdvancedFluidPumpBlockEntity;
import net.minecraft.world.level.block.BaseEntityBlock;

public class AdvancedFluidPumpBlock extends WorkerMachineBlock<AdvancedFluidPumpBlockEntity> {
    public static final MapCodec<AdvancedFluidPumpBlock> CODEC = simpleCodec(AdvancedFluidPumpBlock::new);

    public AdvancedFluidPumpBlock(Properties props) {
        super(
                props,

                EPBlockEntities.ADVANCED_FLUID_PUMP_ENTITY,
                AdvancedFluidPumpBlockEntity.class, AdvancedFluidPumpBlockEntity::new, AdvancedFluidPumpBlockEntity::tick
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
