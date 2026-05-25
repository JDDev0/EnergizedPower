package me.jddev0.ep.block;

import com.mojang.serialization.MapCodec;
import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.AssemblingMachineBlockEntity;
import me.jddev0.ep.block.entity.EPBlockEntities;
import net.minecraft.world.level.block.*;

public class AssemblingMachineBlock extends HorizontallyOrientableWorkerMachineBlock<AssemblingMachineBlockEntity> {
    public static final MapCodec<AssemblingMachineBlock> CODEC = simpleCodec(AssemblingMachineBlock::new);

    protected AssemblingMachineBlock(Properties props) {
        super(
                props,

                EPBlockEntities.ASSEMBLING_MACHINE_ENTITY,
                AssemblingMachineBlockEntity.class, AssemblingMachineBlockEntity::new, AssemblingMachineBlockEntity::tick
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
