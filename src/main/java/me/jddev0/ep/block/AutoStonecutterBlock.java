package me.jddev0.ep.block;

import com.mojang.serialization.MapCodec;
import me.jddev0.ep.block.base.WorkerMachineBlock;
import me.jddev0.ep.block.entity.AutoStonecutterBlockEntity;
import me.jddev0.ep.block.entity.EPBlockEntities;
import net.minecraft.world.level.block.BaseEntityBlock;

public class AutoStonecutterBlock extends WorkerMachineBlock<AutoStonecutterBlockEntity> {
    public static final MapCodec<AutoStonecutterBlock> CODEC = simpleCodec(AutoStonecutterBlock::new);

    public AutoStonecutterBlock(Properties props) {
        super(
                props,

                EPBlockEntities.AUTO_STONECUTTER_ENTITY,
                AutoStonecutterBlockEntity.class, AutoStonecutterBlockEntity::new, AutoStonecutterBlockEntity::tick
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
