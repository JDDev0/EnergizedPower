package me.jddev0.ep.block;

import com.mojang.serialization.MapCodec;
import me.jddev0.ep.block.base.WorkerMachineBlock;
import me.jddev0.ep.block.entity.UnchargerBlockEntity;
import me.jddev0.ep.block.entity.EPBlockEntities;
import net.minecraft.world.level.block.BaseEntityBlock;

public class UnchargerBlock extends WorkerMachineBlock<UnchargerBlockEntity> {
    public static final MapCodec<UnchargerBlock> CODEC = simpleCodec(UnchargerBlock::new);

    public UnchargerBlock(Properties props) {
        super(
                props,

                EPBlockEntities.UNCHARGER_ENTITY,
                UnchargerBlockEntity.class, UnchargerBlockEntity::new, UnchargerBlockEntity::tick
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
