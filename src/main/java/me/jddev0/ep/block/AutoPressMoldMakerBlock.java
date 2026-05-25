package me.jddev0.ep.block;

import com.mojang.serialization.MapCodec;
import me.jddev0.ep.block.base.WorkerMachineBlock;
import me.jddev0.ep.block.entity.AutoPressMoldMakerBlockEntity;
import me.jddev0.ep.block.entity.EPBlockEntities;
import net.minecraft.world.level.block.BaseEntityBlock;

public class AutoPressMoldMakerBlock extends WorkerMachineBlock<AutoPressMoldMakerBlockEntity> {
    public static final MapCodec<AutoPressMoldMakerBlock> CODEC = simpleCodec(AutoPressMoldMakerBlock::new);

    public AutoPressMoldMakerBlock(Properties props) {
        super(
                props,

                () -> EPBlockEntities.AUTO_PRESS_MOLD_MAKER_ENTITY,
                AutoPressMoldMakerBlockEntity.class, AutoPressMoldMakerBlockEntity::new, AutoPressMoldMakerBlockEntity::tick
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
